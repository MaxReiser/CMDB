package at.jku.dke.CMDB;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

/**
 * 
 * Das Model fuer die Kommunikation mit dem Fuseki Server
 *
 */
public class CMDBModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String endpoint = "http://localhost:3030/ElQuijote-JenaRulesReasoner/";
	private List<Software> sw;
	private List<Peripherie> peripherie;
	private List<Workstation> workstations;
	private List<Mitarbeiter> mitarbeiter;
	private List<Projekt> projekte;
	private List<Server> server;
	
	public CMDBModel() {
		sw = new LinkedList<Software>();
		peripherie = new LinkedList<Peripherie>();
		workstations = new LinkedList<Workstation>();
		mitarbeiter = new LinkedList<Mitarbeiter>();
		projekte = new LinkedList<Projekt>();
		server = new LinkedList<>();
		this.load();
	}
	
	/**
	 * @return Liste der vorhandenen Software
	 * 
	 */
	public List<Software> getSoftware() {
		return this.sw;
	}
	
	/**
	 * @return Liste der vorhandenen Peripherie
	 * 
	 */
	public List<Peripherie> getPeripherie() {
		return this.peripherie;
	}
	
	/**
	 * 
	 * @return Liste der vorhandenen Workstations
	 */
	public List<Workstation> getWorkstations() {
		return this.workstations;
	}
	
	/**
	 * 
	 * @return Liste der vorhandenen Mitarbeiter
	 */
	public List<Mitarbeiter> getMitarbeiter() {
		return this.mitarbeiter;
	}
	
	/**
	 * 
	 * @return Liste der vorhandenen Projekte
	 */
	public List<Projekt> getProjekte(){
		return this.projekte;
	}
	
	/**
	 * Laden der Daten aus der Datenbank
	 */
	private void load() {
		loadSoftware();
		loadPeripherie();
		loadWorkstations();
		addSoftwareToWorkstations();
		addPeripherieToWorkstations();
		loadMitarbeiter();
		addWorkstationsToMitarbeiter();
		loadProjekte();
		loadServer();
	}

	/**
	 * Laden der Server 
	 */
	private void loadServer() {
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?s a dke:Server ;\n" +
                " dke:kosten ?kosten; \n" +
                " dke:herstellerHW ?hersteller" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("s").getLocalName();
	        	Double kosten = solution.getLiteral("kosten").getDouble();
	        	String hersteller = solution.getLiteral("hersteller").getString();
	        	server.add(new Server(name, hersteller, kosten));
	        }

        }
        catch(Exception e) {
        	System.out.println("server:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
		
	}

	/**
	 * Hinzufuegen der Peripherie zu den Workstations
	 */
	private void addPeripherieToWorkstations() {
		for(Workstation w : workstations) {
			ParameterizedSparqlString pss = new ParameterizedSparqlString();
			pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
			
			pss.setCommandText(
	                "SELECT  Distinct ?p\n" +
	                "WHERE\n" +
	                "{ ?p a dke:Peripherie. \n" + 
	                " dke:" + w.getName() +" a dke:Workstation ;\n" +
	                " dke:peripherie ?p" +
	                "}\n");

	
	        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
	        qExe.setTimeout(5000, 5000);
	        try {
	        	ResultSet results = qExe.execSelect();
		        
		        //loop all found definitions
		        while(results.hasNext()) {
		        	QuerySolution solution = results.next();
		        	Peripherie per = null;
		        	
		        	for(Peripherie p : peripherie) {
		        		if(p.getName().equals(solution.getResource("p").getLocalName())) {
		        			per = p;
		        			break;
		        		}
		        	}
		        	
		        	if(per != null) {
		        		w.addPeripherie(per);
		        	}
		        }
	
	        }
	        catch(Exception e) {
	        	System.out.println("AddPtoW: " + e.getMessage());
	        }
	        finally {
	        	if(!qExe.isClosed()) {
	        		qExe.close();
	        	}
	        }
		}
	}

	/**
	 * Hinzufuegen der Software zu den Workstations
	 */
	private void addSoftwareToWorkstations() {
		for(Workstation w : workstations) {
			ParameterizedSparqlString pss = new ParameterizedSparqlString();
			pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
			
			pss.setCommandText(
	                "SELECT  Distinct ?s\n" +
	                "WHERE\n" +
	                "{ ?s a dke:Software. \n" + 
	                " dke:" + w.getName() +" a dke:Workstation ;\n" +
	                " dke:fuehrtAus ?s.\n" +
	                "}\n");
	
	        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
	        qExe.setTimeout(5000, 5000);
	        try {
	        	ResultSet results = qExe.execSelect();
		        
		        //loop all found definitions
		        while(results.hasNext()) {
		        	QuerySolution solution = results.next();
		        	Software software = null;
		        	
		        	for(Software s : sw) {
		        		if(s.getName().equals(solution.getResource("s").getLocalName())) {
		        			software = s;
		        			break;
		        		}
		        	}
		        	if(software != null) {
		        		w.addSoftware(software);
		        	}
		        }
	
	        }
	        catch(Exception e) {
	        	System.out.println("AddStoW:" + e.getMessage());
	        }
	        finally {
	        	if(!qExe.isClosed()) {
	        		qExe.close();
	        	}
	        }
		}
	}

	/**
	 * Laden der Workstations
	 */
	private void loadWorkstations() {
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?w a dke:Workstation ;\n" +
                " dke:kosten ?kosten; \n" +
                " dke:herstellerHW ?hersteller. \n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("w").getLocalName();
	        	Double kosten = Double.valueOf((solution.getLiteral("kosten").getLexicalForm()));
	        	String hersteller = solution.getLiteral("hersteller").getString();
	        	workstations.add(new Workstation(name, hersteller, kosten));
	        }

        }
        catch(Exception e) {
        	System.out.println("w:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
	}

	/**
	 * Laden der Peripherie
	 */
	private void loadPeripherie() {
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?p a dke:Peripherie ;\n" +
                " dke:herstellerHW ?hersteller;\n" +
                " dke:kosten ?kosten.\n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("p").getLocalName();
	        	String hersteller = solution.getLiteral("hersteller").getString();
	        	double kosten = solution.getLiteral("kosten").getDouble();
	        	
	        	peripherie.add(new Peripherie(name, hersteller, kosten));
	        }

        }
        catch(Exception e) {
        	System.out.println("p: " + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
	}

	/**
	 * Laden der Software
	 */
	private void loadSoftware() {
		System.out.println("Lade Software");
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?lizenz a dke:Lizenz; \n"+
                "dke:kosten ?kosten.\n" + 
                "?s a dke:Software ;\n" +
                " dke:herstellerSW ?hersteller. \n" +
                "?lizenz dke:lizenziert ?s" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	System.out.println("SW gefunden");
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("s").getLocalName();
	        	String hersteller = solution.getLiteral("hersteller").getLexicalForm();
	        	try {
		        	Double kosten = Double.valueOf(solution.getLiteral("kosten").getLexicalForm());
		        	sw.add(new Software(name, hersteller, kosten));
	        	}
	        	catch(Exception e) {}
	        }

        }
        catch(Exception e) {
        	System.out.println("SW:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
	}
	
	/**
	 * Laden der Mitarbeiter
	 */
	private void loadMitarbeiter() {
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?ma a dke:Mitarbeiter ;\n" +
                " dke:kosten ?kosten" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("ma").getLocalName();
	        	Double kosten = Double.valueOf((solution.getLiteral("kosten").getLexicalForm()));
	        	mitarbeiter.add(new Mitarbeiter(name, kosten));
	        }

        }
        catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
	}
	
	/**
	 * Hinzufuegen der Workstations zu den Mitarbeitern
	 */
	private void addWorkstationsToMitarbeiter() {
		for(Mitarbeiter ma : mitarbeiter) {
			ParameterizedSparqlString pss = new ParameterizedSparqlString();
			pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
			
			pss.setCommandText(
	                "SELECT  Distinct ?w\n" +
	                "WHERE\n" +
	                "{ ?w a dke:Workstation. \n" + 
	                " dke:" + ma.getName() +" a dke:Mitarbeiter ;\n" +
	                " dke:besitzt ?w.\n" +
	                "}\n");
	
	        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );

	        qExe.setTimeout(5000, 5000);
	        try {
	        	ResultSet results = qExe.execSelect();
		        
		        //loop all found definitions
		        while(results.hasNext()) {
		        	QuerySolution solution = results.next();
		        	Workstation workstation = null;
		        	
		        	for(Workstation w : workstations) {
		        		if(w.getName().equals(solution.getResource("w").getLocalName())) {
		        			workstation = w;
		        			break;
		        		}
		        	}
		        	if(workstation != null) {
		        		ma.addWorkstation(workstation);
		        	}
		        }
	
	        }
	        catch(Exception e) {
	        	System.out.println("AddWtoM: " + e.getMessage());
	        }
	        finally {
	        	if(!qExe.isClosed()) {
	        		qExe.close();
	        	}
	        }
		}
	}
	
	/**
	 * Laden der Projekte
	 */
	private void loadProjekte(){
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?p a dke:Projekt .\n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("p").getLocalName();
	        	projekte.add(new Projekt(name));
	        }

        }
        catch(Exception e) {
        	System.out.println("P: " + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
	}
	
	/**
	 * Abfragen der noch verfuegbaren Hardware
	 * @return Liste der verfuegbaren Hardware
	 */
	public List<Hardware> getAvailableHardware() {
		List<Hardware> availableHardware = new LinkedList<>();
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?h a dke:Hardware ;\n" +
                " dke:herstellerHW ?hersteller; \n" +
                " dke:kosten ?kosten" +
                " FILTER NOT EXISTS {?x dke:ressource ?h}" +
                " FILTER NOT EXISTS {?y dke:besitzt ?h}" +
                " FILTER NOT EXISTS {?z dke:peripherie ?h}" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("h").getLocalName();
	        	String hersteller = solution.getLiteral("hersteller").getLexicalForm();
	        	try {
		        	Double kosten = Double.valueOf(solution.getLiteral("kosten").getLexicalForm());
		        	availableHardware.add(new Hardware(name, hersteller, kosten));
	        	}
	        	catch(Exception e) {}
	        }

        }
        catch(Exception e) {
        	System.out.println("SW:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return availableHardware;
	}
	
	/**
	 * Abfragen der Mitarbeiter, die noch nicht zu einem Projekt zugeordnet sind
	 * @return Liste der Mitarbeiter
	 */
	public List<Mitarbeiter> getAvailableMitarbeiter(){
		List<Mitarbeiter> availableMitarbeiter = new LinkedList<>();
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  *\n" +
                "WHERE\n" +
                "{ ?m a dke:Mitarbeiter ;\n" +
                " FILTER NOT EXISTS {?x dke:ressource ?m}" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        
	        //loop all found definitions
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("m").getLocalName();
	        	if(mitarbeiter != null) {
	        		for(Mitarbeiter m : mitarbeiter) {
	        			if(m.getName().equals(name)) {
	        				availableMitarbeiter.add(m);
	        			}
	        		}
	        	}
	        }

        }
        catch(Exception e) {
        	System.out.println("SW:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return availableMitarbeiter;
	}
	
	/**
	 * Berechnen der Gesamtkosten eines Projektes
	 * @param projekt das Projket
	 * @return die Kosten
	 */
	public double getProjektkosten(String projekt) {
		return this.getDirekteProjektkosten(projekt) + this.getIndirekteProjektkosten(projekt);
	}
	
	/**
	 * Abfrage der direkten Projektkosten
	 * @param projekt das Projekt
	 * @return die direkten Kosten
	 */
	private double getDirekteProjektkosten(String projekt) {
		double kosten = 0;
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  (sum(?kosten) as ?gesamtkosten)\n" +
                "WHERE\n" +
                "{ ?c a dke:ConfigurationItem ;\n" +
                " dke:kosten ?kosten .\n" +
                " dke:" + projekt + " a dke:Projekt ;\n" +
                " dke:ressource ?c. \n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        if(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	kosten += solution.getLiteral("gesamtkosten").getDouble();
	        }
	        
	        

        }
        catch(Exception e) {
        	System.out.println("DirekteProjektkosten:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return kosten;
	}
	
	/**
	 * Abfrage jener Kosten, die durch die Hardware und Software entstehen, die ein Mitarbeiter besitzt
	 * @param projekt das Projekt
	 * @return die indirekten Kosten
	 */
	private double getIndirekteProjektkosten(String projekt) {
		double kosten = 0;
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  Distinct *\n" +
                "WHERE\n" +
                "{ ?m a dke:Mitarbeiter .\n" +
                " dke:" + projekt + " a dke:Projekt ;\n" +
                " dke:ressource ?m. \n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );

        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("m").getLocalName();
	        	for(Mitarbeiter m : this.mitarbeiter) {
	        		if(m.getName().equals(name)) {
	        			System.out.println("Mitarbeiter gefunden " + m.getEquipmentKosten());
	        			kosten+=m.getEquipmentKosten();
	        		}
	        	}
	        }
	        
	        

        }
        catch(Exception e) {
        	System.out.println("IndirekteProjektkosten:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return kosten;
	}
	
	/**
	 * Abfrage der Anzahl von Mitarbeitern in einem Projekt
	 * @param projekt das Projekt
	 * @return die Anzahl der Mitarbeiter
	 */
	public int getAnzahlProjektmitarbeiter(String projekt) {
		int anzahl = 0;
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  (COUNT(?m) as ?anzahl)\n" +
                "WHERE\n" +
                "{ ?m a dke:Mitarbeiter .\n" +
                " dke:" + projekt + " a dke:Projekt ;\n" +
                " dke:ressource ?m. \n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        if(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	anzahl = solution.getLiteral("anzahl").getInt();
	        }
        }
        catch(Exception e) {
        	System.out.println("AnzahlProjektmitarbeiter:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return anzahl;
	}
	
	/**
	 * SPARQL Delete Satement zu entfernen einer Ressource von einem Projekt
	 * @param projekt das Projekt
	 * @param resource die Ressource
	 */
	public void removeProjektressource(String projekt, String resource) {
		if(projekt != null && !projekt.equals("") && resource != null && !resource.equals("")) {			

			String delete = 
					"PREFIX dke: <http://dke.jku.at/CMDB#>\n\n" +
	                "DELETE DATA {\n" +
	                " dke:" + projekt + " dke:ressource dke:" + resource + " .\n" +
	                "}\n";
	
			UpdateRequest update = UpdateFactory.create(delete);
			UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, endpoint);
			processor.execute();
		}
	}
	
	/**
	 * Abfragen aller Mitarbeiter, die Teil eines Projektes sind
	 * @param projekt das Projekt
	 * @return Liste mit Projektmitarbeitern
	 */
	public List<Mitarbeiter> getProjektmitarbeiter(String projekt) {
		List<Mitarbeiter> projektmitarbeiter = new LinkedList<>();
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  distinct ?m\n" +
                "WHERE\n" +
                "{ ?m a dke:Mitarbeiter .\n" +
                " dke:" + projekt + " a dke:Projekt ;\n" +
                " dke:ressource ?m. \n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("m").getLocalName();
	        	for(Mitarbeiter m : mitarbeiter) {
	        		if(name.equals(m.getName())) {
	        			projektmitarbeiter.add(m);
	        		}
	        	}
	        }
        }
        catch(Exception e) {
        	System.out.println("AnzahlProjektmitarbeiter:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return projektmitarbeiter;
	}

	/**
	 * Abfragen der Hardware, die momentan dem Projekt zugeordnet ist
	 * @param projekt
	 * @return
	 */
	public Collection<Hardware> getProjektHardware(String projekt) {
		List<Hardware> projektHardware = new LinkedList<>();
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  distinct ?h\n" +
                "WHERE\n" +
                "{ ?h a dke:Hardware .\n" +
                " dke:" + projekt + " a dke:Projekt ;\n" +
                " dke:ressource ?h. \n" +
                "}\n");
		System.out.println(pss.getCommandText());

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("h").getLocalName();
	        	System.out.println(name);
	        	if(peripherie != null) {
		        	for(Hardware h : peripherie) {
		        		if(name.equals(h.getName())) {
		        			projektHardware.add(h);
		        		}
		        	}
	        	}
	        	if(workstations != null) {
		        	for(Hardware h : workstations) {
		        		if(name.equals(h.getName())) {
		        			projektHardware.add(h);
		        		}
		        	}
	        	}
	        	if(server != null) {
	        		for(Hardware h : server) {
	        			if(name.equals(h.getName())) {
	        				projektHardware.add(h);
	        			}
	        		}
	        	}
	        }
        }
        catch(Exception e) {
        	System.out.println("AvailableHardware: " + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        System.out.println(projektHardware.size());
        return projektHardware;
	}

	/**
	 * Abfrage der Software, die aktuell dem Projekt zugeordnet ist
	 * @param projekt das Projekt
	 * @return Liste der Software
	 */
	public Collection<Software> getProjektSoftware(String projekt) {
		List<Software> projektSoftware = new LinkedList<>();
		ParameterizedSparqlString pss = new ParameterizedSparqlString();
		pss.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		
		pss.setCommandText(
                "SELECT  distinct ?m\n" +
                "WHERE\n" +
                "{ ?s a dke:Software .\n" +
                " dke:" + projekt + " a dke:Projekt ;\n" +
                " dke:ressource ?s. \n" +
                "}\n");

        QueryExecution qExe = QueryExecutionFactory.sparqlService(endpoint , pss.asQuery() );
        qExe.setTimeout(5000, 5000);
        try {
        	ResultSet results = qExe.execSelect();
	        while(results.hasNext()) {
	        	QuerySolution solution = results.next();
	        	String name = solution.getResource("s").getLocalName();
	        	for(Software s : sw) {
	        		if(name.equals(s.getName())) {
	        			projektSoftware.add(s);
	        		}
	        	}
	        }
        }
        catch(Exception e) {
        	System.out.println("AnzahlProjektmitarbeiter:" + e.getMessage());
        }
        finally {
        	if(!qExe.isClosed()) {
        		qExe.close();
        	}
        }
        return projektSoftware;
	}
	
	/**
	 * Update Statement zum Hinzufuegen einer Ressource zu einem Projekt
	 * @param projekt das Projekt
	 * @param resource die Ressource
	 */
	public void addRessourceToProjekt(String projekt, String resource) {
		if(projekt != null && !projekt.equals("") && resource != null && !resource.equals("")) {

			String insert = 
					"PREFIX dke: <http://dke.jku.at/CMDB#>\n\n" +
	                "INSERT DATA {\n" +
	                " dke:" + projekt + " dke:ressource dke:" + resource + " .\n" +
	                "}\n";
			
			UpdateRequest update = UpdateFactory.create(insert);
			UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, endpoint);
			processor.execute();
		}
	}
}
