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
	 * NOT TESTED NOT CHECKED !
	 * 
	 * @return
	 */
	public String toStringMatrix() {
		System.out.println("Trace.toStringMatrix() NOT IMPLEMENTED");
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
		return res + "\n" + res2 + "\n";
	}
}
