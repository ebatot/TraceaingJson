import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import model.MetadataFeature;
import model.Trace;
import model.Trace.FormatForPrintingMetadatas;
import transform.ConnectionFactory;
import transform.ConnectionFactory.TraceTypesEncoding;
import transform.ElementFactory;
import transform.JSonTransformer;

@SuppressWarnings("deprecation")
public class Transformer {

	public static void main(String[] args) throws IOException {
//		printLOC();
//		System.exit(0);
		
		System.out.println("       * *  *  *  *  *  *  *  *  *  *  *");
		System.out.println("       * SysMLv2-JSon Transformer v0.1 *");
		System.out.println("       * *  *  *  *  *  *  *  *  *  *  *");
		System.out.println();
		
		Object[] configuration = getCommandLineArguments(args);
		
		String fileIn_name = (String)configuration[1];
		TraceTypesEncoding tracetypesType = (TraceTypesEncoding)configuration[2];
		String fileOut_sysml_name = 	(String)configuration[3];
		String fileOut_json_name = 	(String)configuration[4];
		String fileOut_html_name = 	(String)configuration[5];
		String output_style = 	(String)configuration[6];
		
		
		boolean out_sysml = output_style.contains("s");
		boolean out_html = output_style.contains("h");
		boolean out_json = output_style.contains("j");
		
		System.out.println("\nConfiguration: "+configuration[0]);
		System.out.println("  Model file:     " + fileIn_name);
		System.out.println("  Tracetype type: " + tracetypesType);
		System.out.println("  Output: '"+output_style+"' (available are 'jsh')");
		if(out_sysml)
			System.out.println("    sysml: " + fileOut_sysml_name );
		if(out_json)
			System.out.println("    json:  " + fileOut_json_name );
		if(out_html)
			System.out.println("    html:  " + fileOut_html_name );
		System.out.println();
		
		File fileOut_lne = null, fileOut_sml = null, fileOut_html = null;
		System.out.println("* Check file names...");
		if(out_sysml)
			fileOut_sml = checkOutFileName(fileOut_sysml_name);
		if(out_json)
			fileOut_lne = checkOutFileName(fileOut_json_name);
		if(out_html)
			fileOut_html = checkOutFileName(fileOut_html_name);
		
		/*
		 * Initialization
		 */
		String datamodel = JSonTransformer.checkAndCleanFileInput(fileIn_name);
		ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
		ElementFactory eltFactory = ElementFactory.getInstance(); 
		eltFactory.setDatamodel(datamodel); // Same as the one used by the connection factory.
		connectionFactory.setDatamodel(datamodel); // Same as the one used by the element factory.
		connectionFactory.setTracetypeEncoding(tracetypesType);
		
		System.out.println("\n* Building up trace connections...");
		Trace t = ConnectionFactory.buildTrace(datamodel);
		System.out.println("... done.\n* "+t.getConnections().size()+" connections found.\n");
		
		System.out.println("* Metadata values found:");
		System.out.println("   Metafeatures: " + MetadataFeature.getAllEffectiveNames());
		System.out.println("   Tracetypes:   " + MetadataFeature.getAllTraceTypes());
		System.out.print("   Metafeatures values: {");
//		System.out.println(MetadataFeature.getAllMetadataFeatureValues());
		MetadataFeature.getAllMetadataFeatureValues().forEach((mf, v) -> {
			System.out.print( mf.getEffectiveName() + " -> " + v + ", ");
		});
		System.out.println("}");
		System.out.println();
		
		if(out_json) {
			System.out.println("* Transforming the trace to Tracea-JSon...");
			boolean success = true;
			try {
				FileWriter fw = new FileWriter(fileOut_lne);
				String t_string = t.toStringJSonD3();
				fw.write(t_string);
				fw.close();
			} catch (IOException e) {
				System.out.println("Something went wrong when writing in '"+fileOut_lne.getAbsolutePath()+"'.");
				e.printStackTrace();
				success = false;
			}
			if(success)
				System.out.println("Done. Trace JSon stored in '"+fileOut_lne.getAbsolutePath()+"'.");
			else
				System.out.println("¡¡¡¡ Trace not stored, problem encountered !!!!");
		}

		if (out_sysml) {
			System.out.println("* Transforming the trace to SysMLv2...");
			FileWriter fw = new FileWriter(fileOut_sml);
			// TODO Option to chose the type of SysML print : Aerobase, separated, metas only.
			fw.write(t.toStringSysML(FormatForPrintingMetadatas.SEPARATED));
			fw.close();
			System.out.println("Done. Trace SysMLv2 stored in '" + fileOut_sml.getAbsolutePath() + "'.");
		}

		if (out_html) {
			System.out.println("* Transforming the trace to HTML matrix table...");
			FileWriter fw2 = new FileWriter(fileOut_html);
			fw2.write(t.toStringMatrixHTML());
			fw2.close();
			System.out.println("Done. Trace matrix in HTML stored in '" + fileOut_html.getAbsolutePath() + "'.");
		}
		
		System.out.println("\nExit successful!");
		System.exit(0);
	}
	
	public static Object[] getCommandLineArguments(String[] args) throws IOException {
		Options options = configureOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine;
		String confName = "", imf = null, osf = null, ojf = null, ohf = null;
		String output = "s";
		TraceTypesEncoding tt = ConnectionFactory.TraceTypesEncoding.STRING;
		boolean run = true;
		try {
			commandLine = parser.parse(options, args);
			if (commandLine.hasOption(O_CONFIGURATION_NAME)) {
				confName = commandLine.getOptionValue(O_CONFIGURATION_NAME);
			}
			if (commandLine.hasOption(O_INPUT_MODEL_FILE)) {
				imf = commandLine.getOptionValue(O_INPUT_MODEL_FILE);
				File tmp = new File(imf);
				if (!tmp.exists()) {
					System.out.println("Input model specified '" + tmp.getAbsolutePath() + "' does not exists. "
							+ "\nExit.");
					run = false;
				}

			} else {
				System.out.println("No input model specified. The program needs one. \nExit.");
				run = false;
			}
			if (commandLine.hasOption(O_TRACETYPE)) {
				String ttt = commandLine.getOptionValue(O_TRACETYPE);
				if (ttt.equals("String"))
					tt = ConnectionFactory.TraceTypesEncoding.STRING;
				else if (ttt.equals("Enum"))
					tt = ConnectionFactory.TraceTypesEncoding.ENUM;
				else {
					System.out.println("Unrecognized tracetype kind '" + ttt
							+ "'. Possible values are 'String' or 'Enum'. Default value selected : " + tt);
				}
			} else {
				System.out.println("Undefined tracetype kind. Default value selected : " + tt);
			}

			if (commandLine.hasOption(O_OUTPUT_JSON_FILE)) {
				ojf = commandLine.getOptionValue(O_OUTPUT_JSON_FILE);
			} else {
				System.out.println("No output Json file specified. Default: 'input-model-filename'_out.json");
				ojf = JSonTransformer.convertNameTo(imf, "json");
			}

			if (commandLine.hasOption(O_OUTPUT_SYSML_FILE)) {
				osf = commandLine.getOptionValue(O_OUTPUT_SYSML_FILE);
			} else {
				System.out.println("No output Sysml file specified. Default: 'input-model-filename'_out.sysml");
				osf = JSonTransformer.convertNameTo(imf, "sysml");
			}

			if (commandLine.hasOption(O_OUTPUT_HTML_FILE)) {
				ohf = commandLine.getOptionValue(O_OUTPUT_HTML_FILE);
			} else {
				System.out.println("No output HTML file specified. Default: 'input-model-filename'_out.html");
				ohf = JSonTransformer.convertNameTo(imf, "html");
			}
			
			if (commandLine.hasOption(O_OUTPUT_OPTION)) {
				String output_tmp = commandLine.getOptionValue(O_OUTPUT_OPTION).toLowerCase();
				output = "";
				if(output.equalsIgnoreCase("json"))
					output = "j";
				else if ( output.equalsIgnoreCase("sysml"))
					output = "s";
				else if ( output.equalsIgnoreCase("html"))
					output = "h";
				else {
					if (output_tmp.contains("j"))
						output += "j";
					if (output_tmp.contains("h"))
						output += "h";
					if (output_tmp.contains("s"))
						output += "s";
				}
				if(output.isEmpty()) {
					System.out.println("Output is empty, default is: 's'");
					output = "s";
				}
			} else {
				
			}

		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.setWidth(120);
			System.out.println();
			formatter.printHelp("java -jar JSonTransformer.jar", options, true);
		}

		if (!run) {
			System.out.println("Something went wrong :(\nExit.");
		} else {
			return new Object[] { confName, imf, tt, osf, ojf, ohf, output };
		}
		return null;
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
	
	static String O_INPUT_MODEL_FILE = "imf";
	static String O_TRACETYPE = "tt";
	static String O_OUTPUT_SYSML_FILE = "osf";
	static String O_OUTPUT_JSON_FILE = "ojf";
	static String O_OUTPUT_HTML_FILE = "ohf";
	static String O_CONFIGURATION_NAME = "cn";
	static String O_OUTPUT_OPTION = "o";

	private static Options configureOptions() {
		// Configuration name/specs
		Option configurationNameOption = OptionBuilder.create(O_CONFIGURATION_NAME);
		configurationNameOption.setLongOpt("configuration-name");
		configurationNameOption.setArgName("configuration-name");
		configurationNameOption.setDescription("use <configuration-name>. Give this configuration a name/spec.");
		configurationNameOption.setType(String.class);
		configurationNameOption.setArgs(1);

		// File in SysMLv2 model
		Option inputModelFileOption = OptionBuilder.create(O_INPUT_MODEL_FILE);
		inputModelFileOption.setLongOpt("input-model-file");
		inputModelFileOption.setArgName("input-model-file");
		inputModelFileOption.setDescription("use <input-model-file>. ");
		inputModelFileOption.setType(String.class);
		inputModelFileOption.setArgs(1);
		inputModelFileOption.setRequired(true);

		// Kind of tracetypes (String or Enum)
		Option traceTypeKindOption = OptionBuilder.create(O_TRACETYPE);
		traceTypeKindOption.setLongOpt("tracetype-kind");
		traceTypeKindOption.setArgName("tracetype-kind");
		traceTypeKindOption.setDescription("use <tracetype-kind>. Either 'String' or 'Enum' (defaults use 'String')");
		traceTypeKindOption.setType(String.class);
		traceTypeKindOption.setArgs(1);

		// Output file for SysML reinjection
		Option outputSysmlFileOption = OptionBuilder.create(O_OUTPUT_SYSML_FILE);
		outputSysmlFileOption.setLongOpt("output-sysml-file");
		outputSysmlFileOption.setArgName("output-sysml-file");
		outputSysmlFileOption.setDescription("use <output-sysml-file>. (defaults use 'input-model-file'_out.sysml)");
		outputSysmlFileOption.setType(String.class);
		outputSysmlFileOption.setArgs(1);

		// Output file for Tracea-JSon
		Option outputJSonFileOption = OptionBuilder.create(O_OUTPUT_JSON_FILE);
		outputJSonFileOption.setLongOpt("output-json-file");
		outputJSonFileOption.setArgName("output-json-file");
		outputJSonFileOption.setDescription("use <output-json-file>. (defaults use 'input-model-file'_out.json)");
		outputJSonFileOption.setType(String.class);
		outputJSonFileOption.setArgs(1);

		// Output file for HTML
		Option outputHTMLFileOption = OptionBuilder.create(O_OUTPUT_HTML_FILE);
		outputHTMLFileOption.setLongOpt("output-html-file");
		outputHTMLFileOption.setArgName("output-html-file");
		outputHTMLFileOption.setDescription("use <output-html-file>. (defaults use 'input-model-file'_out.html)");
		outputHTMLFileOption.setType(String.class);
		outputHTMLFileOption.setArgs(1);

		// Output file for HTML
		Option outputOption = OptionBuilder.create(O_OUTPUT_OPTION);
		outputOption.setLongOpt("output");
		outputOption.setArgName("output");
		outputOption.setDescription("use <output>. 'jsh' j:json, s:sysml, h:html (default is 'j': only JSon written in file.)");
		outputOption.setType(String.class);
		outputOption.setArgs(1);

		Options options = new Options();
		options.addOption(inputModelFileOption);
		options.addOption(traceTypeKindOption);
		options.addOption(outputSysmlFileOption);
		options.addOption(outputJSonFileOption);
		options.addOption(outputOption);
		return options;
	}
		


}
