package it.polito.tdp.model;

import java.time.Year;

public class TestModel {

	public static void main(String[] args) {
		Model m = new Model();
		m.creaGrafo(Year.of(2015));
		m.simula(Year.of(2015), 5, 27, 10);
	}

}
