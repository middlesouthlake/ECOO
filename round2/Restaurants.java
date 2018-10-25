/**
 * 
 */
package round2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

/**
 * @author westmount
 *
 */
public class Restaurants {
	private static String driverName = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String dbName = "/home/westmount/workspace/MyDbTest";
	private static String dbURL = "jdbc:derby:"+dbName+";";
	final static int MAX_RESTAURANTS = 100000;
	final static int MAX_M = 100000;
	final static long MAX_K = 1000000000000l;
	final static int MAX_R = 1000000000;

	//private static String tableName = "restaurants";

	// jdbc Connection
	private static Statement stmt = null;

	private static Connection conn = null;
	String tableName;
	private int[] ratings;
	private int[] idx_ratings;
	private int mDropRate;


	Restaurants(String table_name){
		tableName = table_name;
	}
	
	/**
	 * 
	 * @param n number of restaurants
	 * @param m drop rate 
	 */
	Restaurants(int n, int m) throws Exception {
		mDropRate = m;
		
		if (n<=0) return;
		
		try {
			ratings = new int[n];
			idx_ratings = new int[n];
		}catch (OutOfMemoryError e) {
			throw new NullPointerException("No memory for the lunch time.");
		}
		if(ratings == null)
			throw new NullPointerException("No memory for the lunch time.");
		for(int i=0;i<idx_ratings.length;i++)
			idx_ratings[i] = -1;
	}
	
	private void addToIndex(int n, int rate) {
		int head =0, tail = idx_ratings.length;
		int current;
		while(head < tail) {
			current = (head+tail)/2;
			if(ratings[current]>ratings[n]) {
				head = current;
			}
			else if(ratings[current]<ratings[n]) {
				tail = current;
			}
			else if(current > n) {
				
			}
		}
	}
	public void setRateOfRestaurant(int n, int rate) {
		if(n>=ratings.length) return;
		ratings[n]=rate;
		addToIndex(n, rate);		
		
	}
	public static void createConnection(){
		try {
			Class.forName(driverName).newInstance();
	        //Get a connection
	        conn = DriverManager.getConnection(dbURL); 
	    }catch (Exception e){
	            e.printStackTrace();
	    }
	}
	    
	public void insertRestaurant(int id, int rate) {
		try {
			stmt = conn.createStatement();
			//String s = "insert into " + tableName + " values (" +id+ "," + rate + ")";
	        stmt.execute("insert into " + tableName + " values (" +id+ "," + rate + ")");	
	        stmt.close();
	    } catch (SQLException sqlExcept) {
	            sqlExcept.printStackTrace();
	    }
		
	}
	
	public int firstRestaurantID() {
		try {
			stmt = conn.createStatement();
			String s;
			s= "select RESTAURANT_ID from "+tableName+" where RATE=(select MIN(RATE) from "+tableName+")";
					
			ResultSet rs = stmt.executeQuery(s);
	        if(rs.next()) {
	        	stmt.close();
	        	return rs.getInt(1);
	        }
	        stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int firstRestaurantRate() {
		try {
			stmt = conn.createStatement();
			String s;
			s = "select MIN(RATE) from "+tableName;
			ResultSet rs = stmt.executeQuery(s);
	        if(rs.next()) {
	        	stmt.close();
	        	return rs.getInt(1);
	        }
	        stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
		
	}
	
	public int lastRestaurantID() {
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select RESTAURANT_ID from "+tableName+
					" where RATE=(select MAX(RATE) from "+tableName+")");
	        if(rs.next()) {
	        	stmt.close();
	        	return rs.getInt(1);
	        }
	        stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}		
		return -1;
	}
	
	public int lastRestaurantRate() {
		try {
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("select MAX(RATE) from "+tableName);
	        if(rs.next()) {
	        	rs.close();
	        	stmt.close();
	        	return rs.getInt(1);
	        }
	        rs.close();
	        stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void removeRestaurant(int id) {
		try {
			stmt = conn.createStatement();

			stmt.execute("delete from "+tableName+" where restaurant_id="+id);
			
			stmt.close();

		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	
	public void removeAllRestaurants() {
		try {
			stmt = conn.createStatement();

			stmt.execute("delete from "+tableName);
			stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//1. try to remove some restaurants which will never be calculated.
	public void removeRestaurantsNotUsed(int M, long K) {
		int minRate = firstRestaurantRate();
		int minID;
		int sumRate = 0;
		boolean isMinKeyRemoved = false;
		int currentRate, currentID;
		int count=0;
		
		try {
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("select * from "+tableName+" order by rate desc");

			do {
				isMinKeyRemoved = false;
				while(rs.next()) {
				//for(Map.Entry<Double, Integer> entry: rMap.descendingMap().entrySet()) {
					currentRate = rs.getInt(2);
					currentID = rs.getInt(1);
					sumRate += currentRate-minRate;
					if(sumRate/M > K) {
						minID = firstRestaurantID();
						removeRestaurant(minID);
						isMinKeyRemoved = true;
						minRate = firstRestaurantRate();
						sumRate = 0;
						count++;
						break;
					}
				}
			}while (isMinKeyRemoved==true);
			rs.close();
        	stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(count+" days removed.");

	}
	
	//2. try to remove same number of days for each of the restaurant
	/**
	 * 
	 * @param M
	 * @param K
	 * @return DaysRemoved
	 */
	public long removeDays(int M, long K) {
		long daysLeft = K;
		
		try {
			
			int currentRate;
			int currentID;
			
			int minRate = firstRestaurantRate();
			//int minID = firstRestaurantID();
				
	
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("select * from "+tableName+" order by rate desc");

			while(rs.next()) {
				currentRate = rs.getInt(2);
				currentID = rs.getInt(1);
				
				int irate = currentRate-minRate;
				long iday = (long)irate/M;
				if(iday>0) {
					//rMap.remove(entry.getKey());
					minRate = currentRate-(int)(iday*M);
					daysLeft -= iday;
				}
				else {
					minRate = currentRate;
				}
				updateRestaurant(currentID, minRate);	
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(daysLeft+"days left now.");

		return daysLeft;
	}
	
	public void updateRestaurant(int id, int rate) {
		try {
			stmt = conn.createStatement();

			stmt.execute("update "+tableName+" set rate="+rate+" where restaurant_id="+id);
			stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	int size() {
		try {
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("select count(*) from "+tableName);
	        if(rs.next()) {
	        	rs.close();
	        	stmt.close();
	        	return rs.getInt(1);
	        }
	        rs.close();
	        stmt.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;		
	}
	void selectRestaurants() {

		try {
			stmt = conn.createStatement();
	            ResultSet results = stmt.executeQuery("select * from " + tableName);
	            ResultSetMetaData rsmd = results.getMetaData();
	            int numberCols = rsmd.getColumnCount();
	            for (int i=1; i<=numberCols; i++)
	            {
	                //print Column Names
	                System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
	            }

	            System.out.println("\n-------------------------------------------------");

	            while(results.next())
	            {
	                int id = results.getInt(1);
	                String restName = results.getString(2);
	                String cityName = results.getString(3);
	                System.out.println(id + "\t\t" + restName + "\t\t" + cityName);
	            }
	            results.close();
	            stmt.close();
	        }
	        catch (SQLException sqlExcept)
	        {
	            sqlExcept.printStackTrace();
	        }
	    }

	    
	static void shutdown() {
		try {
			//if (stmt != null) stmt.close();
			if (conn != null) {
				DriverManager.getConnection(dbURL + "shutdown=true");
				conn.close();
			}           
		} catch (SQLException sqlExcept) {
			//sqlExcept.printStackTrace();
		}

	}
	/**
	 * @param args
	 */  
	public static void main(String[] args) {
		Restaurants r = new Restaurants("RESTAURANTS");
		
		Restaurants.createConnection();
		//insertRestaurants(5, "LaVals", "Berkeley");
	        //selectRestaurants();
		r.removeAllRestaurants();
		r.insertRestaurant(1, 10);
		r.insertRestaurant(2, 15);
		r.insertRestaurant(3, 30);
		r.insertRestaurant(4, 20);
		r.insertRestaurant(5, 5);
		System.out.println("First ID: "+r.firstRestaurantID());
		System.out.println("First rate: "+r.firstRestaurantRate());
		System.out.println("Last ID: "+r.lastRestaurantID());
		System.out.println("Last rate: "+r.lastRestaurantRate());

		r.removeRestaurant(5);
		System.out.println("First ID After removal: "+r.firstRestaurantID());
		
		r.updateRestaurant(2, 5);
		System.out.println("First ID After update: "+r.firstRestaurantID());

		r.removeAllRestaurants();
		Restaurants.shutdown();
	    
	    }

}
