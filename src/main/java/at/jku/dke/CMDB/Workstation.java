package at.jku.dke.CMDB;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Workstation extends Hardware implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<Peripherie> peripherie;
	private List<Software> software;

	public Workstation(String name, String hersteller, Double kosten) {
		super(name, hersteller, kosten);
		peripherie = new LinkedList<>();
		software = new LinkedList<>();
	}

	public double getPeripherieKosten() {
		double pkosten = 0;

		for(Peripherie p : peripherie) {
			pkosten += p.getKosten();
		}
		return pkosten;
	}
	
	public double getSoftwareKosten() {
		double skosten = 0;

		for(Software s : software) {
			skosten += s.getKosten();
		}
		return skosten;
	}
	
	public List<Peripherie> getPeripherie() {
		return peripherie;
	}
	public List<Software> getSoftware() {
		return software;
	}

	public void setPeripherie(List<Peripherie> peripherie) {
		this.peripherie = peripherie;
	}
	public void setSoftware(List<Software> software) {
		this.software = software;
	}
	public void addSoftware(Software s) {
		this.software.add(s);
	}
	public void addPeripherie(Peripherie p) {
		this.peripherie.add(p);
	}
	public void removeSoftware(Software s) {
		this.software.remove(s);
	}
	public void removePeripherie(Peripherie p) {
		this.peripherie.remove(p);
	}
}
