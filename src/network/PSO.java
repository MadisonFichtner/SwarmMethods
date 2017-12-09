package network;

import java.util.ArrayList;

/*	PSO implements the particle swarm optimization algorithm. It contains a global best, a list of particles, and a number of
 * 	particles, which is tunable. It clusters inputted data based on the PSO algorithm. Each particle is initialized, the global
 * 	best is found, and the velocity and position update equations are called from here but take place in the Particle class.
 * 	The list of clusters is returned at the end.
 */

public class PSO {
	private int particleNum = 20;												//use 20 particles
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private Particle gBest;

	//cluster based on the PSO algorithm - called from main
	//@param data - the list of data points to cluster
	public ArrayList<Cluster> cluster(ArrayList<DataPoint> data) {
		for (DataPoint d : data) {
			d.setLabel(null);
		}
		for (int i = 0; i < particleNum; i++) {									//for each particle
			particles.add(new Particle(data));									//initialize a new particle
		}
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		gBest = particles.get(0);												//just set gBest to be first particle here; it will work itself out
		for (int i = 0; i < particleNum; i++) {								//for each particle
			double fitness = particles.get(i).calcFitness(clusters);		//calculate the fitness and update pBest if applicable
			if (fitness > gBest.getFitness()) {
				gBest = particles.get(i);									//choose particle with best fitness as gBest
			}
		}
		for (int i = 0; i < particleNum; i++) {								//for each particle
			particles.get(i).setGBest(gBest.getFitness());					//update the gBest of every particle
			Cluster c = particles.get(i).update(data);						//then update the velocity and cluster of each particle
			clusters.add(c);												//add the cluster of each particle to the list
		}
		return clusters;													//return the list of clusters
	}
}
