/**
 * 
 */
package round2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author westmount
 *
 */
class ZigZag {
	static long MODULO = 1000000007;
	static char INDEX_EVEN = 'E';
	static char INDEX_ODD = 'O';
	static int AVAILABLE4EVEN = 1;
	static int AVAILABLE4ODD = 2;
	static int AVAILABLE4BOTH = 3;
	static int UNAVAILABLE4EVEN = -1;
	static int UNAVAILABLE4ODD = -2;
	static int UNAVAILABLE4BOTH = -3;
	int  N;
	
	private long count;
	
	TreeSet<Integer> numSelected;
	static HashMap<Integer, Long> countZigZag = new HashMap<Integer, Long>(); //<digits of N, numbers of zigzag>
	static ArrayList<Integer> result = new ArrayList<Integer>();
	static TreeMap<Integer, Boolean> evenMap = new TreeMap<Integer, Boolean>();
	static TreeMap<Integer, Boolean> oddMap = new TreeMap<Integer, Boolean>();
	static TreeMap<Integer, Integer> numberMap = new TreeMap<Integer, Integer>();
	
	
	ZigZag(int n){
		N=n;
		numSelected = new TreeSet<Integer>();
		count = 0;
	}
	
	static long factorial(int n, long modulo) {
		long f = 1;
		for(int i=1;i<=n;i++) {
			f = f*i%modulo;
		}
		return f;
	}
	
	void setN(int n) {
		N = n;
	}
	
	long getCount() {
		return count;
	}
	void setCount(int n) {
		count = n;
	}
	void increaseCount() {
		count = (count+1) % MODULO;
	}
	

	
	void clearCount() {
		count = 0;
	}
	
	void setCountMap(int n, long count) {
		countZigZag.put(n, count);
	}
	

	
	void calculate2(char position, int preceding) {
		if (position == INDEX_ODD) {
			if(!oddMap.containsValue(true)) {//no available number
				count++;
				count %= MODULO;
				if(count == 0) System.out.print("*");
				return;
			}
			for(Map.Entry<Integer, Boolean> entry : oddMap.headMap(preceding).entrySet()) {
				if(entry.getValue()) {
					oddMap.replace(entry.getKey(), false);
					calculate2(INDEX_EVEN, entry.getKey());
					oddMap.replace(entry.getKey(), true);
				}
			}
		}
		else {//INDEX_EVEN
			if(!evenMap.containsValue(true)) {//no available number
				count++;
				count %= MODULO;
				if(count == 0) System.out.print("*");
				return;
			}
			for(Map.Entry<Integer, Boolean> entry : evenMap.tailMap(preceding).entrySet()) {
				if(entry.getValue()) {
					evenMap.replace(entry.getKey(), false);
					calculate2(INDEX_EVEN, entry.getKey());
					evenMap.replace(entry.getKey(), true);
				}
			}			
		}
	}

	void process(char position, int preceding) {
		//boolean isEmpty = true;
		if (numberMap.isEmpty()){//nothing available for ODD position
			increaseCount();
			//System.out.println(numberList);
			if(count == 100000) System.out.print("*");
			return;
		}
		
		Integer v;
		
		if (position == INDEX_ODD) {
			for(int i=numberMap.firstKey(); i<preceding; i++){
				if((v = numberMap.get(i))==null) continue;
				if((v==AVAILABLE4ODD)||(v==AVAILABLE4BOTH)) {
					numberMap.remove(i);
					process(INDEX_EVEN, i);
					numberMap.put(i, v);
				}
			}
		}			
		else {//EVEN position
			for(int i=preceding+1; i<=numberMap.lastKey(); i++){
				if((v = numberMap.get(i))==null) continue;
				if((v==AVAILABLE4EVEN)||(v==AVAILABLE4BOTH)) {
					numberMap.remove(i);
					process(INDEX_ODD, i);
					numberMap.put(i, v);
				}
			}
		}
	}
	
	void calculate3(char position, int preceding) {
		if(N==1) {
			setCount(1);
			return;
		}
		if(N==2) {
			setCount(1);
			return;
		}
		
		clearCount();
		//numberMap.put(0, UNAVAILABLE4BOTH);
		numberMap.put(1, AVAILABLE4ODD);
		
		for(int i=2; i<N; i++) {
			numberMap.put(i, AVAILABLE4BOTH);
		}
		numberMap.put(N, AVAILABLE4EVEN);
		
		
		for(int i=(N+1)/2+1; i<N; i++) {
			numberMap.replace(i, AVAILABLE4ODD);
			process(position, preceding);
			numberMap.replace(i, AVAILABLE4BOTH);
			System.out.println(i+", count is "+getCount());
		}
		count += (factorial(N/2, MODULO)*factorial(N-N/2, MODULO))%MODULO;
	}
	
	boolean calculate(char position, int preceding, TreeSet<Integer> ts) {
		
		if(ts.size()==0) {
			count++;
			if(count%MODULO==0)
				System.out.print("*");
			System.out.println(result);
			result.clear();
			return true;
		}
		

		if(position == INDEX_EVEN) {
			/*
			if(preceding<ts.first()) {
				if(countZigZag.containsKey(ts.size())) {
					count += countZigZag.get(ts.size());
					if(count%10000000==0)
						System.out.print("-");
					return;
				}				
			}*/
			if(ts.tailSet(preceding).size()==0) {
				return false;
			}
			for(Integer i: ts.tailSet(preceding)) {
				TreeSet<Integer> clone = (TreeSet<Integer>)ts.clone();
				clone.remove(i);
				result.add(i);
				if(calculate(INDEX_ODD, i, clone)==false) {
					result.remove(i);
				}
			}
		}
		else {//INDEX_ODD
			/*
			if(preceding>ts.last()) {
				if(countZigZag.containsKey(ts.size())) {
					count += countZigZag.get(ts.size());
					if(count%10000000==0)
						System.out.print("-");
					return;
				}
			}*/
			if(ts.headSet(preceding).size()==0) {
				return false;
			}
			for(Integer i: ts.headSet(preceding)) {
				TreeSet<Integer> clone = (TreeSet)ts.clone();
				clone.remove(i);
				result.add(i);
				if(calculate(INDEX_EVEN, i, clone)==false) {
					result.remove(i);
				}
			}
		}
		return true;
	}
	
}
public class Problem4 {

	
	
	static void test(int n, int pos) {
		System.out.println(n+","+pos);
		for(int i = n;i>0;i--)
			test(i-1, pos+1);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length != 1) {
			System.out.println("Please add N in the command.");
			System.exit(0);
		}
		
		long timeStart, timeEnd;
		timeStart = System.currentTimeMillis();	
		
		
		int N = Integer.valueOf(args[0]);
		ZigZag zz = new ZigZag(N);
		//TreeSet<Integer> t = new TreeSet<Integer>();
		
		//zz.setCountMap(1, 0);
		//zz.setN(N);
		//zz.calculate3(ZigZag.INDEX_ODD,  N+1);
		for(long i=0;i<ZigZag.factorial(N, 0x7fffffff);i++);
		//System.out.println(ZigZag.factorial(10000, ZigZag.MODULO));
		System.out.println("ZigZag("+N+")="+zz.getCount());


/*
		zz.clearCount();
		t.clear();
		for(int j=1;j<=N;j++) t.add(j);
		zz.calculate(ZigZag.INDEX_ODD, N+1,  t);
*/		
/*		
		for(int i=2;i<=N;i++) {
			zz.setN(i);
			zz.clearCount();
			t.clear();
			for(int j=1;j<=i;j++) t.add(j);
			zz.calculate(ZigZag.INDEX_ODD, N+1, t);
			zz.setCountMap(i, zz.getCount());
			System.out.println("ZigZag("+i+")="+zz.getCount());
		}
*/
		timeEnd = System.currentTimeMillis();
		System.out.println((timeEnd-timeStart)+" ms used.");
		
	}

}
