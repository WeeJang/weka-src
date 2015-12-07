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
 * Created by jangwee on 15-11-22.
 */

package weka.homework;

import weka.core.Utils;

import java.io.*;
import java.util.*;

public class HWUtils {

    /**
     * convert ".names" file and ".data" file to ".arff" file
     * @param sourceUCINameFileName the file downloaded from uci_ml which is ending with extension ".names"
     * @param sourceUCIDataFileName the file downloaded from uci_ml which is ending with extension ".data"
     *                              (Note that it must be corresponding to the sourceUCINameFileName)
     * @param destArffName the file ending with extension ".arff" which is combined the sourceUCINameFileName and sourceUCIDataFileName
     * @throws FileNotFoundException
     */
    public static void UCINDF2Arff(String sourceUCINameFilePath, String sourceUCIDataFilePath, String destArffPath) throws FileNotFoundException {
        File sourceNameFile = new File(sourceUCINameFilePath);
        File sourceDataFile = new File(sourceUCIDataFilePath);

        if (!sourceNameFile.exists()) {
            throw new FileNotFoundException("File :" + sourceUCINameFilePath + "  is not exist");
        }
        if (!sourceDataFile.exists()) {
            throw new FileNotFoundException("File :" + sourceUCIDataFilePath + "  is not exist");
        }

        File destArffFile = new File(destArffPath);
        BufferedWriter destBufferedWriter;
        try {
            destBufferedWriter = new BufferedWriter(new FileWriter(destArffFile));
            BufferedReader nameBufferedReader = new BufferedReader(new FileReader(sourceNameFile));
            String readString;
            StringBuffer flagTextBuffer = new StringBuffer();
            StringBuffer plaintextBuffer = new StringBuffer();
            StringBuffer datatextBuffer = new StringBuffer();
            String labelString = null;
            flagTextBuffer.append("\n@relation " + sourceUCIDataFilePath.substring(sourceUCIDataFilePath.lastIndexOf("/")+1,
                    sourceUCIDataFilePath.lastIndexOf(".")));
            try {
                while ((readString = nameBufferedReader.readLine()) != null) {
                    String outputLines = null;
                    if(readString.startsWith("|") || readString.length() == 0){
                        outputLines = "\n%" + readString;
                        plaintextBuffer.append(outputLines);
                    }else if(!readString.contains(":")){
                        if(readString.lastIndexOf(".") == readString.length() - 1){
                            readString = readString.substring(0,readString.length() - 1);
                        }
                        labelString = "\n@attribute label {" + readString  + "}" ;
                    }else{
                        if(readString.lastIndexOf(".") == readString.length() - 1){
                            readString = readString.substring(0,readString.length() - 1);
                        }
                        System.out.println(readString);
                        String[] attri = readString.split(":");
                        if(attri[1].contains("continuous")){
                            attri[1] = "NUMERIC";
                            outputLines = "\n@attribute " + attri[0] + " " + attri[1];
                        }else{
                            outputLines = "\n@attribute " + attri[0] + " {" + attri[1] + "}";
                        }
                        flagTextBuffer.append(outputLines);
                    }
                }
                nameBufferedReader.close();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            } finally {
                try {
                    nameBufferedReader.close();
                } catch (IOException fioex) {
                    fioex.printStackTrace();
                }
            }

            flagTextBuffer.append(labelString);
            flagTextBuffer.append("\n\n@data");

            try {
                destBufferedWriter.write(plaintextBuffer.toString());
                destBufferedWriter.write(flagTextBuffer.toString());
                destBufferedWriter.write(datatextBuffer.toString());
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            BufferedReader dataBufferedReader = new BufferedReader(new FileReader(sourceDataFile));
            try {
                while ((readString = dataBufferedReader.readLine()) != null) {
                    //for hayes-roth.data
//                    int startIndex = readString.indexOf(",");
                    //for abalone.data
                    int startIndex = -1;
                    destBufferedWriter.write("\n" + readString.substring(startIndex + 1));
                }
                dataBufferedReader.close();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            } finally {
                try {
                    dataBufferedReader.close();
                } catch (IOException fioex) {
                    fioex.printStackTrace();
                }
            }

            try {
                destBufferedWriter.close();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void splitTrainAndTestFile(String sourceFileName,String destFileDictionary,int numFolds,long seed)throws IOException{
        File sourceFile = new File(sourceFileName);
        if(!sourceFile.exists()){
            throw new IOException("File " + sourceFileName + "is not exist");
        }

        File destDic = new File(destFileDictionary);
        destDic.mkdir();

        BufferedReader sourceBufferedReader = new BufferedReader(new FileReader(sourceFile));
        String readString ;
        StringBuffer titleAndNameInfo = new StringBuffer();
        ArrayList<String> instanceArray = new ArrayList<>();
        Boolean splitFlag = false;
        while((readString = sourceBufferedReader.readLine()) != null){
            if(!splitFlag){
                titleAndNameInfo.append(readString + "\n");
            }else{
                instanceArray.add(readString);
            }

            if(readString.startsWith("@data")){
                splitFlag = true;
            }
        }

        //Random sort the instance array
        Collections.shuffle(instanceArray,new Random(seed));

        long instanceSize = instanceArray.size();
        long sizePerPartition = instanceSize / numFolds + 1 ;

        for(int i = 0 ; i < numFolds ; i ++ ){
            File traningFile = new File(new String(destDic.getAbsolutePath() + "/" + String.valueOf(i) + "_training.arff"));
            File testingFile = new File(new String(destDic.getAbsoluteFile() + "/" +  String.valueOf(i) + "_testing.arff"));
            BufferedWriter traningBufferWriter = new BufferedWriter(new FileWriter(traningFile));
            BufferedWriter testingBufferWriter = new BufferedWriter(new FileWriter(testingFile));

            try{
                traningBufferWriter.write(titleAndNameInfo.toString());
                testingBufferWriter.write(titleAndNameInfo.toString());
                Iterator<String> iterator = instanceArray.iterator();
                int currentIndex = 0 ;
                while(iterator.hasNext()){
                    String dataString = iterator.next() + "\n";
                    if(currentIndex >= i*sizePerPartition && currentIndex < (i+1)*sizePerPartition){
                        testingBufferWriter.write(dataString);
                    }else{
                        traningBufferWriter.write(dataString);
                    }
                    currentIndex ++ ;
                }
                traningBufferWriter.close();
                testingBufferWriter.close();
            }catch(IOException ioex){
                ioex.printStackTrace();
            }finally{
                try{
                    traningBufferWriter.close();
                    testingBufferWriter.close();
                }catch(IOException ioex){
                    ioex.printStackTrace();
                }
            }
        }

    }

    public static void arrayWriter(ArrayList<Integer> arrayList1,ArrayList<Double> arrayList2,String outputURI){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputURI)));
            bw.write(arrayList1.toString() + "\n");
            bw.write(arrayList2.toString() + "\n");
            bw.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    /**
     * -root-path /home/jangwee/Desktop/Homework/DataMining/third/backup -namesfile hayes-roth.names -datafile hayes-roth.data -split-dir split -outArff hayes-roth.arff  -x 10 -s 17
     * @param args
     */
    public static void main(String[] args) {

        long seed = 0L ;
        int numFold = 0;
        String rootPath = null;
        String splitDirName = null;
        String namesFileName = null;
        String dataFileName = null;
        String arffFileName = null;
        try {
            rootPath = Utils.getOption("root-path", args);
            if(rootPath.length() == 0){
                throw new Exception("No  root path of names file & data file  given!");
            }

            splitDirName = Utils.getOption("split-dir", args);
            if(splitDirName.length() == 0){
                throw new Exception("No  name of split data dictionary  given!");
            }

            namesFileName = Utils.getOption("namesfile", args);
            if(namesFileName.length() == 0){
                throw new Exception("No  names file  given!");
            }

            dataFileName = Utils.getOption("datafile", args);
            if(dataFileName.length() == 0){
                throw new Exception("No  data file  given!");
            }

            arffFileName = Utils.getOption("outArff", args);
            if(arffFileName.length() == 0){
                arffFileName = "output.arff";
            }

            String seedString = Utils.getOption("s",args);
            if(seedString.length() == 0){
                seed = 1;
            }else{
                seed = Long.parseLong(seedString);
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

        String nameFilePath = rootPath + "/" + namesFileName;
        String dataFilePath = rootPath + "/" + dataFileName;
        String splitDict = rootPath + "/" + splitDirName;
        String destArffFilePath = rootPath + "/" + arffFileName;
        try {
            UCINDF2Arff(nameFilePath, dataFilePath, destArffFilePath);
//            UCINDF2Arff(nameFilePath, "/home/jangwee/Desktop/Homework/DataMining/third/newdata2/adultclear.test", "/home/jangwee/Desktop/Homework/DataMining/third/newdata2/adult_test.arff");
//            splitTrainAndTestFile(destArffFilePath, splitDict, numFold,seed);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
