package network;

import java.util.ArrayList;
import java.util.Random;

/*	Cluster represents a cluster of data points. It has a center and a list of members, as well as a pheromone value for
 * 	ACO. There are are a number of clusters that can be created, as is shown below. Essentially, the user can create
 *	a cluster with as little or much information as they have. A cluster can return its members and center, and add points,
 *	update the center, and get and set pheromone levels.
 */

public class Cluster {
	private DataPoint center;
	private ArrayList<DataPoint> members;
	private double pheromone = 0;
	
	Random rand = new Random();
	
	//Allow cluster to contain trivial number of Points (ArrayList)
	//Center variable that will be randomly generated and updated with the algorithm
	//Center will have same number of attributes as the DataPoint members
	
	//create trivial cluster
	public Cluster(){
		center = null;
		members = new ArrayList<DataPoint>();
	}
	
	//create cluster with feature number and data list
	//@param numFeatures - the number of features of a single data point
	//@param data - the list of data to be clustered
	public Cluster(int numFeatures, ArrayList<DataPoint> data) {
		center = new DataPoint(data.get(rand.nextInt(data.size())).getFeatures());	//set center to random point in data
		this.members = new ArrayList<DataPoint>();
	}
	
	//create cluster based only on feature numbers
	//@param numFeatures - the number of features of a single data point
	public Cluster(int numFeatures) {
		double[] dataPoints = new double[numFeatures];
		this.center = new DataPoint(dataPoints);
		this.members = new ArrayList<>();
	}
	
	//create cluster with a center and members already
	//@param center - the data point to serve as the center of a cluster
	//@param members - a list of data points that will act as the rest of the cluster
	public Cluster(DataPoint center, ArrayList<DataPoint> members) {
		this.center = center;
		this.members = members;
		for (DataPoint d : members) {
			d.setLabel(this);
		}
	}
	
	//create cluster with a list of members; determine center here
	//@param members - a list of data points that will act as the rest of the cluster
	public Cluster(ArrayList<DataPoint> members) {
		int numFeatures = members.get(0).getFeatures().length;
		double[] dataPoints = new double [numFeatures];
		this.center = new DataPoint(dataPoints);
		this.members = members;
	}
	
	//add a data point to the cluster
	//@param point - the data point to add to the cluster
	public void addPoint(DataPoint point) {
		members.add(point);
	}
	
	//remove a data point from the cluster
	//@param point - the data point to remove from the cluster
	public boolean removePoint(DataPoint point) {
		return members.remove(point);
	}

	//Logic to update center based on the current members using the geometric mean of each feature
	//@param numFeatures - the number of features of a single data point
	//@param data - the list of data to be clustered
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
	
	//return the center of the cluster
	public DataPoint getCenter() {
		return center;
	}
	
	//return the members of a cluster
	public ArrayList<DataPoint> getMembers() {
		return members;
	}
	
	//search for a specific member
	//@param member - the specific data point to look for in the cluster
	public boolean hasMember(DataPoint member) {
		boolean contains = false;
		if(members.contains(member))
			contains = true;
		else if(members.contains(member) != true)
			contains = false;
		return contains;
	}
	
	//get the cluster's current pheromone value
	public double getPheromone() {
		return pheromone;
	}
	
	//set the cluster's pheromone value
	//@param level - the pheromone value to deposit on the cluster
	public void setPheromone(double level) {
		pheromone = level;
	}
}
