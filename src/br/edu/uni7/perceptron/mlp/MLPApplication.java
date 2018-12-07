package br.edu.uni7.perceptron.mlp;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import br.edu.uni7.perceptron.SurfaceDemo;

public class MLPApplication {

	// Tabela XOR
	private double[][] inputs = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
	private double[][] expected = { { 0 }, { 1 }, { 1 }, { 0 } };
	
	private double learningRate = 0.3;
	
//	private Chart chart;

	private MultiLayerPerceptron mlp = new MultiLayerPerceptron(2, 3, 1, learningRate);
	
	private Mapper mapper = new Mapper() {
		@Override
		public double f(double x, double y) {
//			return x * Math.sin(x * y);
			return mlp.eval(new double[]{x, y})[1][0];
		}
	};
	
	public MLPApplication() {
//		drawChart();
		int iterations = 5000;
		
		for (int i = 0; i < iterations; i++) {
//			for (int j = 0; j < inputs.length; j++) {
			double[] error = mlp.train(inputs, expected);

//			System.out.println("Entrada: " + vec2str(input) + " Esperado: " + vec2str(expected[j]) + " Saída: "
//					+ vec2str(output) + " Erro: " + vec2str(error));
			System.out.println(i + " Erro: " + vec2str(error));
//			}
			if ((i % (iterations / 5)) == 0) {
				drawChart();
			}
		}
		for (int i = 0; i < inputs.length; i++) {
			double[] input = inputs[i];
			double[] output = mlp.eval(input)[1];
			
			double error = expected[i][0] - output[0];
			
			System.out.println("Entrada: " + vec2str(input) + " Esperado: " + vec2str(expected[i]) + " Saída: "
					+ vec2str(output) + " Erro: " + error);
		}
		drawChart();
	}
	
	private void drawChart() {
		try {
			AnalysisLauncher.open(new SurfaceDemo(mapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Scatter getScatterFromInputs(double[][] inputs) {
		Coord3d[] points = new Coord3d[inputs.length];
		Color[] colors = new Color[inputs.length];

		double x;
		double y;
		double z;

		for (int i = 0; i < inputs.length; i++) {
			x = (float) inputs[i][0];
			y = (float) inputs[i][1];
			z = 0f;
			points[i] = new Coord3d(x, y, z);
			colors[i] = Color.BLACK;
//			colors[i] = new Color(0, 0, 0, 1);
		}

		Scatter scatter = new Scatter(points, colors, 5);
		return scatter;
	}

//	public void drawChart() {
//		JFrame frame = new JFrame();
//		frame.setSize(600, 600);
//		frame.getContentPane().add((Component) getChart().getCanvas(), BorderLayout.CENTER);
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	}
//
//	private Chart getChart() {
//		if (chart == null) {
//			// Define a function to plot
//
//			// Define range and precision for the function to plot
//			Range range = new Range(-3, 3);
//			int steps = 80;
//
//			// Create the object to represent the function over the given range.
//			final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
//			surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
//					surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
//			surface.setFaceDisplayed(true);
//			surface.setWireframeDisplayed(false);
//
//			// Create a chart
//			chart = AWTChartComponentFactory.chart(Quality.Advanced, "swing");
//
//			chart.getScene().getGraph().add(surface);
//			chart.getScene().add(getScatterFromInputs(inputs));
//		}
//		return chart;
//	}

	public String vec2str(double[] arr) {
		StringBuilder b = new StringBuilder();
		b.append("[");
		for (int i = 0; i < arr.length; i++) {
			b.append(arr[i]);
			if (i < arr.length - 1) {
				b.append(",");
			}
		}
		b.append("]");
		return b.toString();
	}

	public static void main(String[] args) {
		new MLPApplication();
	}

}
