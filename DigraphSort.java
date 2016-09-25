import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DigraphSort {
	private static ArrayList<Integer> _nodeLog = new ArrayList<Integer>();
	private static HashMap<Integer, Node> _nodeHM = new HashMap<Integer, Node>();
	
	private static ArrayList<ArrayList<Integer>> _nodeOutput = new ArrayList<ArrayList<Integer>>();

	public static void main(String[] args) {

		storeNodes();

		testHM(0);
	
		//testG2D();
		
		calcDepth();
		System.out.println("Calculated depths:");
		testHM(1);
		
		testSort();
		
		testHM(1);
		//sortOutput();
		printOutput();
	}
	
	private static void printOutput() {
		System.out.println("DAG");
		System.out.println(_nodeOutput.size());
		for (int i = 0; i < _nodeOutput.size(); i++) {
			ArrayList<Integer> temp = _nodeOutput.get(i);
			for (int j = 0; j < temp.size(); j++) {
				if (j == 0) {
					System.out.println(temp.size());
				}
				if (i == _nodeOutput.size()-1 && j == temp.size()-1) {
					System.out.print(temp.get(j));
				} else {
					System.out.println(temp.get(j));
				}
			}
		}		
	}
	
	private static void testSort() {
		System.out.println("testSort():");
		// not yet sorted
	    List<Node> StratumByDepth = new ArrayList<Node>(_nodeHM.values());

	    Collections.sort(StratumByDepth, new Comparator<Node>() {
	    	@ Override
	    	public int compare(Node o1, Node o2) {
	            // write comparison logic here like below , it's just a sample
	            return new Integer(o1.getDepth()).compareTo(new Integer(o2.getDepth()));
	        }
	    });

	    int currentDepth = -1;
	    for (Node n : StratumByDepth) {
	    	System.out.print("node = "); n.printValue(); System.out.print("\t"); n.printDepth();
	    	System.out.println();
	    	if (n.getDepth() != currentDepth) {
	    		currentDepth = n.getDepth();
	    		_nodeOutput.add(new ArrayList<Integer>());
	    		_nodeOutput.get(currentDepth).add(n.getValue());
	    	} else if (n.getDepth() == currentDepth) {
	    		_nodeOutput.get(currentDepth).add(n.getValue());
	    	}
	    }
	    // sort the ArrayList of ArrayList
	    for (ArrayList<Integer> i : _nodeOutput) {
	    	Collections.sort(i);
	    }
	}
	
	/*private static void sortOutput() {
		
		Set<Integer> keyset= _nodeHM.keySet();
		for (Integer key : keyset) {
			Node temp = _nodeHM.get(key);
			Integer value = temp.getValue();
			int depth = temp.getDepth();
			if (!_nodeOutput.get(depth).contains(value)) {
				_nodeOutput.get(depth).add(value);
			}
		}
	}*/
	
	private static void calcDepth() {
		Set<Integer> keyset= _nodeHM.keySet();
		for (Integer key : keyset) {
			Node temp = _nodeHM.get(key);
			temp.setDepth(G2DSort(temp));
		}
	}
	
	/*public static void testG2D() {
		System.out.println("Inside testG2D(): ");
		Set<Integer> keyset= _nodeHM.keySet();
		for (Integer key : keyset) {
			Node temp = _nodeHM.get(key);
			System.out.println("Using key = "+key);
			System.out.println("G2DSort("+temp.toString()+") = "+ G2DSort(temp));
		}
		
	}*/
	
	private static int G2DSort(Node x) {
		// if x.flag is set
		int max = -1;
		ArrayList<Node> y = x.getPredecessor();
		for (Node i : y) {
			int tmp = G2DSort(i);
			if (tmp > max) {
				max = tmp;
			}
		}
		
		//remove x.flag
		return max+1;
	}
	
	private static void testHM(int mode){
		if (mode == 0) {
			//test
			System.out.println("_nodeLog: (length "+_nodeLog.size()+" )");
			for (int i = 0; i < _nodeLog.size(); i++) {
				System.out.print(_nodeLog.get(i)+" ");
			} System.out.println("");
			// get keyset value from map
			Set<Integer> keyset= _nodeHM.keySet();
			System.out.println(keyset.size()+" Keys");
			// check key set values //test
			System.out.println("Key set values are: " + keyset);	
			for (Integer key : keyset) {
				Node temp = _nodeHM.get(key);
				System.out.print("Value = "); temp.printValue(); System.out.println();
				temp.printPredecessor(); System.out.println();
				temp.printSucessor(); System.out.println();
				temp.printDepth(); System.out.println();
			}
		} else if (mode == 1) {
			// get keyset value from map
			Set<Integer> keyset= _nodeHM.keySet();
			System.out.println(keyset.size()+" Keys");
			// check key set values //test
			System.out.println("Key set values are: " + keyset);	
			for (Integer key : keyset) {
				Node temp = _nodeHM.get(key);
				System.out.print("Value = "); temp.printValue(); System.out.println();
				temp.printDepth(); System.out.println();
			}
		}
		
	}

	private static void storeNodes() {
		BufferedReader br = null;
		
		br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			String input = br.readLine();
			int totalArcs = Integer.parseInt(input);
			
			for (int i = 0; i < totalArcs; i++) {
				input = br.readLine();
				if (input != null && input != "" ) {
					String[] splitArray = input.split(" ");
					
					// make the nodes first
					Node temp;
					if (!_nodeLog.contains(new Integer(splitArray[0]) ) ) {
						_nodeLog.add(new Integer(splitArray[0]) );
						temp = new Node(new Integer(splitArray[0]) );
						_nodeHM.put(Integer.parseInt(splitArray[0]), temp);
					} 
					if (!_nodeLog.contains(new Integer(splitArray[1]) ) ) {
						_nodeLog.add(new Integer(splitArray[1]) );
						temp = new Node(new Integer(splitArray[1]) );
						_nodeHM.put(Integer.parseInt(splitArray[1]), temp);
					}
					// make the arcs
					_nodeHM.get(Integer.parseInt(splitArray[0])).addSucessor(_nodeHM.get(Integer.parseInt(splitArray[1])));
					_nodeHM.get(Integer.parseInt(splitArray[1])).addPredecessor(_nodeHM.get(Integer.parseInt(splitArray[0])));
				}
			}		
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Node {
	private int _value;
	private ArrayList<Node> _predecessor = new ArrayList<Node>();
	private ArrayList<Node> _sucessor = new ArrayList<Node>();
	private int _depth = -1;
	private boolean _flag;
	private boolean _done;

	// Overloading Constructors
	public Node(int value) {
		_value = value;
	}
	
	public Node(Node predecessor, int value) {
		_value = value;
		_predecessor.add(predecessor);
	}
	
	public Node(int value, Node sucessor) {
		_value = value;
		_sucessor.add(sucessor);
	}
	
	public Node(Node predecessor, int value, Node sucessor) {
		_value = value;
		_predecessor.add(predecessor);
		_sucessor.add(sucessor);
	}

	// adder methods
	public void addPredecessor(Node previous) {
		_predecessor.add(previous);
	}
	
	public void addSucessor(Node next) {
		_sucessor.add(next);
	}
	
	// getter methods
	public ArrayList<Node> getPredecessor() {
		return _predecessor;
	}
	
	public ArrayList<Node> getSucessor() {
		return _sucessor;
	}
	public int getDepth() {
		return _depth;
	}
	public int getValue() {
		return _value;
	}
	
	// private helper method
	private void updateDepth(int newD) {
		if (newD > _depth) {
			_depth = newD;
		}
	}
	
	// setter methods
	public void setDepth(int depth) {
		_depth = depth;
	}
	
	//test
	public void printValue() {
		System.out.print(_value);
	}
	public void printSucessor() {
		System.out.print("Sucessor = ");
		for (int i = 0; i < _sucessor.size(); i++) {
			_sucessor.get(i).printValue();
			System.out.print(", ");
		}
		if (_sucessor.size() == 0) {
			System.out.print("None, ");
		}
	}
	public void printPredecessor() {
		System.out.print("Predecessor = ");
		for (int i = 0; i < _predecessor.size(); i++) {
			_predecessor.get(i).printValue();
			System.out.print(", ");
		}
		if (_predecessor.size() == 0) {
			System.out.print("None, ");
		}
	}
	public void printDepth() {
		System.out.print("Depth = "+_depth);
	}
}

class StratumCompare implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        // write comparison logic here like below , it's just a sample
        return new Integer(o1.getDepth()).compareTo(new Integer(o2.getDepth()));
    }
}