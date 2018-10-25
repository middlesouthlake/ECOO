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

class FamilyComparator implements Comparator<int[]>{

	@Override
	public int compare(int[] o1, int[] o2) {
		for(int i=0;i<o1.length && i<o2.length;i++) {
			if(o1[i] == o2[i]) continue;
			return o1[i]-o2[i];
		}
		return o1.length-o2.length;
	}
	
}
class Family{
	final static int MODULO = 1000000007;

	ArrayList<int[]> familyMembers;
	
	Family(){
		familyMembers = new ArrayList<int[]>();
		familyMembers.add(new int[] {0});//adding root 
	}
	
	Family(int capacity) {
		familyMembers = new ArrayList<int[]>(capacity);
		familyMembers.add(new int[] {0});//adding root 
	}
	
	void reinitialize() {
		familyMembers.clear();
		familyMembers.add(new int[] {0});//adding root 
	}
	static int[] toId(String stringId) {
		if(stringId==null || stringId.length()==0) return null;
		
		String[] sa = stringId.split("\\.");
		int[] id =new int[sa.length];
		for(int j=0;j<id.length;j++)
			id[j] = Integer.parseInt(sa[j]);
		return id;
	}
	void hasSibling(String sId) {
		int[] id = toId(sId);
		if(id == null) return;
		Collections.binarySearch(familyMembers, id, new FamilyComparator());
	}
	
	static boolean isSibling(int[] id1, int[] id2) {
		if(id1==null || id2==null || id1.length<2 || id2.length<2 || id1.length!=id2.length) 
			return false;
		for(int i=0; i<id1.length-1; i++) {
			if(id1[i] != id2[i]) return false;
		}
		return true;
	}
	static boolean isParent(int[] parent, int[] child) {
		if(parent==null || child==null) return false;
		if(parent.length<2 || child.length<2) return false;
		if(parent.length >= child.length) return false;
		
		for(int i=0;i<parent.length;i++)
			if(parent[i]!=child[i]) return false;
		return true;
	}
	boolean addFamilyMember(String stringId) {

		int[] id = toId(stringId);
		
		if(id==null) return false;
		
		int rt = Collections.binarySearch(familyMembers, id, new FamilyComparator());
		if(rt<0) {
			//try to remove the redundant sibling or parents
			int pos = -rt-1;
							
			if(pos<familyMembers.size()) {
				// sibling is bigger than this member, no need to add this member
				if(isSibling(id, familyMembers.get(pos))) return true; 
			}
			
			//this member is bigger, replace the current one
			if(isSibling(id, familyMembers.get(pos-1))) {
				familyMembers.set(pos-1, id); 
				return true;
			}
			
			if(pos<familyMembers.size()) {
				// child exists, no need to add parent
				if(isParent(id, familyMembers.get(pos))) return true; 
			}
			
			//if the pos-1 is parent, replace it with this child
			if(isParent(familyMembers.get(pos-1), id)){
				familyMembers.set(pos-1, id);
				return true;
			}
			
			//otherwise, add this new member 
			familyMembers.add(pos, id);
		}
		return true;//found a duplicated one
	}
	
	ArrayList<int[]> getFamilyMembers(){
		return familyMembers;
	}
	
	
	void printFamily() {
		for(int[] id : familyMembers) {
			System.out.println(Arrays.toString(id));
		}
	}
	int minSizeFamily() {
		//printFamily();
		int[] m1, m2=familyMembers.get(0);
		int size = familyMembers.size();
		int sum = 1; //start from root
		
		for(int i=1;i<size;i++) {
			m1 = m2;
			m2 = familyMembers.get(i);
			int j=0;
			while(j<m1.length && j<m2.length && m1[j]==m2[j]) j++;
			//now m1[j] and m2[j] should be different
			if(j>=m1.length) {
				sum = (sum+m2[j])%MODULO;
			}
			else 
				sum = (sum+m2[j]-m1[j])%MODULO;
			
			for(j++;j<m2.length;j++) {
				sum = (sum+m2[j])%MODULO;
			}
		}
		System.out.println("minFamilySize: "+sum);
		return sum;
	}
}
public class Problem2 {
	
	public static void main(String[] args) {
		if(args.length<1) {
			System.out.println("Please add input file name");
			System.exit(0);
		}
		
		BufferedReader in;
		ArrayList<Integer> result = new ArrayList<Integer>();
		int n;
		String s;
		//String[] sa;
		//FamilyNode root = new FamilyNode(new String[] {"0"});
		
		Family family = new Family();
	
		try {
			in = new BufferedReader(new FileReader(args[0]));
			s = in.readLine();
			while(s!=null) {
				n = Integer.parseInt(s);
				for(int i=0;i<n;i++) {
					s = in.readLine();
					family.addFamilyMember(s);
				}
				
				int size = family.minSizeFamily();
				//System.out.println(size);
				result.add(size);
				family.reinitialize();
				s = in.readLine();
			//input.sort((String[]a, String[]b)->a.length-b.length);//sorting according the length
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
			for(Integer i:result) {
				out.println(i);
			}
			out.close();
		}catch (Exception e) {
			System.err.format("IOException: %s%n", e);
			e.printStackTrace(System.out);
			System.exit(0);
		}
	}
}
