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

/**
 * Created by jangwee on 15-11-23.
 */


package weka.homework;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;


public class HWEvaluation  {

    private String mEvaluationDataRoot ;
    private int mNumFolds ;
    private Classifier mClassifier;

    private double[] accuracyOfTestingSet ;

    public HWEvaluation(String evaluationDataRoot,int numFolds,Classifier classifier){
        this.mEvaluationDataRoot = evaluationDataRoot;
        this.mNumFolds = numFolds;
        this.mClassifier = classifier;
        this.accuracyOfTestingSet = new double[numFolds];
    }

    public double[] getAccuracyOfTestingSet(){
        return this.accuracyOfTestingSet;
    }


    public void runEvaluation(){

        for(int i = 0 ; i < mNumFolds ; i ++ ){
            try {
                System.out.println("do "  + (i + 1) + "th fold cross validation : ");
                //Read data from arff file
                System.out.println( "construct training instances from : " + mEvaluationDataRoot + "/" + String.valueOf(i) + "_training.arff");
                System.out.println( "construct testing instances  from : " + mEvaluationDataRoot + "/" + String.valueOf(i) + "_testing.arff");

                DataSource trainDataSource = new DataSource(
                        mEvaluationDataRoot + "/" + String.valueOf(i) + "_training.arff");
                DataSource testDataSource = new DataSource(
                        mEvaluationDataRoot + "/" + String.valueOf(i) + "_testing.arff");


                Instances trainInstances = trainDataSource.getDataSet();
                Instances testInstances = testDataSource.getDataSet();

                //set class index
                trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
                testInstances.setClassIndex(testInstances.numAttributes() - 1);

                Evaluation evaluation = new Evaluation(testInstances);
                Classifier copiedClassifier = Classifier.makeCopy(mClassifier);
                copiedClassifier.buildClassifier(trainInstances);
                evaluation.evaluateModel(copiedClassifier, testInstances);
                this.accuracyOfTestingSet[i] = evaluation.pctCorrect();
                System.out.println(evaluation.toSummaryString());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    /**
     * -splitDir /home/jangwee/Desktop/Homework/DataMining/third/split -x 10
     * @param args
     */
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

        }catch(Exception ex){
            ex.printStackTrace();
        }

        Classifier classifier = new NaiveBayes();
        //Classifier classifier = new IBk();
        HWEvaluation hwEvaluation = new HWEvaluation(splitDataDictionary,numFold,classifier);
        hwEvaluation.runEvaluation();
    }
}
