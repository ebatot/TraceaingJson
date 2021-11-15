


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.IntNode;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.module.loaders.BuiltinModuleLoader;

public class JSonTransformer {
	
	public static void main(String[] args) throws IOException {
		String jqQuery = 			".[].payload | select(.name != null) | { \"name\" : .name, \"id\": .identifier }";

		
		String fileIn_name = 	"inout/in/EXAMPLE-IN.json";
		String fileOut_name = 	"inout/out/Tracing_FilterExample-t.json";

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
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		JsonNode input = mapper.readTree(fileIn);

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
		System.out.println(out); // => [84]

		FileWriter fw = new FileWriter(fileOut);
		fw.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(out));
		fw.close();
		

	}
	
	
	public static void main2(String[] args) throws JsonProcessingException, IOException {

		
		String fileIn_name = 	"inout/in/EXAMPLE-IN.json";
		String fileOut_name = 	"inout/out/Tracing_FilterExample-t.json";
		String query1 = 		"[*].payload.{name:name, id:identifier}";
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
