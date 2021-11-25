package model;

public abstract class TracingElement {
	String ID;
	
	public TracingElement(String identifier) {
		this.ID = identifier;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	public String getID() {
		return ID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!obj.getClass().equals(this.getClass())) return false;
		return this.getID().equals(((TracingElement)obj).getID());
	}
}
