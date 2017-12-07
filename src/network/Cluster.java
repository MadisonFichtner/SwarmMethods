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
		this.members = new ArrayList<>();
	}
	
	public Cluster(DataPoint center, ArrayList<DataPoint> members) {
		this.center = center;
		this.members = members;
	}
	
	public Cluster(ArrayList<DataPoint> members) {
		int numFeatures = members.get(0).getFeatures().length;
		double[] dataPoints = new double [numFeatures];
		this.center = new DataPoint(dataPoints);
		this.members = members;
	}
	
	public void addPoint(DataPoint point) {
		members.add(point);
	}
	
	public boolean removePoint(DataPoint point) {
		return members.remove(point);
	}

	//Logic to update center based on the current members using the mean of each feature
	//updates this cluster's center and returns a new cluster
	public Cluster updateCenter(int numFeatures, ArrayList<DataPoint> data) {
		double[] newFeatures = new double[numFeatures];
		ArrayList<DataPoint> pointsInCluster = members;
		for(int k = 0; k < numFeatures; k++) {
			double mean = 0;												//Starting at 1 because 
			for(int j = 0; j < pointsInCluster.size(); j++) {					
				mean = mean + pointsInCluster.get(j).getFeature(k);
				//mean = (mean) * pointsInCluster.get(j).getFeature(k);			//Geometric mean
			}
			mean = mean / pointsInCluster.size();
			//mean = Math.pow(mean, 1.0 / pointsInCluster.size());							//takes the numFeatures root of the mean
			newFeatures[k] = mean;
		}
		if(members.size() == 0) {
			DataPoint newCenter = new DataPoint(data.get(rand.nextInt(data.size())).getFeatures());	//set center to random point in data
			Cluster newCluster = new Cluster(newCenter, members);
			return newCluster;
		}
		center = new DataPoint(newFeatures);
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
	
	public boolean hasMember(DataPoint member) {
		boolean contains = false;
		if(members.contains(member))
			contains = true;
		else if(members.contains(member) != true)
			contains = false;
		return contains;
	}
	
}
