package edu.upc.eetac.dsa.abaena.beeter.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
 
import edu.upc.eetac.dsa.abaena.beeter.api.model.BeeterRootAPI;
 
@Path("/")
public class BeeterRootAPIResource {
	@GET
	public BeeterRootAPI getRootAPI() {
		BeeterRootAPI api = new BeeterRootAPI();
		return api;
	}
}