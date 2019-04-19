package com.neu.group2;

import org.jfree.ui.RefineryUtilities;

public class Main {
	public static void main(String[] args) {
		Solution vehicleRoutingProblem = new Solution();
		double generationNum = 0;
		double totalFitness = 0;
		BestResult bestResult = null;
		final DynamicLineAndTimeSeriesChart demo = new DynamicLineAndTimeSeriesChart(
				"Dynamic Line And TimeSeries Chart");
		bestResult = vehicleRoutingProblem.solveVrp();
		totalFitness += bestResult.getBestFitness();
		generationNum += bestResult.getBestGenerationNum();
		for (int i = 0; i < vehicleRoutingProblem.getT(); i += 10) {
			DynamicLineAndTimeSeriesChart.data.add(vehicleRoutingProblem.getbestFitnessPerGene().get(i));
		}
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}
