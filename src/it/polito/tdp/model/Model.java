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
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;

public class Model {
	
	// 1. BASE
	
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
	
	// 1A. GRAFO CREATO CON ADIACENZA
	
	// 1B. GRAFO CREATO CON MODO1
	
	public void creaGrafo(Year anno) {
		
		
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
	
	// 2. VISITA
	
	// 3. CAMMINO MINIMO
	
	// 4.RICORSIONE
	
}
