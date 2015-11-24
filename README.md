just for the homework_part1 of DM.

============================

Usage:

1 place the package "homework" under "src/mian/java/weka"

2 change  HWUtils.java :
    [line31] public static final String rootPath = "/home/jangwee/Desktop/Homework/DataMining/third";
  into the corresponding  dictionary of your machine,which contains the *.names and *.data file.

3 run the HWUils.java to convert files(*.names *.data) to file(*.arff,which can be found at the rootPath) and split file(*.arff) into 20 files(traning and testing,which can be found at  the rootPath/split/).

4  run the HWEvalution to call 10-cross-validataion.


============================
Note:
 the demo data set which I used is :
  -- hayes-roth.data & hayes-roth.names
