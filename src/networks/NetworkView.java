package networks;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseMultigraph;
@SuppressWarnings("unused")
public class NetworkView {
	static Graph<Integer, Integer> ErdosRenyi;
	static Graph<Integer, String> WattsStrogatz, AlbertBarabasi, smallW, txt1, txt2, txt3;
	
    private static double[] degreeDist, theoreticalDegreeDist;

    private static ArrayList<Integer> tmpList = new ArrayList<Integer>();
    private static double[] barabasiPower, theoreticalBarabasiP;
    
	private static Graph<Integer, String> graphPlot;

    
	public static void main(String[] args) throws IOException {
    	Network er = new Network(100, 0.03);
    	Network small = new Network(100, 4, 0.015);
    	Network power = new Network(8, 4, 2, 100);
    	Network smallTxt = new Network("smallWorldExample.txt");
    	Network text1 = new Network("Network1.txt");
    	Network text2 = new Network("Network2.txt");
    	Network text3 = new Network("Network3.txt");
    	
    	ErdosRenyi = er.ErdosRenyi();
    	WattsStrogatz = small.smallWorld();
    	AlbertBarabasi = power.barabasiAlbert();
    	smallW = smallTxt.smallWorldFromTxt(); 
    	txt1 = text1.txtNetwork("Network1.txt");
    	txt2 = text2.txtNetwork("Network2.txt");
    	txt3 = text3.txtNetwork("Network3.txt");
    	
//    	generateGraph();
//		generateDegreePlots(150, 0.03);
		generateDegreeBarabasi(10000, 4, 2);
//    	System.out.println("Clustering coeff for small world txt "+smallTxt.clusteringCoeff(smallW));
//    	System.out.println("Average Clustering coeff for small world txt "+smallTxt.meanClusteringCoeff(smallW));

//    	System.out.println("Average path length for small world txt "+smallTxt.averagePathLength(smallW,1));
//    	System.out.println("Diameter "+smallTxt.max);
//    	generateGraph2();
//    	System.out.println("Clustering coeff for txt1 "+text1.clusteringCoeff(txt1));
//    	System.out.println("Clustering coeff for txt2 "+text2.clusteringCoeff(txt2));
//    	System.out.println("Clustering coeff for txt3 "+text3.clusteringCoeff(txt3));
    }
	
	private static double calcDegreeDist(Network net, Graph<Integer, String> graph){
		double degreedist = 0.0;
		ArrayList<Integer> tmlist = net.degree;
		Collections.sort(tmlist); // Sort the arraylist
		int max = tmlist.get(tmlist.size() - 1);
		double[] degree = new double[max+1];
		for(int i=0;i<tmlist.size();i++) degree[tmlist.get(i)] = degree[tmlist.get(i)] + 1;
		for(int i=0;i<degree.length;i++)degreedist +=degree[i];
		for(int i=0;i<degree.length;i++) degree[i] = degree[i]/graph.getVertexCount();
		return (double)degreedist/degree.length;
	}
	
	private static void generateDegreePlots(final int nodes, final double prob){
		Network temp = new Network(nodes, prob);
		temp.ErdosRenyi();
		tmpList = temp.degree;
		Collections.sort(tmpList); // Sort the arraylist
		int max = tmpList.get(tmpList.size() - 1);
		theoreticalDegreeDist = new double[max+1];
		degreeDist = new double[max+1];
		for(int i=0;i<tmpList.size();i++) degreeDist[tmpList.get(i)] = degreeDist[tmpList.get(i)] + 1;
		for(int k = 0;k<theoreticalDegreeDist.length;k++)theoreticalDegreeDist[ k ] = combinations(nodes - 1,k)*Math.pow(prob, k)*Math.pow(1-prob, nodes - 1 - k);
		for(int i=0;i<degreeDist.length;i++) degreeDist[i] = degreeDist[i]/nodes;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				//create the Scatter-plot
				final Plots modelRun = new Plots(
						"Degree Distribution with p="+prob, 
						degreeDist,
						theoreticalDegreeDist,
						nodes,
						0);
				modelRun.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				modelRun.pack();
				modelRun.setLocationRelativeTo(null);
				modelRun.setVisible(true);
			}
		});
		
	}
	
	private static void generateDegreeBarabasi(final int nodes, final int startConnections, final int nextConnections){
		//for some reason if starting <= 3 we get at an infinite loop.
		//note that it must be the check if we reached m.
		final int starting =  (int) 4;
		Network temp = new Network(starting, startConnections, nextConnections, nodes);
		temp.barabasiAlbert();
		tmpList = temp.degree;
		Collections.sort(tmpList);
		int max = tmpList.get(tmpList.size() - 1);
		barabasiPower = new double[max+1];
		theoreticalBarabasiP = new double[max+1];
		
		for(int i=0;i<tmpList.size();i++){
			barabasiPower[tmpList.get(i)] = barabasiPower[tmpList.get(i)] + 1;
		}
		
		for(int k=6;k<max+1;k++) theoreticalBarabasiP[k-1] = (double) (2*nextConnections*nextConnections)/(k*k);
		for(int i=0;i<barabasiPower.length;i++) barabasiPower[i] = barabasiPower[i]/nodes;

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				//create the Scatter-plot
				final Plots modelRun = new Plots(
						"Degree Distribution", 
						barabasiPower,theoreticalBarabasiP);
				modelRun.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				modelRun.pack();
				modelRun.setLocationRelativeTo(null);
				modelRun.setVisible(true);
			}
		});

	}
	
	private static long combinations(int n, int k) {
		long coeff = 1L;
		for (int i = n - k + 1; i <= n; i++) {
			coeff *= i;
		}
		for (int i = 1; i <= k; i++) {
			coeff /= i;
		}
		return coeff;
	}
	
	@SuppressWarnings({ "static-access", "rawtypes" })
	private static void generateGraph(){
    	NetworkView view = new NetworkView(); //We create our graph in here
    	Layout<Integer, Integer> layout = new KKLayout<Integer, Integer>(view.ErdosRenyi);
    	layout.setSize( new Dimension(2000,2000) ); // sets the initial size of the layout space
    	VisualizationViewer<Integer,Integer> vv = new VisualizationViewer<Integer,Integer>(layout);
    	vv.setPreferredSize(new Dimension(600,600)); //Sets the viewing area size
    	
    	NetworkView view2 = new NetworkView(); 
    	Layout<Integer, String> layout2 = new CircleLayout<Integer, String>(view2.WattsStrogatz);
    	layout2.setSize( new Dimension(2000,2000) ); 
    	VisualizationViewer<Integer, String> vv2 = new VisualizationViewer<Integer, String>(layout2);
    	vv2.setPreferredSize(new Dimension(600,600)); 

    	NetworkView view3 = new NetworkView(); 
    	Layout<Integer, String> layout3 = new CircleLayout<Integer, String>(view3.AlbertBarabasi);
    	layout3.setSize( new Dimension(2000,2000) ); 
    	VisualizationViewer<Integer,String> vv3 = new VisualizationViewer<Integer,String>(layout3);    	
    	vv3.setPreferredSize(new Dimension(600,600)); 
    	
      	NetworkView view4 = new NetworkView(); 
    	Layout<Integer, String> layout4 = new CircleLayout<Integer, String>(view4.smallW);//
    	layout4.setSize( new Dimension(2000,2000) ); 
    	VisualizationViewer<Integer,String> vv4 = new VisualizationViewer<Integer,String>(layout4);    	
    	vv4.setPreferredSize(new Dimension(600,600)); 
    	
    	// Create a graph mouse and add it to the visualization component
    	DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
    	DefaultModalGraphMouse gm2 = new DefaultModalGraphMouse();
    	DefaultModalGraphMouse gm3 = new DefaultModalGraphMouse();
    	DefaultModalGraphMouse gm4 = new DefaultModalGraphMouse();
    	
    	JMenuBar menuBar = new JMenuBar();
    	JMenu modeMenu = gm.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu.setText("Mouse Mode");
    	modeMenu.setIcon(null); // I'm using this in a main menu
    	modeMenu.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar.add(modeMenu);
    	gm.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	JMenuBar menuBar2 = new JMenuBar();
    	JMenu modeMenu2 = gm2.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu2.setText("Mouse Mode");
    	modeMenu2.setIcon(null); // I'm using this in a main menu
    	modeMenu2.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar2.add(modeMenu2);
    	gm2.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm2.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	JMenuBar menuBar3 = new JMenuBar();
    	JMenu modeMenu3 = gm3.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu3.setText("Mouse Mode");
    	modeMenu3.setIcon(null); // I'm using this in a main menu
    	modeMenu3.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar3.add(modeMenu3);
    	gm3.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm3.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	JMenuBar menuBar4 = new JMenuBar();
    	JMenu modeMenu4 = gm4.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu4.setText("Mouse Mode");
    	modeMenu4.setIcon(null); // I'm using this in a main menu
    	modeMenu4.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar4.add(modeMenu4);
    	gm4.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm4.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	vv.setGraphMouse(gm); 
    	vv2.setGraphMouse(gm2); 
    	vv3.setGraphMouse(gm3);
    	vv4.setGraphMouse(gm4);
    	
    	JFrame frame = new JFrame("Erdos Renyi Random Graph");
    	frame.getContentPane().add(vv); 
    	frame.setJMenuBar(menuBar);
    	frame.pack();
    	frame.setVisible(true); 
    	
    	JFrame frame2 = new JFrame("Retarded Watts-Strogatz model");
    	frame2.getContentPane().add(vv2); 
    	frame2.setJMenuBar(menuBar2);
    	frame2.pack();
    	frame2.setVisible(true);      	
    	
    	JFrame frame3 = new JFrame("Albert Barabasi");
    	frame3.getContentPane().add(vv3); 
    	frame3.setJMenuBar(menuBar3);
    	frame3.pack();
    	frame3.setVisible(true);  
    	
    	JFrame frame4 = new JFrame("smallWorld txt");
    	frame4.getContentPane().add(vv4); 
    	frame4.setJMenuBar(menuBar4);
    	frame4.pack();
    	frame4.setVisible(true);  
	}	@SuppressWarnings({ "static-access", "rawtypes" })
	
	private static void generateGraph2(){
    	NetworkView view2 = new NetworkView(); 
    	Layout<Integer, String> layout2 = new CircleLayout<Integer, String>(view2.txt1);
    	layout2.setSize( new Dimension(2000,2000) ); 
    	VisualizationViewer<Integer, String> vv2 = new VisualizationViewer<Integer, String>(layout2);
    	vv2.setPreferredSize(new Dimension(600,600)); 

    	NetworkView view3 = new NetworkView(); 
    	Layout<Integer, String> layout3 = new CircleLayout<Integer, String>(view3.txt2);
    	layout3.setSize( new Dimension(2000,2000) ); 
    	VisualizationViewer<Integer,String> vv3 = new VisualizationViewer<Integer,String>(layout3);    	
    	vv3.setPreferredSize(new Dimension(600,600)); 
    	
      	NetworkView view4 = new NetworkView(); 
    	Layout<Integer, String> layout4 = new CircleLayout<Integer, String>(view4.txt3);//smallW
    	layout4.setSize( new Dimension(2000,2000) ); 
    	VisualizationViewer<Integer,String> vv4 = new VisualizationViewer<Integer,String>(layout4);    	
    	vv4.setPreferredSize(new Dimension(600,600)); 
    	
    	// Create a graph mouse and add it to the visualization component
    	DefaultModalGraphMouse gm2 = new DefaultModalGraphMouse();
    	DefaultModalGraphMouse gm3 = new DefaultModalGraphMouse();
    	DefaultModalGraphMouse gm4 = new DefaultModalGraphMouse();
    	
    	
    	JMenuBar menuBar2 = new JMenuBar();
    	JMenu modeMenu2 = gm2.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu2.setText("Mouse Mode");
    	modeMenu2.setIcon(null); // I'm using this in a main menu
    	modeMenu2.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar2.add(modeMenu2);
    	gm2.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm2.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	JMenuBar menuBar3 = new JMenuBar();
    	JMenu modeMenu3 = gm3.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu3.setText("Mouse Mode");
    	modeMenu3.setIcon(null); // I'm using this in a main menu
    	modeMenu3.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar3.add(modeMenu3);
    	gm3.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm3.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	JMenuBar menuBar4 = new JMenuBar();
    	JMenu modeMenu4 = gm4.getModeMenu(); // Obtain mode menu from the mouse
    	modeMenu4.setText("Mouse Mode");
    	modeMenu4.setIcon(null); // I'm using this in a main menu
    	modeMenu4.setPreferredSize(new Dimension(80,20)); // Change the size
    	menuBar4.add(modeMenu4);
    	gm4.setMode(DefaultModalGraphMouse.Mode.PICKING);
    	gm4.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
    	
    	vv2.setGraphMouse(gm2); 
    	vv3.setGraphMouse(gm3);
    	vv4.setGraphMouse(gm4);
    	
    	
    	JFrame frame2 = new JFrame("text 1");
    	frame2.getContentPane().add(vv2); 
    	frame2.setJMenuBar(menuBar2);
    	frame2.pack();
    	frame2.setVisible(true);      	
    	
    	JFrame frame3 = new JFrame("text 2");
    	frame3.getContentPane().add(vv3); 
    	frame3.setJMenuBar(menuBar3);
    	frame3.pack();
    	frame3.setVisible(true);  
    	
    	JFrame frame4 = new JFrame("text 3");
    	frame4.getContentPane().add(vv4); 
    	frame4.setJMenuBar(menuBar4);
    	frame4.pack();
    	frame4.setVisible(true);  
	}
	
	static class GraphFactory implements Factory<Graph<Integer,String>> {
		public Graph<Integer,String> create() {
			return new SparseMultigraph<Integer,String>();
		}
	}
	
	static class VertexFactory implements Factory<Integer> {
		int a = 0;
		public Integer create() {
			return a++;
		}
		
	}
	static class EdgeFactory implements Factory<String> {
		char aa = 'a';
		public String create() {
			return Character.toString(aa++);
		}
		
	}
    
}
