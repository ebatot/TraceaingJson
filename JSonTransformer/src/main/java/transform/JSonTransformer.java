package transform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.AnnotatingFeature;
import model.Link;
import model.MetadataFeature;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.module.loaders.BuiltinModuleLoader;

public class JSonTransformer {
	
	
	/**
	 * Access to the LiteralRational of a MF and return the entry <ID literal, value>. Value is of type DOUBLE
	 * @param strIn
	 * @param id
	 * @return
	 * @throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException
	 */
	public static boolean affectDoubleValueToMF(String strIn, MetadataFeature mf)
			throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException {
		String id = mf.getID();
		String literalRational_str = JSonTransformer.getElementSpecificFieldFromID(strIn, id, ".payload.ownedFeature[].AAAid");
		List<String> lr_list = new ObjectMapper().readValue(literalRational_str, new TypeReference<List<String>>() {});
		String lr_s = JSonTransformer.getElementsRawJsonFromID(strIn, lr_list.get(0));
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jelem = gson.fromJson(lr_s.substring(1, lr_s.length()-1), JsonElement.class);
		JsonObject jobj = jelem.getAsJsonObject(); 
		JsonElement elt = jobj.get("payload").getAsJsonObject().get("value"); 
		
		if (elt != null) {
			mf.addDoubleValue(id, elt.getAsDouble());
		}
		return elt != null;
	}

	/**
	 * Access to the LiteralRational of a MF and return the entry <ID literal, value>. Value is of type String
	 * @param strIn
	 * @param id
	 * @return
	 * @throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException
	 */
	public static boolean affectStringValueToMF(String strIn, MetadataFeature mf)
			throws JsonQueryException, JsonProcessingException, IOException, JsonMappingException {
		
		String id = mf.getID();
		System.out.println("JSonTransformer.affectDoubleValueToMF()");
		System.out.println("  "+id);
		String s_mf = JSonTransformer.getElementSpecificFieldFromID(strIn, id, ".payload.ownedFeature[].AAAid");
		
		List<String> ss = new ObjectMapper().readValue(s_mf, new TypeReference<List<String>>() {});
		System.out.println("ss: "+ss);
		String s = JSonTransformer.getElementsRawJsonFromID(strIn, ss.get(0));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jelem = gson.fromJson(s.substring(1, s.length()-1), JsonElement.class);
		JsonObject jobj = jelem.getAsJsonObject(); 
		JsonElement elt = jobj.get("payload").getAsJsonObject().get("referent").getAsJsonObject().get("AAAid"); 
		String referentid = elt.getAsString();
		
		System.out.println("refid: "+referentid);
		
		String referent_name_str = JSonTransformer.getElementSpecificFieldFromID(strIn, referentid, ".payload.name");
		jelem = gson.fromJson(referent_name_str, JsonElement.class); //Remove the "[]"
		 
		 
		String name = jelem.getAsString();
		
		System.out.println("aie: "+name);
		System.out.println();
		System.out.println();
		
		
		if (jelem != null) {
			mf.addStringValue(id, name);
		}
		return elt != null;
		
	}


	
	
	/**
	 * Affect AF->MF to the parameter Link.
	 * 
	 * Link -> AF -> MF(effectiveName)
	 * 
	 * @param data
	 * @param l
	 * @param links_af
	 * @return  {IDS of AnnotatingFeatures affected -> {IDs of MetadataFeatures}}
	 * @throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException
	 */
	public static HashMap<String, ArrayList<String>> affectAnnotatingFeaturesToLink(String data, Link l,
			List<String> links_af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		
		HashMap<String, ArrayList<String>> linksMF_map = new HashMap<>();
		
		for (String af_str : links_af) {
			linksMF_map.put(af_str, new ArrayList<>());
			
			AnnotatingFeature af = new AnnotatingFeature(af_str);
			
			List<String> linkmf1 = LinkFactory.getConfidenceFeaturesFromJSon(data, af);
			if(!linkmf1.isEmpty()) 
				af.addMetatadataFeature("confidence", linkmf1.get(0));

			List<String> linkmf2 = LinkFactory.getTracetypeFeaturesFromJSon(data, af);
			if(!linkmf2.isEmpty())
				af.addMetatadataFeature("tracetype",  linkmf2.get(0));
			
			if(!af.getMetadatas().isEmpty()) {
				l.addAnnotatingFeature(af);
				ArrayList<String> mfs = new ArrayList<>(af.getMetadatas().size());
				af.getMetadatas().forEach( (id, mf) -> {mfs.add(mf.getID());});
				linksMF_map.put(af.getID(), mfs);
			}
		}
		return linksMF_map;
	}
	
	/**
	 * 
	 * @param strIn
	 * @param link_id
	 * @return IDS of AnnotatingFeatures 
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public static List<String> getAnnotatingFeaturesIDOfLink(String strIn, String link_id)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String links_af_id = JSonTransformer.extractJSon(strIn,
				".[].payload "
						+ "| select((.AAAtype ==  \"AnnotatingFeature\") and "
						+ "(.annotatedElement[].AAAid  == \""+link_id+"\")) | .identifier"// | "
				); // Return "[ id1, id2, id3 ...]"
		List<String> links_af = new ObjectMapper().readValue(links_af_id, new TypeReference<List<String>>() {});
		return links_af;
	}



	public static String getElementsRawJsonFromIDs(String data, String[] ids) throws JsonQueryException, JsonProcessingException, IOException{
		String jqQuery_idSelect = "(";
		int i = 0;
		for (String id : ids) 
			jqQuery_idSelect += "\n  (.identifier == \""+id+"\")" + (++i < ids.length? " or ":"");
		jqQuery_idSelect += ")";
		String jqQuery = 			".[] "
				+ "| select(.payload | " + jqQuery_idSelect + ")" ;
		
		String outText_elts = JSonTransformer.extractJSon(data, jqQuery);
		return outText_elts;
	}
	
	public static String getElementsRawJsonFromID(String data, String id) throws JsonQueryException, JsonProcessingException, IOException{
		String jqQuery = ".[] | select(.payload | ( .identifier == \""+id+"\") )" ;
		String outText_elts = JSonTransformer.extractJSon(data, jqQuery);
		return outText_elts;
	}
	
	
	/**
	 * 
	 * @param data
	 * @param id
	 * @param attribute ".payload.X.[]..." JQ style.
	 * @return
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static String getElementSpecificFieldFromID(String data, String id, String attribute) throws JsonQueryException, JsonProcessingException, IOException{
		String jqQuery = ".[] | select(.payload | ( .identifier == \""+id+"\") ) | "+attribute ;
		String outText_elts = JSonTransformer.extractJSon(data, jqQuery);
		return outText_elts;
	}

	public static HashMap<String, String> getMapOfElementsRawJsonFromIDs(String data, String[] ids) throws JsonQueryException, JsonProcessingException, IOException{
		HashMap<String, String> res = new HashMap<>(ids.length);
		for (String id : ids) {
			String jqQuery = 			".[] | select(.payload | (.identifier == \""+id+"\") )" ;
			String outText_elts = JSonTransformer.extractJSon(data, jqQuery);
			res.put(id, outText_elts);
		}
		return res;
	}

	
	public static String getPartsAndLinksRaw(String strIn) throws IOException, JsonQueryException, JsonProcessingException {
		
		String jqQuery_elts = 			".[] "
				+ "| select(.payload | (.AAAtype == \"ConnectionUsage\") or (.AAAtype == \"PartUsage\") ) ";
//				+ "| select(.payload | (.AAAtype == \"Annotation\")) ";or (.type == \"EnumerationUsage\") or (.type == \"Annotation\")
//				+ "| select(.payload | select(.type | contains(\"Usage\"))) ";
		
		String outText_elts = JSonTransformer.extractJSon(strIn, jqQuery_elts);
		return outText_elts;
	}

	
	public static String getParts(String strIn) throws IOException, JsonQueryException, JsonProcessingException {
		String eltsFields_strings = 
				" {"
				+ " \"id\" : .identifier,"
				+ " \"name\" : .name,"
				+ " \"type\" : .AAAtype,"
				+ " \"qualifiedname\" : .qualifiedName"
				+ "}";
		
		String jqQuery_elts = 			".[].payload "
				+ "| select((.AAAtype == \"ConnectionUsage\")) "
				+ "| "+eltsFields_strings;
		
		String outText_elts = JSonTransformer.extractJSon(strIn, jqQuery_elts);
		return outText_elts;
	}

	public static String getConnections(String strIn) throws IOException, JsonQueryException, JsonProcessingException {
		String linkssFields_strings = 
				" {"
				+ " \"id\" : .identifier,"
				+ " \"name\" : .name,"
				+ " \"type\" : .AAAtype,"
				+ " \"source\" : .source[].AAAid,"
				+ " \"target\" : .target[].AAAid,"
				+ " \"qualifiedname\" : .qualifiedName"
				+ "}";
		
		String jqQuery_links = 			".[].payload "
				+ "| select((.AAAtype == \"ConnectionUsage\")) "
				+ "| "+linkssFields_strings;
		
		String outText_links = JSonTransformer.extractJSon(strIn, jqQuery_links);
		return outText_links;
	}
	
	public static List<String> getLinksIDs(String data) throws JsonQueryException, JsonProcessingException, IOException {
		String jqQuery_links = 			".[].payload "
				+ "| select((.AAAtype == \"ConnectionUsage\")) | .identifier ";

		String links = JSonTransformer.extractJSon(data, jqQuery_links);
		List<String> links_id = new ObjectMapper().readValue(links, new TypeReference<List<String>>() {});
		return links_id;
	}

	
	public static String extractJSon(String strIn, String jqQuery)
			throws IOException, JsonQueryException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		JsonNode input = mapper.readTree(strIn);

		Scope rootScope = Scope.newEmptyScope();

		// Use BuiltinFunctionLoader to load built-in functions from the classpath.
		BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, rootScope);

		// For import statements to work, you need to set ModuleLoader. BuiltinModuleLoader uses ServiceLoader mechanism to
		// load Module implementations.
		rootScope.setModuleLoader(BuiltinModuleLoader.getInstance());
		
		// per every apply() invocations if you need to do so.
		Scope childScope = Scope.newChildScope(rootScope);

		// Scope#setValue(...) sets a custom variable that can be used from jq expressions. This variable is local to the
		// childScope and cannot be accessed from the rootScope. The rootScope will not be modified by this call.
		childScope.setValue("param", IntNode.valueOf(42));


		JsonQuery q = JsonQuery.compile(jqQuery, Versions.JQ_1_6);

		// You need a JsonNode to use as an input to the JsonQuery. There are many ways you can grab a JsonNode.
		// In this example, we just parse a JSON text into a JsonNode.

		// Finally, JsonQuery#apply(...) executes the query with given input and produces 0, 1 or more JsonNode.
		// The childScope will not be modified by this call because it internally creates a child scope as necessary.
		final List<JsonNode> out = new ArrayList<>();
		q.apply(childScope, input, out::add);
		
		String outText = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(out);
		return outText;
	}
	
	public static String getAndStoreConnectionsAndElements(String strIn, File fileOut_lne)
			throws IOException, JsonQueryException, JsonProcessingException {
		String outText_lne = JSonTransformer.getConnections(strIn);
		String outText_elts = JSonTransformer.getParts(strIn);
		outText_lne = "{ \"elements\": \n" + outText_lne + ",\n\"links\": \n" + outText_elts + "}";
		FileWriter fw = new FileWriter(fileOut_lne);
		fw.write(outText_lne);
		fw.close();
		return outText_lne;
	}




}
