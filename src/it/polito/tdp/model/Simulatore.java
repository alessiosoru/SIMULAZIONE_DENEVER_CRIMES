package it.polito.tdp.model;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.db.EventsDao;
import it.polito.tdp.model.Evento.TipoEvento;

public class Simulatore {
	
	// CODA DEGLI EVENTI
	private PriorityQueue<Evento> queue = new PriorityQueue<>();
	
	//TIPI DI EVENTO
	//1. Evento Criminoso
		//	1.1 La centrale seleziona l'agente più vicino
		//	1.2 Setta l'agente a occupato
		
	//2. Arriva agente
		//	2.1 Definisco quanto durerà l'intervento
		//	2.2 Controlla se l'evento è mal gestito
	
	//3. Crimine terminato
		//	3.1 Libero l'agente
	
	
		
		// Strutture dati che ci servono
	// STATISTIHCE DA CALCOLARE
	private Integer malGestiti;
		
	// PARAMETRI DEL SIMULATORE
	private Integer N;
	private Year anno;
	private Integer mese;
	private Integer giorno;
	
	// MODELLO DEL MONDO
		//mappa di distretto-# agenti
		private Map<Integer,Integer> agenti;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	
	public Simulatore() {		
	}
	
	public void init(Integer N, Year anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> grafo) {
		this.N=N;
		this.anno=anno;
		this.mese=mese;
		this.giorno=giorno;
		this.grafo=grafo;
		
		this.malGestiti=0;
		
		this.agenti = new HashMap<Integer, Integer>();
		for(Integer d: this.grafo.vertexSet()) {
			this.agenti.put(d, 0); // imposto zero agenti per ogni distretto
		}
		
		// scelgo dove si trova la centrale
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMinByYear(anno);
		this.agenti.put(minD, this.N);
		
		// creo la coda
		this.queue = new PriorityQueue<Evento>();
		
		// creo gli eventi iniziali
		for(Event e: dao.listAllEventsByDate(anno, mese, giorno)) {
			queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));
		}
		
	}
	
	public int run() {
		
		Evento e;
		while((e = queue.poll())!=null) {
			
			switch(e.getTipo()) {
			
				case CRIMINE:
					System.out.println("NUOVO CRIMINE: "+e.getCrimine().getIncident_id());
					Integer partenza = null;
					partenza = cercaAgente(e.getCrimine().getDistrict_id());
					if(partenza!=null) {
						// cè un agente libero
						if(partenza.equals(e.getCrimine().getDistrict_id())) {
							// tempo di arrivo = 0 -> come se fossi già dentro
							// arrivo agente senza dover controllare se mal gestito
							System.out.println("AGENTE ARRIVA PER CRIMINE: "+e.getCrimine().getIncident_id());
							Long duration = getDuration(e.getCrimine().getOffense_category_id());
							this.queue.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));
						} else {
							// tempo di arrivo>0 -> devo schedulare l'evento ARRIVO_AGENTE
							Double distance = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza,
									e.getCrimine().getDistrict_id()));
							Long seconds = (long) ((distance*1000)/(60/3.6));
							this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE,
									e.getData().plusSeconds(seconds), e.getCrimine()));
						}
					} else {
						// nessun agente libero
						System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" GESTITO!");
						this.malGestiti++;
					}
					break;
				case ARRIVA_AGENTE:
					System.out.println("AGENTE ARRIVA PER CRIMINE: "+e.getCrimine().getIncident_id());
					Long duration = getDuration(e.getCrimine().getOffense_category_id());
					this.queue.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));
					// controllo se il crimine è mal gestito
					if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
						System.out.println("CRIMINE: "+e.getCrimine().getIncident_id()+" MAL GESTITO");
						this.malGestiti++;
					}
					break;
				case GESTITO:
					System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" GESTITO!");
					Integer distretto = e.getCrimine().getDistrict_id();
					this.agenti.put(distretto, this.agenti.get(distretto)+1);
					break;

			}
			
		}
		System.out.println("TERMINATO !! MAL GESTITI: "+this.malGestiti);
		return this.malGestiti;
		
	}

	private Integer cercaAgente(Integer district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;
	
		for(Integer d: this.agenti.keySet()) {
			if(this.agenti.get(d)>0) {
				if(district_id.equals(d)) { // per evitare eccezioni
					// verifico che sia nello stesso  distretto, nel caso rimane questo
					distanza = Double.valueOf(0);
					distretto = d;
				}else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d))<distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}
		return distretto;
	}

	private Long getDuration(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble()>0.5) {
				return Long.valueOf(2*60*60);
			}else {
				return Long.valueOf(1*60*60);
			}
		} else {
			return Long.valueOf(2*60*60);
		}
	}

}
