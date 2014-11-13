package edu.upc.eetac.dsa.abaena.beeter.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
import edu.upc.eetac.dsa.abaena.beeter.api.model.BeeterError;

 
@Provider //son manejadores de excepciones de manera que las van a procesar y nos la devolveran en el formato que nos interese (status 404- mensaje sting inexistente)
//las aplicaciones webapplication definene la not found excepction, fourbbiden exception
public class WebApplicationExceptionMapper implements
		ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException exception) { //construimos la respuesta
		BeeterError error = new BeeterError(
				exception.getResponse().getStatus(), exception.getMessage());
		return Response.status(error.getStatus()).entity(error) //crea la respuesta a manopla con el estado y el mensaje que le hemos puesta en esta excepcion
				.type(MediaType.BEETER_API_ERROR).build(); //status el codigo http de la respuesta, el entity es el body de la respuesto y el tipo es el content type, y el build permite crear la respuesta
	}
 
}