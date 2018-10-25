package round1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Problem 3: Mountain View
 * Author: Reyno Tilikaynen
 * On his trip to the West Coast, Larry has decided to stop by the Rocky Mountains to do some sightseeing.
 * He only wants to climb one mountain, so he would prefer to climb the mountain with the best view.
 * The quality of the view is judged by the number of other mountains Larry can see from the peak when
 * looking either right or left. A mountain is visible from a peak if the straight line connecting the peaks of
 * the two mountains doesn’t touch any other mountains.
 * Given the heights of all the mountains in the range, can you figure out which mountain has the best
 * view? The mountains are all lined up in a straight line and spaced 1 unit apart.
 * Input Specifications
 * DATA31.txt (DATA32.txt for the second try) will contain 10 test cases. Each test case starts with an
 * integer N (1 ≤ N ≤ 10,000) indicating the number of mountains. The next line contains N integers ranging
 * from 1 to 10,000, representing the heights of the mountains in the range. Mountains are numbered
 * from 1 to N.
 * Output Specifications
 * For each test case, your program should output an integer corresponding to the mountain with the best
 * view. If there is a tie, output the smallest numbered mountain.
 * Sample Input (only 3 cases shown):
 * 3
 * 5 2 4
 * 5
 * 5 2 4 3 3
 * 5
 * 5 4 1 1 1
 * Sample Output:
 * 1
 * 3
 * 2
 * Explanation
 * In the first case, all three mountains have views of the entire range.
 */

class Mountain{
	int positionX; //position X start from 0
	int height;
	
	void setPositionX(int x) {
		positionX = x;
	}
	
	void setHeight(int h) {
		height = h;
	}
	
	int getPositionX() {
		return positionX;
	}
	
	int getHeight() {
		return height;
	}
	
}

class MountainView{
	static final int MAX_MOUNTAINS = 10000;

	Mountain[] mountains;
	boolean[][] visibility;
	
	MountainView(int nMountains) throws Exception{
		if (nMountains>MAX_MOUNTAINS) {
			System.out.println("Too many mountains.");
			throw new Exception("Too many mountains");
		}
		mountains = new Mountain[nMountains];
		if (mountains == null)
			throw new NullPointerException("can't initialize moutains");
		visibility = new boolean[nMountains][nMountains];
		for(int i=0;i<nMountains;i++) {
			mountains[i] = new Mountain();
			for(int j=0;j<nMountains;j++) {
				visibility[i][j] = true;
			}
		}
	}
	
	boolean setMountainHeight(int index, int height) {
		if (index>=mountains.length) return false;
		
		mountains[index].setHeight(height);
		return true;
	}
	
	/*
	 * calculate the y=ax+b,
	 * a=(y2-y1)/(x2-x1)
	 * b=(x2y1-x1y2)/(x2-x1)
	 * 
	 * a mountain is visible if the mountains between them are lower than the y=ax+b.
	 */
	void calculateView() {
		// i=x1, j=x2
		double a, b;
		int y1, y2;
		
		System.out.println("Start calculate view, mountains is "+mountains.length);
		for (int x1=0; x1<mountains.length-1; x1++) {
			y1 = mountains[x1].getHeight();
						
			for (int x2=x1+1; x2<mountains.length; x2++)
			{
				if (visibility[x1][x2] == false)
					continue;
				
				visibility[x1][x2] = visibility[x2][x1] = true;
				
				y2 = mountains[x2].getHeight();
				a = (double)(y2-y1)/(double)(x2-x1);
				b = (double)(x2*y1-x1*y2)/(double)(x2-x1);
				double y;
				
				for(int i=x2+1;i<mountains.length;i++) {
					y = a*(double)i + b;
					if (y < (double)mountains[i].getHeight()) {
						//mountain i is higher, it's visible to x1
						visibility[x1][i] = visibility[i][x1] = true;
					}
					else {
						//mountain is lower, it's invisible to x1
						visibility[x1][i] = visibility[i][x1] = false;
					}
				}
			}
		}
		System.out.println("Calculating view finished.");
	}
	
	int sumVisibleMountains(int n) {
		int sum = 0;
		if (n>mountains.length)
			return 0;
		for(int i=0;i<visibility[n].length;i++) {
			if(visibility[n][i] == true)
				sum++;
		}
		return sum;
	}
	int bestViewMountain() {
		int iBestOne = -1;
		int n, nMountainsVisible = -1;
		
		System.out.println("enter best view mountain.");
		for(int i=0; i<mountains.length; i++) {
			n = sumVisibleMountains(i);
			if(n > nMountainsVisible) {
				iBestOne = i;
				nMountainsVisible = n;
			}
		}
		System.out.println("exit best view mountain, the best view mountain is "+(iBestOne+1));
		return iBestOne;
	}
}

public class Problem3 {
	public static void main(String[] args) {
		String sInFilename, sOutFilename, s1, s2;
				
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}
		sInFilename = args[0];
		sOutFilename = new String(sInFilename+".out");
		BufferedReader in = null;
		int nMountains;
		ArrayList<Integer> winners = new ArrayList<Integer>();
		
		try {
			in = new BufferedReader(new FileReader(sInFilename));
			
			while(((s1=in.readLine())!=null) && ((s2=in.readLine())!=null)) {
				
				nMountains = Integer.parseInt(s1);
				MountainView mountview= new MountainView(nMountains);
				System.out.println("\nnumber of mountains is "+nMountains);
				Scanner sc = new Scanner(s2);
				for(int i=0; i<nMountains; i++) {
					try {
						mountview.setMountainHeight(i, sc.nextInt());
					}catch (Exception e) {
						break;
					}
				}
				sc.close();
				mountview.calculateView();
				winners.add(mountview.bestViewMountain()+1); //the result should start from 1
			}
			in.close();
			
		}catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			System.exit(0);	
		}
		
		//output to file
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			for (Integer winner:winners) {
				out.println(winner);
			}
			out.close();
		} catch(Exception e) {
			System.err.format("IOException: %s%n", e);
			System.exit(0);	
		} 
	
	}
}