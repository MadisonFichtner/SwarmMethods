package network;

/* 	Represents a single data point in the cluster, and contains a list of inputs and then 
 * 	a class value. Data points will be grouped in clusters, so manipulation of a single one
 * 	happens here. 
 */
public class DataPoint {
	private double[] features;
	private Cluster label;
	private double length;
	private boolean normalized = false;

	//create a sample by passing in an array created in main from function
	//@param features: the array of features
	public DataPoint(double[] features) {
		this.features = features;
		this.label = null;
	}
	
	//calculate the distance from this data point to another that is read in based on feature values
	//@param other - the data point to calculate the distance to
	public double calcDistance(DataPoint other) {
		double distance = 0;
		for(int i = 0; i < other.getNumFeatures(); i++) {
			double difference = this.getFeature(i) - other.getFeature(i);
			difference = Math.pow(difference, 2);
			distance += difference;
		}
		distance = Math.sqrt(distance);
		return distance;
}
	
	//set the cluster label of the point
	//@param label - cluster to label the point with
	public void setLabel(Cluster label){
		this.label = label;
	}
	
	//return the point's cluster label
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
	
	//return the number of features of the point
	public int getNumFeatures(){
		return features.length;
	}
	
	//logic to normalize the data point
	public void normalize() {
		if(!normalized){
			length = 0;
			for(double feature : features) {
				length += feature*feature;
			}
			length = Math.sqrt(length);
			
			for(int i = 0; i < features.length; i++) {
				features[i] /= length;
			}
			normalized = true;
		}	
	}
	
	//logic to unnormalize the data point
	public void unNormalize(){
		for(int i = 0; i < features.length; i++){
			features[i] *= length;
		}
	}
}
