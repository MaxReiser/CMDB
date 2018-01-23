package at.jku.dke.CMDB;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Mitarbeiter implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private List<Workstation> workstations;
	private double kosten;

	public Mitarbeiter(String name) {
		super();
		this.name = name;
		this.kosten=0;
		this.workstations = new LinkedList<>();
	}

	public Mitarbeiter(String name, Double kosten) {
		workstations = new LinkedList<Workstation>();
		this.name = name;
		this.kosten = kosten;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public double getMitarbeiterKosten() {
		return kosten;
	}

	public void setMitarbeiterKosten(double kosten) {
		this.kosten = kosten;
	}

	public double getEquipmentKosten() {
		double eqKosten=0;

		for (Workstation workstation : workstations) {
			eqKosten += workstation.getKosten();
			eqKosten += workstation.getPeripherieKosten();
			eqKosten += workstation.getSoftwareKosten();
		}
		return eqKosten;
	}
	
	public List<Workstation> getWorkstations() {
		return workstations;
	}

	public void setWorkstations(List<Workstation> workstations) {
		this.workstations = workstations;
	}
	
	public void addWorkstation(Workstation w) {
		this.workstations.add(w);
	}
	
	public void removeWorkstation(Workstation w) {
		this.workstations.remove(w);
	}

	public int equipmentCount(){
		if(workstations != null)
			return workstations.size();
		else
			return 0;
	}

	@Override
	public String toString() {
		return name;
	}
}
