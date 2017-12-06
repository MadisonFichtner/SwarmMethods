package network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ACO {
	private int numAnts = 20;
	private ArrayList<Ant> ants;
	private ArrayList<Cluster> clusters;
	private double evapFactor = 0.1;

	public ACO(ArrayList<DataPoint> data, int numClusters) {
		clusters = new ArrayList<Cluster>();
		for (int i = 0; i < numClusters; i++) {				//create the clusters/"paths"
			clusters.add(createCluster(data));
		}
		for (Cluster c : clusters) {
			double level = Math.random();					//pheromones on clusters initialized to small random numbers between 0 and 1
			c.setPheromone(level);
		}
	}
	
	public ArrayList<Cluster> cluster() {
		ants = new ArrayList<Ant>();
		ArrayList<Cluster> bestClusters = new ArrayList<Cluster>();
		for (int i = 0; i < numAnts; i++) {					//initialize ants with starting position of first cluster created
			int start = (int)Math.random() * clusters.size();
			Ant a = new Ant(clusters.get(start));
			ants.add(a);
		}
		for (Ant a : ants) {			 					//for each ant, set cluster to move based on the pheromone
			for (Cluster c : clusters) {
				if (a.getCluster().getPheromone() < c.getPheromone()) {
					a.setCluster(c);
				}
			}
		}
		for (Cluster c : clusters) {									//for every path
			double level = (c.getPheromone() * (1 - evapFactor));		//evaporate pheromone level --> pher = (1 - evapFactor) * pher
			c.setPheromone(level);
		}
		for (Ant a : ants) {								//for each ant
			for (Cluster c : clusters) { 					//for each path
				a.calcPheromone(c); 						//deposit pheromone based on fitness of cluster and update pheromone level
			}
		}
		for (Ant a : ants) {
			bestClusters.add(a.getCluster());
		}
		return bestClusters;									//return the best clustering list
	}
	
	//creates the cluster associated with ant "paths"
	public Cluster createCluster(ArrayList<DataPoint> data) {
		double clusterRad = 1 + (Math.random() * 50);
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
		return new Cluster(center, points);														//create a new cluster and return it
	}
}
