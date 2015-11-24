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
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class HWEvaluation  {

    private String mEvaluationDataRoot ;
    private int mNumFolds ;
    private Classifier mClassifier;

    public HWEvaluation(String evaluationDataRoot,int numFolds,Classifier classifier){
        this.mEvaluationDataRoot = evaluationDataRoot;
        this.mNumFolds = numFolds;
        this.mClassifier = classifier;
    }

    public void runEvaluation(){

        for(int i = 0 ; i < mNumFolds ; i ++ ){
            try {
                System.out.println("====================================");
                System.out.println(mEvaluationDataRoot + "/" + String.valueOf(i) + "_training.arff");
                //Read data from arff file
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
                System.out.println(evaluation.toSummaryString());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }


    public static void main(String[] args){
        String splitData = HWUtils.rootPath + "/split";
        Classifier classifier = new NaiveBayesSimple();
        int numFolds = 10;
        HWEvaluation hwEvaluation = new HWEvaluation(splitData,numFolds,classifier);
        hwEvaluation.runEvaluation();
    }
}
