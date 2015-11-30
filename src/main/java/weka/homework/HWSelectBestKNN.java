package weka.homework;


import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Created by jangwee on 15-11-30.
 */
public class HWSelectBestKNN extends IBk{

    public HWSelectBestKNN(int k) {
        super(k);
    }

    public HWSelectBestKNN(){
        super();
    }

    public void handOneOutForBestK(){
        super.crossValidate();
    }

    public static void main(String[] args){

        try {
            String trainingFilePath = Utils.getOption("t",args);
            if(trainingFilePath.length() == 0){
                throw new Exception("no input training file !");
            }

            DataSource trainingDataSource = new DataSource(trainingFilePath);
            Instances trainingInstances = trainingDataSource.getDataSet();
            trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

            HWSelectBestKNN kNNClassifier = new HWSelectBestKNN();
            kNNClassifier.setOptions(args);
            kNNClassifier.setDebug(true);
            kNNClassifier.buildClassifier(trainingInstances);
            kNNClassifier.handOneOutForBestK();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
}
