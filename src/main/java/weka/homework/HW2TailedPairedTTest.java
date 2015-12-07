/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package weka.homework;

/**
 * Created by jangwee on 15-12-7.
 */

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.core.Utils;

import java.lang.Math;
import java.util.Arrays;

public class HW2TailedPairedTTest {


    public static boolean mDebug = false;

    /**
     * get the mean of sample array
     * @param array the sample array
     * @param lengthofarray length of array
     * @return the mean of array
     */
    private static double getMeanOfArray(double[] array,int lengthofarray){
        double accum = 0.0;
        for(int i = 0 ; i < lengthofarray ; i ++ ){
            accum += array[i];
        }
        return accum / lengthofarray ;
    }

    /**
     * get the variance of sample array
     * @param array the sample array
     * @param lengthofarray length of array
     * @return the variance of array
     */
    private static double getVarianceOfArray(double[] array,int lengthofarray){
        double mean = getMeanOfArray(array, lengthofarray);
        double accum = 0.0 ;
        for(int i = 0 ; i < lengthofarray ; i ++){
            accum += Math.pow((array[i] - mean),2);
        }
        return accum / (lengthofarray - 1);
    }

    /**
     * get the T value of two sample arrays
     * @param array1 one sample array
     * @param array2 the other sample array
     * @param lengthofarray the length of 2 array
     * @return the T value
     */
    private static double getTvalue(double[] array1,double[] array2,int lengthofarray){
        double meanOfArray1 = getMeanOfArray(array1,lengthofarray);
        double meanOfArray2 = getMeanOfArray(array2, lengthofarray);
        double varianceOfArray1 = getVarianceOfArray(array1, lengthofarray);
        double varianceOfArray2 = getVarianceOfArray(array2, lengthofarray);
        double varianceOfDifferenceArrayMean = Math.pow((varianceOfArray1 + varianceOfArray2) / lengthofarray ,0.5);

        if(mDebug){
            System.out.println("==========two tailed paired t-test Debug information ==========" +
                               "\nmeanOfArray1 : " + meanOfArray1 +
                               "\nmeanOfArray2 : " + meanOfArray2 +
                               "\nvarianceOfArray1 : " + varianceOfArray1 +
                               "\nvarianceOfArray2 : " + varianceOfArray2 +
                               "\nvarianceOfDifferenceArrayMean : " + varianceOfDifferenceArrayMean +
                               "================================================================");
        }
        return (meanOfArray1 - meanOfArray2) / varianceOfDifferenceArrayMean ;
    }


    public static void main(String[] args){
        int numFold = 0 ;
        String splitDataDictionary = null;
        try {
            splitDataDictionary = Utils.getOption("splitDir", args);
            if(splitDataDictionary.length() == 0){
                throw new Exception("No input arff file given!");
            }

            String numFoldString = Utils.getOption("x",args);
            if(numFoldString.length() == 0){
                numFold = 10;
            }else{
                numFold = Integer.parseInt(numFoldString);
            }

            mDebug = Utils.getFlag("D",args);

        }catch(Exception ex){
            ex.printStackTrace();
        }

        Classifier naiveBayes = new NaiveBayes();
        Classifier kNN = new IBk(21);
        HWEvaluation naiveBayesEvaluation = new HWEvaluation(splitDataDictionary,numFold,naiveBayes);
        HWEvaluation kNNEvaluation = new HWEvaluation(splitDataDictionary,numFold,kNN);
        naiveBayesEvaluation.runEvaluation();
        kNNEvaluation.runEvaluation();
        double[] naiveBayesAccuracy = naiveBayesEvaluation.getAccuracyOfTestingSet();
        double[] kNNBayesAccuracy = kNNEvaluation.getAccuracyOfTestingSet();
        double TValue = getTvalue(naiveBayesAccuracy,kNNBayesAccuracy,numFold);
        System.out.println("=========two tailed paired t-test result ===========");
        System.out.println("NaiveBayesAccuracyList : " + Arrays.toString(naiveBayesAccuracy));
        System.out.println("KNNAccuracyList : " + Arrays.toString(kNNBayesAccuracy));
        System.out.println("Two classifier T value : " + TValue);
    }
}
