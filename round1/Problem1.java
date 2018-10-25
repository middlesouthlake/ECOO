package round1;
/*
 
* Problem 1: Munch ’n’ Brunch
 * Author: Andrew Seidel
 * Student council is looking to organize a school brunch, where the proceeds will be put towards a year-
 * end trip for the graduating class. The council members decide that the price depends on how many
 * years you have been at the school. For someone who has been at the school for one year (Y 1 ), the price
 * will be $12, for someone who has been at the school for two years (Y 2 ), the price will be $10, the three-
 * year (Y 3 ) price will be $7, and the price for someone who has been there all four years (Y 4 ) will be $5.
 * Out of all the proceeds, 50% can be saved towards the year-end trip, as the other 50% is spent on the
 * various costs to run the brunch. Given the following input data, calculate whether or not the council will
 * need to raise additional funds.
 * 
 * Input Specifications
 * DATA11.txt (DATA12.txt for the second try) will contain 10 trips, at 3 lines of data per trip.
 * For each of the trips, the first line will show the cost of the trip as an integer ($50 to $50,000).
 * The next line contains four floating point numbers Y 1 , Y 2 , Y 3 , Y 4 (0 ≤ Y 1 , Y 2 , Y 3 , Y 4 ≤ 1 and
 * Y 1 + Y 2 + Y 3 + Y 4 = 1) representing the percentages of the total number of students from years 1
 * through 4 respectively.
 * The third line contains a single number, N, which contains the total number of students
 * attending the brunch (4 ≤ N ≤ 2000)
 * Note: You cannot have less than a whole person (e.g., 1.8 people is the same as 1 person). Any missing
 * or extra people should be removed from or added to the group with the highest percentage of
 * attendees. There will always be exactly one group with the highest percentage of attendees.
 * Output Specifications
 * Output “YES” if the student council needs to find other funding, and “NO” if the council has raised
 * sufficient funds.
 * 
 * Sample Input
 * 4000
 * 0.5 0.2 0.1 0.2
 * 400
 * 6000
 * 0.1 0.1 0.45 0.35
 * 2000
 * 
 * Sample Output
 * YES
 * NO
*/

import java.io.*;
import java.util.*;

class BrunchRecord {
	private final static int[] iCostByYear= {12, 10, 7, 5};
	private int iTripCost;
	private float[] dPercentYear;
	private int [] iStudentsByYear;
	private int iTotalStudents;
	private int iIncome;
	//private int iMaxPercentYear;

	
	BrunchRecord() {
		iTripCost = 0;
		dPercentYear = new float [4];
		iStudentsByYear = new int [4];
		iTotalStudents = iIncome = 0;
	};
	
	
	public void setTripCost(int tc) {
		iTripCost = tc;
	};
	
	public int setPercentYear(float[] percentage) {
		
		if (Math.round(percentage[0]+percentage[1]+percentage[2]+percentage[3]) !=1)
			return -1;
		dPercentYear[0] = percentage[0];
		dPercentYear[1] = percentage[1];
		dPercentYear[2] = percentage[2];
		dPercentYear[3] = percentage[3];
		return 0;
		
	};
	
	public void setTotalStudents(int ns) {
		iTotalStudents = ns;
	};
	
	public void calcStudentsByYear() {
		iStudentsByYear[0] = (int)((float)iTotalStudents * dPercentYear[0]);
		iStudentsByYear[1] = (int)(iTotalStudents * dPercentYear[1]);
		iStudentsByYear[2] = (int)(iTotalStudents * dPercentYear[2]);
		iStudentsByYear[3] = (int)(iTotalStudents * dPercentYear[3]);
		
		/* calculate the max percentage */
		int maxYear=-1;
		float maxPercent=-1;
		for (int i=0; i<dPercentYear.length; i++) {
			if (maxPercent<dPercentYear[i]) {
				maxYear =i;
				maxPercent = dPercentYear[i];
			}
		}
		
		int totalByYear=0;
		for (int i=0; i<iStudentsByYear.length; i++)
			totalByYear += iStudentsByYear[i];
		/* if the sum of iStudentbyYear does not equal totalStudents, the extra or missing people
		 * should be added or removed to/from the highest percentage year.
		 */
		if (totalByYear>iTotalStudents)
			iStudentsByYear[maxYear] -= totalByYear-iTotalStudents;
		else if (totalByYear<iTotalStudents)
			iStudentsByYear[maxYear] += iTotalStudents-totalByYear;
		
	}
	public void calcIncome() {
		calcStudentsByYear();
		iIncome=0;
		for(int i=0;i<iStudentsByYear.length;i++)
			iIncome += iStudentsByYear[i]*iCostByYear[i];
		
	}
	public boolean needMoreFunding() {
		return iIncome/2<iTripCost;
	};
}

public class Problem1 {
	public static void main(String args[]) {
		String sInFilename, sOutFilename;
		BrunchRecord record = new BrunchRecord();
		int cost, students;
		float[] percentage = new float[4];
				
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}
		sInFilename = args[0];
		sOutFilename = new String(sInFilename+".out");
		
		Scanner sc = null;
		PrintWriter out = null;
		
		try {
			sc = new Scanner(new File(sInFilename));
			out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			
			while( true ) {
				if (sc.hasNextInt() == false){
					System.out.println("end of file.");
					break;
				}
				cost = sc.nextInt();
				
				if ( sc.hasNextFloat()==false ) {
					System.out.println("input file format error.");
					break;
				}
				percentage[0] = sc.nextFloat();
				
				if ( sc.hasNextFloat()==false ) {
					System.out.println("input file format error.");
					break;
				}
				percentage[1] = sc.nextFloat();
				
				if ( sc.hasNextFloat()==false ) {
					System.out.println("input file format error.");
					break;
				}
				percentage[2] = sc.nextFloat();
				
				if ( sc.hasNextFloat()==false ) {
					System.out.println("input file format error.");
					break;
				}
				percentage[3] = sc.nextFloat();
				
				if ( sc.hasNextInt() == false) {
					System.out.println("input file format error.");
					break;					
				}
				students = sc.nextInt();
				
				record.setTripCost(cost);
				if (Math.round(percentage[0]+percentage[1]+percentage[2]+percentage[3]) !=1) {
					System.out.println("sum of the percentage is not 1.");
					out.println("ERROR");
					continue;
				}
				record.setPercentYear(percentage);
				record.setTotalStudents(students);
				
				record.calcIncome();
				if (record.needMoreFunding() == true)
					out.println("YES");
				else
					out.println("NO");
			}
			
			
		} catch (Exception x) {
			System.err.format("IOException: %s%n", x);
		}
		
		if (sc!=null) sc.close();
		if (out!=null) out.close();
	}
}
