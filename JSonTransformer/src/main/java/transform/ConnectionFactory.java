package transform;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.AnnotatingFeature;
import model.Connection;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class ConnectionFactory {

	
	/**
	 * Affects attributes Name, source, and target to the connection which ID is passed in parameter,
	 * 
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param l a Connection
	 *  
	 */
	public static Connection buildConnection(String datamodel, String connection_id)  {
		try {
			String sl = JSonTransformer.getElementsRawJsonFromID(datamodel, connection_id);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement jelem = gson.fromJson(sl.substring(1, sl.length() - 1), JsonElement.class);
			JsonObject jobj = jelem.getAsJsonObject();
			JsonElement elt_con = jobj.get("payload");

			Connection c = new Connection(connection_id);
			JsonElement name = elt_con.getAsJsonObject().get("name");
			if (name != null) {
				c.setEffectiveName(name.getAsString());
			}
			// TODO make it multi ended ! and GSON friendly tant qu'a faire

			String source = JSonTransformer.executeJQuery(elt_con.getAsJsonObject().toString(), ".source[0].AAAid");
			source = source.substring(1, source.length() - 1).trim();
			c.setSourceId(source.substring(1, source.length() - 1));

			String target = JSonTransformer.executeJQuery(elt_con.getAsJsonObject().toString(), ".target[0].AAAid");
			target = target.substring(1, target.length() - 1).trim();
			c.setSourceId(target.substring(1, target.length() - 1));
			return c;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Something went wrong
		return null;
	}

	public static List<String> getTracetypeFeaturesFromJSon(String strIn, AnnotatingFeature af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String tmp2 = JSonTransformer.executeJQuery(strIn,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
						+ "((.owner.AAAid  == \""+af.getID()+"\") and"
						+ " (.effectiveName  == \"tracetype\"))"
						+ ") | .identifier"
				);
		List<String> linkmf2 = new ObjectMapper().readValue(tmp2, new TypeReference<List<String>>() {});
		return linkmf2;
	}


	public static List<String> getConfidenceFeaturesFromJSon(String strIn, AnnotatingFeature af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String tmp1 = JSonTransformer.executeJQuery(strIn,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
						+ "((.owner.AAAid  == \""+af.getID()+"\") and"
						+ " (.effectiveName  == \"confidence\"))"
						+ ") | .identifier"
				);
		List<String> linkmf1 = new ObjectMapper().readValue(tmp1, new TypeReference<List<String>>() {});
		return linkmf1;
	}
	
	
	
	

}
