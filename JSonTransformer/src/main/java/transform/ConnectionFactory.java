package transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import model.Element;
import model.MetadataFeature;
import model.UndefinedDataException;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class ConnectionFactory {
	static HashMap<String, Connection> allConnections = new HashMap<>();
	

	ElementFactory eltFactory;
	String datamodel;
	
	static ConnectionFactory instance;
	public ConnectionFactory(ElementFactory eltFactory) {
		this.eltFactory = eltFactory;
	}

	public static ConnectionFactory getInstance() {
		if(instance == null)
			instance = new ConnectionFactory(ElementFactory.getInstance());
		return instance;
	}
	
	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}

	
	/**
	 * Get a connection from the existing list of connections 
	 * or, if the ID has not been used yet creates a new one then affects attributes (name, source, target, type...) to the new connection.
	 * 
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param connection_id the id of the conneciton to get or create.
	 */
	public Connection getConnection(String connection_id)  {
		return getConnection(connection_id, false);
	}

	/**
	 * Get a connection from the existing list of connections 
	 * or, if the ID has not been used yet creates a new one then affects attributes (name, source, target, type...) to the new connection.
	 * 
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param connection_id the id of the conneciton to get or create.
	 * @param a flag to force the rereading of the data.
	 *  
	 */
	public Connection getConnection(String connection_id, boolean forceReadFromDatamodel)  {
		Connection c = allConnections.get(connection_id);
		if(c != null && ! forceReadFromDatamodel)
			return c;
		
		if(datamodel == null) 
			throw new UndefinedDataException("The datamodel has not been instantiated yet. \nUse 'setDatamodel' with a valid SysMLv2 model written in JSon to continue.");

		try {
			c = Connection.createConnection(connection_id);
			
			String sl = JSonTransformer.getElementRawJsonFromID(datamodel, connection_id);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement jelem = gson.fromJson(sl.substring(1, sl.length() - 1), JsonElement.class);
			JsonObject jobj = jelem.getAsJsonObject();
			JsonElement elt_con = jobj.get("payload");

			allConnections.put(connection_id, c);
			
			JsonElement name = elt_con.getAsJsonObject().get("name");
			if (name != null) {
				c.setEffectiveName(name.getAsString());
			}
			
			JsonElement qname = elt_con.getAsJsonObject().get("qualifiedName");
			if (qname != null) {
				c.setQualifiedName(qname.getAsString());
			}

			
			// TODO make it multi ended ! 
			String source_id = JSonTransformer.executeJQuery(elt_con.getAsJsonObject().toString(), ".source[0].AAAid");
			source_id = JSonTransformer.oneValueJsonArrayToString(source_id);
			Element source = ElementFactory.getInstance().getElement(source_id);
			c.addSourceElement(source);
			
			String target_id = JSonTransformer.executeJQuery(elt_con.getAsJsonObject().toString(), ".target[0].AAAid");
			target_id = JSonTransformer.oneValueJsonArrayToString(target_id);
			Element target = ElementFactory.getInstance().getElement(target_id);
			c.addTargetElement(target);
			
			affectAnnotatingFeaturesToConnection(c);
			affectMetadataFeatureValueToConnection(c);

			
			return c;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Something went wrong
		return null;
	}


	public List<String> getTracetypeFeaturesFromJSon(AnnotatingFeature af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String tmp2 = JSonTransformer.executeJQuery(datamodel,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
						+ "((.owner.AAAid  == \""+af.getID()+"\") and"
						+ " (.effectiveName  == \"tracetype\"))"
						+ ") | .identifier"
				);
		List<String> linkmf2 = new ObjectMapper().readValue(tmp2, new TypeReference<List<String>>() {});
		return linkmf2;
	}


	public List<String> getConfidenceFeaturesFromJSon(AnnotatingFeature af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String tmp1 = JSonTransformer.executeJQuery(datamodel,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
						+ "((.owner.AAAid  == \""+af.getID()+"\") and"
						+ " (.effectiveName  == \"confidence\"))"
						+ ") | .identifier"
				);
		List<String> linkmf1 = new ObjectMapper().readValue(tmp1, new TypeReference<List<String>>() {});
		return linkmf1;
	}

	/**
	 * Get the values of metadatafeature of a connection from the model and affect them to the Connection object.
	 * @param l a Connection
	 */
	private void affectMetadataFeatureValueToConnection(Connection l) {
		l.getMetadatas().forEach((mf) -> {
			affectValueToMetadataFeature(mf);
		});
	}

	private void affectValueToMetadataFeature(MetadataFeature mf) {
		try {
			if(mf.isConfidence()) 
				affectDoubleValueToMetadataFeature(mf);
			if(mf.isTraceType())
				affectEnumValueToMetadataFeature(mf);
		} catch (JsonQueryException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read Annotated connections and affect AnnotatingFeatures to their "Link"
	 * representation. Link read through annotating features to access
	 * Metadatafeatures (and so on their values). This methods links the first three
	 * steps : Link-AF-MF, and returns such a Mapping in Java collection map.
	 * 
	 * Link -> AF -> MF(effectiveName)
	 * 
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param l         Link - will be edited.
	 * @param links_af
	 * @return {IDS of AnnotatingFeatures affected -> {IDs of MetadataFeatures}}
	 */
	private HashMap<String, ArrayList<String>> affectAnnotatingFeaturesToConnection(Connection l) {
	
		List<String> links_af = null;
		try {
			links_af = JSonTransformer.getAnnotatingFeaturesIDOfConnection(datamodel, l.getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
		HashMap<String, ArrayList<String>> linksMF_map = new HashMap<>();
		if (links_af != null) {
	
			for (String af_str : links_af) {
				linksMF_map.put(af_str, new ArrayList<>());
	
				AnnotatingFeature af = new AnnotatingFeature(af_str);
				try {
					List<String> linkmf1 = getInstance().getConfidenceFeaturesFromJSon(af);
					for (String mfconf : linkmf1) 
						af.addMetatadataFeature("confidence", mfconf);
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				try {
					List<String> linkmf2 = getInstance().getTracetypeFeaturesFromJSon(af);
					for (String mfttype : linkmf2) 
						af.addMetatadataFeature("tracetype", mfttype);
				} catch (IOException e) {
					e.printStackTrace();
				}
	
				if (!af.getMetadatas().isEmpty()) {
					l.addAnnotatingFeature(af);
					ArrayList<String> mfs = new ArrayList<>(af.getMetadatas().size());
					af.getMetadatas().forEach((id, mf) -> {
						mfs.add(mf.getID());
					});
					linksMF_map.put(af.getID(), mfs);
				}
			}
		}
		return linksMF_map;
	}

	/**
	 * Access to the LiteralRational of a MF and return the entry <ID of the literal, value of the literal>. Value is of type DOUBLE
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param id
	 * @return
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws JsonMappingException
	 */
	private boolean affectDoubleValueToMetadataFeature(MetadataFeature mf)
			throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException {
		String id = mf.getID();
		String literalRational_str = JSonTransformer.getElementSpecificFieldFromID(datamodel, id, ".payload.ownedFeature[].AAAid");
		List<String> lr_list = new ObjectMapper().readValue(literalRational_str, new TypeReference<List<String>>() {});
		String lr_s = JSonTransformer.getElementRawJsonFromID(datamodel, lr_list.get(0));
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jelem = gson.fromJson(lr_s.substring(1, lr_s.length()-1), JsonElement.class);
		JsonObject jobj = jelem.getAsJsonObject(); 
		JsonElement elt = jobj.get("payload").getAsJsonObject().get("value"); 
		
		if (elt != null) 
			mf.addDoubleValue(id, elt.getAsDouble());
		
		return elt != null;
	}

	/**
	 * Access to the LiteralRational of a MF and return the entry <ID of the literal, value of the literal>. Value is of type String.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param id
	 * @return
	 * @throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException
	 */
	private boolean affectEnumValueToMetadataFeature(MetadataFeature mf)
			throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException {
		
		String id = mf.getID();
		String s_mf = JSonTransformer.getElementSpecificFieldFromID(datamodel, id, ".payload.ownedFeature[].AAAid");
		
		List<String> ss = new ObjectMapper().readValue(s_mf, new TypeReference<List<String>>() {});
		String s = JSonTransformer.getElementRawJsonFromID(datamodel, ss.get(0));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jelem = gson.fromJson(s.substring(1, s.length()-1), JsonElement.class);
		JsonObject jobj = jelem.getAsJsonObject(); 
		JsonElement elt = jobj.get("payload").getAsJsonObject().get("referent").getAsJsonObject().get("AAAid"); 
		String referentid = elt.getAsString();
		
		String referent_name_str = JSonTransformer.getElementSpecificFieldFromID(datamodel, referentid, ".payload.name");
		jelem = gson.fromJson(referent_name_str, JsonElement.class); //Remove the "[]"
		 
		 
		String name = jelem.getAsString();
		
		if (jelem != null) {
			mf.addStringValue(id, name);
		}
		return elt != null;
		
	}
	
	
	
	

}
