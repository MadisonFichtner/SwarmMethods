package network;
import java.util.ArrayList;

/* Represents a single layer in the network and it's functions. Contains
 * a list of neurons/nodes, the number of those, and the activation function 
 * for that layer, which is passed on to neurons for the layer.
 */
public class Layer {
	private ArrayList<Neuron> neurons;

	/*
	 * creates new layer
	 * @param numNodes: number of nodes in the layer
	 * @param actFun: type of activation function to be used
	 */
	public Layer(int numNodes) {
		neurons = new ArrayList<Neuron>();

		//add nodes to layer
		for(int i = 0; i < numNodes; i++){
			addNeuron(new Neuron());
		}
	}

	//adds a neuron to the layer
	public void addNeuron(Neuron n) {
		neurons.add(n);
	}

	//returns the neuron at the provided index
	public Neuron getNeuron(int index){
		return neurons.get(index);
	}

	//returns the size of the neuron array; effectively the layer size in neurons
	public int size(){
		return neurons.size();
	}

	//prints out information about layer
	public void printLayer(int num){
		System.out.println("Layer " + num + ":");

		System.out.println("\tNeuron Type: Weighted Sum");

		for(Neuron n : neurons){
			n.printNeuron(neurons.indexOf(n)+1);
		}
		System.out.println();
	}
}
