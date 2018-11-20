package br.edu.uni7.perceptron;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.opencsv.CSVReader;

public class Application {
	
	private static double[][] EMPTY_DOUBLE_MATRIX = {};

	// Tabela Diagn�stico
	// Press�o - Glicemia - Diagn�stico (diabetes)
//	private double[][] values = {
//			{10, 70, -1},
//			{18, 80, +1},
//			{17, 70, +1},
//			{12, 80, -1},
//			{11, 90, -1},
//			{16, 90, +1},
//			{10, 80, -1}
//	};
//	private double[] startValues = {-20., 20.};
//	private double[] graphRange = {0, 30, 0, 100};

	// Tabela E
	private double[][] values = { { 0, 0, -1 }, { 0, 1, -1 }, { 1, 0, -1 }, { 1, 1, +1 } };
	private double[] startValues = {-1., 1.};
	private double[] graphRange = {-1, 2, -1, 2};
	
	// Setosa-versicolor - comprimento da p�tala e largura da p�tala
	//private double[][] values = loadFile("setosa-versicolor.txt");
	//private double[] startValues = {-1, 1.};
	//private double[] graphRange = {0, 5.5, 0, 2};

	private Perceptron perceptron = new Perceptron(2, 0.2, startValues[0], startValues[1]);

	public Application() {
		JFrame frame = new JFrame("Perceptron");

		XYSeriesCollection dataset = createDataset();
		// Create chart
//	    JFreeChart chart = ChartFactory.createLineChart(
//	        "Perceptron", // Chart title
//	        "X", // X-Axis Label
//	        "Y", // Y-Axis Label
//	        dataset
//	        );

		JFreeChart chart = ChartFactory.createScatterPlot("Perceptron", "X-Axis", "Y-Axis", dataset);

		XYPlot plot = chart.getXYPlot();
		plot.getDomainAxis().setRange(graphRange[0], graphRange[1]);
		plot.getRangeAxis().setRange(graphRange[2], graphRange[3]);
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// "0" is the line plot
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(0, false);

		// "1" is the scatter plot for class -1
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesShapesVisible(1, true);
		
		// "1" is the scatter plot for class 1
		renderer.setSeriesLinesVisible(2, false);
		renderer.setSeriesShapesVisible(3, true);

		plot.setRenderer(renderer);

		ChartPanel panel = new ChartPanel(chart);
		frame.setContentPane(panel);

		SwingUtilities.invokeLater(() -> {
			frame.setAlwaysOnTop(true);
			frame.pack();
			frame.setSize(800, 600);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});

		new Thread() {
			public void run() {
				try {
//					int valuesIndex = 0;
					while (true) {
						sleep(250);
						int valuesIndex = (int) (Math.random() * (values.length - 1));
						System.out.println("valuesIndex: " + valuesIndex);
						
						double[] inputs = getInputsFromValue(values[valuesIndex]);
						double expected = getClassFromValue(values[valuesIndex]);
						
						double output = perceptron.train(inputs, expected);
						
						System.out.println("input: " + arrayToString(inputs) + " expected: " + expected + " output: " + output);
						
						updateLineSeries();
//						if (valuesIndex + 1 >= values.length) {
//							valuesIndex = 0;
//						} else {
//							valuesIndex++;
//						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public double[] getInputsFromValue(double[] value) {
		double[] inputs = new double[values[0].length - 1];
		for (int i = 0; i < inputs.length; i++) {
			inputs[i] = value[i];
		}
		return inputs;
	}
	
	public double getClassFromValue(double[] value) {
		double clazz = value[value.length - 1];
		return clazz;
	}

	private XYSeriesCollection createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		dataset.addSeries(getLineSeries());

		XYSeries series1 = new XYSeries("Inputs 1");
		XYSeries series2 = new XYSeries("Inputs 2");
		
		for (double[] input: values) {
			if (input[2] == 1) {
				series1.add(input[0], input[1]);
			} else {
				series2.add(input[0], input[1]);
			}
		}

		dataset.addSeries(series1);
		dataset.addSeries(series2);

		return dataset;
	}

	private XYSeries lineSeries;

	private XYSeries getLineSeries() {
		if (lineSeries == null) {
				
			double[] yPoints = calcLinePoints();

			lineSeries = new XYSeries("Reta de Classifica��o");

			lineSeries.add(yPoints[0], yPoints[1]);
			lineSeries.add(yPoints[2], yPoints[3]);
		}
		return lineSeries;
	}
	
	private double[] calcLinePoints() {
		// reta => x1 + x2 + xB = 0
		// x1 = x * w0
		// x2 = y * w1
		// xb = bias * wBias
		
		double[] weights = perceptron.getWeights();
		
		// Limiares de x0 e y1 de acordo com o tamanho do gr�fico
		
		double x0 = graphRange[0];
		double y0 = - ((x0 * weights[0] + Perceptron.BIAS * perceptron.getBiasWeight()) / weights[1]);
		
//		double y1 = - (weights[0] + (Perceptron.BIAS * perceptron.getBiasWeight())) / weights[1];
		
//		double y1 = graphRange[2];
//		double x1 = - ((y1 * weights[1] + Perceptron.BIAS * perceptron.getBiasWeight()) / weights[0]);
		
		double x1 = graphRange[1];
		double y1 = - ((x1 * weights[0] + Perceptron.BIAS * perceptron.getBiasWeight()) / weights[1]);
		
		double[] points = new double[4];
		points[0] = x0;
		points[1] = y0;
		points[2] = x1;
		points[3] = y1;
		
		System.out.println("Weights: " + arrayToString(weights));
		System.out.println("Points: " + arrayToString(points));
		
		return points;
	}

	private void updateLineSeries() {
		XYSeries lineSeries = getLineSeries();

//		int[] input1 = { 0, 0 };
//		double y1 = perceptron.apply(input1);
//		int[] input2 = { 1, 1 };
//		double y2 = perceptron.apply(input2);
		
		double[] yPoints = calcLinePoints();
		
		lineSeries.clear();
		lineSeries.add(yPoints[0], yPoints[1]);
		lineSeries.add(yPoints[2], yPoints[3]);
	}
	
	
	public String arrayToString(int[] values) {
		StringBuilder b = new StringBuilder();
		b.append("[");
		
		for (int i : values) {
			b.append(i);
			b.append(",");	
		}
		
		b.append("]");
		
		return b.toString();
	}
	
	
	public String arrayToString(double[] values) {
		StringBuilder b = new StringBuilder();
		b.append("[");
		
		for (double i : values) {
			b.append(i);
			b.append(",");	
		}
		
		b.append("]");
		
		return b.toString();
	}
	
	public static double[][] loadFile(String fileName) {
		List<double[]> data = new ArrayList<>();
		List<String> classes = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
			String[] line;
			while ((line = reader.readNext()) != null) {
				double[] dataLine = new double[line.length];
				for (int i = 0; i < line.length - 1; i++) {
					dataLine[i] = Double.parseDouble(line[i]);
				}
				double classValue;
				if (classes.contains(line[line.length - 1])) {
					classValue = classes.indexOf(line[line.length - 1]);
				} else {
					classValue = classes.size();
					classes.add(line[line.length - 1]);
				}
				// Se � �ndice 0, ent�o classe � -1. Sen�o, classe � 1.
				if (classValue == 0) {
					classValue = -1;
				} else {
					classValue = 1;
				}
				dataLine[line.length - 1] = classValue;
				
				data.add(dataLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toArray(EMPTY_DOUBLE_MATRIX);
	}
	
	public static void main(String[] args) throws Exception {
		new Application();

//		
//		double[] chart1 = {0., y1};
//		double[] chart2 = {1., y2};
//		
//
//		double phase = 0;
//		double[][] initdata = getSineData(phase);
//
//		// Create Chart
//		final XYChart chart = QuickChart.getChart("Simple XChart Real-time Demo", "Radians", "Sine", "sine",
//				chart1, chart2);
//
//		// Show it
//		final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
//		sw.displayChart();

//		while (true) {
//
//			phase += 2 * Math.PI * 2 / 20.0;
//
//			Thread.sleep(100);
//
//			final double[][] data = getSineData(phase);
//
//			javax.swing.SwingUtilities.invokeLater(new Runnable() {
//
//				@Override
//				public void run() {
//
//					chart.updateXYSeries("sine", data[0], data[1], null);
//					sw.repaintChart();
//				}
//			});
//		}

	}

//	private static double[][] getSineData(double phase) {
//
//		double[] xData = new double[100];
//		double[] yData = new double[100];
//		for (int i = 0; i < xData.length; i++) {
//			double radians = phase + (2 * Math.PI / xData.length * i);
//			xData[i] = radians;
//			yData[i] = Math.sin(radians);
//		}
//		return new double[][] { xData, yData };
//	}
}