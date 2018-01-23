package at.jku.dke.CMDB;

public class Hardware {
	private String name;
	private String hersteller;
	private double kosten;
	
	public Hardware(String name) {
		super();
		this.name = name;
	}

	public Hardware(String name, String hersteller, Double kosten) {
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
	public String toString() {
		return name;
	}
}
