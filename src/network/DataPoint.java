package network;

/* Represents a single data point in the cluster, and contains a list of inputs and then 
 * a class value.
 */
public class DataPoint {
	private double[] features;
	private Cluster label;

	//create a sample by passing in an array created in main from function
	//@param features: the array of features
	public DataPoint(double[] features) {
		this.features = features;
		this.label = null;
	}
	
	public void setLabel(Cluster label){
		this.label = label;
	}
	
	public Cluster getLabel() {
		return label;
	}

	//returns feature at a specific index
	//@param index: the index of the array needing to be returned
	public double getFeature(int index) {
		return features[index];
	}

	//returns all the feature values in the data point
	public double[] getFeatures(){
		return features;
	}
	
	public int getNumFeatures(){
		return features.length;
	}
}
