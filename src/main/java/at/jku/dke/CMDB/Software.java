package at.jku.dke.CMDB;

import java.io.Serializable;

public class Software implements Serializable, Comparable<Software> {
	private static final long serialVersionUID = 1L;
	private String name;
	private String hersteller;
	private double kosten;
	
	public Software(String name, String hersteller, double kosten) {
		this.name = name;
		this.hersteller = hersteller;
		this.kosten = kosten;
	}

	public String getName() {
		return name;
	}
	
	public String getHersteller() {
		return this.hersteller;
	}

	public double getKosten() {
		return this.kosten;
	}

	@Override
	public int compareTo(Software s) {
		return this.name.compareTo(s.getName());
	}
	
}
