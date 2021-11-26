package model;

import java.util.ArrayList;
import java.util.HashSet;
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
	public String toStringMatrix() {
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
