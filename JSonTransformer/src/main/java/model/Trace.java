package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import transform.ElementFactory;

public class Trace {
	public enum FormatForPrintingMetadatas {WITH_AEROBASE, SEPARATED};
	

	List<Connection> connections = new ArrayList<>();
	
	public Trace() {
	}
	
	public boolean addConnection(Connection c){
		return connections.add(c);
	}
	
	public List<Connection> getConnections() {
		return connections;
	}
	
	public Connection getConnection(String effectiveName) {
		for (Connection c : connections) {
			if(c.getEffectiveName().equals(effectiveName))
				return c;
		}
		return null;
	}

	public String generateD3JSon() {
		List<Element> elements = getAllElements();
		
		String links = "\"links\": [\n";
		for (Connection c : connections) 
			links += "  "+c.generateD3JSon() + ",\n";
		links = links.substring(0, links.length() - 2) +" \n]";

		String nodes = "\"nodes\": [\n";
		for (Element e : elements) {
			nodes +=  "  "+e.toStringJSon() + ",\n";
		}
		nodes = nodes.substring(0, nodes.length() - 2) +" \n]";
		
		String res = "{\n " + links + ", "+nodes+"\n}";
		return res;
	}
	
	
	public String generateTraceaJSon() {
		List<Element> elements = getAllElements();
		
		String links = "\"links\": [\n";
		for (Connection c : connections) 
			links += "  "+c.generateTraceaJSon() + ",\n";
		links = links.substring(0, links.length() - 2) +" \n]";

		String nodes = "\"nodes\": [\n";
		for (Element e : elements) {
			nodes +=  "  "+e.toStringJSon() + ",\n";
		}
		nodes = nodes.substring(0, nodes.length() - 2) +" \n]";
		
		String res = "{\n " + links + ", "+nodes+"\n}";
		return res;
	}


	public String toStringPretty() {
		List<Element> elements = getAllElements();
		String res = "Trace ("+connections.size()+" links connect "+elements.size()+" elements.";
		for (Connection c : connections) 
			res += "\n"+ c.toStringPretty("  ");
		
		return res;
	}

	private List<Element> getAllElements() {
		return ElementFactory.getInstance().getAllElements();
	}

	/**
	 * 
	 * @return
	 */
	public String generateMatrixText() {
		ArrayList<String> eltsNames = new ArrayList<>(getAllElements().size());
		int max = 0;
		for (Element c : getAllElements()) 
			if(c.getName().length() > max) max = c.getName().length();
		
		for (Element c : getAllElements()) 
			eltsNames.add(missingSpaces(c.getName(), max));
		
		int n = (""+eltsNames.size()).length(); 
		
		String res = "  " + missingSpaces("", max)+" ";
		for (int i = 0; i < eltsNames.size(); i++) 
			res += " "+ Trace.padLeftZeros(""+i, n)  + " ";
		
		String res2 = "";
		int i = 0;
		for (Element e : getAllElements()) {
			res2 += Trace.padLeftZeros(""+i, n)  + " " + eltsNames.get(i++)+"|";
			for (Element e2 : getAllElements()) 
				res2 += (e2.connects(e)?" x ":"   ") + "";
			res2 += "\n";
		}
		
		return res + "\n" + res2 ;
	}
	
	/**
	 * 
	 * @return
	 */
	public String generateMatrixHTML() {
		boolean printEletNames = true;
		
		String res = "\t<tr>\n\t\t<th></th>\n";
		for (Element e : getAllElements()) 
			res += "\t\t<th class=\"linkName\">"+e.getName()+(printEletNames?"<br/>"+e.ID:"")+"</th>\n";
		res += "\t</tr>\n";
		
		String res2 = "";
		for (Element e : getAllElements()) {
			res2 += "\t<tr>\n";
			res2 += "\t\t<td class=\"linkName\" width=\"150px\">"+e.getName()+(printEletNames?"<br/>"+e.ID:"")+ "</td>\n";
			for (Element e2 : getAllElements()) {
				res2 += "\t\t<td class=\"linkCell\" width=\"150px\">";
				HashSet<String> cs = e2.connectionTypes(e);
				if (cs.isEmpty()) {
				} else {
					for (String type : cs) 
						res2 += type + ", ";
					if(res2.endsWith(", "))
						res2 = res2.substring(0, res2.length()-2);
				}
				res2 += "</td>\n";
			}
			res2 += "\t</tr>\n";
		}
		res2 += "\n";
		String table = "<table border=1 style=\"border-collapse: collapse;\">\n" + res + res2 + "</table>";
		String HEADER  = "<html>\r\n"
				+ "<head>\r\n"
				+ "<style>\r\n"
				+ "table {\r\n"
				+ "  width: 100%;\r\n"
				+ "  border: 1px solid black;\r\n"
				+ "  border-collapse: collapse;\r\n"
				+ "}\r\n"
				+ "th {\r\n"
				+ "  background-color: #04AA6D;\r\n"
				+ "  color: white;\r\n"
				+ "}\r\n"
				+ "tr { width:100px; }\r\n"
				+ "tr:hover {background-color: yellow;}\r\n"
				+ "tr:nth-child(even) {background-color: #f2f2f2;}\r\n"
				+ ".linkName{\r\n"
				+ "  font-family: verdana;\r\n"
				+ "  font-size: 15px;\r\n"
				+ "  font-style: bold;\r\n"
				+ "}\r\n"
				+ "\r\n"
				+ "</style>\r\n"
				+ "</head>\r\n"
				+ "<body>\n"
				+ "<h1>Trace matrix</h1>\n"
				+ "\t<div style=\"overflow-x:auto;\">\n";
		return  HEADER + table + "\n\t</div>\n</body>" ;
	}

	/**
	 * 
	 * @param format
	 * @return
	 */
	public String generateSysML(FormatForPrintingMetadatas format) {
		String res = "";
		for (Connection c : connections) {
			String sources = "";
			for (Element e : c.getSourceElements()) 
				sources += e.getName() +", ";
			String targets = "";
			for (Element e : c.getTargetElements()) 
				targets += e.getName() +", ";
			
			String connection = "connection "+c.getEffectiveName() + 
					" connect " + sources.substring(0, sources.length()-2) + 
					" to " + targets.substring(0, targets.length()-2) +"";
			
			String tmpConfAndTracetype = getSysmlMetadataDeclaration(c);
			
			String metaConfidence2 = "";
			try {
				metaConfidence2 = "  @ConfidenceTracing { confidence = "+c.getConfidenceValue()+";}";
			} catch (Exception e1) {
//				System.out.println("Connection "+c.getEffectiveName()+":"+c.getID()+" has no confidence value. SHOULD WE SET IT TO 1.0 ?");
//				e1.printStackTrace();
			}
			
			String metaTracetype2 = "";
			for (String tt : c.getTracetypes()) {
				metaTracetype2 += "  @TraceType { tracetype = \""+tt+"\";}\n";
			}
	    
			switch (format) {
			case WITH_AEROBASE:
				res += connection + "{\n" + metaConfidence2 + "\n" + metaTracetype2 + "}\n";
				break;
			case SEPARATED:
				res += connection + ";\n" + tmpConfAndTracetype + "\n";
				break;
			default:
				throw new IllegalArgumentException("Not supposed to get here !");
			}
		}
		return res;
	}

	/**
	 * 
	 * @return
	 */
	public String toStringSysMLNoConnections() {
		String res = "";
		for (Connection c : connections) 
			res += "" + getSysmlMetadataDeclaration( c) + "\n";
		return res;
	}

	private String getSysmlMetadataDeclaration(Connection c) {
		String tmpRes = "";
		String metaConfidence = "";
		try {
			metaConfidence = "metadata m"+(new Random().nextInt(10000))+": ConfidenceTracing about "+c.getEffectiveName() +
					" { confidence = "+c.getConfidenceValue()+";}";
		} catch (Exception e1) {
			//No confidence defined, no need for metadata definition
//				System.out.println("Connection "+c.getEffectiveName()+":"+c.getID()+" has no confidence value. SHOULD WE SET IT TO 1.0 ?");
//				e1.printStackTrace();
		}
		
		String metaTracetype = "";
		for (String tt : c.getTracetypes()) {
			//No confidence defined, no need for metadata definition
			metaTracetype += "metadata m"+(new Random().nextInt(10000))+": TraceType about "+c.getEffectiveName() +
				" { tracetype = \""+tt+"\";}";
		}
		tmpRes = metaConfidence + "\n" + metaTracetype + "";
		return tmpRes.trim();
	}
	
	public static String padLeftZeros(String inputString, int length) {
	    if (inputString.length() >= length) {
	        return inputString;
	    }
	    StringBuilder sb = new StringBuilder();
	    while (sb.length() < length - inputString.length()) {
	        sb.append('0');
	    }
	    sb.append(inputString);
	
	    return sb.toString();
	}

	public static String missingSpaces(String o, int size) {
		String res = o;
		for (int i = 0; i < size-o.length(); i++) 
			res += " ";
		return res;
	}
}
