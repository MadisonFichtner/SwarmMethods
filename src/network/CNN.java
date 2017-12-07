package network;
import java.util.ArrayList;
import java.util.Random;

/*	The Network class represents a single network in a population and its related functions and attributes. This class
 * 	is changed a bit from Project 2, as it had to incorporate new functionality for the 3 new training algorithms.
 */

public class CNN{
	private Random random = new Random();
	private ArrayList<Layer> layers;
	private double learningRate;
	private int numInputs;
	private int numHidLayers;
	private int numHidNodes;
	private int numOutputs;
	private int actFunHidden;
	private int actFunOutput;
	private ArrayList<Cluster> clusters;

	/*
	 * Create an MLP network
	 * @param numInputs: number of input nodes
	 * @param numHidLayers: number of hidden layers
	 * @param numHidNodes: number of nodes in hidden layers
	 * @param numOutputs: number of output nodes
	 * @param actFun: type of activation function for nodes
	 */
	public CNN(int numInputs, int numHidLayers, int numHidNodes, int numOutputs, int actFunHidden, int actFunOutput, ArrayList<DataPoint> data) {
		layers = new ArrayList<Layer>();
		//create input layer with inputs number of nodes and a linear activation function
		layers.add(new Layer(numInputs, 1));
		
		//create hidden layers with hidNode number of nodes and given activation function
		for(int i = 0; i < numHidLayers; i++) {
			layers.add(new Layer(numHidNodes, actFunHidden));
		}
		
		//create output layer with outputs number of nodes and given activation function
		layers.add(new Layer(numOutputs, actFunOutput));
		
		//add random weights between layers
		for(int i = 0; i < layers.get(0).size(); i++){
			for(int j = 0; j < layers.get(1).size(); j++){
				layers.get(0).getNeuron(i).addWeight(random.nextDouble());
			}
		}
		
		clusters = new ArrayList<>();
		for(int i = 0; i < numOutputs; i++){
			clusters.add(new Cluster());
		}
		
		//this block represents our manually tunable parameters
		this.learningRate = 0.01;
		this.numInputs = numInputs;
		this.numHidLayers = numHidLayers;
		this.numHidNodes = numHidNodes;
		this.numOutputs = numOutputs;
		this.actFunHidden = actFunHidden;
		this.actFunOutput = actFunOutput;
	}

	public ArrayList<Cluster> cluster(ArrayList<DataPoint> data) {
		for(int i = 0; i < layers.get(0).size(); i++) {	//iterate through input layer to normalize weights
			Neuron cur = layers.get(0).getNeuron(i);
			cur.normalize();
		}
		
		int numChanges = 0;
		int changeResetCounter = 0;
		
		//randomly get points from data to train the network
		for(int i = 0; i < 100000; i++) {
			DataPoint point = data.get(random.nextInt(data.size())); //get random point from data
			point.normalize();		//normalize data point
			calcOutputs(point.getFeatures());
			int winner = 0;
			double best = 0;
			for(int j = 0 ; j < layers.get(1).size(); j++) {
				if(layers.get(1).getNeuron(j).getOutput() > best) {
					winner = j;
					best = layers.get(1).getNeuron(j).getOutput();
				}
			}
			Cluster cRemoved = null;
			for(Cluster c : clusters){
				if(c.removePoint(point)) //remove this point from any other clusters
					cRemoved = c;		 //the cluster from which the point was removed
			}
			
			if(cRemoved == null)
				numChanges++;
			else if(cRemoved != clusters.get(winner))
				numChanges++;
			
			clusters.get(winner).addPoint(point);	//add point to the winning cluster
			
			//printNetwork();
			/*String pointData = "";
			for(double feature : point.getFeatures())
				pointData += String.format("%.2f", feature) + " ";
			System.out.println(pointData + ": cluster " + (winner+1));*/
						
			updateWeights(winner);
			
			changeResetCounter++;
			if(changeResetCounter >= 100){
				System.out.println("Number of Changes:  " + numChanges);
				
				if(numChanges < 5){	//end training if fewer than 5 changes
					System.out.println("Final Network: ");
					printNetwork();
					break;
				}
				
				numChanges = 0;
				changeResetCounter = 0;
			}
		}
		
		//reset clusters
		for(Cluster c : clusters){
			c.getMembers().removeAll(data);
		}
		
		//cluster all data by forward iterating through data
		for(DataPoint point : data){
			point.normalize();		//normalize data point
			calcOutputs(point.getFeatures());
			int winner = 0;
			double best = 0;
			for(int j = 0 ; j < layers.get(1).size(); j++) {
				if(layers.get(1).getNeuron(j).getOutput() > best) {
					winner = j;
					best = layers.get(1).getNeuron(j).getOutput();
				}
			}
			
			clusters.get(winner).addPoint(point);	//add point to the winning cluster
		}
		
		for(Cluster c : clusters){
			for(DataPoint point : c.getMembers()){
				point.unNormalize();
			}
			c.updateCenter(numInputs, data);
		}
		return clusters;
	}

	//Randomly reset weights in network
	public void reset(){
		for(int i = 0; i < layers.size()-1; i++) {
			for(int j = 0; j < layers.get(i).size(); j++) {
				for(int k = 0; k < layers.get(i+1).size(); k++) {
					double weight = (random.nextDouble()*2)-1;
					layers.get(i).getNeuron(j).setWeightTo(k, weight);
				}
			}
		}
	}

	
	//calulates the output of the network
	//@param inputs - the inputs for the network (from a data point)
	public void calcOutputs(double[] inputs) {
		//initialize input layer
		for(int i = 0; i < layers.get(0).size(); i++){
			layers.get(0).getNeuron(i).setOutput(inputs[i]);
		}
		
		for(int i = 0; i < layers.get(1).size(); i++) {
			ArrayList<Double> ins = new ArrayList<>();
			ArrayList<Double> weights = new ArrayList<>();
			for(int j = 0; j < layers.get(0).size(); j++) {
				ins.add(layers.get(0).getNeuron(j).getOutput());
				weights.add(layers.get(0).getNeuron(j).getWeightTo(i));
			}
			layers.get(1).getNeuron(i).calculate(ins, weights);
		}
	}
	
	public void updateWeights(int winNeuron) {
		for(int i = 0; i < layers.get(0).size(); i++) {	//iterate through input layer to update weights
			Neuron cur = layers.get(0).getNeuron(i);
			double newWeight = cur.getWeightTo(winNeuron) + learningRate*(cur.getOutput()-cur.getWeightTo(winNeuron));
			cur.setWeightTo(winNeuron, newWeight);
			cur.normalize();
		}
	}

	//prints out information about network
	public void printNetwork(){
		for(Layer l : layers) {
			l.printLayer(layers.indexOf(l)+1);
		}
	}
	
	//returns the number of network inputs
	public int getNumInputs(){
		return numInputs;
	}
	
	//returns the number of hidden layers
	public int getNumHidLayers(){
		return numHidLayers;
	}
	
	//returns the number of hidden nodes
	public int getNumHidNodes(){
		return numHidNodes;
	}
	
	//returns the number of outputs of the network
	public int getNumOutputs(){
		return numOutputs;
	}
	
	//returns the network's activation function for the hidden layers
	public int getActFunHidden() {
		return actFunHidden;
	}
	
	//returns the activation function for the output layer
	public int getActFunOutput() {
		return actFunOutput;
	}
	
	//returns the learning rate for assessment of convergence rate
	public double getLearningRate(){
		return learningRate;
	}
}

