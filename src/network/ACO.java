package network;

import java.util.ArrayList;

public class ACO {
	private int numAnts = 1;
	private ArrayList<Ant> ants = new ArrayList<Ant>();
	private double evapFactor = 0.1;

	public ArrayList<Cluster> cluster(ArrayList<DataPoint> data, int numClusters) {
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		ArrayList<Cluster> bestClusters = new ArrayList<Cluster>();
		for (int t = 0; t < 500; t++) {
			for (int i = 0; i < numAnts; i++) {					//initialize ants with starting position, and pheromones to small random numbers
				Ant a = new Ant(data);
				ants.add(a);
				clusters.add(a.getCluster());
			}
			for (Cluster c : clusters) {
				double level = Math.random();					//pheromones initialized to small random numbers between 0 and 1
				c.setPheromone(level);
			}
			for (Ant a : ants) {			 					//for each ant
				a.cluster(data);
			}
			for (Cluster c : clusters) {						//for every path
				double level = (c.getPheromone() * (1 - evapFactor));		//evaporate pheromone level --> pher = (1 - evapFactor) * pher
				c.setPheromone(level);
			}
			for (Ant a : ants) {								//for each ant
				for (Cluster c : clusters) { 					//for each path
					a.calcPheromone(c); 						//deposit pheromone based on fitness of cluster and update pheromone level
				}
			}
		}
		//find best numClusters clusters, add to best clusters
		for (int i = 0; i < clusters.size(); i++) {
			for (int j = 0; j < clusters.size() - 1; j++) {
				if (clusters.get(j).getPheromone() < clusters.get(j + 1).getPheromone()) {
					Cluster temp = clusters.get(j);
					clusters.add(j, clusters.get(j + 1));
					clusters.add(j + 1, temp);
				}
			}
		}
		for (int i = 0; i < numClusters; i++) {
			bestClusters.add(clusters.get(i));
		}
		return bestClusters;									//return the best clustering list
	}
}
