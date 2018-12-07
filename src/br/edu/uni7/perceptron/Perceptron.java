package br.edu.uni7.perceptron;

public class Perceptron {

	public static final int BIAS = 1;

	private double[] weights;
	private double biasWeight;
	private double learningRate;

//	private int epochs; 

	public Perceptron(int inputSize, double learningRate, double minValue, double maxValue) {
		// Um peso para cada entrada mais um para o bias
		weights = new double[inputSize];

		for (int i = 0; i < weights.length; i++) {
			// Gera um n�mero aleat�rio com valor entre -1 e 1
			weights[i] = randomValue(minValue, maxValue);
		}
		biasWeight = randomValue(minValue, maxValue);

		this.learningRate = learningRate;
	}

//	public double train(double[] inputs, double expected) {
//		double output = eval(inputs);
//		
//		double error = expected - output;
//		
//		if (error != 0) {
//			for (int i = 0; i < weights.length; i++) {
//				weights[i] = weights[i] + learningRate * error * inputs[i];
//			}
//			biasWeight = biasWeight + learningRate * error * BIAS;
//		}
//		return output;
//	}	

	public double train(double[] inputs, double expected) {
		double output = eval(inputs);

		double error = expected - output;

		while (error != 0) {
//			System.out.println("Error: " + error + " Expected: " + expected + " output: " + output);
			for (int i = 0; i < weights.length; i++) {
				weights[i] = weights[i] + learningRate * error * inputs[i];
			}
			biasWeight = biasWeight + learningRate * error * BIAS;

			output = eval(inputs);

			// Uma função de erro comum : erro médio quadrático
			error = expected - output;
		}
		return output;
	}

	public double eval(double[] inputs) {
		double sum = apply(inputs);
		// Função de ativação Passo: -1 ou 1
		return activationFunction(sum);
	}

	public double apply(double[] inputs) {
		double sum = 0;
		for (int i = 0; i < inputs.length; i++) {
			sum += weights[i] * inputs[i];
		}
		sum += biasWeight * BIAS;
		return sum;
	}

	private double activationFunction(double value) {
		return value >= 0 ? 1 : -1;
	}

	private double randomValue(double min, double max) {
		double range = max - min;
		return (Math.random() * range) + min;
	}

	public double[] getWeights() {
		return weights;
	}

	public double getBiasWeight() {
		return biasWeight;
	}
}
