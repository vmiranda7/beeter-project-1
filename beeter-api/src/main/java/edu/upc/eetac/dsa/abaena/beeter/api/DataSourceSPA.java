package edu.upc.eetac.dsa.abaena.beeter.api;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
 
public class DataSourceSPA {
    private DataSource dataSource; //referencia al datasource
	private static DataSourceSPA instance; //referencia al singelton por eso es privada y estatica
 
	private DataSourceSPA() { //constructor privado
		super();
		Context envContext = null;
		try {
			envContext = new InitialContext();
			Context initContext = (Context) envContext.lookup("java:/comp/env");
			dataSource = (DataSource) initContext.lookup("jdbc/beeterdb"); //se obtiene la referencia al datasource, por eso podemos garantziar q solo hay una referencia
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
	}
 
	public final static DataSourceSPA getInstance() { //Como obtener la instancia de singleton
		if (instance == null)
			instance = new DataSourceSPA();
		return instance;
	}
 
	public DataSource getDataSource() { //obtener el datasource, para obtener las conexiones del pool
		return dataSource;
	}
}