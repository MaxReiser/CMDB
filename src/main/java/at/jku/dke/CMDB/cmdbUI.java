package at.jku.dke.CMDB;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class cmdbUI extends UI {
	private static final long serialVersionUID = 1L;
	public static final String ENDPOINT = "http://localhost:3030/ElQuijote-JenaRulesReasoner";
	CMDBModel model = null;
	Projekt projekt = null;
	

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		model = new CMDBModel();
		/*
		 * Grid<String> maingrid = new Grid<>();
		 * maingrid.setSelectionMode(SelectionMode.NONE);
		 * 
		 * Button btnSoftware = new Button("Software");
		 * btnSoftware.addClickListener(e -> { ParameterizedSparqlString
		 * queryString = new ParameterizedSparqlString();
		 * queryString.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		 * queryString.setCommandText("SELECT *\n" +
		 * "WHERE{ ?software a dke:Software;\n" + "dke:name ?name}");
		 * 
		 * List<String> software = new ArrayList<>();
		 * 
		 * QueryExecution qExe = QueryExecutionFactory.sparqlService(ENDPOINT ,
		 * queryString.asQuery() ); ResultSet results = qExe.execSelect();
		 * 
		 * while(results.hasNext()) { QuerySolution result = results.next();
		 * software.add(result.getResource("software").getLocalName() + " - " +
		 * result.getLiteral("name")); } qExe.close();
		 * 
		 * maingrid.removeAllColumns(); maingrid.setItems(software);
		 * maingrid.addColumn(s -> s).setCaption("Software"); });
		 * 
		 * Button btnHardware = new Button("Workstation");
		 * btnHardware.addClickListener(e -> { ParameterizedSparqlString
		 * queryString = new ParameterizedSparqlString();
		 * queryString.setNsPrefix("dke", "http://dke.jku.at/CMDB#");
		 * queryString.setCommandText("SELECT *\n" +
		 * "WHERE{ ?hardware a dke:Hardware.\n" + "}");
		 * 
		 * List<String> hardware = new ArrayList<>();
		 * 
		 * QueryExecution qExe = QueryExecutionFactory.sparqlService(ENDPOINT ,
		 * queryString.asQuery() ); ResultSet results = qExe.execSelect();
		 * 
		 * while(results.hasNext()) { QuerySolution result = results.next();
		 * hardware.add(result.getResource("workstation").getLocalName()); }
		 * qExe.close();
		 * 
		 * maingrid.removeAllColumns(); maingrid.setItems(hardware);
		 * maingrid.addColumn(s -> s).setCaption("Workstation"); });
		 */
		
		VerticalLayout mainPage = new VerticalLayout();
		mainPage.setSpacing(true);
		mainPage.setMargin(true);
		mainPage.setWidth("100%");
		
//		Chart chart = new Chart();
//		Configuration conf = chart.getConfiguration();
		
		HorizontalLayout mitarbeiterContainer = new HorizontalLayout();
		HorizontalLayout softwareContainer = new HorizontalLayout();
		HorizontalLayout hardwareContainer = new HorizontalLayout();
		HorizontalLayout projectContainer = new HorizontalLayout();
		VerticalLayout detailContainer = new VerticalLayout();
		Grid<Mitarbeiter> mitarbeiterGrid = new Grid<>();
		Grid<Hardware> hardwareGrid = new Grid<>();
		Grid<Software> softwareGrid = new Grid<>();
		projectContainer.setWidth("100%");
		mitarbeiterContainer.setWidth("100%");
		softwareContainer.setWidth("100%");
		hardwareContainer.setWidth("100%");
		detailContainer.setWidth("100%");
		
		VerticalLayout gridContainer = new VerticalLayout();
		gridContainer.setWidth("100%");
		Grid<Projekt> projekte = new Grid<Projekt>();
		projekte.setItems(model.getProjekte());
		projekte.setSelectionMode(SelectionMode.SINGLE);
		projekte.addColumn(Projekt::getName).setId("Projektname").setCaption("Projekte");
		projekte.addColumn(p -> model.getProjektkosten(p.getName())).setId("Kosten_Projekt").setCaption("Kosten des Projekts");
		projekte.addColumn(p -> model.getAnzahlProjektmitarbeiter(p.getName())).setId("Anzahl_Mitarbeiter").setCaption("Anzahl der Mitarbeiter");
		projekte.addSelectionListener(e -> {
			try {
				this.projekt = e.getFirstSelectedItem().get();
			}
			catch(Exception ex) {
				this.projekt = null;
			}
		});
		gridContainer.addComponent(projekte);
		
		VerticalLayout buttonContainer = new VerticalLayout();
		buttonContainer.setWidth("100%");
		Button availableMitarbeiter = new Button("Mitarbeiter");
		Button availableHardware = new Button("Hardware");
		Button availableSoftware = new Button("Software");
		Button detailButton = new Button("Details");
		availableMitarbeiter.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		availableMitarbeiter.setIcon(VaadinIcons.PLUS);
		availableMitarbeiter.addClickListener(e ->{
			if(projekt != null) {
				detailContainer.removeAllComponents();
				Button addMitarbeiter = new Button("hinzufuegen");
				addMitarbeiter.addClickListener(ev->{
					Set<Mitarbeiter> mitarbeiter = mitarbeiterGrid.getSelectedItems();
					if(!mitarbeiter.isEmpty()) {
						for(Mitarbeiter m : mitarbeiter) {
							model.addRessourceToProjekt(projekt.getName(), m.getName());
						}
						detailContainer.removeAllComponents();
						projekte.deselectAll();
						this.projekt = null;
					}
				});
				mitarbeiterGrid.setItems(model.getAvailableMitarbeiter());
				detailContainer.addComponents(mitarbeiterContainer, addMitarbeiter);
			}
		});
		availableHardware.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		availableHardware.setIcon(VaadinIcons.PLUS);
		availableHardware.addClickListener(e -> {
			if(projekt != null) {
				detailContainer.removeAllComponents();
				Button addHardware = new Button("hinzufuegen");
				addHardware.addClickListener(ev->{
					Set<Hardware> hardware = hardwareGrid.getSelectedItems();
					if(!hardware.isEmpty()) {
						for(Hardware h : hardware) {
							model.addRessourceToProjekt(projekt.getName(), h.getName());
						}
						detailContainer.removeAllComponents();
						projekte.deselectAll();
						this.projekt = null;
					}
				});
				hardwareGrid.setItems(model.getAvailableHardware());
				detailContainer.addComponents(hardwareContainer, addHardware);
			}
			else {
				Notification.show("Fehler", "Kein Projekt ausgewaehlt", Notification.Type.ERROR_MESSAGE);
			}
		});
		availableSoftware.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		availableSoftware.setIcon(VaadinIcons.PLUS);
		availableSoftware.addClickListener(e -> {
			if(projekt != null) {
				detailContainer.removeAllComponents();
				Button addSoftware = new Button("hinzufuegen");
				addSoftware.addClickListener(ev->{
					Set<Software> software = softwareGrid.getSelectedItems();
					if(!software.isEmpty()) {
						for(Software s : software) {
							model.addRessourceToProjekt(projekt.getName(), s.getName());
						}
						detailContainer.removeAllComponents();
						projekte.deselectAll();
						this.projekt = null;
					}
				});
				softwareGrid.setItems(model.getSoftware());
				detailContainer.addComponents(addSoftware, mitarbeiterContainer);
			}
			else {
				Notification.show("Fehler", "Kein Projekt ausgewaehlt", Notification.Type.ERROR_MESSAGE);
			}
		});
		detailButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		detailButton.addClickListener(e -> {
			if(projekt != null) {
				detailContainer.removeAllComponents();
				
				Button removeMitarbeiter = new Button("entfernen");
				removeMitarbeiter.addClickListener(ev -> {
					Set<Mitarbeiter> selectedMitarbeiter = mitarbeiterGrid.getSelectedItems();
					if(selectedMitarbeiter.size() != 0) {
						for(Mitarbeiter m : selectedMitarbeiter) {
							model.removeProjektressource(projekt.getName(), m.getName());
						}
						detailContainer.removeAllComponents();
						projekte.deselectAll();
						this.projekt = null;
					}
				});
				Collection<Mitarbeiter> mitarbeiter = model.getProjektmitarbeiter(projekt.getName());
				if(!mitarbeiter.isEmpty()) {
					mitarbeiterGrid.setItems(mitarbeiter);
					detailContainer.addComponents(mitarbeiterContainer, removeMitarbeiter);
				}
				
				Button removeHardware = new Button("entfernen");
				removeHardware.addClickListener(ev -> {
					Set<Hardware> selectedHardware = hardwareGrid.getSelectedItems();
					if(selectedHardware.size() != 0) {
						for(Hardware h : selectedHardware) {
							model.removeProjektressource(projekt.getName(), h.getName());
						}
						detailContainer.removeAllComponents();
						projekte.deselectAll();
						this.projekt = null;
					}
				});
				Collection<Hardware> hardware = model.getProjektHardware(projekt.getName());
				if(!hardware.isEmpty()) {
					hardwareGrid.setItems(hardware);
					detailContainer.addComponents(hardwareContainer, removeHardware);
				}
				
				Button removeSoftware = new Button("entfernen");
				removeSoftware.addClickListener(ev -> {
					Set<Software> selectedSoftware = softwareGrid.getSelectedItems();
					if(selectedSoftware.size() != 0) {
						for(Software s : selectedSoftware) {
							model.removeProjektressource(projekt.getName(), s.getName());
						}
						detailContainer.removeAllComponents();
						projekte.deselectAll();
						this.projekt = null;
					}
				});
				Collection<Software> software = model.getProjektSoftware(projekt.getName());
				if(!software.isEmpty()) {
					softwareGrid.setItems(software);
					detailContainer.addComponents(softwareContainer, removeSoftware);
				}
			}
			else {
				Notification.show("Fehler", "Kein Projekt ausgewaehlt", Notification.Type.ERROR_MESSAGE);
			}
		});
		buttonContainer.addComponentsAndExpand(detailButton, availableMitarbeiter, availableHardware, availableSoftware);
		
		projectContainer.addComponents(gridContainer, buttonContainer);
		mainPage.addComponents(projectContainer, detailContainer);
		
		softwareGrid.setItems(model.getSoftware());
		softwareGrid.setSelectionMode(SelectionMode.SINGLE);
		softwareGrid.addColumn(Software::getName).setCaption("Name");
		softwareGrid.addColumn(Software::getHersteller).setCaption("Hersteller");
		softwareGrid.addColumn(Software::getKosten).setCaption("Kosten");
		softwareGrid.setWidth("100%");
		softwareContainer.addComponent(softwareGrid);
		
		hardwareGrid.setItems(model.getAvailableHardware());
		hardwareGrid.setSelectionMode(SelectionMode.SINGLE);
		hardwareGrid.addColumn(Hardware::getName).setCaption("Name");
		hardwareGrid.addColumn(Hardware::getHersteller).setCaption("Hersteller");
		hardwareGrid.addColumn(Hardware::getKosten).setCaption("Kosten");
		hardwareGrid.setWidth("100%");
		hardwareContainer.addComponent(hardwareGrid);
		
		
		mitarbeiterGrid.setItems(model.getAvailableMitarbeiter());
		mitarbeiterGrid.setSelectionMode(SelectionMode.SINGLE);
		mitarbeiterGrid.addColumn(Mitarbeiter::getName).setCaption("Name");
		mitarbeiterGrid.addColumn(Mitarbeiter::getMitarbeiterKosten).setCaption("Gehalt");
		mitarbeiterGrid.addColumn(Mitarbeiter::getEquipmentKosten).setCaption("Equipment-Kosten");
		mitarbeiterGrid.addColumn(m -> m.getMitarbeiterKosten() + m.getEquipmentKosten()).setCaption("Gesamtkosten");
		mitarbeiterGrid.setWidth("100%");
		mitarbeiterContainer.addComponent(mitarbeiterGrid);
		
		
		
		/*TwinColSelect<Mitarbeiter> twinColSelect = new TwinColSelect<>();
		twinColSelect.setVisible(false);
		twinColSelect.setRows(6);
		twinColSelect.setLeftColumnCaption("Available options");
		twinColSelect.setRightColumnCaption("Selected options");
		twinColSelect.setItems(mitarbeiterAll);

		projekte.asSingleSelect().addValueChangeListener(event -> {
			twinColSelect.setVisible(true);
			twinColSelect.deselectAll();
			
			if(projekte.getSelectedItems().stream().findAny().isPresent()){
				for(Mitarbeiter m: (projekte.getSelectedItems().stream().findFirst().get().getMitarbeiter())){
					twinColSelect.select(m);
				}
			}
			
		});

		splitPanel.addComponent(projekte);
		splitPanel.addComponent(twinColSelect);
		//Section Projekte - Mitarbeiter - Ende
		
		//Section Mitarbeiter - Workingstation - Anfang
		HorizontalSplitPanel splitPanel2 = new HorizontalSplitPanel();
		
		Grid<Mitarbeiter> mitarbeiter_grid = new Grid<Mitarbeiter>();
		mitarbeiter_grid.setSizeFull();
		
		
		mitarbeiter_grid.setItems(mitarbeiterAll);
		mitarbeiter_grid.setHeightByRows(mitarbeiterAll.size());
		mitarbeiter_grid.setSelectionMode(SelectionMode.SINGLE);
		mitarbeiter_grid.addColumn(Mitarbeiter::getName).setId("Mitarbeitername").setCaption("Mitarbeiter");
		mitarbeiter_grid.addColumn(Mitarbeiter::getMitarbeiterKosten).setId("Kosten_Mitarbeiter").setCaption("Kosten des Mitarbeiters");
		mitarbeiter_grid.addColumn(Mitarbeiter::equipmentCount).setId("Anzahl_Workstation").setCaption("Anzahl der Workstation");
		
		
		TwinColSelect<Workstation> twinColSelect2 = new TwinColSelect<>();
		twinColSelect2.setVisible(false);
		twinColSelect2.setRows(6);
		twinColSelect2.setLeftColumnCaption("Available options");
		twinColSelect2.setRightColumnCaption("Selected options");
		twinColSelect2.setItems(workstationsAll);

		mitarbeiter_grid.asSingleSelect().addValueChangeListener(event -> {
			twinColSelect2.setVisible(true);
			twinColSelect2.deselectAll();
			
			if(mitarbeiter_grid.getSelectedItems().stream().findAny().isPresent()){
				for(Workstation w: (mitarbeiter_grid.getSelectedItems().stream().findFirst().get().getWorkstations())){
					twinColSelect2.select(w);
				}
			}
			
		});
		
		splitPanel2.addComponent(mitarbeiter_grid);
		splitPanel2.addComponent(twinColSelect2);
		//Section Mitarbeiter - Workingstation - Ende
		
		//Section Workingstationdetail - Anfang
		HorizontalSplitPanel splitPanel3 = new HorizontalSplitPanel();
		
		Grid<Workstation> workstation_grid = new Grid<Workstation>();
		workstation_grid.setSizeFull();
		
		
		//workstation_grid.setItems(workstationsAll);
		workstation_grid.setItems(model.getWorkstations());
		workstation_grid.setHeightByRows(workstationsAll.size());
		workstation_grid.setSelectionMode(SelectionMode.SINGLE);
		workstation_grid.addColumn(Workstation::getName).setId("Workstationname").setCaption("Workstation");

		Panel panel = new Panel();
		panel.setVisible(false);
		
		VerticalLayout verticalLayoutWorkingStation = new VerticalLayout();
		Label kosten = new Label();
		verticalLayoutWorkingStation.addComponent(kosten);
		panel.setContent(verticalLayoutWorkingStation);
		
		workstation_grid.asSingleSelect().addValueChangeListener(event -> {
			if(workstation_grid.getSelectedItems().stream().findFirst().isPresent()){
				verticalLayoutWorkingStation.removeAllComponents();
				Workstation w = workstation_grid.getSelectedItems().stream().findFirst().get();
				panel.setCaption(w.getName());
				kosten.setValue("Kosten: " + w.getKosten());
				
				Grid<Peripherie> peripherie_grid = new Grid<Peripherie>();
				Grid<Software> software_grid = new Grid<Software>();
				
				if (w.getPeripherie() != null) {
					peripherie_grid.setItems(w.getPeripherie());
					peripherie_grid.setHeightByRows(w.getPeripherie().size());
					peripherie_grid.setSelectionMode(SelectionMode.NONE);
					peripherie_grid.addColumn(Peripherie::getName).setId("Peripheriename").setCaption("Peripherie");
				}
				if (w.getSoftware() != null){
					software_grid.setItems(w.getSoftware());
					software_grid.setHeightByRows(w.getSoftware().size());
					software_grid.setSelectionMode(SelectionMode.NONE);
					software_grid.addColumn(Software::getName).setId("Softwarename").setCaption("Software");
				}
				
				verticalLayoutWorkingStation.addComponent(kosten);
				verticalLayoutWorkingStation.addComponent(peripherie_grid);
				verticalLayoutWorkingStation.addComponent(software_grid);		
				panel.setVisible(true);
			}
		});
		
		splitPanel3.addComponent(workstation_grid);
		splitPanel3.addComponent(panel);
		//Section Workingstationdetail - Ende
		//Section Hardware
		Grid<Hardware> hardware_grid = new Grid<Hardware>();
		hardware_grid.setSizeFull();

		hardware_grid.setItems(hardwareAll);
		hardware_grid.setHeightByRows(hardwareAll.size());
		hardware_grid.setSelectionMode(SelectionMode.SINGLE);
		hardware_grid.addColumn(Hardware::getName).setId("Hardwarename").setCaption("Hardware");
		
		mainPage.addComponent(splitPanel);
		mainPage.addComponent(splitPanel2);
		mainPage.addComponent(splitPanel3);
		mainPage.addComponent(hardware_grid);*/
		setContent(mainPage);
	}

	@WebServlet(urlPatterns = "/*", name = "cmdbUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = cmdbUI.class, productionMode = false)
	public static class cmdbUIServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
	}

}
