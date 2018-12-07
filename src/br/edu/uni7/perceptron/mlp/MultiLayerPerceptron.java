package br.edu.uni7.perceptron.mlp;

public class MultiLayerPerceptron {

	public enum ActivationFunction {
		LOGISTIC, TANH
	};

	private final ActivationFunction activationFunction = ActivationFunction.LOGISTIC;
	private int inputSize;
	private int hiddenSize;
	private int outputSize;
	private double learningRate;
	
	// TODO bias
	
	private double[][] inputHiddenWeights;
	private double[][] hiddenOutputWeights;

	public MultiLayerPerceptron(int inputSize, int hiddenSize, int outputSize, double learningRate) {
		this.inputSize = inputSize;
		this.hiddenSize = hiddenSize;
		this.outputSize = outputSize;
		this.learningRate = learningRate;
		
		initWeights();
	}
	
	private void initWeights() {
		inputHiddenWeights = new double[inputSize][hiddenSize];
		hiddenOutputWeights = new double[hiddenSize][outputSize];
		
		for (int i = 0; i < inputHiddenWeights.length; i++) {
			for (int j = 0; j < inputHiddenWeights[i].length; j++) {
				inputHiddenWeights[i][j] = randomValue(-1, 1);
			}
		}
		
		for (int i = 0; i < hiddenOutputWeights.length; i++) {
			for (int j = 0; j < hiddenOutputWeights[i].length; j++) {
				hiddenOutputWeights[i][j] = randomValue(-1, 1);
			}
		}
//		inputHiddenWeights[0][0] = -0.424;
//		inputHiddenWeights[0][1] = -0.740;
//		inputHiddenWeights[0][2] = -0.961;
//		
//		inputHiddenWeights[1][0] = 0.358;
//		inputHiddenWeights[1][1] = -0.577;
//		inputHiddenWeights[1][2] = -0.469;
//		
//		hiddenOutputWeights[0][0] = -0.017;
//		hiddenOutputWeights[1][0] = -0.893;
//		hiddenOutputWeights[2][0] = 0.148;
	}
	
	public double[] train(double[][] inputBatch, double[][] expected) {
		int batchSize = inputBatch.length;
		// 2 corresponde às saídas do Eval (ativações da escondida e ativações da saída)
		double[][][] evaluations = new double[batchSize][2][outputSize];
		double[][] batchHiddenActivations = new double[batchSize][hiddenSize];
		double[][] batchOutput = new double[batchSize][outputSize];
		double[][] batchOutputDelta =  new double[batchSize][outputSize];
		double[] meanError = new double[outputSize];
		for (int b = 0; b <batchSize; b++) {
			evaluations[b] = eval(inputBatch[b]);
			
			batchHiddenActivations[b] = evaluations[b][0];
			batchOutput[b] = evaluations[b][1];
			
			for (int i = 0; i < outputSize; i++) {
				double outputError = expected[b][i] - batchOutput[b][i];
				meanError[i] += outputError;
				double outputDerivate = derivateFromActivation(batchOutput[b][i]);
				batchOutputDelta[b][i] = outputError * outputDerivate;
			}
		}
		for (int i = 0; i < outputSize; i++) {
			// Depois de somado os erros de todas as entradas, dividindo para obter a média 
			meanError[i] /= batchSize;
		}
		
		double[][] originalHiddenOutputWeights = copyMatrix(hiddenOutputWeights);
				
		// Ajuste de pesos da camada escondida -> saída:
		for (int i = 0; i < outputSize; i++) {
			for (int j = 0; j < hiddenSize; j++) {
				double hiddenActivationTimesDelta = 0;
				for (int b = 0; b < batchSize; b++) {
					hiddenActivationTimesDelta += (batchHiddenActivations[b][j] * batchOutputDelta[b][i]);
				}
				hiddenOutputWeights[j][i] = hiddenActivationTimesDelta * learningRate + hiddenOutputWeights[j][i];
				// TODO Ver: novopeso = peso antigo - derivada * taxa de aprendizagem
			}
		}
		
		double[][] batchHiddenDelta = new double[batchSize][hiddenSize];
		
		for (int b = 0; b < batchSize; b++) {
			for (int i = 0; i < hiddenSize; i++) {
				double hiddenDerivate = derivateFromActivation(batchHiddenActivations[b][i]);
				for (int j = 0; j < outputSize; j++) {
					// Avaliar corretude para várias saídas - fazer o somatório é correto?
					batchHiddenDelta[b][i]  += hiddenDerivate * originalHiddenOutputWeights[i][j] * batchOutputDelta[b][j];
				}
			}
		}
		
		// Ajuste de pesos da camada de entrada -> escondida:
		for (int i = 0; i < hiddenSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				double inputTimesDelta = 0;
				for (int b = 0; b < batchSize; b++) {
					inputTimesDelta += inputBatch[b][j] * batchHiddenDelta[b][i];
				}
				inputHiddenWeights[j][i] = inputTimesDelta * learningRate + inputHiddenWeights[j][i];
			}
		}	

		return meanError;
	}
	
	public double[][] copyMatrix(double[][] original) {
		// Uma matrix deve ter largura e altura fixa
		if (original.length > 0) {
			int width = original.length;
			int height = original[0].length;
			double[][] copy = new double[width][height];
			for (int i = 0; i < width; i++) {
				copy[i] = original[i].clone();
			}
			return copy;
		} else {
			return new double[][]{};
		}		
	}
	
	// Retorna ativações da camada escondida e da camada de saída
	public double[][] eval(double[] input) {
		double[] hiddenActivations = new double[hiddenSize];
		for (int i = 0; i < hiddenActivations.length; i++) {
			double weightedInputsSum = 0.0;
			for (int j = 0; j < inputSize; j++) {
				weightedInputsSum += input[j] * inputHiddenWeights[j][i];
			}
			hiddenActivations[i] = activationFunction(weightedInputsSum);
		}
		double[] outputs = new double[outputSize];
		for (int i = 0; i < outputs.length; i++) {
			double weightedHiddenSum = 0.0;
			for (int j = 0; j < hiddenSize; j++) {
				weightedHiddenSum += hiddenActivations[j] * hiddenOutputWeights[j][i];
			}
			outputs[i] = activationFunction(weightedHiddenSum);
		}
		return new double[][] {hiddenActivations, outputs};
	}

	private double activationFunction(double value) {
		switch (activationFunction) {
		case TANH:
			return Math.tanh(value);
		case LOGISTIC:
		default:
			return 1.0 / (1.0 + Math.exp(-value));
		}
	}

//	private double activationDerivate(double value) {
//		switch (activationFunction) {
//		case TANH:
//			double fx = activationFunction(value);
//			return 1.0 - (fx * fx);
//		case LOGISTIC:
//		default:
//			fx = activationFunction(value);
//			return fx * (1.0 - fx);
//		}
//	}
	
	private double derivateFromActivation(double value) {
		switch (activationFunction) {
		case TANH:
			return 1.0 - (value * value);
		case LOGISTIC:
		default:
			return value * (1.0 - value);
		}
	}
	
	private double randomValue(double min, double max) {
		double range = max - min;
		return (Math.random() * range) + min;
	}
}
