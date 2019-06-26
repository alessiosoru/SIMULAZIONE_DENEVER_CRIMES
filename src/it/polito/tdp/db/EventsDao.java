package it.polito.tdp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.polito.tdp.model.District;
import it.polito.tdp.model.Event;


public class EventsDao {
	
	public List<Event> listAllEventsByYear(Year anno){
		String sql = "SELECT * FROM events WHERE YEAR(reported_date) =? " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Event evento = new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic"));
					list.add(evento);
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Event> listAllEventsByDate(Year anno, Integer mese, Integer giorno){
		String sql = "SELECT * FROM events WHERE YEAR(reported_date) =? "
					+"AND Month(reported_date) = ? AND Day(reported_date) =? ";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			st.setInt(2, mese);
			st.setInt(3, giorno);
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Event evento = new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic"));
					list.add(evento);
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Year> listAnni(){
		String sql = "SELECT DISTINCT year(reported_date) AS anno " + 
				"FROM EVENTS " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql);
			List<Year> list = new ArrayList<Year>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
					list.add(Year.of(res.getInt("anno")));
			}
			
			conn.close();
			Collections.sort(list);
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Integer> listAllDistricts() {

		String sql = "SELECT DISTINCT district_id AS id " + 
				"FROM EVENTS " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql);
			List<Integer> list = new ArrayList<Integer>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
//				District d = new District(res.getInt("id"));
//				list.add(d);
				list.add(res.getInt("id"));
			}
			
			conn.close();
			Collections.sort(list);
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> listAllDistrictsByYear(Year anno) {

		String sql = "SELECT DISTINCT district_id AS id " + 
				"FROM EVENTS WHERE YEAR(reported_date) =? " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno.getValue());
			List<Integer> list = new ArrayList<Integer>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
//				District d = new District(res.getInt("id"));
//				list.add(d);
				list.add(res.getInt("id"));
			}
			
			conn.close();
			Collections.sort(list);
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}


	public Double getLatMedia(int id, Year anno) {
		String sql = "SELECT AVG(geo_lat) as latMedia " + 
				"FROM EVENTS " + 
				"WHERE district_id = ? AND YEAR(reported_date) = ? " ;
		Double latMedia = null;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			st.setInt(2, anno.getValue());
		
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				latMedia = res.getDouble("latMedia");
			}
			
			
			conn.close();
			return latMedia;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public Double getLonMedia(int id, Year anno) {
		String sql = "SELECT AVG(geo_lon) as lonMedia " + 
				"FROM EVENTS " + 
				"WHERE district_id = ? AND YEAR(reported_date) = ? " ;
		Double lonMedia = null;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			st.setInt(2, anno.getValue());
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				lonMedia = res.getDouble("lonMedia");
			}
			
			
			conn.close();
			return lonMedia;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Event> getEventsByDay(Year anno, Month mese, Integer giorno) {
		String sql ="SELECT * " + 
				"FROM EVENTS " + 
				"WHERE YEAR(reported_date) = ? AND " + 
				"MONTH(reported_date) = ? AND " + 
				"DAY(reported_date) = ? ";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			st.setInt(2, mese.getValue());
			st.setInt(3, giorno);
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					Event evento = new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic"));
					list.add(evento);
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	
	}

	public Integer getDistrettoMinByYear(Year anno) {
		String sql = "SELECT district_id " + 
				"FROM EVENTS " + 
				"WHERE YEAR(reported_date) = ? " + 
				"GROUP BY district_id " + 
				"ORDER BY COUNT(*) ASC " + 
				"LIMIT 1 ";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno.getValue());
			
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				conn.close();
				return res.getInt("district_id");
			}
			conn.close();
			return null;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

}
