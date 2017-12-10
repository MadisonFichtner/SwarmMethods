package network;
import java.util.ArrayList;
import java.util.Random;

/*	
 * The CNN class represents a competitive neural network which clusters data by first updating the weights based on the inputs
 * then clustering based on the which output node produces the highest output
 */

public class CNN{
	private Random random = new Random();
	private ArrayList<Layer> layers;//the layer objects in the network
	private double learningRate;	//network learning rate
	private int numInputs;			//number of inputs
	private int numOutputs;			//number of outputs or clusters
	private ArrayList<Cluster> clusters; //clusters for the data to be assigned to

	/*
	 * Create an Competitive learning neural network
	 * @param numInputs: number of input nodes
	 * @param numOutputs: number of output nodes
	 * @param actFunOutput: type of activation function for nodes
	 */
	public CNN(int numInputs, int numOutputs, ArrayList<DataPoint> data) {
		layers = new ArrayList<Layer>();
		//create input layer with inputs number of nodes and a linear activation function
		layers.add(new Layer(numInputs));
		
		//create output layer with outputs number of nodes and given activation function
		layers.add(new Layer(numOutputs));
		
		//add random weights between layers
		for(int i = 0; i < layers.get(0).size(); i++){
			for(int j = 0; j < layers.get(1).size(); j++){
				layers.get(0).getNeuron(i).addWeight(random.nextDouble());
			}
		}
		
		//initialize empty clusters
		clusters = new ArrayList<>();
		for(int i = 0; i < numOutputs; i++){
			clusters.add(new Cluster());
		}
		
		
		this.learningRate = 0.01;	//set the learning rate for the network
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
	}

	/*
	 * this method recieves a list of DataPoints and returns a list of clusters
	 * to which the data points are assigned
	 */
	public ArrayList<Cluster> cluster(ArrayList<DataPoint> data) {
		for(int i = 0; i < layers.get(0).size(); i++) {	//iterate through input layer to normalize weights
			Neuron cur = layers.get(0).getNeuron(i);
			cur.normalize();	//normalize weight vector
		}
		
		int numChanges = 0;			//number of changes in clusters
		int changeResetCounter = 0; //count how many iterations have been done
		
		//randomly get points from data to train the network
		for(int i = 0; i < 100000; i++) {
			DataPoint point = data.get(random.nextInt(data.size())); //get random point from data
			point.normalize();		//normalize data point
			calcOutputs(point.getFeatures());	//calculate output nodes ouputs
			int winner = 0;
			double best = 0;
			for(int j = 0 ; j < layers.get(1).size(); j++) {	//determine which output wins
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
			
			//check if there was a change in clusters
			if(cRemoved == null)
				numChanges++;
			else if(cRemoved != clusters.get(winner))
				numChanges++;
			
			clusters.get(winner).addPoint(point);	//add point to the winning cluster
						
			updateWeights(winner);	//update weight vectors based on winning neuron
			
			changeResetCounter++;
			
			//determine how many changeds in the clusters there have been
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
		
		//restore data points to original values before returning
		for(Cluster c : clusters){
			for(DataPoint point : c.getMembers()){
				point.unNormalize();
			}
			c.updateCenter(numInputs, data);
			System.out.println(c.getCenter());
		}
		return clusters;	//return clustered data
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
	
	//update the weights leading to the winning neuron
	public void updateWeights(int winNeuron) {
		for(int i = 0; i < layers.get(0).size(); i++) {	//iterate through input layer to update weights
			Neuron cur = layers.get(0).getNeuron(i);
			double newWeight = cur.getWeightTo(winNeuron) + learningRate*(cur.getOutput()-cur.getWeightTo(winNeuron));
			cur.setWeightTo(winNeuron, newWeight);
			cur.normalize();	//normalize weight vector
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
	
	//returns the number of outputs of the network
	public int getNumOutputs(){
		return numOutputs;
	}
	
	//returns the learning rate for assessment of convergence rate
	public double getLearningRate(){
		return learningRate;
	}
}

