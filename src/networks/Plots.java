package networks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class Plots extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final XYSeries degreeD = new XYSeries("Distribution degree");
    private final XYSeries theoryD = new XYSeries("Theoretical");
    
    private double[] theoreticDD,dd;
    private double[] ddd;
    private double[] tdd;
    public JPanel control;
    private String title;
    private int nodes;
	
    public Plots(String s,  double[] DD,  double[] theoreticalDD, int nodess, int firstK) {
        super(s);
        
        this.nodes = nodess;
		title = s;
		theoreticDD = theoreticalDD;
		dd = DD;
        
		addData(firstK);
        final ChartPanel chartPanel = createPanel(" for Erdos-Renyi model, "+Integer.toString(nodes)+ " nodes");
        this.add(chartPanel, BorderLayout.CENTER);
        control = new JPanel();
        this.add(control, BorderLayout.SOUTH);
	}
    
    public Plots(String s, double[] degDist, double[] tdegDist ){
        super(s);
		title = s;
		ddd = degDist;
		tdd = tdegDist;
		addData();
        final ChartPanel chartPanel = createPanel(" for Albert-Barabasi Model");
        this.add(chartPanel, BorderLayout.CENTER);
        control = new JPanel();
        this.add(control, BorderLayout.SOUTH);
    }
    
	private void addData(int start) {
		for (int i = start; i < dd.length; i++) degreeD.add( (int)i, dd[i] );
		for(int i=start;i<theoreticDD.length;i++)theoryD.add( (int)i, theoreticDD[i] );
	}
	
	private void addData() {
		for(int i=5;i<tdd.length-30;i++) {
			theoryD.add(Math.log((double) i ), Math.log( (double)tdd[i] ));
			degreeD.add(Math.log( (double) i ), Math.log( (double)ddd[i] ));
		}
	}
	
    private XYDataset getData() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(degreeD);
        xySeriesCollection.addSeries(theoryD);
        return xySeriesCollection;
    }
    
    
    private ChartPanel createPanel(String bonus) {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
            title+bonus, 
            "k", 
            "P(k)", 
            getData(),
            PlotOrientation.VERTICAL, 
            true, true, false);//legend, tooltips, urls
        
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        //create custom shapes
        Shape up = ShapeUtilities.createUpTriangle(0);
        Shape down = ShapeUtilities.createDownTriangle(0);

        //set colours and shapes with renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesShape(0, up);
        renderer.setSeriesShape(1, down);
        xyPlot.setRenderer(renderer);

        //return the panel with dimensions
        return new ChartPanel(jfreechart){
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 600);
            }
        };
    }
    
}
