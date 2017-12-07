package network;

import java.util.ArrayList;

public class PSO {
	private int particleNum = 20;												//use 20 particles
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private Particle gBest;

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
