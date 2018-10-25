/**
 * 
 */
package round3;

/**
 * @author westmount
 *
 */

import java.io.*;
import java.util.*;


class Node implements Comparable<Node>{
	int x;
	int y;	
	
	Node(int ix, int iy){
		x = ix;
		y = iy;
	}
	int getX() {
		return x;
	}
	int getY() {
		return y;
	}	

	int cost(Node node) {
		return (node.getX()-x)*(node.getX()-x)+(node.getY()-y)*(node.getY()-y);
	}
	
	@Override
	public int compareTo(Node o) {
		return x-o.getX();
	}
	
	@Override
	public boolean equals(Object o) {
		Node node = (Node)o;
		return (x==node.getX() && y==node.getY());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 1;
		hash = prime * hash + x;
		hash = prime * hash + y;
		return hash; 
	}
	@Override
	public String toString() {
		return "["+String.valueOf(x)+","+String.valueOf(y)+"]";
	}
	
}


public class Problem3 {
	//ArrayList<Line> connections ;
	ArrayList<Node> schools;
	
	
	Problem3(int n){
		//schools = new TreeSet<School>();
		if(n<=0) return;
		schools = new ArrayList<Node>(n);
		//connections = new ArrayList<Line>(n*(n+1)/2);
	}

	
	void addSchool(Node s) {
		if(s==null || schools.contains(s)) return;
		
/*		for(Node sch : schools) {
			connections.add(new Line(sch,s));
		}*/
		schools.add(s);
	}

	int getMinSumCost() {

		System.out.println(Arrays.asList(schools));
		
		
		if(schools==null || schools.size()<3) return 0;
		
		int nSchools = schools.size();
		int dp[] = new int[nSchools+1];
		Node[][] semiFinals = new Node[nSchools+1][2];

		dp[0] = 0;
		dp[1] = 0;
		dp[2] = 0;
		semiFinals[1][0] = schools.get(0);
		semiFinals[2][0] = schools.get(0);
		semiFinals[2][1] = schools.get(1);
		
		for(int i=3; i<=nSchools; i++) {
			Node current, localSemiFinal2;
			
			current = schools.get(i-1);
			localSemiFinal2 = (current.cost(semiFinals[i-1][0])>current.cost(semiFinals[i-1][1]))?
					semiFinals[i-1][0]:semiFinals[i-1][1];
				
			dp[i] = 0;
			for(int j=0;j<i;j++) {
				Node node = schools.get(j);
				dp[i] += Math.min(node.cost(current), node.cost(localSemiFinal2));
			}

			semiFinals[i][0] = current;
			semiFinals[i][1] = localSemiFinal2;
			int currentSum = dp[i-1]+Math.min(current.cost(semiFinals[i-1][0]), current.cost(semiFinals[i-1][1]));
			if(currentSum<dp[i] || 
				(currentSum==dp[i] && semiFinals[i-1][0].cost(semiFinals[i-1][1])<=current.cost(localSemiFinal2))) {
					semiFinals[i][0] = semiFinals[i-1][0];
					semiFinals[i][1] = semiFinals[i-1][1];
					dp[i] = currentSum;
				}
			System.out.printf("%d schools -> %s and %s, cost is %d\n", i, semiFinals[i][0], semiFinals[i][1], dp[i]);
		}
		return dp[nSchools];
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1) {
			System.out.println("Please add input file name");
			System.exit(0);
		}
			
		BufferedReader in;
		ArrayList<Integer> result = new ArrayList<Integer>();
		int n;
		String s;
		
		try {
			in = new BufferedReader(new FileReader(args[0]));
			s = in.readLine();
			while(s!=null) {
				n = Integer.parseInt(s);
				Problem3 p = new Problem3(n);
				for(int i=0;i<n;i++) {
					String[] sa = in.readLine().split(" ");
					p.addSchool(new Node(Integer.parseInt(sa[0]), Integer.parseInt(sa[1])));
				}
				n = p.getMinSumCost();
				result.add(n);
				System.out.println(n);
				s=in.readLine();
			}
			in.close();
		}catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}
		System.out.println(result);
	}

}
