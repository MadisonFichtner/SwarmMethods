package network;

import java.util.ArrayList;
import java.util.Random;

public class Ant {
	private Cluster thisCluster;
	private double clusterRad;
	private double pheromone;

	public Ant(ArrayList<DataPoint> data) {
		clusterRad = 1 + (Math.random() * 100);								//initialize the cluster radius to be a random number between 1 and 100
		cluster(data);
		pheromone = Math.random();											
	}


	//creates the cluster associated with this ant's "path"
	public Cluster cluster(ArrayList<DataPoint> data) {
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		DataPoint closest = null;
		double close = 100000;
		Random rand = new Random();
		DataPoint center = new DataPoint(data.get(rand.nextInt(data.size())).getFeatures());	//set center to random point in data
		for (int i = 0; i < data.size(); i++) {													//loop through data
			double dist = center.calcDistance(data.get(i));										//calculate distance from center to each point in the list
			if (dist < close) {
				close = dist;
				closest = data.get(i);
			}
			if (dist <= clusterRad) {															//if the point is within the cluster radius
				points.add(data.get(i));														//then add it to the cluster
			}
		}
		if (points.isEmpty()) {
			points.add(closest);
		}
		thisCluster = new Cluster(center, points);												//create a new cluster and return it
		return thisCluster;
	}

	public double calcPheromone(Cluster c) {
		double counter = 0;
		double total = 0;
		double ave = 0;
		for (int i = 0; i < c.getMembers().size(); i++) {
			for (int j = 0; j < c.getMembers().size(); j++) {
				total += c.getMembers().get(i).calcDistance(c.getMembers().get(j));				//calc distance to each point
				counter++;	
			}
		}
		ave = total / counter;										//then calculate the average distance between points

		//now give fitness value based on average
		double normalizedAve = 1 / (1 + Math.exp(-ave));			//normalize the average to be between 0 and 1 with sigmoidal function
		pheromone = 1 + normalizedAve;								//assign a fitness value based on that average distance
		c.setPheromone(pheromone);
		return pheromone;
	}
	
	public Cluster getCluster() {
		return thisCluster;
	}
}
