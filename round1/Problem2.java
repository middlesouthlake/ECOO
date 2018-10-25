package round1;
/*
 * Problem 2: Chocolate Chewsday
 * Author: Andrew Seidel
 * At the local candy factory, every Tuesday, there is a contest for whoever can come up with the best new
 * chocolate. To make a decision on whether or not each chocolate is a winner, there is a panel of impartial
 * judges that come in from the community. The judges are given the following criteria for judging:



 * Packaging (P), up to 1 point
 * Flavour (F), up to 2 points
 * Minimal ingredients (G), up to 3 points
 * A score, S, is given to each chocolate from each judge assigned to that chocolate (0 ≤ S ≤ 6). There will
 * be a random number of judges, J, assigned to each chocolate (1 ≤ J ≤ 100).
 * Your task is to declare the winner based on the highest total score in the competition. If there is a tie for
 * highest total score, it can sometimes be broken using the total scores for P, F and G (try G first, then F,
 * then P).
 * The competition is not really fair because some chocolates get more judges than others. But that’s life at
 * the candy factory.
 * Input Specifications
 * DATA21.txt (DATA22.txt for the second try) will contain 10 competitions. The first line of each
 * competition will contain a single integer, N, to indicate the number of chocolates in the competition
 * (1 ≤ N ≤ 100). For each of the N chocolates, there will be J+1 lines in the file. The 1 st line is the name of
 * the chocolate (a single word with no spaces) and the next J lines will contain the judges’ scores
 * (1 ≤ J ≤ 100). Each score will be contained on a single line, starting with the letter J followed by the 3
 * integers P, F, and G separated by spaces. Each competition ends with an asterisk (*).
 * 
 * 
 * Output Specifications
 * Output the name of the winner. If there is more than one winner, print out all winners on a single line
 * separated by commas (order does not matter – i.e., an output of A, B is the same as B, A).
 * Sample Input (only 1 case shown)
 * 2
 * C1
 * J 0 1 1
 * J 0 1 0
 * J 1 0 0
 * C2
 * J 1 2 3
 * 
 * Sample Output
 * C2
 * (See next page for another example)Sample Input (only 2 cases shown)
 * 4
 * ChocolateOfChocolates
 * J 0 2 2
 * J 0 1 2
 * J 1 2 0
 * Choco-Fun
 * J 1 2 3
 * J 1 2 0
 * ChocolateHaven
 * J 1 2 0
 * J 0 2 3
 * J 1 0 1
 * ChocolatesRock
 * J 1 2 1
 * J 1 2 0
 * J 1 2 0
 * 
 * 1
 * ChocolateFilledCandy
 * J 0 0 0
 * 
 * Sample Output
 * ChocolatesOfChocolates
 * ChocolateFilledCandy
 * Explanation
 * For the first competition, there is a tie between Chocolate of Chocolates, Chocolate Haven, and
 * Chocolates Rock. We had to then look at the G values, which were tied for Chocolate of Chocolates and
 * Chocolate Haven. Consequently, we had to then check the F value. At this point, Chocolate of Chocolates
 * has the higher value.
 */

import java.io.*;
import java.util.*;

/* 
 * Solution:
 * Data File <- Competitions <- Chocolates <- Scores
 */
class Score{
	final static int MAX_JUDGERS = 100;
	final static int MAX_JUDGE_DIMENSIONS = 3;
	final static int[] POINTS_OF_DIMENSIONS = {1, 1, 1};
	int [] record;
	
	Score(){
		record = new int [MAX_JUDGE_DIMENSIONS];
	};
	
	
	int totalPoints(int[] judge) {
		if (judge.length<MAX_JUDGE_DIMENSIONS)
			return -1;
		int sum, i;
		for (sum=i=0;i<MAX_JUDGE_DIMENSIONS;i++)
			sum += judge[i]*POINTS_OF_DIMENSIONS[i];
		return sum;
	}
	boolean addJudge(int[] judge) {
/*		if (totalPoints(record)<totalPoints(judge)) {
			record = judge.clone();
		}
*/
		if (judge.length < MAX_JUDGE_DIMENSIONS)
			return false;
		for (int i=0;i<judge.length;i++)
			record[i] += judge[i];
		return true;
		
	};
	
	int getMaxPoints() {
		return totalPoints(record);
	};
	
	int[] getMaxRecord() {
		return record;
	}
	
	boolean isLargerThan(Score sc) {
		int [] rec = sc.getMaxRecord();
		if (rec.length<MAX_JUDGE_DIMENSIONS)
			return false;
		if (totalPoints(record)>totalPoints(rec))
			return true;
		if (totalPoints(record) == totalPoints(rec)) {
			for (int i=MAX_JUDGE_DIMENSIONS-1;i>=0;i--) {
				if (record[i] > rec[i])
					return true;
				else if (record[i]<rec[i])
					return false;
			}
			return false;
		}
		// smaller or equal
		return false;		
	}
	boolean isEqualTo(Score sc) {
		int[] rec = sc.getMaxRecord();
		
		if (rec.length<MAX_JUDGE_DIMENSIONS)
			return true;
		if (totalPoints(record) == totalPoints(rec))
		{
			return Arrays.equals(record, rec);
		}
		return false;
	}
}

class Chocolate{
	String sChocolateName;
	Score score;
	
	Chocolate(){
		sChocolateName = null;
		score =new Score();
	}
	void setName(String sName) {
		sChocolateName = new String(sName);
	}
	String getName() {
		return sChocolateName;
	}
	
	void setPoints(int[] judge) {
		score.addJudge(judge);
	}
	
	int getPoints() {
		return score.getMaxPoints();
	}
	
	int[] getScoreRecord() {
		return score.record;
	}
	
	Score getScore() {
		return score;
	}
	
	
}
class Competition{
	String sBestChocolateName;
	int nChocolates;
	HashMap<String, Chocolate> chocolates;
	
	Competition(){
		sBestChocolateName = null;
		nChocolates = 0;
		chocolates = new HashMap<String, Chocolate>();
	}
	void addTotalChocolates(int n){
		nChocolates = n;
	}
	
	int newChocolagte(String sChocolateName) {
		Chocolate c = new Chocolate();
		c.setName(sChocolateName);
		chocolates.put(sChocolateName, c);
		return 0;
	};
	
	boolean addScore(String sChocolateName, int[] judge) {
		Chocolate c = chocolates.get(sChocolateName);
		if (c==null)
			return false;
		c.setPoints(judge);
		return true;
	};
	
	ArrayList<String> getWinners() {
		ArrayList<String> winnerNames = new ArrayList<String>();
		Chocolate winner=null;
		for (Chocolate c : chocolates.values()) {
			if ((winner == null) || c.getScore().isLargerThan(winner.getScore())) {
				winner = c;
				winnerNames.clear();
			}
			else if (c.getScore().isEqualTo(winner.getScore())) {
				winnerNames.add(c.getName());
			}
		}
		winnerNames.add(winner.getName());
		return winnerNames;
	};
}

public class Problem2 {
	
	public static void main(String[] args) {
		
		String sInFilename, sOutFilename, s;
		
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}
		sInFilename = args[0];
		sOutFilename = new String(sInFilename+".out");
		
		ArrayList<Competition> competitions = new ArrayList<Competition>();
		BufferedReader in = null;
		PrintWriter out = null;
		
		
		try {
			in = new BufferedReader(new FileReader(sInFilename));

			int nChocolate;
			String sChocolateName;;
			boolean bBreakWithErr=false;
			
			if ((s = in.readLine())==null) {
				in.close();
				System.out.println("Empty File:"+sInFilename);
				System.exit(1);
			}
			while (true) {
				// 0. new a new round of competition
				Competition competition = new Competition();
				
				//1. read N as the number of chocolates
				nChocolate = Integer.parseInt(s);
				competition.addTotalChocolates(nChocolate);
				
				// 2.1 read the chocolate name
				if ((s = in.readLine())==null) {
					bBreakWithErr = true;
					break;
				}
				
				
				for(int iChocolate=0;iChocolate<nChocolate;iChocolate++) {
					//2.2 set the Chocolate name
					competition.newChocolagte(s);
					sChocolateName = s;
					
					boolean bEndCompetition = false;
					//3. read the Chocolate judge scores
					while ((s = in.readLine()) != null) {
						if (s.equals("*")) {//end of the competition
							bEndCompetition = true;
							break;
						}
						else if (Character.toUpperCase(s.toCharArray()[0]) !='J') {//not the judge score, next chocolate name
							break;
						}
						// new score line, staring with 'J'
						Scanner sc = new Scanner(s);
						int[] judge=new int[3];
						sc.next(); //skip 'J'
						try {
							for (int j=0;j<3;j++)
								judge[j] = sc.nextInt();
							competition.addScore(sChocolateName, judge);
						} catch (Exception e) {
							bBreakWithErr = true;
							sc.close();
							break;
						}
						sc.close();
					}
					
					//1. read '*' 
					if (bEndCompetition == true) break;							
					
					if (s==null || bBreakWithErr==true) {
						//2. or s is null (read to end of file)
						//3. or exception happened during reading score
						bBreakWithErr = true;
						break;
					}						
				}
				competitions.add(competition);
				
				if ((s = in.readLine())==null)
					break; // end of file
			}
			if (in!=null) in.close();
		}catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			System.exit(0);
		} 
			

		
		// output to the destination file
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			
			for(Competition cmpt : competitions) {
				ArrayList<String> winners = cmpt.getWinners();
				String sout = new String();
				for (String w : winners) {
					sout += w+",";
				}
				if(sout.length()>0)
					out.println(sout.substring(0,sout.length()-1));
			}

		} catch (Exception e) {
			System.err.format("Output file IOException: %s%n", e);
		} finally { 
			if (out != null) out.close();
		}
		
	}// end of main
}