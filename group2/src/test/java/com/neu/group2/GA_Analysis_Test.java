package com.neu.group2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.ietf.jgss.GSSContext;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("ALL")
public class GA_Analysis_Test {
	static Solution ga = new Solution();
	
	@Test
	public void calculateFitnessTest() {
		ga.initData();
		ga.initGroup();
		double fit = ga.caculateFitness(ga.oldMatrix[0]);
		System.out.println("fit:"+fit);
	}
	
	// check the fitness function is right
	@Test
	public void countRateTest() throws Exception{
		ga.populationScale = 5;
		ga.fitnessArr = new double[]{1,2,3,4,5};
		ga.countRate();
		double[] probabilityArr = ga.probabilityArr;
		double expected = 0;
		for(double d : ga.fitnessArr) {
			expected += d/15;
		}
		assertEquals(expected, probabilityArr[4],0.0); 
	}
	
	@Test
	public void selectTest() {
		//make sure high probability can get choose more times
		ga.populationScale = 5;
		ga.probabilityArr = new double[]{0.1,0.2,0.3,0.4,1};
		ga.ra = new Random();
		int count = 0;
		for(int i=0;i<1000;i++) {
			int k = ga.select();
			if(k==4) {
				count++;
			}
		}
		System.out.println(count);
		assertTrue(count>=500);
	}
	
	@Test
	public void oxCrossoverTest() {
		ga.initData();
		ga.initGroup();
		// to test assume newMatrix also is init status
		ga.newMatrix = ga.oldMatrix;
		int[][] temp = new int[2][ga.clientNum]; 
		for(int i=0;i<2;i++) {
			for(int j=0;j<ga.clientNum;j++) {
				temp[i][j] = ga.newMatrix[i][j];
			}
		}
		ga.oxCrossover(0, 1);
		//evaluate if the chromosome is different and is there any value is duplicated value in one chromesome
		int count = 0;
		Set<Integer> set = new HashSet<Integer>();
		for(int i=0;i<2;i++) {
			for(int j=0;j<ga.clientNum;j++) {
				set.add(ga.newMatrix[i][j]);
				if(temp[i][j] == ga.newMatrix[i][j]) {
					count++;
				}
			}
			if(set.size()<ga.clientNum) {
				assertFalse(false);
			}
			set.clear();
			assertFalse(count==ga.clientNum);
			count = 0;
		}
	}
	
	@Test
	/**
	 * @param k the nth chromosome this case choose 0 to test
	 */
	public void mutationTest() {
		int k = 0 ;
		ga.clientNum = 5;
		ga.ra = new Random();
		ga.newMatrix = new int[][] {{1,2,3,4,5}};
		ga.mutation(0);
		System.out.println(Arrays.toString(ga.newMatrix[0]));
	}
	
	@Test
	public void optimalTest() {
		ga.initData();
		ga.initGroup();
		int[] tempGA = new int[ga.clientNum];
		 
        // 计算初始化种群适应度，Fitness[max]
        for (int k = 0; k < ga.populationScale; k++) {
            for (int i = 0; i < ga.clientNum; i++) {
                tempGA[i] = ga.oldMatrix[k][i];
            }
            ga.fitnessArr[k] = ga.caculateFitness(tempGA);
        }
       
        //sum of original fitness
        double originalSum = 0;
        for(int i=0;i<ga.fitnessArr.length;i++) {
        	originalSum += ga.fitnessArr[i];
        }
        ga.countRate();
		ga.evolution();
		for (int k = 0; k < ga.populationScale; k++) {
            for (int i = 0; i < ga.clientNum; i++) {
                tempGA[i] = ga.newMatrix[k][i];
            }
            ga.fitnessArr[k] = ga.caculateFitness(tempGA);
        }
		double evolutionSum = 0;
		for(int i=0;i<ga.fitnessArr.length;i++) {
			evolutionSum += ga.fitnessArr[i];
        }
		
		System.out.println("original:"+originalSum+" after: "+evolutionSum);
		assertTrue((evolutionSum-originalSum)>0);
	}
	
	
	

}
