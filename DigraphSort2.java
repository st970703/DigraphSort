import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DigraphSort2 {
	private static ArrayList<Integer> _nodeLog = new ArrayList<Integer>();
	private static HashMap<Integer, Node> _nodeHM = new HashMap<Integer, Node>();
	private static ArrayList<Node> _process = new ArrayList<Node>();
	private static ArrayList<ArrayList<Integer>> _nodeOutput = new ArrayList<ArrayList<Integer>>();
	private static boolean gotoFlag = true;
	
	public static void main(String[] args) {

		storeNodes();

		//test
		System.out.println("testHM(0);");
		testHM(0);

		calcDepth();

		sortOutput();
		
		System.out.println("testHM(0);");
		testHM(0);
		//printOutput();
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

	private static void sortOutput() {
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

	private static void calcDepth() {
		Set<Integer> keyset= _nodeHM.keySet();
		for (Integer key : keyset) {
			Node temp = _nodeHM.get(key);
			temp.setDepth(G2DSort(temp));
		}
	}

	private static int G2DSort(Node x) throws InvalidInputException {
		System.out.println("G2DSort(Node "+x.getValue()+") : "+"x.getFlag() = "+x.getFlag()+" ; _process = "+_process);
		if (x.getFlag() == true) {
			System.out.println("throw InvalidInputException("+x.getValue()+"x); _process = "+_process  );
			throw new InvalidInputException(x);
		} 
		_process.add(x);
		// set x.flag
		x.setFlag();
		 
		int max = -1;
		// goto control
		gotoFlag = true;
		while (gotoFlag == true ) {

			ArrayList<Node> y = x.getPredecessor();
			if (y.isEmpty()) {
				break;
			} 
			for (Node i : y) {
				try {
					//gotoFlag = true;
					int tmp = G2DSort(i);
					if (tmp > max) {
						max = tmp;
					}
					_process.remove(i);
					//if no exceptions, don't "goto"
					gotoFlag = false;
				} catch (InvalidInputException iie) {
					System.out.println("Caught (InvalidInputException iie)  "+iie.getNode().getValue()+" at x = "+x.getValue()+"\n current _process = "+_process );
					Node xc = iie.getNode();
					if (i.getValue() != xc.getValue() ) {
						System.out.println("InvalidInputException "+xc.getValue()+" "+_process);
						// merge x into xc
						xc.merge(_process, _nodeLog);
						_process.clear();
						gotoFlag = false;
						throw new InvalidInputException(xc);
					} else {
						System.out.println(i.getValue()+".getValue() == "+xc.getValue()+".getValue()");
						// remove reflexive arc
						iie.getNode().removeReflexiveArc();
						// goto line 5, don't touch the flag
						gotoFlag = true; 
					}
				}
			}
			x.removeFlag();
		}		
		// remove from _process
		if (_process.contains(x)) {
			_process.remove(x);
			System.out.println("_process.remove(x); "+_process+" "+x.getValue());
		}
		gotoFlag = false;
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
				System.out.print("\nValue = "); temp.printValue(); System.out.println();
				System.out.print("Cycles = "+temp.getCycleNodesAsInt());System.out.println();
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
	private ArrayList<Node> _cycleNodes = new ArrayList<Node>();
	private int _depth = -1;
	private boolean _flag = false;
	private boolean _done;

	// Overloading Constructors
	public Node(int value) {
		_value = value;
		_cycleNodes.add(this);
	}

	public Node(Node predecessor, int value) {
		_value = value;
		_cycleNodes.add(this);
		_predecessor.add(predecessor);
	}

	public Node(int value, Node sucessor) {
		_value = value;
		_cycleNodes.add(this);
		_sucessor.add(sucessor);
	}

	public Node(Node predecessor, int value, Node sucessor) {
		_value = value;
		_cycleNodes.add(this);
		_predecessor.add(predecessor);
		_sucessor.add(sucessor);
	}
	
	public boolean hasCycleNodes() {
		if (_cycleNodes.size() > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<Integer> getCycleNodesAsInt() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (Node i : this._cycleNodes ) {
			Integer tempInt = new Integer(i.getValue());
			if (!temp.contains(tempInt) ) {
				temp.add(tempInt);
			}
		}
		Collections.sort(temp);
		return temp;
	}
	
	public ArrayList<Integer> getPredecessorsAsInt() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (Node i : this._predecessor ) {
			Integer tempInt = new Integer(i.getValue());
			if (!temp.contains(tempInt) ) {
				temp.add(tempInt);
			}
		}
		Collections.sort(temp);
		return temp;
	}

	public boolean equals(Node xc) { 
		ArrayList<Integer> xc_Predecessors = xc.getPredecessorsAsInt();
		ArrayList<Integer> this_Predecessors = this.getPredecessorsAsInt();
		
	    Collections.sort(xc_Predecessors);
	    Collections.sort(this_Predecessors);      
	    return xc_Predecessors.equals(this_Predecessors);
	}

	public void merge(ArrayList<Node> mergedList, ArrayList<Integer> nodeLog ) {
		// remove deplicates
		Set<Node> mergedSet = new TreeSet<Node>(mergedList);
		mergedList = new ArrayList<Node>(mergedSet);

		for (Node i : mergedList) {
			if (i.getValue() != this.getValue() ) {
				ArrayList<Node> merged_predecessor = i.getPredecessor();
				ArrayList<Node> merged_sucessor = i.getSucessor();

				for (Node j : merged_predecessor) {
					if (!this._predecessor.contains(j)) {
						this._predecessor.add(j);
					}
					merged_predecessor.remove(j);
				}
				for (Node j : merged_sucessor) {
					if (!_sucessor.contains(j)) {
						this._sucessor.add(j);
					}
					merged_sucessor.remove(j);
				}
				if (merged_sucessor.size() != 0 | merged_predecessor.size() != 0) {
					System.out.println(this.getValue()+" not fully merged with "+i.getValue()+" !!!");
				}
				// add to cycle list
				if (!this._cycleNodes.contains(i)) {
					this._cycleNodes.add(i);
				}
				// remove from record
				nodeLog.remove(i.getValue());
			}
		}
		Collections.sort(nodeLog);
	}
	
	// adder methods
	public void addPredecessor(Node previous) {
		_predecessor.add(previous);
	}

	public void addSucessor(Node next) {
		_sucessor.add(next);
	}

	// getter methods
	public ArrayList<Node> getCycleNodes() {
		return _cycleNodes;
	}
	
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

	public boolean getFlag() {
		return _flag;
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
	public void setFlag() {
		_flag = true;
	}
	public void removeFlag() {
		_flag = false;
	}
	public void removeReflexiveArc() {
		if (_predecessor.contains(this) ) {
			_predecessor.remove(this);
		}
		if (_sucessor.contains(this) ) {
			_sucessor.remove(this);
		}
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
	
	//Edited by Darius
	public String toString(){
		return String.valueOf(_value);
	}
}

@SuppressWarnings("serial")
class InvalidInputException extends IllegalArgumentException {
	private Node _cycle;
	String message = "nonDAG";
	public InvalidInputException(Node n) {
		_cycle = n;
	}
	public Node getNode() {
		return _cycle;
	}
	public String getMessage() {
		return message;
	}
}