package test;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;

import model.AnnotatingFeature;
import model.Connection;
import model.MetadataFeature;
import model.Trace;
import model.Trace.FormatForPrintingMetadatas;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import transform.ConnectionFactory;
import transform.ConnectionFactory.TraceTypesEncoding;
import transform.ElementFactory;
import transform.JSonTransformer;

public class TestTransformer {
	
	static Object[] test_configuration_enum = new Object[] {
			"Tracetypes as ENUM, conf&types separated",
			"inout/in/Tracing_FilterExample_orginial_20211126.json",
			TraceTypesEncoding.ENUM,
			null,
			"inout/out/Tracing_FilterExample_orginial_20211126_OUT.json",
			null,
			"3c367802-9e00-4e95-983b-e00501307c9e"
		};
	
	static Object[] test_configuration_string = new Object[] {
			"Tracetypes as STRING, conf&types separated",
			"inout/in/Tracing_FilterExample_StrTypes.json",
			TraceTypesEncoding.STRING,
			null,
			"inout/out/Tracing_FilterExample_StrTypes_OUT.json",
			null,
			"5a5c47cc-e02e-429e-ba1a-d66712d3973a"
		};
	
	static Object[] configuration_eVehicle_architecture = new Object[] {
			"eVehicle example from the Textual langage notation book (String tracetypes)",
			"inout/in/eVehicle_LogicalArchitecture.json",
			TraceTypesEncoding.STRING,
			null,
			"inout/out/eVehicle_LogicalArchitecture_OUT.json",
			null,
			"a132c05f-9372-4c8b-9730-4821aff645bd"
		};
	
	static Object[] configuration_eVehicle_architecture_named = new Object[] {
			"eVehicle example from the Textual langage notation book (String tracetypes)",
			"inout/in/eVehicle_LogicalArchitecture_named.json",
			TraceTypesEncoding.STRING,
			null,
			"inout/out/eVehicle_LogicalArchitecture_OUT.json",
			null,
			"4158b158-4110-4067-9d39-7ba17c16961c"
		};
	
	static Object[] configuration_eDrone_architecture_named = new Object[] {
			"eDrone example from the Textual langage notation book (String tracetypes)",
			"inout/in/eDrone_LogicalArchitecture_named.json",
			TraceTypesEncoding.STRING,
			null,
			"inout/out/eDrone_LogicalArchitecture_OUT.json",
			null,
			null
		};
	
	static Object[] configuration_tracing_MetaConnections = new Object[] {
			"eDrone example from the Textual langage notation book (String tracetypes)", //0
			"inout/in/tracing_MetaConnections.json", //1
			TraceTypesEncoding.STRING, //2
			null,//3
			"inout/out/tracing_MetaConnections_OUT.json", //4
			null, //5
			null, //6
		};
	


	
	public static void main(String[] args) throws IOException {
//		printLOC();
//		System.exit(0);
		
		System.out.println("      *  SysMLv2-JSon Transformer v0.1 *");
		System.out.println("      *  *  *  *  *  *  *  *  *  *  *  *");
		System.out.println("      *  *  *    T E S T S    *  *  *  *");
		System.out.println("      *  *  *  *  *  *  *  *  *  *  *  *");
		System.out.println();
		
		Object[] configuration = null;
//		configuration = configuration_eDrone_architecture_named;//test_configuration_string;
		configuration = configuration_tracing_MetaConnections;
		
		
		String fileIn_name = (String)configuration[1];
		TraceTypesEncoding tracetypesType = (TraceTypesEncoding)configuration[2];
		String fileOut_sysml_name = 	(String)configuration[3];
		String fileOut_json_name = 	(String)configuration[4];
		String fileOut_html_name = 	(String)configuration[5];
		String link1_id = null;
		try {
			link1_id = (String)configuration[6];
		} catch (Exception e1) {
			//Silent undefined, keep it null.
		}
		
		boolean chg_name_html = false;
		boolean chg_name_json = false;
		boolean chg_name_sysml = false;
		
		if((chg_name_html = fileOut_html_name == null))
			fileOut_html_name = JSonTransformer.convertNameTo(fileIn_name, "html");
		if((chg_name_json = fileOut_json_name == null))
			fileOut_json_name = JSonTransformer.convertNameTo(fileIn_name, "json");
		if((chg_name_sysml = fileOut_sysml_name == null))
			fileOut_sysml_name = JSonTransformer.convertNameTo(fileIn_name, "sysml");
		
		
		System.out.println("Configuration: "+configuration[0]);
		System.out.println("  Model file:         " + fileIn_name);
		System.out.println("  Tracetype type:     " + tracetypesType);
		System.out.println("  Output:");
		System.out.println("    sysml :           " + fileOut_sysml_name + (chg_name_sysml? " *" : ""));
		System.out.println("    json :            " + fileOut_json_name + (chg_name_json? " *" : ""));
		System.out.println("    html :            " + fileOut_html_name + (chg_name_html? " *" : ""));
		System.out.println("  Test connection ID: " + link1_id);
		System.out.println();
		
		String datamodel = checkAndCleanFileInput(fileIn_name);
		
		
		ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
		ElementFactory eltFactory = ElementFactory.getInstance(); 
		eltFactory.setDatamodel(datamodel); // Same as the one used by the connection factory.
		connectionFactory.setDatamodel(datamodel); // Same as the one used by the element factory.
		connectionFactory.setTracetypeEncoding(tracetypesType);
		

//		System.out.println("\n    **** * * * Links and Elements RAW:");
//		String fileOut_lne_raw_name = 	"inout/out/Tracing_FilterExample-lne-raw.json";
//		String outText_lne_raw = JSonTransformer.getPartsAndLinksRaw(datamodel); 
//		FileWriter fw0 = new FileWriter(checkOutFileName(fileOut_lne_raw_name));
//		fw0.write(outText_lne_raw);
//		fw0.close();
		
		
//		System.out.println("\n    **** * * * Meta batch:");
//		String fileOut_meta_name = 	"inout/out/Tracing_FilterExample-meta.json";
//		File fileOut_meta = checkOutFileName(fileOut_meta_name);
//		String outText_meta = getTracingmetas(datamodel);
//		FileWriter fw2 = new FileWriter(fileOut_meta);
//		fw2.write(outText_meta);
//		fw2.close();
		
		
		
		List<String> links_id = JSonTransformer.getLinksIDs(datamodel);
		System.out.println("* Connections IDs retrieval: ");
		links_id.forEach(System.out::println);
		System.out.println(" -> "+links_id.size() + " connections.");
		System.out.println("- end");
		System.out.println();
		
//		Map<String,String> mapOfElements = JSonTransformer.getMapOfElementsRawJsonFromIDs(datamodel, (String[]) links_id.toArray(new String[links_id.size()]));
//		mapOfElements.forEach((key, value) -> {
//		    System.out.println("Key : " + key + " Value : " );
//		});
//		links_af.forEach(System.out::println);
		
		
		System.out.println("* One Connection (specific ID) build up:");
		Connection link1 = null;
		try {
			if (link1_id == null) {
				int idx_id = new Random().nextInt(links_id.size());
				link1_id = links_id.get(idx_id);
				System.out.println(" No ID defined, random pick: "+link1_id);
			}
			link1 = connectionFactory.getConnection(link1_id);
			System.out.println(link1.toStringPretty());
		} catch (Exception e) {
			System.err.println("Check that ID '" + link1_id + "' points to a ConnectionUsage ID in the datamodel.");
			e.printStackTrace();
		}
		System.out.println("- end");
		System.out.println();

		
		
		System.out.println("* Building up all connections:");
		HashMap<String, Connection> links_map = new HashMap<>(); // Un ID a su Link-object
		links_id.forEach((id)-> {
			Connection c = connectionFactory.getConnection(id);
			links_map.put(id, c);
			System.out.println(c.toStringPrettyWithID());
		});
		System.out.println(" --> "+links_map.values().size()+" connections found.");
		System.out.println("- end\n");
		
		
		
		System.out.println("* Add extra-tracetype 'typeC' to Link1.");
		if(link1 == null) {
			System.out.println("Something wrong with link1, confront ID with datamodel to run this section.");
		} else {
			AnnotatingFeature af_typeC = new AnnotatingFeature("ADDED-AF-0001");
			MetadataFeature mf_typeC = new MetadataFeature("ADDED-MF-CONF-0001", "tracetype");
			mf_typeC.addStringValue("ADDED-LR-0001", "typeC");
			af_typeC.addMetatadataFeature(mf_typeC);
			link1.addAnnotatingFeature(af_typeC);
			System.out.println("  Link1 tracetypes: " + link1.getTracetypes());
			try {
				System.out.println("  Link1 confidence: " + link1.getConfidenceValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("- end\n");
		
		
		
		System.out.println("* Playing with metadata values:");
		System.out.println("  Metafeatures: " + MetadataFeature.getAllEffectiveNames());
		System.out.println("  Tracetypes:   " + MetadataFeature.getAllTraceTypes());
		System.out.print("  Metafeatures values: {");
		MetadataFeature.getAllMetadataFeatureValues().forEach((mf, v) -> {
			System.out.print( mf.getEffectiveName() + " -> " + v + ", ");
		});
		System.out.println("}");
		System.out.println("- end\n");

		
		
		System.out.println("* Building up a trace:");
		Trace t = new Trace();
		links_map.forEach((id,c)->{t.addConnection(c);});
		System.out.println(t.toStringPretty());
		File fileOut_lne = checkOutFileName(fileOut_json_name);
		boolean success = true; 
		try {
			FileWriter fw = new FileWriter(fileOut_lne);
			String t_string = t.generateTraceaJSon();
			fw.write(t_string);
			fw.close();
		} catch (IOException e) {
			System.out.println("Something went wrong when writing in '"+fileOut_lne.getAbsolutePath()+"'.");
			e.printStackTrace();
			success = false;
		}
		if(success)
			System.out.println("Trace stored in '"+fileOut_lne.getAbsolutePath()+"'.");
		else
			System.out.println("¡¡¡¡ Trace not stored, problem encountered !!!!");
		
		
		File fileOut_sml = checkOutFileName(fileOut_sysml_name);
		FileWriter fw = new FileWriter(fileOut_sml);
		fw.write(t.generateSysML(FormatForPrintingMetadatas.SEPARATED));
		fw.close();
		System.out.println("Trace SysMLv2 stored in '"+fileOut_sml.getAbsolutePath()+"'.");
		
		System.out.println();
//		System.out.println(t.toStringMatrixText());
//		System.out.println(t.toStringMatrixHTML());
//		System.out.println(t.toStringSysML(FormatForPrintingMetadatas.WITH_AEROBASE));
//		System.out.println(t.toStringSysMLNoConnections().trim());
		System.out.println("- end\n");
		
		
		System.out.println("\nExit !");
		System.exit(0);
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
			//System.out.println("In file:  "+fileIn.getAbsolutePath());
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
	public static String getTracingmetas(String strIn) throws IOException, JsonQueryException, JsonProcessingException {
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
