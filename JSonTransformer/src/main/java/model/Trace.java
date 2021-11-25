package model;

import java.util.ArrayList;
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
		ArrayList<Element> elements = getAllElements();
		
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
		ArrayList<Element> elements = getAllElements();
		String res = "Trace ("+connections.size()+" links connect "+elements.size()+" elements.";
		for (Connection c : connections) 
			res += "\n"+ c.toStringPretty("  ");
		
		return res;
	}

	private ArrayList<Element> getAllElements() {
		ArrayList<Element> elements = new ArrayList<>(connections.size()*2);
		connections.forEach(c -> {
			elements.add(c.getSourceElement());
			elements.add(c.getTargetElement());
		});
		return elements;
	}
	
	public String toStringMatrix() {
		String res = "";
		for (Element e : getAllElements()) {
			res+= ";" + e.getName();
		}
		
		return res;
	}

}
