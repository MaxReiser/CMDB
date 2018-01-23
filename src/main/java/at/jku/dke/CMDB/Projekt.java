package at.jku.dke.CMDB;

import java.io.Serializable;
import java.util.List;

public class Projekt implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	
	public Projekt() {
		super();
	}
	public Projekt(String name) {
		super();
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public double getKosten() {
		return 0;
	}
	
	public int getNumberOfMitarbeiter() {
		return 0;
	}
}
