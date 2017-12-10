package network;
import java.util.ArrayList;

/*
 * 
    Find the epsilon (eps) neighbors of every point, and identify the core points with more than minPts neighbors.
    
    Find the connected components of core points on the neighbor graph, ignoring all non-core points.
    
    Assign each non-core point to a nearby cluster if the cluster is an epsilon (eps) neighbor, otherwise assign it to noise.

 */
public class DBScan {
	private int minPoints;			//Minimum number of points to form a "dense" region
	private int numFeatures;
	private double maxDistance;
	
	/*
	 * Creates a new DBScan object, setting maxDistance to the entered epsilon value, the dataSet, and the minimumPoints required for a cluster as well as sets numFeatures
	 * @param dataSet -> dataSet that was entered
	 * @param eps -> epsilon (max distance between points in a cluster)
	 * @param minPoints -> minimum points to create a cluster
	 */
	public DBScan(ArrayList<DataPoint> dataSet, double eps, int minPoints) {
		maxDistance = eps;
		this.minPoints = minPoints;
		this.numFeatures = dataSet.get(0).getNumFeatures();
	}
	
	/*
	 * Clusters an arraylist of datapoints using DBScan
	 * @param dataSet -> data set being clustered
	 */
	public ArrayList<Cluster> cluster(ArrayList<DataPoint> dataSet) {
		ArrayList<Cluster> clusters = new ArrayList<>();				//New array list of clusters to add onto as we go
		Cluster noise = new Cluster(numFeatures);						//New "noise" cluster to keep track of datapoints that dont fit into real clusters
		
		int clusterCounter = -1;										//keep track of number of clusters
		for(int i = 0; i < dataSet.size(); i++) {						//For each datapoint in the dataset
			DataPoint currentData = dataSet.get(i);						//Set the currentData to the data point for this i'th iteration
			if(currentData.getLabel() != null)							//If the label is not null, continue;
				continue;
			ArrayList<DataPoint> neighbors = setNeighbors(dataSet, currentData);		//Set the neighbors of the currentData point given the entire dataSet, and the epsilon entered
			ArrayList<DataPoint> seeds = new ArrayList<>();								//Create new arraylist of datapoints to keep track of "seeds" or points that need to be added to the cluster or "neighborhood"
			if(neighbors.size() < minPoints){							//If the size of the neighborhood is smaller than the minimum required points for a cluster, set label to "noise"
				currentData.setLabel(noise);
			}
			
		    else if(neighbors.size() >= minPoints) {				//If size of neighborhood is greater than the minimum required points, create a new cluster
				clusterCounter++;									//Increment clusterCount
				clusters.add(new Cluster(numFeatures));				//Create new cluster in clusters 
				clusters.get(clusterCounter).addPoint(currentData);		//Add currentData to cluster
				currentData.setLabel(clusters.get(clusterCounter));		//Set label of currentData to the cluster
				for(int k = 1; k < neighbors.size(); k++)				//For each neighbor, add the neighbor to the seeds. (seeds is same as neighborhood sans the currentData)
					seeds.add(neighbors.get(k));
			}
			
			for(int j = 0; j < seeds.size(); j++) {			//For each point in seeds
				if(seeds.get(j).getLabel() == null)									//If the label is currently null, set it to the current cluster
					seeds.get(j).setLabel(clusters.get(clusterCounter));
				else if(seeds.get(j).getLabel() == noise)							//If label is noise, set it to current cluster
					seeds.get(j).setLabel(clusters.get(clusterCounter));
				else																//Basically always set to current cluster, this stuff is redudant and needs to be removed :P
					seeds.get(j).setLabel(clusters.get(clusterCounter));
				
				neighbors = setNeighbors(dataSet, seeds.get(j));			//Create new neighborhood for each j dataPoint
				if(neighbors.size() >= minPoints) {							//if minimum point are met in neighborhood, add neighbors to seeds
					for(DataPoint dataPoints : neighbors) {					//FOr all datapoints in neighborhood, if seeds doesn't contain the neighbor, add it to seeds
						if(!seeds.contains(dataPoints))
							seeds.add(dataPoints);
					}
					for(DataPoint dataPoints : seeds) {						//For all dataPoints in seeds, if the current cluster does not contain the datapoint, add it. And if the datapoint changes clusters, remove it from it's old cluster
						if(clusters.get(clusterCounter).hasMember(dataPoints) == false) {
							if(dataPoints.getLabel() != clusters.get(clusterCounter) && dataPoints.getLabel() != null) {
								Cluster tempCluster;
								tempCluster = dataPoints.getLabel();
								tempCluster.removePoint(dataPoints);
							}
							clusters.get(clusterCounter).addPoint(dataPoints);
						}
					}
					for(DataPoint dataPoints : clusters.get(clusterCounter).getMembers()) {		//FOr all datapoints in the currentClusters members, set their labesl to the current cluster
						dataPoints.setLabel(clusters.get(clusterCounter));
					}
				}
				
			}
		}
		for(DataPoint dataPoints : dataSet) {			//Ensure that if there are any datapoints with no labels to assign them as noise
			if(dataPoints.getLabel() == null) {
				dataPoints.setLabel(noise);
			}
			if(dataPoints.getLabel() == noise) {		//Ensure that if there are any datapoints marked as noise, to add them to the noise cluster
				noise.addPoint(dataPoints);
			}
		}
		/**
		for(int i = 0; i < clusters.size(); i++) {		//printing out clusters
			System.out.println("Cluster " + (i+1) + ": " + clusters.get(i).getMembers().size() + " data points");
			for(int j = 0; j < clusters.get(i).getMembers().size(); j++) {
				for(int k = 0; k < numFeatures; k++) {
					System.out.print(clusters.get(i).getMembers().get(j).getFeature(k) + "	");
				}
				System.out.println("");
			}
			System.out.println("");
		}
		**/
		
		//Calculating average distance between all points in each cluster and printing them out
		double overallAverage = 0;
		for(int i = 0; i < clusters.size(); i++) {		//For each cluster
			double average = 0;							//Set average for that cluster to 0
			System.out.println("Cluster " + (i+1) + ": " + clusters.get(i).getMembers().size() + " data points");	//Print out number of points in current cluster
			for(int j = 0; j < clusters.get(i).getMembers().size(); j++) {				//For each member in the cluster
				for(int k = 0; k < clusters.get(i).getMembers().size(); k++) {			//For each member in the cluster
					average += clusters.get(i).getMembers().get(i).calcDistance(clusters.get(i).getMembers().get(k));	//Add distance between point j and k to average
				}
			}
			int numberConnections = 0;							//Set numberConnections to 0
			numberConnections = clusters.get(i).getMembers().size();		//Set numberConnections to the number of members in the cluster
			average = average / ((numberConnections)*(numberConnections-1)/2);		//set average to the sum of all distances divided by the number of connections in the cluster
			System.out.println(average);
			overallAverage += average;				//Increment cluster average to overall average
		}
		overallAverage = overallAverage / clusters.size();		//Divide overall average by number of clusters
		System.out.println("Overall Avg: " + overallAverage);
		System.out.println("\nSuccessfully clustered data into: " + clusters.size() + " clusters not including outliers.");
		System.out.println("There were " + noise.getMembers().size() + " outliers tagged as 'noise'");
		return clusters;
	}
	
	/*
	 * If point has minPoints neighbors in epsilon distance, create cluster. If not, mark point as noise that can later be found in another epsilon
	 * If a point is found to be a dense part of cluster, its neighborhood is also part of that cluster. Hence, all points found within the neighborhood are added
	 * 
	 * @param dataSet -> the overall data set being clustered
	 * @param point -> the point beign measured from
	 */
	public ArrayList<DataPoint> setNeighbors(ArrayList<DataPoint> dataSet, DataPoint point) {		
		ArrayList<DataPoint> neighbors = new ArrayList<>();
		for(int i = 0; i < dataSet.size(); i++) {
			if(point.calcDistance(dataSet.get(i)) < maxDistance) {			//if dataset.get(i) is within epsilon, add to neighbors
				neighbors.add(dataSet.get(i));
			}
		}
		return neighbors;
	}
	
}
