package com.neu.group2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Solution {
	//make sure the last car can accomplish the task
	static int maxqvehicle = 1024;
	static int maxdvehicle = 1024;
	Random ra = new Random(123);
	int K;// expected maximum car amount;
	int KK;// real used car amount;
	int clientNum;// chromosome length
	double punishWeight;// punish when the car over amount
	double crossRate, mutationRate;
	int populationScale;
	int T;// generation times
	int t;// latest generation times
	int[] bestGhArr;// the best Chromosome
	double bestFitness;// the best fitness
	int bestGenerationNum;// the generation of the best fitness
	double decodedEvaluation;// sum of decoding mileage
	double[][] vehicleInfoMatrix;// K from index 1, 0 : the maximum load, 1: maximum mileage, 2: speed
	int[] decodedArr;// the order of client
	double[][] distanceMatrix;// distance between clients
	double[] weightArr;// amount need of every clients
	int[][] oldMatrix;// row : each group , column : chromosome
	int[][] newMatrix;// row : each group , column : chromosome
	double[] fitnessArr;// fitness of every entity
	double[] probabilityArr;// accumulation fitness occupation of every entity
	double[] x1; //
	double[] y1;
	ArrayList<Double> bestFitnessPerGene; // the best chromosome of every generation

	public int getT() {
		return this.T;
	}

	public ArrayList<Double> getbestFitnessPerGene() {
		return this.bestFitnessPerGene;
	}

	// init data
	void initData() {
		int i, j;
		decodedEvaluation = 0;// sum of decoding mileage
		punishWeight = 300;// punish when the car over amount
		clientNum = 20;// chromosome length
		K = 5;// Expected the largest car amount
		populationScale = 100;// Population Scale
		crossRate = 0.9;
		mutationRate = 0.09;// mutation rate it's (1-Pc)*0.9=0.09
		T = 3000;// generation times
		bestFitness = 0;// the best Fitness
		vehicleInfoMatrix = new double[K + 2][3];// K from index 1, 0 : the maximum load, 1: maximum mileage, 2: speed
		bestGhArr = new int[clientNum];// the best Chromosome
		decodedArr = new int[clientNum];// the order of client
		distanceMatrix = new double[clientNum + 1][clientNum + 1];// distance between clients
		weightArr = new double[clientNum + 1];// amount need of every clients
		oldMatrix = new int[populationScale][clientNum];// row : each group , column : chromosome
		newMatrix = new int[populationScale][clientNum];// row : each group , column : chromosome
		fitnessArr = new double[populationScale];// fitness of every entity
		probabilityArr = new double[populationScale];// accumulation fitness occupation of every entity
		x1 = new double[clientNum + 1];
		y1 = new double[clientNum + 1];
		bestFitnessPerGene = new ArrayList<Double>();

		for (int i1 = 1; i1 < K + 1; i1++) {
			vehicleInfoMatrix[i1][0] = 8.0;
			vehicleInfoMatrix[i1][1] = 50.0;
		}
		vehicleInfoMatrix[K + 1][0] = maxqvehicle;
		vehicleInfoMatrix[K + 1][1] = maxdvehicle;

		// dispacher center
		x1[0] = 14.5;
		y1[0] = 13.0;
		weightArr[0] = 0.0;

		// random generate clients location
		for (int init = 1; init <= 20; init++) {
			x1[init] = ra.nextDouble() * 20;
			y1[init] = ra.nextDouble() * 20;
			weightArr[init] = ra.nextDouble() * 2;

		}

		double x = 0, y = 0;
		// init distanceMatrix
		int endIndex = clientNum + 1;
		for (i = 0; i < endIndex; i++) {
			for (j = 0; j < endIndex; j++) {
				x = x1[i] - x1[j];
				y = y1[i] - y1[j];
				distanceMatrix[i][j] = Math.sqrt(x * x + y * y);
			}
		}

	}

	// evaluate the chromosome
	double caculateFitness(int[] Gh) {
		// index from 0 to L-1
		int i, j;// i is car number，j is client number
		int flag;// over used car number
		double cur_d, cur_q, evaluation;// current mileage，current load，sum of mileage

		cur_d = distanceMatrix[0][Gh[0]];// Gh[0] is first client
		cur_q = weightArr[Gh[0]];

		i = 1;
		evaluation = 0;
		flag = 0;

		for (j = 1; j < clientNum; j++) {
			cur_q = cur_q + weightArr[Gh[j]];
			cur_d = cur_d + distanceMatrix[Gh[j]][Gh[j - 1]];

			// if load is get to maximum or distance get to maximum send next car
			if (cur_q > vehicleInfoMatrix[i][0] || cur_d + distanceMatrix[Gh[j]][0] > vehicleInfoMatrix[i][1]) {
				i = i + 1;
				evaluation = evaluation + cur_d - distanceMatrix[Gh[j]][Gh[j - 1]] + distanceMatrix[Gh[j - 1]][0];
				cur_d = distanceMatrix[0][Gh[j]];// from dispacher center to client
				cur_q = weightArr[Gh[j]];
			}
		}
		evaluation = evaluation + cur_d + distanceMatrix[Gh[clientNum - 1]][0];
		flag = i - K;// check the over amount car number
		if (flag < 0)
			flag = 0;
		evaluation = evaluation + flag * punishWeight;
		return 10 / evaluation;// compress evaluation

	}

	// get the order of client (chromosome)
	void decoding(int[] Gh) {
		int i, j;// i car number，j client number
		double cur_d, cur_q, evaluation;// current distance, current load, sum of distance
		cur_d = distanceMatrix[0][Gh[0]];// Gh[0] means the first client，
		cur_q = weightArr[Gh[0]];
		i = 1;// from car 1
		decodedArr[i] = 1;
		evaluation = 0;
		for (j = 1; j < clientNum; j++) {
			cur_q = cur_q + weightArr[Gh[j]];
			cur_d = cur_d + distanceMatrix[Gh[j]][Gh[j - 1]];
			if (cur_q > vehicleInfoMatrix[i][0] || cur_d + distanceMatrix[Gh[j]][0] > vehicleInfoMatrix[i][1]) {
				i = i + 1;
				decodedArr[i] = decodedArr[i - 1] + 1;//
				evaluation = evaluation + cur_d - distanceMatrix[Gh[j]][Gh[j - 1]] + distanceMatrix[Gh[j - 1]][0];
				cur_d = distanceMatrix[0][Gh[j]];
				cur_q = weightArr[Gh[j]];
			} else {
				decodedArr[i] = decodedArr[i] + 1;//
			}
		}
		decodedEvaluation = evaluation + cur_d + distanceMatrix[Gh[clientNum - 1]][0];
		KK = i;

	}

	void initGroup() {
		int i, k;
		int randomNum = 0;
		for (k = 0; k < populationScale; k++) {
			for (i = 0; i < clientNum; i++)
				oldMatrix[k][i] = i + 1;
			for (i = 0; i < clientNum; i++) {
				randomNum = ra.nextInt(clientNum);
				swap(oldMatrix[k], i, randomNum);
			}
		}
	}

	public void swap(int arr[], int index1, int index2) {
		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}

	// calculate accumulation of fitness
	void countRate() {
		int k;
		double sumFitness = 0;

		for (k = 0; k < populationScale; k++) {
			sumFitness += fitnessArr[k];
		}

		// count every one accumulation fitness
		probabilityArr[0] = fitnessArr[0] / sumFitness;
		for (k = 1; k < populationScale; k++) {
			probabilityArr[k] = fitnessArr[k] / sumFitness + probabilityArr[k - 1];
		}
	}

	void copyChrosome(int k, int kk) {
		System.arraycopy(oldMatrix[kk], 0, newMatrix[k], 0, clientNum);
	}

	// pick the best fitness child to the newMatrix and pick them randomly by
	// accumulation probability
	void selectBestChrosome() {
		int k, maxid;
		double maxevaluation;
		maxid = 0;
		maxevaluation = fitnessArr[0];
		for (k = 1; k < populationScale; k++) {
			if (maxevaluation < fitnessArr[k]) {
				maxevaluation = fitnessArr[k];
				maxid = k;
			}
		}
		bestFitnessPerGene.add(maxevaluation);

		if (bestFitness < maxevaluation) {
			bestFitness = maxevaluation;
			bestGenerationNum = t; // set current generation to the bestGeneration
			System.arraycopy(oldMatrix[maxid], 0, bestGhArr, 0, clientNum);
		}
		copyChrosome(0, maxid);// copy the highest fitness child to newMatrix
	}

	int select() {
		int k;
		double ran1;
		ran1 = Math.abs(ra.nextDouble());
		for (k = 0; k < populationScale; k++) {
			if (ran1 <= probabilityArr[k]) {
				break;
			}
		}
		return k;
	}

	// cross the chromosome in one group
	void oxCrossover(int k1, int k2) {
		int i, j, k, flag;
		int ran1, ran2, temp;
		int[] Gh1 = new int[clientNum];
		int[] Gh2 = new int[clientNum];
		ran1 = ra.nextInt(clientNum);
		ran2 = ra.nextInt(clientNum);
		while (ran1 == ran2)
			ran2 = ra.nextInt(clientNum);
		if (ran1 > ran2) {
			temp = ran1;
			ran1 = ran2;
			ran2 = temp;
		}
		flag = ran2 - ran1 + 1;

		for (i = 0, j = ran1; i < flag; i++, j++) {
			Gh1[i] = newMatrix[k2][j];
			Gh2[i] = newMatrix[k1][j];
		}
		// make sure no duplicate number in chromosome
		for (k = 0, j = flag; j < clientNum; j++) {
			i = 0;
			while (i != flag) {
				Gh1[j] = newMatrix[k1][k++];
				i = 0;
				while (i < flag && Gh1[i] != Gh1[j])
					i++;
			}
		}

		for (k = 0, j = flag; j < clientNum; j++) {
			i = 0;
			while (i != flag) {
				Gh2[j] = newMatrix[k2][k++];
				i = 0;
				while (i < flag && Gh2[i] != Gh2[j])
					i++;
			}
		}
		System.arraycopy(Gh1, 0, newMatrix[k1], 0, clientNum);
		System.arraycopy(Gh2, 0, newMatrix[k2], 0, clientNum);
	}

	void mutation(int k) {
		int ran1, ran2;
		ran1 = ra.nextInt(clientNum);
		ran2 = ra.nextInt(clientNum);
		while (ran1 == ran2)
			ran2 = ra.nextInt(clientNum);
		swap(newMatrix[k], ran1, ran2);

	}

	void evolution() {
		int k, selectId;
		double r;
		// Select the best chromosome
		selectBestChrosome();
		// select the child generation by accumulation probablity
		for (k = 1; k < populationScale; k++) {
			selectId = select();
			copyChrosome(k, selectId);
		}
		for (k = 1; k + 1 < populationScale / 2; k = k + 2) {
			r = Math.abs(ra.nextDouble());
			// crossover
			if (r < crossRate) {
				oxCrossover(k, k + 1);
			} else {
				r = Math.abs(ra.nextDouble());
				if (r < mutationRate) {
					mutation(k);
				}
				r = Math.abs(ra.nextDouble());
				if (r < mutationRate) {
					mutation(k + 1);
				}
			}
		}
		if (k == populationScale / 2 - 1)// the last one don't have cross pair
		{
			r = Math.abs(ra.nextDouble());
			if (r < mutationRate) {
				mutation(k);
			}
		}

	}

	public BestResult solveVrp() {
		int i, j, k;
		BestResult bestResult = new BestResult();
		// init data of the property
		initData();

		// init group
		initGroup();
		int[] tempGA = new int[clientNum];

		// calculate the fitness
		for (k = 0; k < populationScale; k++) {
			for (i = 0; i < clientNum; i++) {
				tempGA[i] = oldMatrix[k][i];
			}

			fitnessArr[k] = caculateFitness(tempGA);
		}

		// calculate the accumulation of fitness
		countRate();
		for (t = 0; t < T; t++) {
			evolution();
			// copy newMatrix to oldMatrix for the loop
			for (k = 0; k < populationScale; k++)
				System.arraycopy(newMatrix[k], 0, oldMatrix[k], 0, clientNum);
			// calculate the fitness
			for (k = 0; k < populationScale; k++) {
				System.arraycopy(oldMatrix[k], 0, tempGA, 0, clientNum);
				fitnessArr[k] = caculateFitness(tempGA);
			}
			// calculate the accumulation of fitness
			countRate();
		}
		System.out.println("We have " + T + " Generation");
		System.out.println("The Best Generation Exists In ：" + bestGenerationNum + "th child");
		System.out.println("The Best Fitness Which is 10/(Sum(Distance)+Punishment) is " + bestFitness);
		System.out.println("The Best Chromosome is " + Arrays.toString(bestGhArr));
		// decoding Best chromosome
		decoding(bestGhArr);
		System.out.println("The Best Chromosome Used car Amount：" + KK);
		System.out.println("The Best Chromosome Mileage: " + decodedEvaluation);
		String tefa = "";
		int tek;

		for (i = 1; i <= KK; i++) {
			tefa = "0-";
			tek = decodedArr[i - 1];
			for (j = tek, k = 2; j < decodedArr[i]; j++, k++) {
				tefa = tefa + bestGhArr[j] + "-";
			}
			tefa = tefa + "0";
			System.out.println("The " + i + "Car Route: " + tefa);
		}
		bestResult.setBestFitness(10 / bestFitness);
		bestResult.setBestGenerationNum(bestGenerationNum);
		return bestResult;
	}
}
