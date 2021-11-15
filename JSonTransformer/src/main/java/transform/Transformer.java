package transform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;

public class Transformer {
	
	// test git
	File fileIn, fileOut;
	String fileIn_name, fileOut_name;
	String query;

	
	public Transformer(String fileIn_name, String fileOut_name) throws IOException {
		this.fileIn_name = fileIn_name;
		this.fileOut_name = fileOut_name;
		
		fileIn = new File(fileIn_name);
		if(!fileIn.exists()) {
			System.out.println("File '"+fileIn.getAbsolutePath()+"' does not exist.");
			System.out.println("Exit.");
			System.exit(0);
		}
	
		fileOut = new File(fileOut_name);
		if(fileOut.exists()) {
			System.out.println("File '"+fileOut.getAbsolutePath()+"' already exists. It will be replaced.");
			fileOut.delete();
			fileOut.createNewFile();
		}
		System.out.println("In file:  "+fileIn.getAbsolutePath());
		System.out.println("Out file: "+fileOut.getAbsolutePath());
	}
	
	public String executeQuery(String query) throws JsonProcessingException, IOException {
		String res = "";

		JmesPath<JsonNode> jmespath = new JacksonRuntime();
		Expression<JsonNode> expression = jmespath.compile(query);
		System.out.println("Query: " + query);
		System.out.println("    -> " +expression);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode input = mapper.readTree(fileIn);
		System.out.println("Input: "+input);

		JsonNode result = expression.search(input);
		
		System.out.println("Result: "+result);
		return res;
	}
	
	public void storeResults(String res) throws IOException {
		FileWriter fw = new FileWriter(fileOut);
		fw.write(res);
		fw.close();
	}
	
	public File getFileOut() {
		return fileOut;
	}
}
