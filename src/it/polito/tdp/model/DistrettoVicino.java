package it.polito.tdp.model;

public class DistrettoVicino implements Comparable<DistrettoVicino>{
	
	private Integer idV;
	private Double distanza;
	
	public DistrettoVicino(Integer idV, Double distanza) {
		super();
		this.idV = idV;
		this.distanza = distanza;
	}
	public int getId() {
		return idV;
	}
	public void setId(Integer idV) {
		this.idV = idV;
	}
	public Double getDistanza() {
		return distanza;
	}
	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idV;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistrettoVicino other = (DistrettoVicino) obj;
		if (idV != other.idV)
			return false;
		return true;
	}
	@Override
	public int compareTo(DistrettoVicino other) {
		return this.distanza.compareTo(other.distanza);
	}
	@Override
	public String toString() {
		return "Distante "+this.distanza+" kilometri dal distretto "+this.idV;
	}
	
	
	

}
