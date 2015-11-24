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

import java.io.*;
import java.util.*;

public class HWUtils {

    /**
     * the root path of file
     */
    public static final String rootPath = "/home/jangwee/Desktop/Homework/DataMining/third";

    /**
     * convert ".names" file and ".data" file to ".arff" file
     * @param sourceUCINameFileName the file downloaded from uci_ml which is ending with extension ".names"
     * @param sourceUCIDataFileName the file downloaded from uci_ml which is ending with extension ".data"
     *                              (Note that it must be corresponding to the sourceUCINameFileName)
     * @param destArffName the file ending with extension ".arff" which is combined the sourceUCINameFileName and sourceUCIDataFileName
     * @throws FileNotFoundException
     */
    public static void UCINDF2Arff(String sourceUCINameFileName, String sourceUCIDataFileName, String destArffName) throws FileNotFoundException {
        File sourceNameFile = new File(sourceUCINameFileName);
        File sourceDataFile = new File(sourceUCIDataFileName);

        if (!sourceNameFile.exists()) {
            throw new FileNotFoundException("File :" + sourceUCINameFileName + "  is not exist");
        }
        if (!sourceDataFile.exists()) {
            throw new FileNotFoundException("File :" + sourceUCIDataFileName + "  is not exist");
        }

        File destArffFile = new File(destArffName);
        BufferedWriter destBufferedWriter;
        try {
            destBufferedWriter = new BufferedWriter(new FileWriter(destArffFile));
            BufferedReader nameBufferedReader = new BufferedReader(new FileReader(sourceNameFile));
            String readString;
            try {
                while ((readString = nameBufferedReader.readLine()) != null) {
                    destBufferedWriter.write("%" + readString + "\n");
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

            StringBuffer textBuffer = new StringBuffer();
            textBuffer.append("\n@relation Hayes-Roth");
            textBuffer.append("\n@attribute hobby {1,2,3}" +
                    "\n@attribute age {1,2,3,4}" +
                    "\n@attribute education_level {1,2,3,4}" +
                    "\n@attribute marital_status {1,2,3,4}" +
                    "\n@attribute class {1,2,3}");

            textBuffer.append("\n\n@data");

            try {
                destBufferedWriter.write(textBuffer.toString());
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            BufferedReader dataBufferedReader = new BufferedReader(new FileReader(sourceDataFile));
            try {
                while ((readString = dataBufferedReader.readLine()) != null) {
                    int startIndex = readString.indexOf(",");
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

    public static void splitTrainAndTestFile(String sourceFileName,String destFileDictionary,int numFolds)throws IOException{
        File souceFile = new File(sourceFileName);
        if(!souceFile.exists()){
            throw new IOException("File " + sourceFileName + "is not exist");
        }

        File destDic = new File(destFileDictionary);
        destDic.mkdir();

        BufferedReader sourceBufferedReader = new BufferedReader(new FileReader(souceFile));
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
        Collections.shuffle(instanceArray);

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


    public static void main(String[] args) {

        String nameFileName = rootPath + "/hayes-roth.names";
        String dataFileName = rootPath + "/hayes-roth.data";
        String splitDict = rootPath + "/split";
        String destArffFileName = rootPath + "/hayes-roth.arff";
        try {
            UCINDF2Arff(nameFileName, dataFileName, destArffFileName);
            splitTrainAndTestFile(destArffFileName, splitDict, 10);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
