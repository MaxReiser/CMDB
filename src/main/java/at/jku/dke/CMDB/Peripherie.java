package at.jku.dke.CMDB;

import java.io.Serializable;

public class Peripherie extends Hardware implements Serializable, Comparable<Peripherie> {
	private static final long serialVersionUID = 1L;

	public Peripherie(String name, String hersteller, double kosten) {
		super(name, hersteller, kosten);
	}

	@Override
	public int compareTo(Peripherie p) {
		return this.getName().compareTo(p.getName());
	}
}
