package it.polito.tdp.model;

import com.javadocmd.simplelatlng.LatLng;

public class District implements Comparable<District> {
	private int id;
	private LatLng centro;
	
	public District(int id) {
		super();
		this.id = id;
		this.centro= null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public LatLng getCentro() {
		return centro;
	}

	public void setCentro(LatLng centro) {
		this.centro = centro;
	}

	@Override
	public String toString() {
		return "Distretto "+this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		District other = (District) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(District other) {
		return this.id-other.id;
	}

}
