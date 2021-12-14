/*****************************************************************************

* Copyright (c) 2015, 2018 CEA LIST, Edouard Batot

*

* All rights reserved. This program and the accompanying materials

* are made available under the terms of the Eclipse Public License 2.0

* which accompanies this distribution, and is available at

* https://www.eclipse.org/legal/epl-2.0/

*

* SPDX-License-Identifier: EPL-2.0

*

* Contributors:

* CEA LIST - Initial API and implementation

* Edouard Batot (UOC SOM) ebatot@uoc.edu 

*****************************************************************************/


package transform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import model.Trace;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.module.loaders.BuiltinModuleLoader;

public class JSonTransformer {
	
	/**
	 * 
	 * @param source '[ "STRING-WITH-GUILLEMETS-AND-BRACKETS" ]'
	 * @return 'STRING-WITHOUT-GUILLEMETS-AND-BRACKETS'
	 */
	public static String oneValueJsonArrayToString(String source) {
		source = source.substring(1, source.length() - 1).trim();
		source = source.substring(1, source.length() - 1).trim();
		return source;
	}

	
	
	/**
	 * Returns the IDentifier of annotating feature annotating a specific link identified by its ID.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param link_id
	 * @return IDS of AnnotatingFeatures 
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public static List<String> getAnnotatingFeaturesIDOfConnection(String datamodel, String link_id)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String links_af_id = JSonTransformer.executeJQuery(datamodel,
				".[].payload "
						+ "| select((.AAAtype ==  \"AnnotatingFeature\") and "
						+ "(.annotatedElement[].AAAid  == \""+link_id+"\")) | .identifier"// | "
				); // Return "[ id1, id2, id3 ...]"
		List<String> links_af = new ObjectMapper().readValue(links_af_id, new TypeReference<List<String>>() {});
		return links_af;
	}

	public static String getRawConfidenceAndTracetypeMetadataFeatures(String datamodel) {
		
		//Get their owner as MetadataFeature MDF
		String mfs_raw = null;
		try {
			mfs_raw = JSonTransformer.executeJQuery(datamodel,
					".[].payload "
							+ "| select((.AAAtype ==  \"MetadataFeature\") and "
							+ " ((.effectiveName  == \"confidence\") or "
							+ "  (.effectiveName  == \"tracetype\"))"
							+ ") "
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mfs_raw;

	}
	
	/**
	 * Returns the raw JSon expression of IDentified elements of the model
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param ids
	 * @return
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static String getElementsRawJsonFromIDs(String datamodel, String[] ids) throws JsonQueryException, JsonProcessingException, IOException{
		String jqQuery_idSelect = "(";
		int i = 0;
		for (String id : ids) 
			jqQuery_idSelect += "\n  (.identifier == \""+id+"\")" + (++i < ids.length? " or ":"");
		jqQuery_idSelect += ")";
		String jqQuery = 			".[] "
				+ "| select(.payload | " + jqQuery_idSelect + ")" ;
		
		String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery);
		return outText_elts;
	}
	
	/**
	 * Returns the raw JSon expression of an IDentified element of the model
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param id
	 * @return
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static String getElementRawJsonFromID(String datamodel, String id) throws JsonQueryException, JsonProcessingException, IOException{
		String jqQuery = ".[] | select(.payload | ( .identifier == \""+id+"\") )" ;
		String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery);
		return outText_elts;
	}
	
	
	/**
	 * Returns the specific value of an attribute of an IDentified element of the model
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param id
	 * @param attribute ".payload.X.[]..." JQ style.
	 * @return
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static String getElementSpecificFieldFromID(String datamodel, String id, String attribute) throws JsonQueryException, JsonProcessingException, IOException{
//		System.out.println("dm: "+datamodel);
		String jqQuery = ".[] | select(.payload | ( .identifier == \""+id+"\") ) | "+attribute ;
		String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery);
		return outText_elts;
	}

	/**
	 * Return a Map<ID, Elements[]> from ids and JSon SysMLv2 model
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param ids
	 * @return
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static HashMap<String, String> getMapOfElementsRawJsonFromIDs(String datamodel, String[] ids) throws JsonQueryException, JsonProcessingException, IOException{
		HashMap<String, String> res = new HashMap<>(ids.length);
		for (String id : ids) {
			String jqQuery = 			".[] | select(.payload | (.identifier == \""+id+"\") )" ;
			String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery);
			res.put(id, outText_elts);
		}
		return res;
	}

	/**
	 * Attention - USE OF PARTUSAGE !
	 * Returns in a String the raw JSon syntax of the Connections and Elements of the model passed in parameter.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @return
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 */
	public static String getPartsAndLinksRaw(String datamodel) throws IOException, JsonQueryException, JsonProcessingException {
		
		String jqQuery_elts = 			".[] "
				+ "| select(.payload | (.AAAtype == \"ConnectionUsage\") or (.AAAtype == \"PartUsage\") ) ";
//				+ "| select(.payload | (.AAAtype == \"Annotation\")) ";or (.type == \"EnumerationUsage\") or (.type == \"Annotation\")
//				+ "| select(.payload | select(.type | contains(\"Usage\"))) ";
		
		String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery_elts);
		return outText_elts;
	}

	/**
	 * Returns in a String the raw JSon syntax of the Elements of the model passed in parameter.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @return
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 */
	public static String getPartUsagesJSon(String datamodel) throws IOException, JsonQueryException, JsonProcessingException {
		String eltsFields_strings = 
				" {"
				+ " \"id\" : .identifier,"
				+ " \"name\" : .name,"
				+ " \"type\" : .AAAtype,"
				+ " \"qualifiedname\" : .qualifiedName"
				+ "}";
		
		String jqQuery_elts = 			".[].payload "
				+ "| select((.AAAtype == \"PartUsage\")) "
				+ "| "+eltsFields_strings;
		
		String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery_elts);
		return outText_elts;
	}
	
	/**
	 * Returns in a String the raw JSon syntax of the Elements of the model passed in parameter.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @return
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 */
	public static String getPartUsagesRaw(String datamodel) throws IOException, JsonQueryException, JsonProcessingException {
		String jqQuery_elts = 			".[].payload "
				+ "| select((.AAAtype == \"PartUsage\")) "
				;
		String outText_elts = JSonTransformer.executeJQuery(datamodel, jqQuery_elts);
		return outText_elts;
	}

	/**
	 * Returns in a String the raw JSon syntax of the Connections of the model passed in parameter.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @return
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 */
	public static String getConnections(String datamodel) throws IOException, JsonQueryException, JsonProcessingException {
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
		
		String outText_links = JSonTransformer.executeJQuery(datamodel, jqQuery_links);
		return outText_links;
	}
	
	/**
	 * TODO Cuidado con el "ConnectionUsage" !! We want also other subkinds.
	 * 
	 * Returns the IDs of ConnectionUsages in the model
	 * @param datamodel A SysMLv2 model written in JSon
	 * @return IDs of Connections
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static List<String> getLinksIDs(String datamodel) throws JsonQueryException, JsonProcessingException, IOException {
		String jqQuery_links = 			".[].payload "
				+ "| select((.AAAtype == \"ConnectionUsage\")) | .identifier ";

		String links = JSonTransformer.executeJQuery(datamodel, jqQuery_links);
		List<String> links_id = new ObjectMapper().readValue(links, new TypeReference<List<String>>() {});
		return links_id;
	}
	

	/**
	 * Execute a JQ query on the model passed in paramater and returns its JSon result.
	 * @param datamodel A SysMLv2 model written in JSon
	 * @param jqQuery A query written in JQ
	 * @return
	 * @throws IOException
	 * @throws JsonQueryException
	 * @throws JsonProcessingException
	 */
	public static String executeJQuery(String datamodel, String jqQuery)
			throws IOException, JsonQueryException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		JsonNode input = mapper.readTree(datamodel);

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
	




	/**
	 * Add "_out" at the end of the filename and put the specified extension
	 * @param filename
	 * @param ext
	 * @return filename[-extension].ext
	 */
	public static String convertNameTo(String filename, String ext) {
		String ojf;
		int point = filename.lastIndexOf('.');
		ojf = filename.substring(0, point) + "_out."+ext;
		return ojf;
	}



	public static String stripApartAerobases(File fileIn) throws IOException {
		String strIn  = "";
		strIn = Files.readString(Paths.get(fileIn.getAbsolutePath()));
		strIn = strIn.replaceAll("@", "AAA");
		return strIn;
	}



	/** 
	 * Verify that the file exists and replaces the @symbol that plagues SysMLv2 JSon persistence. They are replaced with an arbitrary "AAA" sequence.
	 * @param fileIn_name
	 * @return
	 * @throws IOException
	 */
	public static String checkAndCleanFileInput(String fileIn_name) throws IOException {
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

}
