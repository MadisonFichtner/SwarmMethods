package network;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/*	Main class drives the creation and training of networks for Project 4. It reads in data files (which we manually have to specify) and then
 * 	gives the user an option of clustering that data using k-means, db-scan, a competitive learning neural network, PSO, or ACO
 */

public class Main {
	public static void main(String args[]) {
		ArrayList<DataPoint> data = new ArrayList<DataPoint>();						//create list of samples to use - dataset essentially
		int numInputs = 0;
		int numDataPoints = 0;
		String filename = "wine.data";
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
				}
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
		}

		for(DataPoint d : data){
			for(double input : d.getFeatures()){
				System.out.print(input + " ");
			}
			System.out.println();
		}

		int numHidLayers = 2;															//specify network configurations - here is where we will tune the CNN
		int numHidNodes = 20;
		int numOutputs = 1;
		int hiddenActivation = 2; 			//sigmoidal
		int outputActivation = 1; 			//linear for function approximation

		Scanner in = new Scanner(System.in);											//grab user input for the algorithm to use
		System.out.println("What algorithm do you want to use?");
		System.out.println("\t1)K-Means Clustering"); 
		System.out.println("\t2)DB-Scan");
		System.out.println("\t3)Competitive Learning Network");
		System.out.println("\t4)Particle Swarm Optimization (PSO)");
		System.out.println("\t5)Ant Colony Optimization (ACO)");

		int selection = in.nextInt();
		//in.close();

		Collections.shuffle(data);													//randomize the data

		//cluster the data using chosen algorithm
		ArrayList<Cluster> clusteredData;
		switch(selection){
		case 1:	
			System.out.println("How many centroids?");
			int k = in.nextInt();
			KMeans kmeans = new KMeans(data, k);
			clusteredData = kmeans.cluster(data);
			for(int i = 0; i < clusteredData.size(); i++) {
				System.out.println("Cluster " + (i+1) + "'s average distance between data points: " + calcAverageDistance(clusteredData.get(i)));
			}
			break;
		case 2:	
			System.out.println("Enter epsilon (max distance between neighbors): ");
			double epsilon = in.nextDouble();
			System.out.println("Enter minimum points required to make cluster: ");
			int minPoints = in.nextInt();
			DBScan dbScan = new DBScan(data, epsilon, minPoints);
			clusteredData = dbScan.cluster(data);
			for(int i = 0; i < clusteredData.size(); i++) {
				System.out.println("Cluster " + (i+1) + "'s average distance between data points: " + calcAverageDistance(clusteredData.get(i)));
			}
			break;
		case 3:
			CNN cnn = new CNN(numInputs, numHidLayers, numHidNodes, numOutputs, hiddenActivation, outputActivation);
			clusteredData = cnn.cluster(data);
			break;
		case 4:		
			PSO pso = new PSO();
			double total = 0;
			System.out.println("Average distance between points in the clusters is:");
			for (int t = 0; t < 500; t++) {
				clusteredData = pso.cluster(data);
				for (int i = 0; i < clusteredData.size(); i++) {
					double err = calcError(clusteredData.get(i));
					total += err;
				}
				double ave = total / clusteredData.size();
				System.out.println(ave);
				total = 0;
			}
			break;
		case 5:
			ACO aco = new ACO(data, 5);
			total = 0;
			for (int t = 0; t < 50; t++) {
				clusteredData = aco.cluster();
				for (int i = 0; i < clusteredData.size(); i++) {
					double err = calcError(clusteredData.get(i));
					total += err;
				}
				double ave = total / clusteredData.size();
				System.out.println(ave);
				total = 0;
			}
			break;
		}
		in.close();
	}
	
	/*
	 * returns the average distance from centroid to members of the cluster
	 */
	private static double calcAverageDistance(Cluster cluster) {
		double averageDistance = 0;
		for(int i = 0; i < cluster.getMembers().size(); i++) {
			for(int j = 0; j < cluster.getMembers().size(); j++) {
				averageDistance += cluster.getMembers().get(i).calcDistance(cluster.getMembers().get(j));
			}
		}
		averageDistance = averageDistance / cluster.getMembers().size();
		return averageDistance;
	}

	public static double calcError(Cluster c) {
		double counter = 1;
		double total = 0;
		double ave = 0;
		for (int i = 0; i < c.getMembers().size(); i++) {
			for (int j = 0; j < c.getMembers().size(); j++) {
				total += c.getMembers().get(i).calcDistance(c.getMembers().get(j));				//calc distance to each point
				counter++;	
			}
		}
		ave = total / counter;										//then calculate the average distance between points
		return ave;
	}
}