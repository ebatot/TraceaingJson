


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import model.AnnotatingFeature;
import model.Connection;
import model.MetadataFeature;
import model.Trace;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import transform.ConnectionFactory;
import transform.ElementFactory;
import transform.JSonTransformer;

public class Transformer {
	// Test git
	public static void main(String[] args) throws IOException {
//		printLOC();
//		System.exit(0);
		
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
		
		
		
		ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
		ElementFactory eltFactory = ElementFactory.getInstance(); 
		eltFactory.setDatamodel(datamodel); // Same as the one used by the connection factory.
		connectionFactory.setDatamodel(datamodel); // Same as the one used by the element factory.
	
		

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
		System.out.println("* Connections IDs retrieval: ");
		links_id.forEach(System.out::println);
		System.out.println("- end");
		System.out.println();
		
//		Map<String,String> mapOfElements = JSonTransformer.getMapOfElementsRawJsonFromIDs(datamodel, (String[]) links_id.toArray(new String[links_id.size()]));
//		mapOfElements.forEach((key, value) -> {
//		    System.out.println("Key : " + key + " Value : " );
//		});
//		links_af.forEach(System.out::println);
		
		
		
		System.out.println("* One Connection (specific ID) build up:");
		String link_id = "3c367802-9e00-4e95-983b-e00501307c9e";
		Connection link1 = connectionFactory.getConnection(link_id);
		System.out.println(link1.toStringPretty());
		System.out.println("- end");
		System.out.println();

		
		
		System.out.println("* Building up all connections:");
		HashMap<String, Connection> links_map = new HashMap<>(); // Un ID a su Link-object
		links_id.forEach((id)-> {
			Connection c = connectionFactory.getConnection(id);
			links_map.put(id, c);
			System.out.println(c.toStringPretty());
		});
		System.out.println(" --> "+links_map.values().size()+" connections found.");
		System.out.println("- end\n");
		
		
		
		System.out.println("* Add extra-tracetype 'typeC' to Link1.");
		AnnotatingFeature af_typeC = new AnnotatingFeature("ADDED-AF-0001");
		MetadataFeature mf_typeC = new MetadataFeature("ADDED-MF-CONF-0001", "tracetype");
		mf_typeC.addStringValue("ADDED-LR-0001", "typeC");
		af_typeC.addMetatadataFeature(mf_typeC);
		link1.addAnnotatingFeature(af_typeC);
		System.out.println("  Link1 tracetypes: "+link1.getTracetypes());
		System.out.println("  Link1 confidence: "+link1.getConfidenceValue());
		System.out.println("- end\n");
		
		
		
		System.out.println("* Playing with metadata values:");
		System.out.println("  Metafeatures: " + MetadataFeature.getAllEffectiveNames());
		System.out.println("  Tracetypes:   " + MetadataFeature.getAllTraceTypes());
		System.out.print("  Metafeatures values: {");
//		System.out.println(MetadataFeature.getAllMetadataFeatureValues());
		MetadataFeature.getAllMetadataFeatureValues().forEach((mf, v) -> {
			System.out.print( mf.getEffectiveName() + " -> " + v + ", ");
		});
		System.out.println("}");
		System.out.println("- end\n");

		
		
		System.out.println("* Building up a trace:");
		Trace t = new Trace();
		links_map.forEach((id,c)->{t.addConnection(c);});
		System.out.println(t.toStringPretty());
		File fileOut_lne = checkOutFileName(fileOut_lne_name);
		JSonTransformer.getAndStoreConnectionsAndElements(t, fileOut_lne);
		System.out.println("Trace stored in '"+fileOut_lne.getAbsolutePath()+"'.");
		System.out.println(t.toStringMatrix());
		System.out.println("- end\n");

		
		
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


	public static void printLOC(){
		int[] i;
		try {
			i = countLOC(new File("./src"));
			System.out.println("Main.main(src:"+i[0]+") ("+i[1]+" classes)");
//			i = countLOC(new File("./test"));
//			System.out.println("Main.main(test:"+i[0]+")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static int[] countLOC(File f) throws IOException {
		int[] res = new int[] {0, 0};
		if(f.getName().endsWith(".java"))
			res[1]++;
		if(f.getName().startsWith("result"))
			return res;
		if(f.isDirectory()){
			for (File f2 : f.listFiles()) {
				res[0] += countLOC(f2)[0];
				res[1] += countLOC(f2)[1];
			}
//			System.out.println("Dir:"+f.getCanonicalPath()+" : "+res);
		} else {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = "";
			while((line = br.readLine()) != null){
				if(!line.isEmpty())
					res[0]++;
			}
			br.close();
//			System.out.println(f.getCanonicalPath()+" : "+res);
		}
		return res;
	}

}
