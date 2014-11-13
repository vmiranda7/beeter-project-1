package edu.upc.eetac.dsa.abaena.beeter.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.digest.DigestUtils;

import edu.upc.eetac.dsa.abaena.beeter.api.model.Sting;
import edu.upc.eetac.dsa.abaena.beeter.api.model.User;


@Path("/users")
public class UserResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
 
	private final static String GET_USER_BY_USERNAME_QUERY = "select * from users where username=?";
	private final static String INSERT_USER_INTO_USERS = "insert into users values(?, MD5(?), ?, ?)";
	private final static String INSERT_USER_INTO_USER_ROLES = "insert into user_roles values (?, 'registered')";
	private final static String GET_USER_BY_USERNAME ="select * from users where username=?";
	private final static String DELETE_USER_QUERY = "Delete from users where username=? ";
	private final static String UPDATE_USER_QUERY = "update users set email=ifnull(?, email), name=ifnull(?, name) where username=?";
	
	@POST
	@Consumes(MediaType.BEETER_API_USER)
	@Produces(MediaType.BEETER_API_USER)
	public User createUser(User user) {
		validateUser(user);
 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmtGetUsername = null;
		PreparedStatement stmtInsertUserIntoUsers = null;
		PreparedStatement stmtInsertUserIntoUserRoles = null;
		try {
			//Busca el nombre de usuario a la base de datos y mira si ya existe
			stmtGetUsername = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmtGetUsername.setString(1, user.getUsername());
 
			ResultSet rs = stmtGetUsername.executeQuery();
			if (rs.next())
				throw new WebApplicationException(user.getUsername()
						+ " already exists.", Status.CONFLICT);
			rs.close();
 
			conn.setAutoCommit(false); //no se hace, hasta que confirmemos que queremos que se haga, ya que un insert significa insertar datas en dos tablas, y da un problema puede quedar un usuario sin rol
			stmtInsertUserIntoUsers = conn //creamos los stmt
					.prepareStatement(INSERT_USER_INTO_USERS);
			stmtInsertUserIntoUserRoles = conn
					.prepareStatement(INSERT_USER_INTO_USER_ROLES);
 //cuando verificas que el nombre es unico, insertas el usuario a la base de datos
			//damos valor
			stmtInsertUserIntoUsers.setString(1, user.getUsername());
			stmtInsertUserIntoUsers.setString(2, user.getPassword());
			stmtInsertUserIntoUsers.setString(3, user.getName());
			stmtInsertUserIntoUsers.setString(4, user.getEmail());
			stmtInsertUserIntoUsers.executeUpdate(); //ejecutamos
 //damos valor
			stmtInsertUserIntoUserRoles.setString(1, user.getUsername());
			stmtInsertUserIntoUserRoles.executeUpdate(); //ejecutamos
 
			conn.commit(); // se inserta en la base de datos en las dos tablas
		} catch (SQLException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
				}
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally { //tenemos que poner el commit a true
			try {
				if (stmtGetUsername != null)
					stmtGetUsername.close();
				if (stmtInsertUserIntoUsers != null)
					stmtGetUsername.close();
				if (stmtInsertUserIntoUserRoles != null)
					stmtGetUsername.close();
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
			}
		}
		user.setPassword(null);
		return user;
	}
 
	private void validateUser(User user) {
		if (user.getUsername() == null)
			throw new BadRequestException("username cannot be null.");
		if (user.getPassword() == null)
			throw new BadRequestException("password cannot be null.");
		if (user.getName() == null)
			throw new BadRequestException("name cannot be null.");
		if (user.getEmail() == null)
			throw new BadRequestException("email cannot be null.");
	}
 
	@Path("/login")
	@POST
	@Produces(MediaType.BEETER_API_USER)
	@Consumes(MediaType.BEETER_API_USER)
	public User login(User user) {
		if (user.getUsername() == null || user.getPassword() == null)
			throw new BadRequestException(
					"username and password cannot be null.");
 
		String pwdDigest = DigestUtils.md5Hex(user.getPassword()); //calculamos el md5 de la contraseña
		String storedPwd = getUserFromDatabase(user.getUsername(), true) //nos devuelve el pasword en md5 y en hexadecimal y se puede comparar con el de la base de datos y que sean iguales
				.getPassword();
 
		user.setLoginSuccessful(pwdDigest.equals(storedPwd)); //ponemos el atributo de login si es true o false si coinciden
		user.setPassword(null); //cuando nos pasan el user, de manera que la contraseña no aparece
		return user;
	}
 
	private User getUserFromDatabase(String username, boolean password) {
		User user = new User();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmt.setString(1, username);
 
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				if (password)
					user.setPassword(rs.getString("userpass"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
			} else
				throw new NotFoundException(username + " not found.");
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
 
		return user;
	}
	
	
	@Path("/{username}")
	@GET
	@Produces(MediaType.BEETER_API_USER)
	public Response getProfile(@QueryParam("username") String username, @Context Request request){
		
		
		System.out.println("Implementamos el cache");
		
		// Create CacheControl
		CacheControl cc = new CacheControl();
		
		User user = getprofileUserFromDatabase(username);
		//pillamos el correo y el username para ver si ha modificado
		String s = user.getEmail() + " " + user.getName();
		
		//Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(s.hashCode()));
	 
		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);
		
		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
	 
		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(user).cacheControl(cc).tag(eTag);
	 
		return rb.build();
		
	}
	
	private User getprofileUserFromDatabase( String username) {
		System.out.println("Conectamos a la base de datos" + username);
		
		User user = new User();
		 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmt = null;
		try {
			System.out.println("Preparamos la query");
			stmt = conn.prepareStatement(GET_USER_BY_USERNAME);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			System.out.println("Ejecutamos la query");
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				
			
			} else {
				throw new NotFoundException("There's no user with username="
						+ username);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return user;
	 
		
		
	}
	
	
	
	
	

	@DELETE //metodo para borrar un sting concreto
	@Path("/{username}")
	public void deleteUser(@PathParam("username") String username) {
		//tenemos un void de manera que no devuelve nada ni consume ni produce, devuelve 204 ya que no hay contenido
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_USER_QUERY);
			stmt.setString(1, username);
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no sting with username="
						+ username);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	
	
	
	
	
	@PUT
	@Path("/{username}")
	@Consumes(MediaType.BEETER_API_USER)
	@Produces(MediaType.BEETER_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {
		
		//validateUser(username);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			
			
			stmt = conn.prepareStatement(UPDATE_USER_QUERY);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, username);
	 
			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUsernameFromDatabase(username);
	 
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return user;
	}
	 

		private User getUsernameFromDatabase(String username) {// Cogemos datos del
		// username
		User user = new User();

// Hacemos la conexión a la base de datos
	Connection conn = null;
	try {
		conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);	
			}

	PreparedStatement stmt = null;
	try {
		stmt = conn.prepareStatement(buildGetUserByUsername());
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			user.setUsername(rs.getString("username"));
			user.setName(rs.getString("name"));
			user.setEmail(rs.getString("email"));

		} else {
			throw new NotFoundException("There's no sting with username ="
					+ username);
		}

	} catch (SQLException e) {
		throw new ServerErrorException(e.getMessage(),
				Response.Status.INTERNAL_SERVER_ERROR);
	} finally {
		try {
			if (stmt != null)
				stmt.close();
			conn.close();
		} catch (SQLException e) {

		}
	}
	return user;

		}
	
		public String buildGetUserByUsername() {
			return "SELECT *FROM users WHERE username=?;";
		}
		
		
		
	
	
}