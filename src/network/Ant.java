package network;

import java.util.ArrayList;

/*	The ant class represents a single ant for ACO. Each ant has a specific cluster that it is on at the moment,
 * 	and a pheromone level that it is depositing. All that the ant does is keep track of the cluster it is on
 * 	and calculate a pheromone value to deposit on a cluster.
 */

public class Ant {
	private Cluster thisCluster;
	private double pheromone;

	public Ant(Cluster c) {
		thisCluster = c;											
	}

	//calculates the pheromone level to drop based on the "fitness" of the cluster.
	//cluster fitness is evaluated using the metric described in main.
	//@param clusters - the list of clusters created in ACO
	//@param c - the current cluster the ant is calculating the pheromone of
	public double calcPheromone(ArrayList<Cluster> clusters, Cluster c) {
		pheromone = 0;
		double counter = 0;
		double total = 0;
		double ave = 0;
		for (int i = 0; i < c.getMembers().size(); i++) {
			for (int j = 0; j < c.getMembers().size(); j++) {
				total += c.getMembers().get(i).calcDistance(c.getMembers().get(j));				//calc distance to each point
				counter++;	
			}
		}
		for (Cluster d : clusters) {															//this block penalizes clusters if they are very close together
			for (Cluster e : clusters) {
				double distance = d.getCenter().calcDistance(e.getCenter());
				if (distance < 20) {
					total += distance;
				}
			}
		}
		ave = total / counter;										//then calculate the average distance between points

		//now give fitness value based on average
		double normalizedAve = 1 / (1 + Math.exp(-ave));			//normalize the average to be between 0 and 1 with sigmoidal function
		pheromone = 1 + normalizedAve;								//assign a fitness value based on that average distance
		return pheromone;
	}
	
	//return the ant's current cluster
	public Cluster getCluster() {
		return thisCluster;
	}
	
	//set the ant's current cluster location
	//@param c - the cluster to set the position to
	public void setCluster(Cluster c) {
		thisCluster = c;
	}
}

