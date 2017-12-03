package network;
import java.util.ArrayList;

/*	Neuron class represents a single neuron within a neural network and its corresponding functions and 
 * 	attributes. All that it has for Project 3 are weights, its output, and its activation function.
 */

public class Neuron {
	private int actFun;
	private ArrayList<Double> weights;
	private double output;

	//create a new neuron with its activation function
	//@param actFun: activation function to be used
	public Neuron(int actFun) {
		this.actFun = actFun;
		weights = new ArrayList<Double>();
	}

	//the function to calculate the output of the activation function of the neuron
	public void activate() {
		switch (actFun) {
		case 1:						//represents a linear activation function
			break;
		case 2:						//cnn activation
			output = 1/(1+Math.exp(-output));
			break;
		}
	}
	
	//returns derivative of activation function
	public double derivActivate() {
		switch (actFun) {
		case 1:						//derivative of linear is constant here
			return 1;
		case 2:						//sigmoidal - logistic
			return output * (1-output);
		default:
			return 0;
		}
	}

	//adds a connection to the previous layer neuron and the weight of the connection
	public void addWeight(double weight){
		weights.add(weight);
	}

	
	//calculates the output of the neuron
	//@param ins: inputs to the neuron
	//@param weights: corresponding weights of inputs
	public void calculate(ArrayList<Double> ins, ArrayList<Double> weights){
		output = 0;
		for(int i = 0; i < ins.size(); i++){
			output+=ins.get(i)*weights.get(i);				//calculate output
		}
	}

	//returns the weight of the connection this neuron has
	public double getWeightTo(int index){
		return weights.get(index);
	}

	//sets the weight of the connection this neuron is the end of
	public void setWeightTo(int index, double weight) {
		weights.set(index, weight);
	}
	
	//add weights to the list
	public void addWeights(ArrayList<Double> weights){
		this.weights.addAll(weights);
	}
	
	public void normalize() {
		double length = 0;
		for(double weight : weights) {
			length += weight*weight;
		}
		length = Math.sqrt(length);
		
		for(int i = 0; i < weights.size(); i++) {
			weights.set(i, weights.get(i)/length);
		}
	}

	//prints out info about neuron
	public void printNeuron(int num){
		System.out.println("\tNode " + num + ":");

		for(int i = 0; i < weights.size(); i++){
			System.out.println("\t  Connection " + (i+1) + " Weight: " + weights.get(i));
		}

		System.out.println("\t  Output: " + output);
	}

	//returns the neuron's output
	public double getOutput(){
		return output;
	}

	//set's the neuron's output (if special layer)
	public void setOutput(double value){
		output = value;
	}

	//returns the neuron's activation function
	public int getActFun(){
		return actFun;
	}

	//returns this neuron's connection list
	public ArrayList<Double> getWeights() {
		return weights;
	}
}


