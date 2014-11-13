package edu.upc.eetac.dsa.abaena.beeter.api.model;

import java.util.List;

import javax.ws.rs.core.Link;
 
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;
 
import edu.upc.eetac.dsa.abaena.beeter.api.BeeterRootAPIResource;
import edu.upc.eetac.dsa.abaena.beeter.api.MediaType;
import edu.upc.eetac.dsa.abaena.beeter.api.StingResource;
 
public class BeeterRootAPI {
	@InjectLinks({ //nos devuelve los primeros enlaces a partir de los cuales evoluciona la aplicacion
		@InjectLink(resource = BeeterRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "Beeter Root API", method = "getRootAPI"),
		@InjectLink(resource = StingResource.class, style = Style.ABSOLUTE, rel = "stings", title = "Latest stings", type = MediaType.BEETER_API_STING_COLLECTION),
		@InjectLink(resource = StingResource.class, style = Style.ABSOLUTE, rel = "create-stings", title = "Latest stings", type = MediaType.BEETER_API_STING) })
	//injectlinks los enlaces son un array, resource indicas el enlace, sytle-< abslolute veremos la uri absoluta, metodo es indiferente que lo pongas o no
	//resource = sitingresource indica que el path esta con /stings, y indicamos el tipo de media que trabaja el recurso
	private List<Link> links; //atributo con getters y setters
 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}