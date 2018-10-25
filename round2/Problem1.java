package round2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;



class Panel {
	public final static char GLASS = 'G';
	public final static char WINDOW = 'W';
	public final static char UNKNOWN = 0;
	
	char attribute; //'G' or 'W' or 'U' (Unknown)
	int positionWidth;
	int positionFloor;
	
	Panel(){
		attribute = Panel.UNKNOWN;
	}
	
	void setAttribute(char c) {
		attribute = c;
	};
	
	char getAttribute() {
		return attribute;
	}
	
	void setPosition(int width, int floor) {
		positionWidth = width;
		positionFloor = floor;
	}
}

class Key {
	HashMap<String, Character> KeyTable;
	
	Key(){
		KeyTable = new HashMap<String, Character>();
	}
	
	void setKeyTable(String id, char c) {
		KeyTable.put(id, c);
	}
	
	static boolean isKeyRecord(String s) {
		return s.matches("[GW][GW]\\s[GW]");
	}
	char calculateUpperPanel(char left, char right) {
		String s = String.valueOf(left);
		s += right;
		
		return KeyTable.getOrDefault(s, 'U');
	}
}

class Tower {
	public static int MAX_WIDTH = 8;
	public static int MAX_FLOORS = 1000;
	public final static char PANEL_GLASS = 'G';
	public final static char PANEL_WINDOW = 'W';
	public final static char PANEL_UNKNOWN = 0;

	int nFloor, nWidth;
	char[][] panels;
	
	Key KeyTable;
	
	Tower(int width, int floors){
		panels = new char[floors][width];
		KeyTable = new Key();
		nFloor = floors;
		nWidth = width;
	}
	
	void setKeyTable(String s, char c) {
		KeyTable.setKeyTable(s,c);
	}
	
	void setFloorPanels(int iFloor, char[] width) {
		if((iFloor<0) || (iFloor>=panels.length) || width.length!=panels[iFloor].length)
			return;
		
		for(int i=0;i<panels[iFloor].length;i++)
			panels[iFloor][i]=width[i];
		
	}
	char[] getFloorPanels(int iFloor) {
		if((iFloor<0) || (iFloor>=panels.length))
			return null;	
		System.out.format("Floor %d plan:%s%n",iFloor+1, new String(panels[iFloor]));
		return panels[iFloor];
	}
	
	void setPanel(int iFloor, int iWidth, char attribute) {
		if ((iFloor<0) || (iWidth<0) || (iFloor >= panels.length) || (iWidth >= panels[0].length))
			return;
		panels[iFloor][iWidth] = attribute;
	}
	
	void generateFloorPlan() {
		for(int floor=1;floor<nFloor;floor++) {
			//start from 1st floor
			for(int width=0;width<nWidth;width++) {
				int left = (width-1+nWidth)%nWidth;
				int right = (width+1)%nWidth;
				panels[floor][width] =KeyTable.calculateUpperPanel(
						panels[floor-1][left], panels[floor-1][right]);
			}
		}
	}
}

public class Problem1 {
	public static void main(String[] args) {
		String sInFilename;
		
		if(args.length != 1) {
			System.out.println("Please add text file name in the command.");
			System.exit(0);
		}
		
		sInFilename = args[0];
		
		BufferedReader in = null;
		Scanner sc;
		Tower t = null;
		int width, floors=0;
		ArrayList<String> results = new ArrayList<String>();
		
		try {
			in = new BufferedReader(new FileReader(sInFilename));
			String s=in.readLine();
			while (s!=null && !s.equals("*")) {
				sc = new Scanner(s);
				
				//the frist line is tower width and floors
				width = sc.nextInt();
				floors = sc.nextInt();
				t = new Tower(width, floors);
				sc.close();
				
				while ((s=in.readLine())!=null)
				{
					if(s.equals("*")) {
						s = in.readLine();
						break;
					}
					if(Key.isKeyRecord(s)) {
						//the string is in format "[GW][GW]\\s[GW]".
						String key = s.substring(0, 2);
						char value = s.charAt(3);
						t.setKeyTable(key, value);
					}
					else { // the bottom floor
						t.setFloorPanels(0, s.toCharArray());
					}
					
				}
				if (t!=null)
					t.generateFloorPlan();
				results.add(new String(t.getFloorPanels(floors-1)));
			};

		}catch (Exception e){
			System.err.format("IOException: %s%n", e);
			System.exit(0);
		}
		
		//output to file
		try {
			String sOutFilename = new String(sInFilename+".out");

			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sOutFilename)));
			for (String s:results) {
				out.println(s);
			}
			out.close();
		} catch(Exception e) {
			System.err.format("IOException: %s%n", e);
			System.exit(0);	
		} 


	}
}