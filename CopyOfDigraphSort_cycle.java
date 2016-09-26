import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class CopyOfDigraphSort_cycle {
	private static ArrayList<Integer> _nodeLog = new ArrayList<Integer>();
	private static HashMap<Integer, Node> _nodeHM = new HashMap<Integer, Node>();
	private static ArrayList<Integer> _cycleList = new ArrayList<Integer>();

	private static ArrayList<ArrayList<Integer>> _nodeOutput = new ArrayList<ArrayList<Integer>>();
	
	private static GraphType _graphType = GraphType.DAG;
	
	public static void main(String[] args) {

		storeNodes();
				
		calcDepth();

		sortOutput();

		printOutput();
		System.out.println("\t"+_cycleList);
	}

	private static void printOutput() {
		if (_graphType == GraphType.DAG) {
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
		} else if (_graphType == GraphType.nonDAG) {
			System.out.print("nonDAG");
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
					if (currentDepth < _nodeOutput.size()) {
						//test
						System.out.println("currentDepth = "+n.getDepth() );
						_nodeOutput.add(new ArrayList<Integer>());
						// test
						System.out.println("_nodeOutput.size() = "+_nodeOutput.size()+"_nodeOutput.get("+currentDepth+").add("+n.getValue()+");");
						_nodeOutput.get(currentDepth).add(n.getValue());
					}



				} else if (n.getDepth() == currentDepth ) {
					if (currentDepth < _nodeOutput.size()) {
						// test
						System.out.println("n.getDepth() == currentDepth == "+n.getDepth()+" == "+currentDepth);
						System.out.println("_nodeOutput.get("+currentDepth+").add("+n.getValue()+");");
						_nodeOutput.get(currentDepth).add(n.getValue());
					}
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
			int tempInt = G2DSort(temp );
			temp.setDepth(G2DSort(temp ));
		}
	}

	private static int G2DSort(Node x) {
		System.out.println(x.getValue()+".getFlag() = "+x.getFlag());
		//test
		//System.out.println("G2DSort("+x.getValue()+")");


		//}
		try {
			if (x.getFlag() == true) {
				//test
				//System.out.println(x.getValue()+".getFlag() == "+x.getFlag());
				System.out.println("throw new InvalidInputException("+x.getValue()+")");
				throw new InvalidInputException(x);
				/*if (!_cycleList.contains(new Integer(x.getValue()) )) {
				System.out.println("throw new InvalidInputException("+x.getValue()+")");
				_cycleList.add(new Integer(x.getValue()) );
			}*/
				//x.removeFlag();
				//return -2;
			} /*else {*/

			// set x.flag
			//System.out.println(x.getValue()+".setFlag()");
			x.setFlag();

			int max = -1;
			ArrayList<Node> y = x.getPredecessor();
			for (Node i : y) {

				int tmp = G2DSort(i);
				/*if (tmp == -2) {
				System.out.println("return -2;");
				return -2;
			}*/
				if (tmp > max) {
					max = tmp;
				}
			}

			System.out.println(x.getValue()+".removeFlag()");
			x.removeFlag();
			return max+1;
		} catch (InvalidInputException iie) {
			// TODO Auto-generated catch block
			_graphType = GraphType.nonDAG;
			if (!_cycleList.contains(iie.getNode().getValue() )) {

				_cycleList.add(iie.getNode().getValue() );
			}
			return -1;
		} 

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
			Collections.sort(_nodeLog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
@SuppressWarnings("serial")
class InvalidInputException extends Exception {
	Node cycle;
	String message = "nonDAG";
	public InvalidInputException(Node n) {
		cycle = n;
	}
	public String getMessage() {
		return message;
	}
	public Node getNode() {
		return cycle;
	}
}