package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Trace {
	
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

	public String toStringJSonD3() {
		HashSet<Element> elements = getAllElements();
		
		String links = "\"links\": [\n";
		for (Connection c : connections) 
			links += "  "+c.toStringJSon() + ",\n";
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
		HashSet<Element> elements = getAllElements();
		String res = "Trace ("+connections.size()+" links connect "+elements.size()+" elements.";
		for (Connection c : connections) 
			res += "\n"+ c.toStringPretty("  ");
		
		return res;
	}

	private HashSet<Element> getAllElements() {
		HashSet<Element> elements = new HashSet<>(connections.size()*2);
		connections.forEach(c -> {
			elements.addAll(c.getSourceElements());
			elements.addAll(c.getTargetElements());
		});
		return elements;
	}

	/**
	 * 
	 * @return
	 */
	public String toStringMatrixText() {
		String res = "     ";
		for (Element e : getAllElements()) {
			res += e.getName() +" ";
		}
		
		String res2 = "";
		for (Element e : getAllElements()) {
			res2 += e.getName()+"|";
			for (Element e2 : getAllElements()) 
				res2 += (e2.connects(e)?"  x  ":"     ") + "";
			res2 += "\n";
		}
		return res + "\n" + res2 ;
	}
	
	/**
	 * 
	 * @return
	 */
	public String toStringMatrixHTML() {
		String res = "\t<tr>\n\t\t<th></th>\n";
		for (Element e : getAllElements()) 
			res += "\t\t<th class=\"linkName\">"+e.getName() +"</th>\n";
		res += "\t</tr>\n";
		
		String res2 = "";
		for (Element e : getAllElements()) {
			res2 += "\t<tr>\n";
			res2 += "\t\t<td class=\"linkName\" width=\"150px\">"+e.getName() + "</td>\n";
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
	/*
	 * 


	 */
	public String toStringSysML(FormatForPrintingMetadatas format) {
		String res = "";
		int i = 0;
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
			
			String metaConfidence = "metadata m"+i+++": ConfidenceTracing about "+c.getEffectiveName() +
					" { confidence = "+c.getConfidenceValue()+";}";
			
			String metaTracetype = "";
			for (String tt : c.getTracetypes()) {
				metaTracetype += "metadata m"+i+++": TraceType about "+c.getEffectiveName() +
					" { tracetype = \""+tt+"\";}";
			}
			
			String metaConfidence2 = "  @ConfidenceTracing { confidence = "+c.getConfidenceValue()+";}";
			
			String metaTracetype2 = "";
			for (String tt : c.getTracetypes()) {
				metaTracetype2 += "  @TraceType { tracetype = \""+tt+"\";}\n";
			}
			
			
//	        @ConfidenceTracing { confidence = 0.95; } 
//	        @TraceType {tracetype = "typeA";}
	    
			switch (format) {
			case WITH_AEROBASE:
				res += connection + "{\n" + metaConfidence2 + "\n" + metaTracetype2 + "}\n";
				break;
			case SEPARATED:
				res += connection + ";\n" + metaConfidence + "\n" + metaTracetype + "\n";
				break;
			default:
				throw new IllegalArgumentException("Not supposed to get here !");
			}
		}
		return res;
	}
	public enum FormatForPrintingMetadatas {WITH_AEROBASE, SEPARATED};
}
