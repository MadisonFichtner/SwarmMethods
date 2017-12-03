package network;

import java.util.ArrayList;
import java.util.Random;

/*	Particle class represents a single particle in the particle swarm. It has a velocity value, a cluster radius, a cluster related to that particle,
 * 	a fitness value based on the cluster, values tracking the particle's personal best and the swarm's global best, and learning factors for the update equation.
 */
public class Particle {
	private double velocity;
	private double clusterRad;
	private Cluster thisCluster;
	private double fitness;
	private double pBest = 0;
	private double gBest = 0;
	private double lf1 = 2;					//lf for learning factors; typically equal to 2
	private double lf2 = 2;
	
	//creates a new particle
	public Particle(ArrayList<DataPoint> data) {
		
		/*
		 * NEED TO TUNE CLUSTERRAD RANGE
		 */
		
		
		clusterRad = 0.001 + (Math.random() * 100.999);								//initialize the cluster radius to be a random number between 0.001 and 100
		velocity = -10 + (Math.random() * 21);										//initialize velocity to be between -10 and 10 to encourage varying rates of convergence
		cluster(data);
	}
	
	//creates the cluster associated with this particle
	public Cluster cluster(ArrayList<DataPoint> data) {
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		Random rand = new Random();
		DataPoint center = new DataPoint(data.get(rand.nextInt(data.size())).getFeatures());	//set center to random point in data
		for (int i = 0; i < data.size() - 1; i++) {												//loop through data
			double dist = center.calcDistance(data.get(i + 1));									//calculate distance from center to each point in the list
			if (dist <= clusterRad) {															//if the point is within the cluster radius
				points.add(data.get(i));														//then add it to the cluster
			}
		}
		thisCluster = new Cluster(center, points);												//create a new cluster and return it
		return thisCluster;
	}
	
	//calculates the fitness of this particle based on average distance between data points in the cluster
	public double calcFitness() {
		double counter = 0;
		double total = 0;
		double ave = 0;
		for (int i = 0; i < thisCluster.getMembers().size(); i++) {
			for (int j = 0; j < thisCluster.getMembers().size(); j++) {
				total += thisCluster.getMembers().get(i).calcDistance(thisCluster.getMembers().get(j));				//calc distance to each point
				counter++;	
			}
		}
		ave = total / counter;										//then calculate the average distance between points
		
		//now give fitness value based on average
		double normalizedAve = 1 / (1 + Math.exp(-ave));			//normalize the average to be between 0 and 1 with sigmoidal function
		fitness = 1 - normalizedAve;								//assign a fitness value based on that average distance
		
		if (fitness > pBest) {										//if fitness is better than best fitness value pBest in history
			pBest = fitness;										//set current value as new pBest
		}
		return fitness;
	}
	
	public Cluster update(ArrayList<DataPoint> data) {
		velocity = velocity + (lf1 * Math.random() * (pBest - fitness)) + (lf2 * Math.random() * (gBest - fitness));	//calc particle velocity according to equation
		clusterRad += velocity;																							//update particle position
		return cluster(data);																							//update the particle's cluster with the new cluster radius
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public void setGBest(double val) {
		gBest = val;
	}
}