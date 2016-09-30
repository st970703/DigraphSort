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
	private static HashMap<Integer, ArrayList<Node>> _nodeOutput = new HashMap<Integer, ArrayList<Node>>();
	//private static boolean gotoFlag = true;
	private static GraphType _graphType = GraphType.DAG;
	
	public static void main(String[] args) {

		storeNodes();

		calcDepth();

		sortOutput();
		
		
		testHM(0);
		printOutput();
	}

	private static void printOutput() {
		System.out.println(_graphType);
		List<Integer> keyList = new ArrayList<Integer>(_nodeOutput.keySet());
		Collections.sort(keyList);
		System.out.println(keyList.size());
		for (int i = 0; i < keyList.size(); i++) {
			ArrayList<Node> tempAL = _nodeOutput.get(new Integer(keyList.get(i)));
			System.out.println(tempAL.size());
			for (int n = 0; n < tempAL.size(); n++) {
				if (tempAL.get(n).isStrongComponent()) {
					ArrayList<Integer> temp = new ArrayList<Integer>(tempAL.get(n).getCycleNodesAsInt());
					temp.add(tempAL.get(n).getValue());
					Collections.sort(temp);
					StringBuilder tempString = new StringBuilder(); 
					for (int j = 0; j < temp.size(); j++) {
						tempString.append(temp.get(j).toString());
						if (j != temp.size()-1) {
							tempString.append(" ");
						}
					}

					System.out.print(tempString);
				} else {
					System.out.print(tempAL.get(n).getValue());
				}
				if (i == keyList.size()-1 && n == tempAL.size()-1) {
				} else {
					System.out.println();
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

				return new Integer(o1.getDepth()).compareTo(new Integer(o2.getDepth()));
			}
		});
		
		for (Node n : StratumByDepth) {
			int depth = n.getDepth();
			if (_nodeOutput.get(depth) == null) {
				_nodeOutput.put(depth, new ArrayList<Node>() );			
			} 
			if (!n.getIsRemoved()) {
				_nodeOutput.get(depth).add(n);
			}

			
			Collections.sort(_nodeOutput.get(depth), new Comparator<Node>() {
				@ Override
				public int compare(Node o1, Node o2) {

					return new Integer(o1.getValue()).compareTo(new Integer(o2.getValue()));
				}
			});
		}
	}

	private static void calcDepth() {
		ArrayList<Integer> keyToRemove = new ArrayList<Integer>(); 
		Set<Integer> keyset= _nodeHM.keySet();

		for (Integer key : keyset) {
			Node temp = _nodeHM.get(key);

			//test
			//System.out.println(temp.getValue()+".setDepth("+G2DSort(temp)+");");
			//G2DSort(temp);
			int tempDepth = G2DSort(temp);
			temp.setDepth(tempDepth);
			//test
			System.out.println(_nodeHM.get(key)+".get(key).setDepth(G2DSort(temp));"); testHM(1);
			if (temp.isStrongComponent()) {
				ArrayList<Integer> tempCycleList = temp.getCycleNodesAsInt();
				for(Integer i : tempCycleList) {
					if (i != temp.getValue()) {
						keyToRemove.add(i);
					}
				}
				resetAllFlags();
				tempDepth = G2DSort(temp);
				temp.setDepth(tempDepth);
			}
		}
		for (Integer i : keyToRemove) {
			_nodeHM.remove(i);
		}
	}
	
	private static void resetAllFlags() {
		ArrayList<Integer> keyToRemove = new ArrayList<Integer>(); 
		Set<Integer> keyset= _nodeHM.keySet();

		for (Integer key : keyset) {
			Node temp = _nodeHM.get(key);
			temp.removeFlag();
		}
	}

	private static int G2DSort(Node x) throws InvalidInputException {
		if (x.getFlag() == true) {
			_graphType = GraphType.nonDAG;
			throw new InvalidInputException(x);
		} 
		// set x.flag
		x.setFlag();
		 
		int max = -1;
		// goto control
		//gotoFlag = true;
		//while (gotoFlag == true ) {

			// This is line 5
			max = -1;
			ArrayList<Node> y = x.getPredecessor();
			/*if (y.isEmpty()) {
				break;
			}*/ 
			for (int i = 0; i < y.size(); i++) {
				try {
					int tmp = G2DSort(y.get(i));
					if (tmp > max) {
						max = tmp;
					}

					//i = -1;
					//if no exceptions, don't "goto"
					//gotoFlag = false;
				} catch (InvalidInputException iie) {
					Node xc = iie.getNode();
					if (!x.toString().equals(xc.toString()) ) {
						//test
						System.out.println("merge "+x+" into "+xc);
						// merge x into xc
						xc.merge(x);

						//gotoFlag = false;

						throw new InvalidInputException(xc);
					} else {
						System.out.println("iie.getNode().removeReflexiveArc();");
						// remove reflexive arc
						iie.getNode().removeReflexiveArc();
						// goto line 5, don't touch the flag
						//gotoFlag = true; 
					}
				}
			}
		//}		
		x.removeFlag();
		//gotoFlag = false;
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
				System.out.println("isRemoved() = "+temp.getIsRemoved());
				System.out.print("Strong Compornent = "+temp.isStrongComponent());System.out.println();
				if (temp.isStrongComponent()) {
					System.out.print("Cycles = "+temp.getCycleNodesAsInt());System.out.println();
					System.out.println("getMinCycleNode() = "+temp.getMinCycleNode());
				}

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
	private ArrayList<Node> _cycleNodes;
	private int _depth = -1;
	private boolean _flag = false;
	private boolean _done;
	private boolean _isRemoved = false;

	public boolean getIsRemoved() {
		return _isRemoved;
	}
	public void removeNode() {
		_isRemoved = true;
	}
	
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
	
	public boolean isStrongComponent() {
		if (_cycleNodes != null) {
			return true;
		}
		return false;
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

	public void merge(Node mergedNode) {
		
		ArrayList<Node> merged_predecessor = mergedNode.getPredecessor();
		ArrayList<Node> merged_sucessor = mergedNode.getSucessor();
		
		//test
		for (int j = 0; j < merged_predecessor.size(); j++) {
			Node temp = merged_predecessor.get(j);
			if (temp.getIsRemoved()) {
				System.out.println("line 335: try to merge "+temp.getValue()+" inside "+_value+" getIsRemoved() depth = "+temp.getIsRemoved()+" merged_predecessor = "+merged_predecessor);
			}
		}
		for (int j = 0; j < merged_sucessor.size(); j++) {
			Node temp = merged_sucessor.get(j);
			if (temp.getIsRemoved()) {
				System.out.println("line 341: try to merge "+temp.getValue()+" inside "+_value+" getIsRemoved() depth = "+temp.getIsRemoved()+" merged_sucessor = "+merged_sucessor);
			}
		}

		for (int j = 0; j < merged_predecessor.size(); j++) {
			Node temp = merged_predecessor.get(j);
			if (!this._predecessor.contains(temp)) {
				this._predecessor.add(temp);
			}
			
			merged_predecessor.remove(temp);
			j = -1;
		}
		for (int j = 0; j < merged_sucessor.size(); j++) {
			Node temp = merged_sucessor.get(j);
			if (!this._sucessor.contains(temp)) {
				this._sucessor.add(temp);
			}
			
			ArrayList<Node> reverseList = temp.getPredecessor();
			for (int k = 0; k < reverseList.size(); k++) {
				Node tempReverse = reverseList.get(k);
				if (tempReverse.getValue() == mergedNode.getValue()) {
					reverseList.remove(tempReverse);
					k = -1;
					if (!reverseList.contains(this)) {
						reverseList.add(this);
					}
				}
			}
			merged_sucessor.remove(temp);
			j = -1;
		}

		// add to cycle list
		if (this._cycleNodes == null) {
			_cycleNodes = new ArrayList<Node>();
			//test
			//_cycleNodes.add(this);
		}
		if (!this._cycleNodes.contains(mergedNode)) {
			this._cycleNodes.add(mergedNode);
		}
		if (mergedNode.isStrongComponent()) {
			ArrayList<Node> temp = mergedNode.getCycleNodes();
			for (int i = 0; i < temp.size(); i++) {
				Node tempNode = temp.get(i);
				if (!this._cycleNodes.contains(tempNode)) {
					this._cycleNodes.add(tempNode);
				}
			}
		}
		if (mergedNode.getDepth() > _depth) {
			_depth = mergedNode.getDepth();
		}
		
		mergedNode.removeFlag();
		mergedNode.removeNode();
	}
	
	// adder methods
	public void addPredecessor(Node previous) {
		_predecessor.add(previous);
	}

	public void addSucessor(Node next) {
		_sucessor.add(next);
	}

	// getter methods
	public int getMinCycleNode() {
		int minFromCycleNodeList =  Collections.min(this.getCycleNodesAsInt());
		return Math.min(minFromCycleNodeList, this.getValue());
	}
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
enum GraphType {
	DAG, nonDAG
}