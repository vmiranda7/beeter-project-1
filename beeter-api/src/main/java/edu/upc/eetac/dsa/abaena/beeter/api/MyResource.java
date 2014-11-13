package edu.upc.eetac.dsa.abaena.beeter.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */ 
@Path("myresource") //URI relativa hacia la trayectoria de contexto,
public class MyResource { //POJO no heredar ni implementar interfaz

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	//Como mínimo tiene que tener un metodo
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
}
