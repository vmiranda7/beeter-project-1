package edu.upc.eetac.dsa.abaena.beeter.api.model;

import java.util.ArrayList;
import java.util.List;
 
import javax.ws.rs.core.Link;
 
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;
 
import edu.upc.eetac.dsa.abaena.beeter.api.MediaType;
import edu.upc.eetac.dsa.abaena.beeter.api.StingResource;
 
public class StingCollection {
	@InjectLinks({
			@InjectLink(resource = StingResource.class, style = Style.ABSOLUTE, rel = "create-sting", title = "Create sting", type = MediaType.BEETER_API_STING),
			@InjectLink(value = "/stings?before={before}", style = Style.ABSOLUTE, rel = "previous", title = "Previous stings", type = MediaType.BEETER_API_STING_COLLECTION, bindings = { @Binding(name = "before", value = "${instance.oldestTimestamp}") }),
			@InjectLink(value = "/stings?after={after}", style = Style.ABSOLUTE, rel = "current", title = "Newest stings", type = MediaType.BEETER_API_STING_COLLECTION, bindings = { @Binding(name = "after", value = "${instance.newestTimestamp}") }) })
	private List<Link> links;
	//devuelve un array de links
	//si pones cadenas como los dos enlaces ultimos es peor ya que tienes que cambiarlo si lo has modificado por algun caso pero se hace pq no se pueden poner queryparams, el valor esta entre corchetes porque es un valor
	//con binding das valor a after y before, siempre es igual a un array si hay dos variables en un mismo enlace los separas con comas, el name tiene que coincidir con el recurso y el valor es dolar y entre llames el valor que quieres poner, instance haces referencia a un atributo de esta instancia (stingcollection)
	private List<Sting> stings;
	private long newestTimestamp;
	private long oldestTimestamp;
 
	public StingCollection() {
		super();
		stings = new ArrayList<>();
	}
 
	public List<Sting> getStings() {
		return stings;
	}
 
	public void setStings(List<Sting> stings) {
		this.stings = stings;
	}
 
	public void addSting(Sting sting) {
		stings.add(sting);
	}
 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}
 
	public long getNewestTimestamp() {
		return newestTimestamp;
	}
 
	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}
 
	public long getOldestTimestamp() {
		return oldestTimestamp;
	}
 
	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}
}