package br.edu.uni7.perceptron;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
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

public class SurfaceDemo extends AbstractAnalysis {
	
	private static double[][] inputs = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
	
	private Mapper mapper;
	
	public SurfaceDemo(Mapper mapper) {
		this.mapper = mapper;
	}

	public static void main(String[] args) throws Exception {
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return x * Math.sin(x * y);
			}
		};
		AnalysisLauncher.open(new SurfaceDemo(mapper));
	}
	
	public Scatter getScatterFromInputs(double[][] inputs) {
		Coord3d[] points = new Coord3d[inputs.length];
		Color[] colors = new Color[inputs.length];

		double x;
		double y;
		double z;

		for (int i = 0; i < inputs.length; i++) {
			x = inputs[i][0];
			y = inputs[i][1];
			z = .5;
			points[i] = new Coord3d(x, y, z);
			colors[i] = Color.BLACK;
//			colors[i] = new Color(0, 0, 0, 1);
		}

		Scatter scatter = new Scatter(points, colors, 5f);
		return scatter;
	}

	@Override
	public void init() {
		// Define a function to plot

		// Define range and precision for the function to plot
		Range range = new Range(-2, 2);
		int steps = 80;

		// Create the object to represent the function over the given range.
		final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);

		// Create a chart
		chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        chart.getScene().getGraph().add(surface);
		chart.getScene().add(getScatterFromInputs(inputs));
	}
}
