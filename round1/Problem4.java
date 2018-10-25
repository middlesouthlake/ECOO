package round1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/* 
 * Problem 4: Almost Sorted
 * Author: Reyno Tilikaynen
 * After the lecture on sorting, Diana’s CS teacher has given the class a challenge: what’s the most efficient
 * way to sort the class list? Students are only allowed to sort by repeatedly swapping two names in the
 * list, and the person who makes the fewest swaps wins.
 * Diana really wants to win, so she decides to do something sneaky. When showing the teacher her
 * efficient sort, she will omit one of the names in the list, which will let her use fewer swaps. Using her
 * tactical advantage, what is the fewest swaps she has to do?
 * Input Specifications
 * DATA41.txt (DATA42.txt for the second try) will contain 10 test cases. Each test case starts with an
 * integer N (1 ≤ N ≤ 1,000). The next N lines represent the class list, with one name on each line in capital
 * letters. Names will be unique and only contain uppercase letters.
 * Output Specifications
 * For each test case, your program should output the minimum number of swaps Diana needs to sort the
 * list using her trick.
 * Sample Input (only 2 cases shown)
 * 3
 * SAM
 * DIANA
 * REBECCA
 * 5
 * DEREK
 * MEGAN
 * BRIAN
 * BOB
 * DIANA
 * Sample Output
 * 0
 * 1
 * Explanation
 * In the first example, removing “Sam” will leave the list sorted, so no swaps are required. In the second
 * example, Diana could omit “Megan” and then sort the list by swapping “Derek” and “Bob”.
 * 
 * */

class Problem4 {
	final static int MAX_STUDENTS = 1000;
	LinkedList<String> names;
	LinkedList<String> namesSorted;
	int TotalStudents;
	int[] numSwaps;
	
	int nBestSwap;
	String name2omit;
	
	Problem4(int nStudents) throws Exception{
		if (nStudents>MAX_STUDENTS)
			throw new Exception("Too many students.");
		
		TotalStudents = nStudents;
		nBestSwap = MAX_STUDENTS+1;

		names = new LinkedList<String>();
		namesSorted = new LinkedList<String>();
		numSwaps = new int[nStudents];
		if ((names == null) || (namesSorted == null) || (numSwaps == null))
			throw new NullPointerException("No memory for the problem4.");
	}
	
	int addStudentName(int i, String s) {
		if (i<0 || i>=TotalStudents) return -1;
		
		names.add(i,s);
		
		return i;
	}
	
	void addStudentNameFinished() {
		namesSorted = (LinkedList<String>)names.clone();
		namesSorted.sort(Comparator.naturalOrder());

	}
	
	
	int getTimesOfSwapSort(int omitIndex) {
		
		if (omitIndex<0 || omitIndex>=TotalStudents)
			return -1;
		
		String omitName = names.get(omitIndex);
		
		names.remove(omitIndex); //remove the name to omit
		
		String[] name2sort = names.toArray(new String[names.size()]);
		
		int omitIndexSorted = namesSorted.indexOf(omitName);
		if (omitIndexSorted < 0) {
			names.add(omitIndex, omitName);
			return -1;
		}
		
		namesSorted.remove(omitIndexSorted);
		
		HashMap<String, Integer> mapSorted = new HashMap<String, Integer>();
		int i=0;
		for (String s:namesSorted) {
			mapSorted.put(s,  i);
			i++;
		}

		int nSwap=0;
		String s;
		boolean swapped = false;
		do {
			swapped = false;
			for(int n=0; n<name2sort.length; n++) {
				if(((i=mapSorted.get(name2sort[n]))>=0) && i!=n) {
				//if(((i = namesSorted.indexOf(name2sort[n]))>=0) && (i!=n)){
					nSwap++;
					s = name2sort[n];
					name2sort[n] = name2sort[i];
					name2sort[i] = s;
					swapped = true;
				}
			}
		} while (swapped == true);
		names.add(omitIndex, omitName);
		namesSorted.add(omitIndexSorted, omitName);
		return nSwap;
	}
	
	
	void omitOneSwapSortAll() {
		nBestSwap = MAX_STUDENTS+1;
		int n;
		int length = names.size();
		for(int i=0;i<length;i++) {
			n = getTimesOfSwapSort(i);
			if(n<0) {
				System.out.println("Error in getTimesOfSwapSort.");
				continue;
			}
			if(n<nBestSwap) {
				nBestSwap = n;
				name2omit = names.get(i);
			}
		}
		System.out.format("swap %d times with removing name:%s%n", nBestSwap, name2omit);
	}
	
	int getBestNumSwaps() {
		return nBestSwap;
	}
	
	public static void main(String[] args) {
		String sInFilename;
						
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}
		
		sInFilename = args[0];
		
		BufferedReader in = null;
		ArrayList<Integer> results = new ArrayList<Integer>();
		int nStudents;
		String s;
		
		try {
			in = new BufferedReader(new FileReader(sInFilename));
			while ((s=in.readLine())!=null) {
				nStudents = Integer.parseInt(s);
				Problem4 p = new Problem4(nStudents);
				for (int i=0;i<nStudents;i++)
					p.addStudentName(i, in.readLine());
				
				p.addStudentNameFinished();
				p.omitOneSwapSortAll();
				results.add(p.getBestNumSwaps());
			};
			
		}catch (Exception e){
			System.err.format("IOException: %s%n", e);
			System.exit(0);
		}
		
		String sOutFilename = new String(sInFilename+".out");
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			for (Integer n:results) {
				out.println(n);
			}
			out.close();
		} catch(Exception e) {
			System.err.format("IOException: %s%n", e);
			System.exit(0);	
		} 
	
	}
}
