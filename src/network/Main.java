package network;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*	Main class drives the creation and running of algorithms for Project 4. It reads in data files (which we manually have to specify) and then
 * 	gives the user an option of clustering that data using k-means, db-scan, a competitive learning neural network, PSO, or ACO. These algorithms
 * 	print their final error value, which is calculated by finding the average of the distance between points in a cluster, the distance between
 * 	points and their center, and the distance between clusters. A few of the algorithms print the error for every iteration, and others print
 * 	just the final error. Tables for the former are included in the final project paper.
 */

public class Main {
	public static void main(String args[]) {
		ArrayList<DataPoint> data = new ArrayList<DataPoint>();						//create list of samples to use - dataset essentially
		int numInputs = 0;
		int numDataPoints = 0;
		String filename = "car.txt";
		try {
			Scanner s = new Scanner(new File(filename));							//create a new scanner, checks lines of data in file
			while (s.hasNextLine()) {												//loop while there is another line
				String line = s.nextLine();												//grab the next line
				ArrayList<Double> inputs = new ArrayList<Double>();						//create an arraylist for the inputs
				int counter = 0;														//counter to determine size of input array
				Scanner lineScan = new Scanner(line);									//create new scanner that checks individual pieces of a line
				lineScan.useDelimiter(",");												//separates tokens with a comma, space (as they were inputted that way)
				while (lineScan.hasNext()) {											//loop while there are still tokens to be read
					counter++;																//update counter (1 more input)
					double element = Double.parseDouble(lineScan.next());
					inputs.add(element);													//parse the token to be a double and add to the input arraylist
				}																		//update counter to reflect input size (total - 1, since last token is output
				counter--;
				double[] passIn = new double[counter];									//this is the array that will be passed to sample class
				for (int i = 0; i < counter; i++) {
					passIn[i] = inputs.get(i);											//initialize the input array
				}
				data.add(new DataPoint(passIn));							//create new datapoint with input array and add that sample to the list of data
				numInputs = passIn.length;
				numDataPoints ++;
				lineScan.close();
			}
			s.close();																	//print information about the dataset
			System.out.println("Filename: " + filename);
			System.out.println("Number of Inputs: " + numInputs);
			System.out.println("Number of Data Points: " + numDataPoints);
			System.out.println();
		}
		catch (Exception e) {
			System.out.println("File not found.");
			for(DataPoint d : data){
				for(double input : d.getFeatures()){
					System.out.print(input + " ");
				}
				System.out.println();
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		
		int numOutputs = 5;					//number of possible clusters			

		Scanner in = new Scanner(System.in);											//grab user input for the algorithm to use
		System.out.println("What algorithm do you want to use?");
		System.out.println("\t1)K-Means Clustering"); 
		System.out.println("\t2)DB-Scan");
		System.out.println("\t3)Competitive Learning Network");
		System.out.println("\t4)Particle Swarm Optimization (PSO)");
		System.out.println("\t5)Ant Colony Optimization (ACO)");

		int selection = in.nextInt();

		Collections.shuffle(data);													//randomize the data

		
		//cluster the data using chosen algorithm
		ArrayList<Cluster> clusteredData = null;
		switch(selection){
		case 1:																			//use k-means clustering
			System.out.println("How many centroids?");
			int k = in.nextInt();
			KMeans kmeans = new KMeans(data, k);
			clusteredData = kmeans.cluster(data);
			break;
		case 2:																			//use db-scan
			System.out.println("Enter epsilon (max distance between neighbors): ");
			double epsilon = in.nextDouble();
			System.out.println("Enter minimum points required to make cluster: ");
			int minPoints = in.nextInt();
			DBScan dbScan = new DBScan(data, epsilon, minPoints);
			clusteredData = dbScan.cluster(data);
			break;
		case 3:																			//use competitive learning neural network
			CNN cnn = new CNN(numInputs, numOutputs, data);
			clusteredData = cnn.cluster(data);
			break;
		case 4:																			//use particle swarm optimization
			PSO pso = new PSO();
			for (int t = 0; t < 200; t++) {
				clusteredData = pso.cluster(data);
				double err = calcError(clusteredData);
				System.out.println(err);
			}
			break;
		case 5:																			//use ant colony optimization
			ACO aco = new ACO(data, 10);
			for (int t = 0; t < 200; t++) {
				clusteredData = aco.cluster();
				double err = calcError(clusteredData);
				System.out.println(err);
			}
			break;
		}
		in.close();
		System.out.println(calcError(clusteredData));
	}

	//this method is to calculate the "error" value, as explained in the header comment
	public static double calcError(ArrayList<Cluster> clusters) {
		double counter = 1;
		double total = 0;
		double ave = 0;
		for(Cluster cluster : clusters){																		//calc distance from each point to center
			double clusterDistance = 0;
			for(int i = 0; i < cluster.getMembers().size(); i++) {
				clusterDistance += cluster.getMembers().get(i).calcDistance(cluster.getCenter());
			}
			
			clusterDistance = clusterDistance / cluster.getMembers().size();
			total += clusterDistance;
			
			counter++;
			for (int i = 0; i < cluster.getMembers().size(); i++) {
				for (int j = 0; j < cluster.getMembers().size(); j++) {
					total += cluster.getMembers().get(i).calcDistance(cluster.getMembers().get(j));				//calc distance to each point
					counter++;	
				}
			}
			double numberConnections = cluster.getMembers().size();
			total += ((numberConnections)*(numberConnections-1)/2);
		}
		for (Cluster d : clusters) {																			//this block penalizes clusters if they are very close together
			for (Cluster e : clusters) {
				double distance = d.getCenter().calcDistance(e.getCenter());
				if (distance < 20) {
					total += distance;
					counter++;
				}
			}
		}
		ave = total / counter;										//then calculate the average distance between points
		return ave;
	}
}