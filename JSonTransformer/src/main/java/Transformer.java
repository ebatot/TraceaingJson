


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import model.AnnotatingFeature;
import model.Connection;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import transform.JSonTransformer;
import transform.ConnectionFactory;

public class Transformer {
	// Test git
	public static void main(String[] args) throws IOException {
		/* ARGS : 
		 * 		[0: fileIn_name]
		 * 		[1: fileOut_name]
		 * 		[2: query]
		 */
		
		String fileIn_name = 	"inout/in/Tracing_FilterExample.json";//Tracing_FilterExample  EXAMPLE-IN
		String fileOut_lne_name = 	"inout/out/Tracing_FilterExample-lne.json";
		String fileOut_lne_raw_name = 	"inout/out/Tracing_FilterExample-lne-raw.json";
		String fileOut_meta_name = 	"inout/out/Tracing_FilterExample-meta.json";
		
		
		String datamodel = checkAndCleanFileInput(fileIn_name);
		
		
	
		System.out.println("\n    **** * * * Links and Elements pretty:");
		File fileOut_lne = checkOutFileName(fileOut_lne_name);
		JSonTransformer.getAndStoreConnectionsAndElements(datamodel, fileOut_lne);
		

		System.out.println("\n    **** * * * Links and Elements RAW:");
		String outText_lne_raw = JSonTransformer.getPartsAndLinksRaw(datamodel); 
		FileWriter fw0 = new FileWriter(checkOutFileName(fileOut_lne_raw_name));
		fw0.write(outText_lne_raw);
		fw0.close();
		
		
		System.out.println("\n    **** * * * Meta batch:");
		File fileOut_meta = checkOutFileName(fileOut_meta_name);
		String outText_meta = getTracingmetas(datamodel);
		FileWriter fw2 = new FileWriter(fileOut_meta);
		fw2.write(outText_meta);
		fw2.close();
		
		
		
		List<String> links_id = JSonTransformer.getLinksIDs(datamodel);
		System.out.println("* Connections retrieval: ");
		links_id.forEach(System.out::println);
		System.out.println("- end");
		System.out.println();
		
//		Map<String,String> mapOfElements = JSonTransformer.getMapOfElementsRawJsonFromIDs(datamodel, (String[]) links_id.toArray(new String[links_id.size()]));
//		mapOfElements.forEach((key, value) -> {
//		    System.out.println("Key : " + key + " Value : " );
//		});
//		links_af.forEach(System.out::println);
		
		
		HashMap<String, Connection> links_map = new HashMap<>(); // Un ID a su Link-object
		
		System.out.println("* One Connection build up:");
		String link_id = "3c367802-9e00-4e95-983b-e00501307c9e";
		Connection link1 = ConnectionFactory.buildConnection(datamodel, link_id);
		System.out.println(link1.toStringPretty());
		HashMap<String, ArrayList<String>> linksMF_list = JSonTransformer.affectAnnotatingFeaturesToConnection(datamodel, link1);
		System.out.println("- end");
		System.out.println();

		links_id.forEach((id)-> {
			Connection c = ConnectionFactory.buildConnection(datamodel, id);
			links_map.put(id, c);
		});
		
		System.out.println("* Building up all connections:");
		for (Connection l : links_map.values()) {
			JSonTransformer.affectAnnotatingFeaturesToConnection(datamodel, l);
			l.getMetadatas().forEach((mf) -> {
				try {
					if(mf.isConfidence()) 
						JSonTransformer.affectDoubleValueToMetadataFeature(datamodel, mf);
					if(mf.isTraceType())
						JSonTransformer.affectEnumValueToMetadataFeature(datamodel, mf);
				} catch (JsonQueryException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			});
			System.out.println(l.toStringPretty());
		}
		System.out.println(" --> "+links_map.values().size()+" connections found.");
		System.out.println("* end");
		
		System.out.println("\nExit !");
		System.exit(0);
		
		
		//Get their owner as MetadataFeature MDF
		String links_af_id = JSonTransformer.executeJQuery(datamodel,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
//						+ " ((.owner.AAAid  == \"afbd0bd8-e341-4cc1-8f85-70f049012777\") or "
//						+ "  (.owner.AAAid  == \"187755de-aeb7-4a31-8d1d-449a4544cc30\"))"
						+ " ((.effectiveName  == \"confidence\") or "
						+ "  (.effectiveName  == \"tracetype\"))"
						+ ") "// | "
//						+ "((.annotatedElement[].AAAid  == \"3c367802-9e00-4e95-983b-e00501307c9e\") or (.annotatedElement[].AAAid  == \\\"3c367802-9e00-4e95-983b-e00501307c9e\\\"))) "// | "
				);
//		System.out.println(links_id);
		FileWriter fwtest = new FileWriter(checkOutFileName("inout/out/Tracing_FilterExample-test3.json"));
		fwtest.write(links_af_id);
		fwtest.close();

		
		
		System.out.println("\n    **** * * * Test batch:");
		fwtest = new FileWriter(checkOutFileName("inout/out/Tracing_FilterExample-test.json"));
		fwtest.write(JSonTransformer.executeJQuery(datamodel,
				".[].payload "
						+ "| select((.effectiveName != null)) | "
						+ "{ "
						+ "\"effectiveName\": .effectiveName, "
						+ "\"type\": .AAAtype, "
						+ "\"id\": .identifier, "
						+ "\"owner\": .owner.AAAid"
						+ "} " 
				));
		fwtest.close();
		
		

		
		

	}



	/** 
	 * Verify that the file exists and replaces the @symbol that plagues SysMLv2 JSon persistence. They are replaced with an arbitrary "AAA" sequence.
	 * @param fileIn_name
	 * @return
	 * @throws IOException
	 */
	private static String checkAndCleanFileInput(String fileIn_name) throws IOException {
		File fileIn = new File(fileIn_name);
		if(!fileIn.exists()) {
			System.out.println("File '"+fileIn.getAbsolutePath()+"' does not exist.");
			System.out.println("Exit.");
			System.exit(0);
		} else {
			System.out.println("In file:  "+fileIn.getAbsolutePath());
		}	
		String strIn = stripApartAerobases(fileIn);
		return strIn;
	}




	private static String stripApartAerobases(File fileIn) throws IOException {
		String strIn  = "";
		strIn = Files.readString(Paths.get(fileIn.getAbsolutePath()));
		strIn = strIn.replaceAll("@", "AAA");
		return strIn;
	}




	/**
	 * Returns in a String the elements of the model that are either MetadataFeature, or MetadataFeatureValue
	 * @param strIn A SysMLv2 model written in JSon
	 * @return
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 */
	private static String getTracingmetas(String strIn) throws IOException, JsonQueryException, JsonProcessingException {
//		String metasFields_strings = 
//				" {"
//				+ " \"id\" : .identifier,"
//				+ " \"name\" : .name,"
//				+ " \"type\" : .type,"
//				+ " \"qualifiedname\" : .qualifiedName"
//				+ "}";
		
		
		String jqQuery_meta = 			".[] "
				+ "| select(.payload | (.AAAtype == \"MetadataFeature\") or (.AAAtype == \"MetadataFeatureValue\") ) " 
				+ "| select(.payload | (.qualifiedName != null)  ) "; //and (.qualifiedName | contains(\"Link95\"))
//				+ "| "+metasFields_strings;

		
		
		String outText_metas = JSonTransformer.executeJQuery(strIn, jqQuery_meta);
		return outText_metas;
	}

	
	/**
	 * If the file exists it is blanked, if not it is created.
	 * @param fileOutLinksAndElements_name
	 * @return
	 * @throws IOException
	 */
	private static File checkOutFileName(String fileOutLinksAndElements_name) throws IOException {
		File fileOutLinksAndElements = new File(fileOutLinksAndElements_name);
		if(fileOutLinksAndElements.exists()) {
			System.out.println("File '"+fileOutLinksAndElements.getAbsolutePath()+"' already exists. It will be replaced.");
			fileOutLinksAndElements.delete();
			fileOutLinksAndElements.createNewFile();
		}
		return fileOutLinksAndElements;
	}


	
	
	public static void main2(String[] args) throws JsonProcessingException, IOException {

		
		String fileIn_name = 	"inout/in/EXAMPLE-IN.json";
		String fileOut_name = 	"inout/out/Tracing_FilterExample-t.json";
//		String query1 = 		"[*].payload.{name:name, id:identifier}";
//		String query = 			"[*].payload[?name=='Base'].{name:name, id:identifier}";
		String query = 			"[*].payload[?name.contains(@, 'e') == `true`]";
		
		File fileIn = new File(fileIn_name);
		if(!fileIn.exists()) {
			System.out.println("File '"+fileIn.getAbsolutePath()+"' does not exist.");
			System.out.println("Exit.");
			System.exit(0);
		}
	
		File fileOut = new File(fileOut_name);
		if(fileOut.exists()) {
			System.out.println("File '"+fileOut.getAbsolutePath()+"' already exists. It will be replaced.");
			fileOut.delete();
			fileOut.createNewFile();
		}
		System.out.println("In file:  "+fileIn.getAbsolutePath());
		System.out.println("Out file: "+fileOut.getAbsolutePath());

		JmesPath<JsonNode> jmespath = new JacksonRuntime();
		Expression<JsonNode> expression = jmespath.compile(query);
		System.out.println("Query: " + query);
		System.out.println("    -> " +expression);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		JsonNode input = mapper.readTree(fileIn);
		System.out.println("Input: "+input);

		JsonNode result = expression.search(input);
		
		System.out.println("Result: "+result);
		
	
		FileWriter fw = new FileWriter(fileOut);
		fw.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
		fw.close();
	}

}
