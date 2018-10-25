package round2;

import java.awt.Point;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class Maze {
	final static int MAX_MAZE_SIZE = 1000;
	final static char EMPTY_SPACE = '.';
	final static char WALL = '#';
	final static char KEY = 'K';
	final static char TREASURE = 'T';
	final static char START_POINT = 'S';
	final static char PASSED_THROUGH = '*';
		
	char[][] maze_map;
	int maze_size;
	
	int nKeysFound;
	int nTreasuresFound;
	
	// Hashmap value is the number of keys a door require.
	// Hashmap key is the point of the door located.
	ConcurrentHashMap<Point, Integer> doors = new ConcurrentHashMap<Point, Integer>(); 

	
	Maze(int size){
		if(size>MAX_MAZE_SIZE || size<=0) return;
		maze_map = new char[size+2][size+2]; // add a wall around the maze
		for(int i=0;i<size+2;i++)
			for(int j=0;j<size+2;j++)
				maze_map[i][j] = WALL;
		maze_size = size;
		nKeysFound = nTreasuresFound = 0;
	}
	
	void printMazeMap() {
		for(int y=1;y<=maze_size;y++) {
			for(int x=1;x<=maze_size;x++)
				System.out.print(maze_map[y][x]);
			System.out.println();
		}
		System.out.println();
	}
	//x and y start from 0
	void setMaze(int x, int y, char value) {
		if(x<0 || x>=maze_size || y<0 || y>=maze_size)
			return;
		
		maze_map[y+1][x+1] = value;
	}
	/*
	 * Recursive method to traverse the map
	 */
	void traverse(int x, int y) {
		System.out.print(".");
		//printMazeMap();

		switch (maze_map[y][x]) {
		case KEY:
			//count the key, and continue the traverse
			nKeysFound++;
			maze_map[y][x]=EMPTY_SPACE;
			break;
		case TREASURE:
			//count the treasure and continue the traverse
			nTreasuresFound++;
			maze_map[y][x]=EMPTY_SPACE;
			break;
		case EMPTY_SPACE:
		case START_POINT:
			//continue the traverse
			break;
		case WALL:
		case PASSED_THROUGH:
			//go back to the previous point
			//System.out.println("|");
			return;
		default: // door
			if(maze_map[y][x]>'0' && maze_map[y][x]<='9') {
				if ((maze_map[y][x]-'0')<=nKeysFound) {
					//it's the door and the door can be opened
					maze_map[y][x] = EMPTY_SPACE;					
				}
				else {
					//it's the door, but we can't open it yet since the keys are not enough
					doors.put(new Point(x,y), maze_map[y][x]-'0');
					//System.out.println("|");
					return;
				}

			}
			else {//error
				//System.out.println("|");
				return;
			}
			break;
		}
		
		char c = maze_map[y][x];
		maze_map[y][x] = PASSED_THROUGH;
		traverse(x+1, y);
		
		//System.out.format("(%d,%d):%c -> ",x, y, c);
		traverse(x, y+1);
		
		//System.out.format("(%d,%d):%c -> ",x, y, c);
		traverse(x-1, y);
		
		//System.out.format("(%d,%d):%c -> ",x, y, c);
		traverse(x, y-1);
		//maze_map[y][x] = c;
	}
	
	void findTreasures() {
		int y, x = maze_size;
		
		//locate the starting point first
		for(y=1;y<=maze_size;y++){
			for (x=1;x<=maze_size;x++)
				if (maze_map[y][x] == START_POINT) break;
			if (maze_map[y][x] == START_POINT) break;
		}
		
		if (x>maze_size || y>maze_size) {
			System.out.println("Can't find the starting point");
			return;
		}
		System.out.format("Start the traverse from point (%d,%d).%n", x, y);
		traverse(x,y);
		
		boolean doors_opened = false;
		do {
			doors_opened = false;
			System.out.println("keys found:"+nKeysFound);
			System.out.println("doors to open:"+doors);
			
			Iterator<Map.Entry<Point, Integer>> it = doors.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry<Point, Integer> pair = it.next();
			    if(pair.getValue()<=nKeysFound) {
					x = (int)pair.getKey().getX();
					y = (int)pair.getKey().getY();
					System.out.format("Start the traverse from point (%d,%d).%n", x, y);
					traverse(x,y);
					it.remove();
					doors_opened = true;
				}
			}
		} while (doors_opened == true);
		System.out.println("Traversed map:");
		printMazeMap();
			
	}
	int getTreasuresFound() {
		return nTreasuresFound;
	}
	
	/* Find the treasures method
	 *  @param no parameter
	 *  @return int the number of treasures found
	 *  @exception exception no exception thrown
	 */
	int findTreasures2() {
		// previous_points saves the previous steps when traversing the maze.
		Stack<Point> previous_points = new Stack<Point>();
		
		// Hashmap key is the number of keys a door require.
		// Hashmap value is the point of the door located.
		
		int y, x = maze_size;
		
		//locate the starting point first
		for (y=1;y<maze_size;y++)
			for(x=1;x<maze_size;x++)
				if (maze_map[y][x] == START_POINT) break;
		
		if (x==maze_size || y==maze_size) {
			System.out.println("Can't find the starting point");
			return 0;
		}
		
		// start the traverse, the path order is direction (1,0), (0,1), (-1,0) and then (0, -1)
		Point current = new Point();
		while (true) {
			previous_points.push(new Point(x,y));
			
			x=x++;
			current.setLocation(x, y);
			
			boolean go_previous = false;
			

			switch (maze_map[y][x]) {
				case EMPTY_SPACE:
					//continue the traverse
					break;
				case TREASURE:
					//count the treasure and continue the traverse
					nTreasuresFound++;
					break;
				case WALL:
					//go back to the previous point
					go_previous = true;
					break;
				case KEY:
					//count the key, and continue the traverse
					nKeysFound++;
					break;
				default: // door
					if(maze_map[y][x]>0 && maze_map[y][x]<10) {
						if (maze_map[y][x]<=nKeysFound) {
							//it's the door and the door can be opened
							maze_map[y][x] = EMPTY_SPACE;					
						}
						else {
							//it's the door, but we can't open it yet since the keys are not enough
							doors.put(current, maze_map[y][x]-'0');
							go_previous = true;
						}

					}
					else {
						System.out.format("Wrong point(%d,%d):%s",x, y, maze_map[y][x]);
						go_previous = true;
					}
					break;
			}
			if(go_previous) {
				
			}
			else {//continue to traverse
				
			}
			
			
		}
		
		
		
	}
}

public class Problem2{
	
	public static void main(String[] args) {
		String sInFilename;
		
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}
		
		sInFilename = args[0];
		
		BufferedReader in = null;
		Maze m = null;
		int size=0;
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		try {
			in = new BufferedReader(new FileReader(sInFilename));
			String s;
			while ((s=in.readLine())!=null) {
				size = Integer.parseInt(s);
				m = new Maze(size);
				
				for(int i=0; i<size; i++) {
					char[] arow = in.readLine().toCharArray();
					for(int j=0;j<arow.length;j++) {
						m.setMaze(j, i, arow[j]);
					}					
				}
				if (m!=null)
					m.findTreasures();
				results.add(m.getTreasuresFound());
				System.out.println("********Treasures found:"+m.getTreasuresFound());
				
			};
	
		}catch (Exception e){
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}
		
		//output to file
		try {
			String sOutFilename = new String(sInFilename+".out");
	
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			for (Integer i:results) {
				out.println(i);
			}
			out.close();
		} catch(Exception e) {
			System.err.format("IOException: %s%n", e);
			System.exit(0);	
		} 
	
}
}