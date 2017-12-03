package network;
import java.util.ArrayList;

/*
 * 
    Find the epsilon (eps) neighbors of every point, and identify the core points with more than minPts neighbors.
    
    Find the connected components of core points on the neighbor graph, ignoring all non-core points.
    
    Assign each non-core point to a nearby cluster if the cluster is an epsilon (eps) neighbor, otherwise assign it to noise.

 */
public class DBScan {
	private int iterations;
	private int minPoints;			//Minimum number of points to form a "dense" region
	private int numFeatures;
	private double maxDistance;
	
	
	public DBScan(ArrayList<DataPoint> dataSet, double eps, int minPoints) {
		maxDistance = eps;
		this.minPoints = minPoints;
	}
	
	  
	public ArrayList<Cluster> cluster(ArrayList<DataPoint> dataSet) {
		ArrayList<Cluster> clusters = new ArrayList<>();
		Cluster noise = new Cluster(numFeatures);
		
		int clusterCounter = -1;					//keep track of number of clusters
		for(int i = 0; i < dataSet.size(); i++) {
			if(dataSet.get(i).getLabel() != null)		//If the label is not null, break;
				continue;
			ArrayList<DataPoint> neighbors = setNeighbors(dataSet, dataSet.get(i));
			ArrayList<DataPoint> seeds = new ArrayList<>();
			if(neighbors.size() < minPoints){
				dataSet.get(i).setLabel(noise);
			}
			
		    else if(neighbors.size() >= minPoints) {
				clusterCounter++;
				clusters.add(new Cluster(neighbors));
				dataSet.get(i).setLabel(clusters.get(clusterCounter));
				seeds = neighbors;			//Create new set of "seeds" sans original point to search and label to the same cluster
				seeds.remove(dataSet.get(i));
			}
			
			for(int j = 0; j < seeds.size(); j++) {			//For each point in seeds
				if(seeds.get(j).getLabel() == noise) {
					seeds.get(j).setLabel(clusters.get(clusterCounter));
				}
				if(seeds.get(j).getLabel() != null) {
					continue;
				}
				seeds.get(j).setLabel(clusters.get(clusterCounter));
				neighbors = setNeighbors(dataSet, seeds.get(j));
				if(neighbors.size() >= minPoints) {							//if minimum point are met in neighborhood, add neighbors to seeds
					for(DataPoint dataPoints : neighbors) {
						seeds.add(dataPoints);
					}
				}
				
			}
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	//if point has minPoints neighbors in epsilon distance, create cluster. If not, mark point as noise that can later be found in another epsilon
	//If a point is found to be a dense part of cluster, its neighborhood is also part of that cluster. Hence, all points found within the neighborhood are added
	public ArrayList<DataPoint> setNeighbors(ArrayList<DataPoint> dataSet, DataPoint point) {		
		ArrayList<DataPoint> neighbors = new ArrayList<>();
		neighbors.add(point);
		for(int i = 0; i < dataSet.size(); i++) {
			//if dataset.get(i) is within epsilon, add to neighbors
			if(getDistanceTo(point, dataSet.get(i)) < maxDistance) {
				neighbors.add(dataSet.get(i));
			}
		}
		return neighbors;
	}
	
	public double getDistanceTo(DataPoint point, DataPoint point2) {
	
		return 0;
	}
}
