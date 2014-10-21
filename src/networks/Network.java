package networks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Network {
	private int networkSize;

    //Kleinberg Small World
    private double neighbourhood;
    //Erdos-Renyi
    private double wiringProbability;
    private int nEdge = 0;
    public ArrayList<Integer> degree = new ArrayList<Integer>();
    //Barabasi Albert
    private int m, initial_connects, finalSize;
    //for files
    private String filename;
    public int max = 0;
    
    /**
     * Constructors
     * they should become the corresponding constructors at subclasses
     * **/
    //Erdos-Renyi
	public Network(int size, double probability) {
		networkSize = size;
		wiringProbability = probability;
	}
	//Watts-Strogatz
	public Network(int size, double neighbou, double probability){
		neighbourhood = neighbou;
		wiringProbability = probability;
		networkSize = size;
	}
	//Barabasi-Albert
	public Network(int size, int mu, int initialC, int finalNodes){
		networkSize = size;
		m = mu;
		initial_connects = initialC;
		finalSize = finalNodes;
	}
	//constructor for all other crap
	public Network(String filen){
		this.filename = filen;
	}
	
	/*
	 * Useful functions. Average Path Length and Mean Clustering Coefficient 
	 * */
	public double clusteringCoeff(Graph<Integer,String> graph){
		ArrayList<Integer> vertices = new ArrayList<Integer>(graph.getVertices());
		int count = 0;
		double totaldegree=0;
		for(int v : vertices){
			int n = graph.getNeighborCount(v);
			if( n > 2 ){
				ArrayList<Integer> neighbors = new ArrayList<Integer>(graph.getNeighbors(v));
	            for (int i=0;i<n;i++){
	            	int w = neighbors.get(i);
	            	for (int j=i+1;j<n;j++){
	            		int x = neighbors.get(j);
	            		if(
	            			(graph.containsEdge(v+":"+x) || graph.containsEdge(x+":"+v)) && 
	            			(graph.containsEdge(w+":"+x) || graph.containsEdge(x+":"+w)) && 
	            			(graph.containsEdge(v+":"+w) || graph.containsEdge(w+":"+v)) 
	            		){
	            			count++;
	            		}
	            	}
	            }

	            double possible_edges = (n*(n - 1))/2.0;
	            totaldegree += possible_edges;
			}

		}
		return (double)count/totaldegree;
	}
	
	public double meanClusteringCoeff(Graph<Integer,String> graph){
		double coefficient = 0;
		ArrayList<Integer> vertices = new ArrayList<Integer>(graph.getVertices());

		for(int v : vertices){
			int n = graph.getNeighborCount(v);
            double edge_count = 0;
			if( n > 2 ){
				ArrayList<Integer> neighbors = new ArrayList<Integer>(graph.getNeighbors(v));
	            for (int i=0;i<n;i++){
	            	int w = neighbors.get(i);
	            	for (int j=i+1;j<n;j++){
	            		int x = neighbors.get(j);
	            		edge_count+= graph.isNeighbor(w, x) ? 1 : 0;
	            	}
	            }

	            double possible_edges = (n*(n - 1))/2.0;
	            coefficient += (double) edge_count / possible_edges;
			}

		}
		//average clustering coefficient
		return coefficient/graph.getVertexCount();
	}
	
	
	public double averagePathLength(Graph<Integer,String> graph, int type){
		ArrayList<Integer> vert = new ArrayList<Integer>(graph.getVertices());
		int n = vert.size(), count = 0, sumPathL = 0;
		Number pathL = 0;
		if(type == 2) count = 1;
		while(true){
			for(int i = 0;i<n;i++){
				pathL = new UnweightedShortestPath<Integer, String>(graph).getDistance(count,vert.get(i));
				sumPathL += (Integer) pathL;
				if( max< (Integer) pathL ) max = (Integer) pathL;
			}
			count++;
			if(count == n) break;
		}

		return ( (double) sumPathL/( n*(n-1) ) );
	}
	
	public int[][] toMatrix(Graph<Integer, String> graph){
		return null;
	}
	
	/*
	 * Graph Constructors. These should become subclasses
	 * */
	public Graph<Integer, String> txtNetwork(String nextwork) throws IOException{
		SparseGraph<Integer, String> graph = new SparseGraph<Integer, String>();
    	BufferedReader reader = null;
    	try {
			reader = new BufferedReader(new FileReader(nextwork));
	    	String line = null;
	    	//insert the nodes
	    	while((line = reader.readLine()) != null ){
	    		String[] parts = line.split(" ");
	    		int node1 = Integer.parseInt(parts[0]), node2 = Integer.parseInt(parts[1]);
	    		if( !graph.containsVertex(node1) ) graph.addVertex( node1 );
	    		if( !graph.containsVertex(node2) ) graph.addVertex( node2 );
	    		if( !graph.containsEdge(node1+":"+node2 ) ) graph.addEdge( node1+":"+node2, node1, node2, EdgeType.UNDIRECTED );
	    	}
	    	reader.close();
	    	//add edges
		} catch (IOException e) {
			e.printStackTrace();
		}
    	reader.close();
		for(int i = 1;i < graph.getVertexCount();i++){
			degree.add( graph.degree( i ) );
		}
	    return graph;
	}

	public Graph<Integer, String> smallWorldFromTxt() throws FileNotFoundException{
	    UndirectedSparseGraph<Integer, String> graph = new UndirectedSparseGraph<Integer, String>();
    	BufferedReader reader = null, reader2 = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			reader2 = reader;
	    	String line = null;
	    	//insert the nodes
	    	int nodeCounter = 0;
	    	while((line = reader2.readLine()) != null ){
		    	graph.addVertex(nodeCounter);
		    	nodeCounter++;
	    	}
	    	reader2.close();
	    	//add edges
    		int thisV = 0;
    		reader = new BufferedReader(new FileReader(filename));
	    	while((line = reader.readLine()) != null ){
	    		char[] tmp = line.toCharArray();
	    		for(int i=0;i<tmp.length;i++){
	    			int thatV = i;
	    			if(tmp[i] == '1' && !graph.containsEdge(thisV+":"+thatV) && !graph.containsEdge(thatV+":"+thisV)){
	    				graph.addEdge(thisV+":"+thatV, thisV,thatV);
	    			}
	    		}
	    		thisV++;
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graph;
	}
	
	public Graph<Integer,Integer> ErdosRenyi() {
	    UndirectedSparseGraph<Integer, Integer> graph = new UndirectedSparseGraph<Integer, Integer>();

		//Add the Vertices
		for(int i=0; i<networkSize; i++) {
			graph.addVertex(i);
		}
		//get a list with the Vertices to manipulate
        List<Integer> list = new ArrayList<Integer>(graph.getVertices());
		//add edges with our method
        for (int i = 0; i < networkSize; i++) {
			int v_i = list.get(i);
			for (int j = i+1; j < networkSize; j++) {
				int v_j = list.get(j);
				double rand = Math.random();
				if (rand < wiringProbability && i!=j) {
					graph.addEdge(nEdge++, v_i, v_j, EdgeType.UNDIRECTED);
				}
			}
		}
        
		//Add the degree of each node
		for(int i = 0;i < networkSize;i++){
			degree.add( graph.degree( i ) );
		}
		
		return graph;
	}

	public Graph<Integer,String> smallWorld() {
	    UndirectedSparseGraph<Integer, String> graph = new UndirectedSparseGraph<Integer, String>();

		//Add the Vertices
		for(int i = 0; i<networkSize;i++){
			graph.addVertex( i );
		}
		
		//get a list with the Vertices to manipulate
		List<Integer> list = new ArrayList<Integer>(graph.getVertices());
		
		//add edges with our method
		for(int i = 0; i<networkSize;i++){
			int v_i = list.get(i);
			for(int j=i+1;j<networkSize;j++){
				int v_j = list.get(j);
				if( Math.abs(i-j)%(networkSize -1 - neighbourhood/2) <= neighbourhood/2 && (i-j)!=0 && i!=j ){
					graph.addEdge(v_i+" to "+v_j, v_i, v_j, EdgeType.UNDIRECTED );
				}else{
					double rand = Math.random();
					if( rand < wiringProbability && i!=j ){
						graph.addEdge(v_i+" to "+v_j, v_i, v_j, EdgeType.UNDIRECTED );
					}
				}
				
			}
		}
		
		return graph;
	}
	
	public Graph<Integer, String> barabasiAlbert(){
	    UndirectedSparseGraph<Integer, String> graph = new UndirectedSparseGraph<Integer, String>();

		//Add the Vertices
		for(int i=0; i<networkSize; i++) {
			graph.addVertex(i);
		}

		//get a list with the Vertices to manipulate
        List<Integer> list = new ArrayList<Integer>(graph.getVertices());
		//add edges initial edges
        int connectCounter = 0;
        for (int i = 0; i < networkSize; i++) {
        	connectCounter = graph.degree(i);
			int v_i = list.get(i);
	        for(int j = 0; j < networkSize; j++) {
				int v_j = list.get(j);
				if(graph.degree(j) >= initial_connects || i==j){
					continue;
				}else if(connectCounter < initial_connects){
					graph.addEdge(v_i+" to "+v_j, v_i, v_j, EdgeType.UNDIRECTED );
					connectCounter++;
				}else{
					break;
				}
	        }
        }
        System.gc();
        List<Integer> updatedList = new ArrayList<Integer>(graph.getVertices());

        //add new nodes and connect them according to their degree
        for(int i=networkSize;i<finalSize;i++){
        	graph.addVertex(i);
			//we choose a random number and initialize it's variables
			int newConnectCounter = 0, sumDegrees = 0;
			//sum the degree of the network
			for(int deg = 0;deg<=i;deg++) sumDegrees += graph.degree(deg);
			//now we need to add m edges for the new node
			updatedList = new ArrayList<Integer>(graph.getVertices());
			//we need to sort the list because JUNG adds node with spatial parameters
			Collections.sort(updatedList);
			int this_node = updatedList.get(i);

			while(newConnectCounter<m){
				double rand = Math.random();
				//we need to shuffle so the first m nodes don't dominate
				Collections.shuffle(updatedList);
				for(int j=0;j<=i;j++){
					int v_j = updatedList.get(j);
					double chance = (double) graph.degree(j)/sumDegrees;
					if( 
						rand > chance && v_j != this_node && 
						!graph.containsEdge(this_node+" to "+v_j) && 
						!graph.containsEdge(v_j +" to "+this_node)
					){
						graph.addEdge(this_node+" to "+v_j, this_node, v_j, EdgeType.UNDIRECTED );
						newConnectCounter++;
					}
					if(newConnectCounter == m) break;
				}
			}
        }
        
		//Add the degree of each node
		for(int i = 0;i < finalSize;i++){
			degree.add( graph.degree( i ) );
		}
		return graph;
	}


}
