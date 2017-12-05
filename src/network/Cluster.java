package network;

import java.util.ArrayList;
import java.util.Random;

public class Cluster {
	private DataPoint center;
	private ArrayList<DataPoint> members;
	
	Random rand = new Random();
	
	//Allow cluster to contain trivial number of Points (ArrayList)
	//Center variable that will be randomly generated and updated with the algorithm
	//Center will have same number of attributes as the DataPoint members
	
	public Cluster(){
		center = null;
		members = new ArrayList<DataPoint>();
	}
	
	public Cluster(int numFeatures, ArrayList<DataPoint> data) {
		center = new DataPoint(data.get(rand.nextInt(data.size())).getFeatures());	//set center to random point in data
		this.members = new ArrayList<DataPoint>();
	}
	
	public Cluster(int numFeatures) {
		double[] dataPoints = new double[numFeatures];
		this.center = new DataPoint(dataPoints);
	}
	
	public Cluster(DataPoint center, ArrayList<DataPoint> members) {
		this.center = center;
		this.members = members;
	}
	
	public void addPoint(DataPoint point) {
		members.add(point);
	}
	
	public boolean removePoint(DataPoint point) {
		return members.remove(point);
	}

	//Logic to update center based on the current members using the geometric mean of each feature
	public Cluster updateCenter(int numFeatures) {
		double[] newFeatures = new double[numFeatures];
		ArrayList<DataPoint> pointsInCluster = members;
		for(int k = 0; k < numFeatures; k++) {
			double mean = pointsInCluster.get(0).getFeature(k);													//Starting at 1 because 
			for(int j = 1; j < pointsInCluster.size(); j++) {
				mean += pointsInCluster.get(j).getFeature(k);			//Geometric mean
			}
			mean /= numFeatures;							//takes the numFeatures root of the mean
			newFeatures[k] = mean;
		}
		DataPoint newCenter = new DataPoint(newFeatures);
		Cluster newCluster = new Cluster(newCenter, members);
		return newCluster;
	}
	
	public DataPoint getCenter() {
		return center;
	}
	
	public ArrayList<DataPoint> getMembers() {
		return members;
	}
}
