package it.polito.tdp.model;

import java.time.Month;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;

public class Model {
	
//	private static final LengthUnit KILOMETER = null;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Integer> distretti;
//	private Map<Integer, District> distrettiIdMap;
	private EventsDao dao;
	
//	private List<Year> anni;
	
	private List<Event> eventi;
//	private Map<Long, Event> eventiIdMap;
	
	
	private Year anno;
	private Month mese;
	private Integer giorno;
	private Integer N;
	
	
	public Model() {
		dao = new EventsDao();
//		this.eventiIdMap = new HashMap<Long, Event>();
//		this.distrettiIdMap = new HashMap<Integer, District>();
		
	}
	
	// ATTENZIONE ! LEGGERE BENE IL DB E NON FARE QUERY INUTILI PIU' COMPLESSE
				// IN QUESTO CASO PRENDO TUTTO DALLLA TABELLA FLIGHTS
				// NON FACCIO IL CONTROLLO A1<A2 PERCHE' devo considereare entrambe le direzioni,
				// avrò collegammenti doppi per lo stesso arco che non è orientato, quindi che sia 
				// source uno o l'altro, l'arco sarà lo stesso per entrambi i casi, perciò
				// se l'arco non esiste -> aggiungo
				// se esiste -> rotta inversa ma stesso arco, aggiorno il peso

	
	public void creaGrafo(Year anno) {
		
		// CREARE TUTTE LE MAPPE E LE LISTE UTILI PER IL GRAFO COME
		// NEW QUI IN CREA GRAFO
		
//		this.eventi =  dao.listAllEventsByYear(this.eventiIdMap, anno);
		this.distretti = dao.listAllDistricts();
//		for(District d: distretti) {
//			LatLng centro = new LatLng(dao.getLatMedia(d.getId(), anno) //lat
//					, dao.getLonMedia(d.getId(), anno));//lon
//			d.setCentro(centro);
//		}
		
		this.grafo = new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, this.distretti);
		
		for(Integer v1: this.distretti) {
			for(Integer v2:this.distretti) {
				if(!v1.equals(v2)) {
					if(this.grafo.getEdge(v1, v2)==null) {
						Double latMediaV1 = dao.getLatMedia(v1, anno);
						Double lonMediaV1 = dao.getLonMedia(v1, anno);
						Double latMediaV2 = dao.getLatMedia(v2, anno);
						Double lonMediaV2 = dao.getLonMedia(v2, anno);
						
						Double distanzaMedia = LatLngTool.distance(
								new LatLng(latMediaV1, lonMediaV1),
								new LatLng(latMediaV2, lonMediaV2),
								LengthUnit.KILOMETER);
						
						Graphs.addEdgeWithVertices(this.grafo, v1, v2, distanzaMedia);
						
					}
					
				}
			}
			
			// CON METODO ADIACENZE TROPPO LENTO IL DB, CONVIENE PORTARE TUTTO SU JAVA
		}
		
		System.out.println("Grafo creato!\nVertici: "+grafo.vertexSet().size()+"\nArchi: "+grafo.edgeSet().size()+"\n");
//		
//		
//		
//		for(District d1: this.distretti) {
//			for(District d2:this.distretti) {
//				if(!grafo.containsEdge(d1, d2) && !grafo.containsEdge(d2, d1) &&
//						!d1.equals(d2)) {
//					Double distance = LatLngTool.distance(d1.getCentro(), d2.getCentro(),LengthUnit.KILOMETER);
//					Graphs.addEdge(this.grafo, d1, d2, distance);
//				}
//			}
//		}
//		
//		System.out.println("Vertici: "+grafo.vertexSet().size()+"\nArchi: "+grafo.edgeSet().size()+"\n");
	}
	


	public Integer getNumVertici() {
		return this.grafo.vertexSet().size();
	}

	public Integer getNumArchi() {
		return this.grafo.edgeSet().size();
	}

	public List<DistrettoVicino> getVicini(Integer d) {
		List<DistrettoVicino> adiacenti =  new ArrayList<DistrettoVicino>();
		List<Integer> neighbors = Graphs.neighborListOf(this.grafo, d);
		for(Integer vicino : neighbors) {
			DistrettoVicino dv = new DistrettoVicino(vicino, 
					this.grafo.getEdgeWeight(this.grafo.getEdge(d, vicino)));
			adiacenti.add(dv);
		}
		Collections.sort(adiacenti);
		return adiacenti;
	}

	public List<Year> getAnni() {
//		return anni;
		return dao.listAnni();
	}

//	public void setAnni(List<Year> anni) {
//		this.anni = anni;
//	}

	public List<Event> getEventi() {
		return eventi;
	}

	public void setEventi(List<Event> eventi) {
		this.eventi = eventi;
	}

	public List<Integer> getDistretti() {
		return distretti;
	}

	public void setDistretti(List<Integer> distretti) {
		this.distretti = distretti;
	}


	public List<Integer> getMesi() {
		List<Integer> mesi = new LinkedList<Integer>();
		for(int i = 1; i<=12; i++)
			mesi.add(i);
		return mesi;

	}

	public List<Integer> getGiorni() {
		List<Integer> giorni = new LinkedList<Integer>();
		for(int i = 1; i<=31; i++)
			giorni.add(i);
		return giorni;
	}
	
	public int simula(Year anno, Integer mese, Integer giorno, Integer N) {
		Simulatore sim = new Simulatore();
		sim.init(N, anno, mese, giorno, grafo);
		return sim.run();
	}
	
	
	
	
	
	
	
	
	// CAMMINO MINIMO
	// quando MI SERVONO TUTTI I VERTICI RAGGIUNGIBILI DATA UN'ORIGINE E UNA PARTENZA
	// CON IL MINIMO CAMMINO MINIMO DI DIJKSTRA
//	public List<Fermata> trovaCamminoMinimo(Fermata partenza, Fermata arrivo) {
//		DijkstraShortestPath<Fermata, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(this.grafo);
//		// dati origine e arrivo
//		GraphPath<Fermata, DefaultWeightedEdge> path = dijkstra.getPath(partenza, arrivo); // path cammino
//		// data solo l'origine
//		dijkstra.getPaths(partenza);
//		return path.getVertexList();
//	}
	

	
	
	
	
	
	
	
	

// RICORSIONE
	/*
	 * RICORSIONE FORMULA 1
	 * 
	 * Soluzione parziale:mappa di driver team
	 * 	(mappa di driver con tasso di sconfitta = vittorie di altri tot)
	 * Livello della ricorsione: numero di piloti nel team
	 * Casi terminali: 
	 * 	1. livello ricorsione diventa k piloti -> Verifica se il team
	 * 		ha min tasso di sconfitta visto fino ad ora
	 * Generazione delle soluzioni: dato un vertice, aggiungo un vertice
	 * 	non ancora parte del percorso
	 * 
	 * Calcolo il tasso di sconfitta come peso archi entranti per ogni pilota
	 * 
	 */

//	public Map<Integer, Driver> getDreamTeam(Integer k){
//		
//		// inizializzo qui il tasso di sconofitta perchè se rifaccio ricorsione da zero
//		//  e non trovo tasso migliore della ricorsione prec che non c'entra nulla
//		// non riesco a trovare un nuovo best
//		// CREO IL PARAMETRO DEL BEST
//		bestTassoSconfitta = Integer.MAX_VALUE;
//		
//		//INIZIALIZZO RICORSIONE
//		// VARIABILI PER LA RICORSIONE
//		//CREO SOLUZIONE PARZIALE
//		Map<Integer, Driver> parziale = new HashMap<Integer, Driver>();
//		this.bestTeam = new HashMap<Integer, Driver>();
//
//		Integer tassoSconfitta = 0;
//		
//		// INZIIO LA RICORSIONE
//		cerca(parziale, 0, k, tassoSconfitta); 
//		
//		return this.bestTeam; // RITORNO IL BEST
//		
//	}
//
//
//	// METODO RICORSIVE
//	private void cerca(Map<Integer, Driver> parziale, int livello, Integer k, Integer tassoSconfitta) {
//		System.out.println(livello);
//		if(livello == k) { // VERIFICO CONDIZIONE DI TERMINAZIONE PARZIALE
//			// VERIFICO SE PARZIALE HA TASSO MIGLIORE DEL BEST
//			// SE VERO SALVO COME !! NEW !! PASSANO PARZIALE
//			if(tassoSconfitta<this.bestTassoSconfitta) {
//				this.bestTassoSconfitta = tassoSconfitta;
//				this.bestTeam = new HashMap<Integer, Driver>(parziale);
//			}
//			
//			// RETURN !!!
//			
//			// RICORDA DI INSERIRE IL !!!| RETURN !!!! nella condizione di terminazione
//			
//			// in questo caso è fuori dall'if sul controllo del best
//			// perchè ho comunque raggiunto il livello massimo e devo uscire
//		return;
//		}
//		
//		// PER OGNI OGGETTO VERIFICO CHE PARZIALE NON LO CONTENGA E SE
//		// SODDISFA CONDIZIONE DI INSERIMENTO, SE VERO
//		// INSERISCO IN PARZIALE
//		// CHIAMO RICORSIONE
//		// FACCIO BACKTRACKING
//		for(Driver d : this.grafo.vertexSet()) {
//			if(!parziale.containsKey(d.getDriverId())) {
//				
//				parziale.put(d.getDriverId(), d);
//
//				Integer aggiuntaTasso = calcolaTassoSconfitta(d, parziale);
//				tassoSconfitta = tassoSconfitta + aggiuntaTasso;
//				
//				cerca(parziale, livello+1, k, tassoSconfitta);
//				
//				parziale.remove(d.getDriverId(), d);
//				tassoSconfitta = tassoSconfitta - aggiuntaTasso;
//				
//			}
//		}
//	}
//
//
//	// METODO PER IL CALCOLO DEI VALORI DI VERIFICA
//	private Integer calcolaTassoSconfitta(Driver d, Map<Integer, Driver> parziale) {
//		Integer aggiuntaTasso = 0;
//		System.out.println("DREAM TEAM\n");
//		for(Driver driv : parziale.values()) {
//			System.out.println("dt"+driv.getSurname());
//		}
//			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(d)) {
//				System.out.println("battenti"+this.grafo.getEdgeSource(e).getSurname());
//				if(!parziale.containsKey(this.grafo.getEdgeSource(e).getDriverId())) {
//					
//					aggiuntaTasso = (int) (aggiuntaTasso + this.grafo.getEdgeWeight(e));
//			}
//		}
//		return aggiuntaTasso;
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
//	// VISITA, TUTTI I VERTICI RAGGIUNGIBILI DA UN VERTICE
//	public List<Country> getReachableCountries(Country selectedCountry) {
//
//		if (!graph.vertexSet().contains(selectedCountry)) {
//			throw new RuntimeException("Selected Country not in graph");
//		}
//
//		List<Country> reachableCountries = this.displayAllNeighboursIterative(selectedCountry);
//		System.out.println("Reachable countries: " + reachableCountries.size());
//		reachableCountries = this.displayAllNeighboursJGraphT(selectedCountry);
//		System.out.println("Reachable countries: " + reachableCountries.size());
//		reachableCountries = this.displayAllNeighboursRecursive(selectedCountry);
//		System.out.println("Reachable countries: " + reachableCountries.size());
//
//		return reachableCountries;
//	}

	/*
	 * VERSIONE ITERATIVA
	 */
//	
	
//	private List<Country> displayAllNeighboursIterative(Country selectedCountry) {
		//
//				// Creo due liste: quella dei noti visitati ..
//				List<Country> visited = new LinkedList<Country>();
		//
//				// .. e quella dei nodi da visitare
//				List<Country> toBeVisited = new LinkedList<Country>();
		//
//				// Aggiungo alla lista dei vertici visitati il nodo di partenza.
//				visited.add(selectedCountry);
		//
//				// Aggiungo ai vertici da visitare tutti i vertici collegati a quello inserito
//				toBeVisited.addAll(Graphs.neighborListOf(graph, selectedCountry));
		//
//				while (!toBeVisited.isEmpty()) {
		//
//					// Rimuovi il vertice in testa alla coda
//					Country temp = toBeVisited.remove(0);
		//
//					// Aggiungi il nodo alla lista di quelli visitati
//					visited.add(temp);
		//
//					// Ottieni tutti i vicini di un nodo
//					List<Country> listaDeiVicini = Graphs.neighborListOf(graph, temp);
		//
//					// Rimuovi da questa lista tutti quelli che hai già visitato..
//					listaDeiVicini.removeAll(visited);
		//
//					// .. e quelli che sai già che devi visitare.
//					listaDeiVicini.removeAll(toBeVisited);
		//
//					// Aggiungi i rimanenenti alla coda di quelli che devi visitare.
//					toBeVisited.addAll(listaDeiVicini);
//				}
		//
//				// Ritorna la lista di tutti i nodi raggiungibili
//				return visited;
//			}
		//
//			/*
//			 * VERSIONE LIBRERIA JGRAPHT
//			 */
//			private List<Country> displayAllNeighboursJGraphT(Country selectedCountry) {
		//
//				List<Country> visited = new LinkedList<Country>();
		//
//				// Versione 1 : utilizzo un BreadthFirstIterator
////				GraphIterator<Country, DefaultEdge> bfv = new BreadthFirstIterator<Country, DefaultEdge>(graph,
////						selectedCountry);
////				while (bfv.hasNext()) {
////					visited.add(bfv.next());
////				}
		//
//				// Versione 2 : utilizzo un DepthFirstIterator
//				GraphIterator<Country, DefaultEdge> dfv = new DepthFirstIterator<Country, DefaultEdge>(graph, selectedCountry);
//				while (dfv.hasNext()) {
//					visited.add(dfv.next());
//				}
		//
//				return visited;
//			}
		//
//			/*
//			 * VERSIONE RICORSIVA
//			 */
//			private List<Country> displayAllNeighboursRecursive(Country selectedCountry) {
		//
//				List<Country> visited = new LinkedList<Country>();
//				recursiveVisit(selectedCountry, visited);
//				return visited;
//			}
		//
//			private void recursiveVisit(Country n, List<Country> visited) {
//				// Do always
//				visited.add(n);
		//
//				// cycle
//				for (Country c : Graphs.neighborListOf(graph, n)) {	
//					// filter
//					if (!visited.contains(c))
//						recursiveVisit(c, visited);
//						// DO NOT REMOVE!! (no backtrack)
//				}
//			}
		//	
}
