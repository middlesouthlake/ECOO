package round2;

import java.io.*;
import java.util.*;

class Restaurant implements Comparable<Restaurant> {
	private int id;
	private int rate;
	
	Restaurant(int i, int r){
		id = i;
		rate = r;
	}

	@Override
	public int compareTo(Restaurant r) {
		if(this.rate == r.rate)
			//return r.id - this.id;	//ascending order
			return this.id-r.id;	//descending order
		else if(this.rate > r.rate)
			return -1;
		else
			return 1;
		//return this.rate - r.rate; //ascending order
		//return r.rate-this.rate; //descending order
	}
	
	@Override
	public String toString() {
		return "["+id+","+rate+"]";
	}
	
	void setId(int i) {
		id = i;
	}
	void setRate(int r) {
		rate = r;
	}
	int getId() {
		return id;
	}
	int getRate() {
		return rate;
	}
	boolean isGreaterThan(Restaurant r) {
		return this.compareTo(r)<0;
	}
	boolean isSmallerThan(Restaurant r) {
		return this.compareTo(r)>0;
	}
}


class LunchTime {
	final static int MAX_RESTAURANTS = 100000;
	final static int MAX_M = 100000;
	final static long MAX_K = 1000000000000l;
	final static int MAX_R = 1000000000;
	int nRestaurants; //[1,10^5]
	int mDropRate;		//[1,10^5]
	long kDaysToLeave;	//[1, 10^12] = [1, 2^40]
	TreeMap<Double, Integer> ratings; //key is rates.index to avoid the duplication, value is index
	ArrayList<Restaurant> restaurants;
	
	
	LunchTime(int n, int m,long k) throws Exception{
		nRestaurants = n;
		mDropRate = m;
		kDaysToLeave = k;
		System.out.printf("New LunchTime with N=%d,M=%d,K=%d\n", n, m, k);
				
		try {
			restaurants = new ArrayList<Restaurant>(n);
		}catch (OutOfMemoryError e) {
			throw new NullPointerException("No memory for the lunch time.");
		}
		if(restaurants == null)
			throw new NullPointerException("No memory for the lunch time.");

	}
	void addRestaurant(int id, int rate) {
		restaurants.add(new Restaurant(id,rate));
		Collections.sort(restaurants);
	}
	void addRestaurant(Restaurant r) {
		restaurants.add(r);
		Collections.sort(restaurants);
		
	}
	
	void removeRestaurant(int id, int rate) {
		restaurants.remove(new Restaurant(id,rate));
		Collections.sort(restaurants);
	}
	void removeRestaurant(Restaurant r) {
		restaurants.remove(r);
		Collections.sort(restaurants);

	}
	
	void setRestaurant(int id, int current_rate, int new_rate) {
		if(current_rate == new_rate) return;
		
		int i = restaurants.indexOf(new Restaurant(id,current_rate));
		Restaurant r = restaurants.get(i);
		r.setRate(new_rate);
		Collections.sort(restaurants);
	}
	
	void setRestaurant(Restaurant r, int new_rate) {
		Restaurant rt = restaurants.get(restaurants.indexOf(r));
		if((rt != null) && (rt.getRate()!=new_rate)) {
			rt.setRate(new_rate);
			Collections.sort(restaurants);
		}
	}
	
	Restaurant getLastRestaurant() {
		return restaurants.get(restaurants.size()-1);
	}
	
	Restaurant getFirstRestaurant() {
		return restaurants.get(0);
	}
	
	Restaurant pollFirstRestaurant() {
		Restaurant r = getFirstRestaurant();
		removeRestaurant(r);
		return r;
	}
	Restaurant pollLastRestaurant() {
		Restaurant r = getLastRestaurant();
		removeRestaurant(r);
		return r;		
	}
	////////////////////////////////////////////////////////////////

	
	void setRatings(int p, long rate) {
		if(p>=nRestaurants) return;
		Double d;
		d = 1.0/(double)p;
		d += (double)rate;
		ratings.put(d, p);
	}
	
	int getPreferredRestaurant() {
		Map.Entry<Double, Integer> entry = ratings.lastEntry();
		if (entry!=null) return entry.getValue();
		return 0; // the map is empty
	}
	
	void perfRatings() {
		long startTime;
		
		System.out.println("performance staring...");
		
		startTime = System.currentTimeMillis();

	System.out.format("Map lastEntry, %d ms.\n", System.currentTimeMillis()-startTime);
		
	}
		
	int restaurantAfterKDays() {
		//key is rates.index to avoid the duplication, value is index

		long timeStart, timeEnd;
		timeStart = System.currentTimeMillis();		
		
		
		//1. try to remove some restaurants which will never be calculated.
		long sumRate = 0;
		boolean isMinKeyRemoved = false;
		Restaurant last = getLastRestaurant();
		do {
			isMinKeyRemoved = false;
			for(Restaurant r: restaurants) {
				sumRate += r.getRate()-last.getRate();
				if(sumRate/mDropRate > kDaysToLeave) {
					restaurants.remove(last);
					isMinKeyRemoved = true;
					last = getLastRestaurant();
					sumRate = 0;
					break;
				}
			}
		}while (isMinKeyRemoved==true);
		
		System.out.format("New Map size is %d, min is %s, max is %s.\n",
				restaurants.size(), getLastRestaurant(), getFirstRestaurant());
		
		//2. try to remove same number of days for each of the restaurant
		long daysLeft = kDaysToLeave;
		last = getLastRestaurant();

		Restaurant rt;
		int rate;
		for(int i=restaurants.size()-1;i>=0;i--) {
			rt = restaurants.get(i);
			int irate = rt.getRate()-last.getRate();
			int iday = irate/mDropRate;

			if(iday>0) {
				rate = rt.getRate()-iday*mDropRate;
				daysLeft -= iday;
			}
			else {
				rate = rt.getRate();
			}
			setRestaurant(rt, rate);
			
		}

		System.out.format("New rating Map size is %d, min is %s, max is %s, %d days left.\n",
				restaurants.size(), getLastRestaurant(), getFirstRestaurant(), daysLeft);

		//3. now the rate difference between each of the restaurants is less than a M,
		//if the days left are still huge, try to remove days from each of the restaurants.
		//daysLeft = ratings.firstKey().intValue()%ratings.size();
		//if(daysLeft/ratings.size() > 1)
			//daysLeft = ratings.size() + daysLeft%ratings.size();
		
		System.out.println("Days left reduced to: "+daysLeft);
		
		//4. calculate the final restaurant.
		int maxId = -999;
		
		Restaurant max = new Restaurant(-999, 0x80000000);
		
		
		last = getLastRestaurant();
		int lastId = last.getId();
		int currentRestaurants = restaurants.size();
		int n;
		//lastEntryKey = maxKey+1;
		
		System.out.format("min rate's restaurant is %s.\n", last);
		for(long day=1;day<daysLeft;day++) {
			if((restaurants.size()>0) && max.isSmallerThan(getFirstRestaurant())) {
				if(max.getId()>=0) {
					addRestaurant(max);
				}
				max = pollFirstRestaurant();
			}
			if(restaurants.size()>0) {
				n = (max.getRate() - getFirstRestaurant().getRate())/mDropRate;
				day += n;
				max.setRate(max.getRate()-mDropRate*(n+1)); 
				if(n > 0) {
					System.out.println(n+" days moved forward.");
				}
			}
			else
				max.setRate(max.getRate()-mDropRate); 

			if(lastId == max.getId() && (daysLeft-day)>currentRestaurants) {
				day += ((daysLeft-day)/(long)currentRestaurants)*(long)currentRestaurants;
				System.out.println(" day moved forward to "+day);
				lastId = -999;
			}
			if(day%100000000 == 0) System.out.format("%d day, current restaurant is %s.\n", day, max);
		}
		
		if ((restaurants.size()>0) && max.isSmallerThan(getFirstRestaurant())) {
			max = getFirstRestaurant();
			maxId = max.getId();
		}
		maxId = max.getId();
		timeEnd = System.currentTimeMillis();
		System.out.format("Restaurant %d will be choosen. %d ms used.%n%n", maxId+1, timeEnd-timeStart);
		return maxId+1;
	}
}

public class Problem3{
	public static void main(String[] args) {
/*
		long timeStart, timeEnd;
		timeStart = System.currentTimeMillis();		
		try {
			LunchTime testLunch = new LunchTime(LunchTime.MAX_RESTAURANTS, 2, 1000000000l);
			testLunch.setRateOfRestaurantAll(LunchTime.MAX_R);
			testLunch.perfRatings();
			//testLunch.restaurantAfterKDays();

		} catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}
		timeEnd = System.currentTimeMillis();
		System.out.printf("time used for loop %d is %d ms\n", 100000000, timeEnd-timeStart);

		System.exit(0);

		
		Restaurant a = new Restaurant(1, 10);
		Restaurant b = new Restaurant(2, 20);
		
		System.out.println(b.compareTo(a));
		
		System.exit(0);
*/		
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}

		String sInFilename = args[0];
		
		Scanner sc;
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		try {
			//in = new BufferedReader(new FileReader(sInFilename));
			sc = new Scanner(new File(sInFilename));
			//while ((s=in.readLine())!=null) {
			while(sc.hasNextInt()) {
				//sc = new Scanner(s);
				int n, m;
				long k;
				n = sc.nextInt();
				m = sc.nextInt();
				k = sc.nextLong();
				LunchTime lt = new LunchTime(n, m, k);
				//sc.close();
				//s = in.readLine();
				//if (s==null) {
					//System.out.println("File format error! Can't read R.");
					//break;
				//}
				//sc = new Scanner(s);
				//n=0;
				//while(sc.hasNextInt()) {
				for(int i=0;i<n;i++) {
					if(sc.hasNextInt())
						lt.addRestaurant(i, sc.nextInt());
				}
				//sc.close();
				//lt.printRateOfRestaurantAll();
				n = lt.restaurantAfterKDays();
				results.add(n);

			}
			//in.close();
			String sOutFilename = new String(sInFilename+".out");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			for (Integer id: results) {
				out.println(id);
			}
			sc.close();
			out.close();
		}catch(Exception e) {
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}

		
	}
}