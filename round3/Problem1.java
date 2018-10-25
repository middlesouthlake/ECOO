/**
 * 
 */
package round3;

import java.io.*;
import java.util.*;

/**
 * @author westmount
 *
 */
public class Problem1 {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1) {
			System.out.println("Please add data file");
			System.exit(0);
		}
		
		BufferedReader in;
		int[] soldByFranchises;
		int nDozens = 0;
		final int DOZEN = 13;
		int nFranchises, nDays;
		String s;
		ArrayList<Integer> result = new ArrayList<Integer>();
		try {
			in = new BufferedReader(new FileReader(args[0]));
			s = in.readLine();
			while(s!=null) {
				Scanner sc = new Scanner(s);
				nFranchises = sc.nextInt();
				nDays = sc.nextInt();
				sc.close();
				soldByFranchises = new int[nFranchises];
				nDozens = 0;
				for(int i=0;i<nDays;i++) {
					s = in.readLine();
					sc = new Scanner(s);
					int sold, sumOfDay=0;
					for(int j=0;j<nFranchises;j++) {
						sold = sc.nextInt();
						soldByFranchises[j] += sold;
						sumOfDay += sold;
					}
					if (sumOfDay%DOZEN == 0)
						nDozens += sumOfDay/DOZEN;
					sc.close();
				}
				for(int j=0;j<nFranchises;j++) {
					if(soldByFranchises[j]%DOZEN == 0)
						nDozens += soldByFranchises[j]/DOZEN;
				}
				result.add(nDozens);
				s = in.readLine();
			}
			in.close();
		}catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}
		
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(args[0]+".out")));
			for(Integer i:result)
				out.println(i);
			out.close();
		}catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}
	}

}
