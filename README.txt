# Fast entropy clustering of sparse high dimensional binary data - demo application

This package implements a method described in the paper:

Marek Śmieja, Szymon Nakoneczny, Jacek Tabor: Fast entropy clustering of sparse high dimensional binary data, IEEE International Joint Conference on Neural Networks (IJCNN 2016), pp. 2397-2404, 2016

The code is written in Java with use of Idea Intellij IDE.

To run SEC you need to provide properties file (exemplary file is placed in SEC/toy.properties). There are several input parameters to fill in:
-fileIn is a path to vectorized data set
-representation is a format of data set (dense/sparse)
-fileLog is a path to log file
-fileOut is a path to file with the results (clusters membership)
-clNum is the maximal number of cluster (the algorithm is able to reduce unnecessary clusters)
-initTimes is the number of restarts (the algorithm is non-deterministic and has to be restarted multiple times to find good local minimum)
-omega is a trade-off parameter (default value is 0.5; it ranges from 0 to 1)
-epsNum is a threshold used to determine cluster reduction (is the fraction of data that belongs to a given cluster falls below eps then this cluster is reduced)

To run SEC with "toy.properties" file, open the console in "SEC" directory and type: 

java -jar "out/artifacts/SEC_jar/SEC.jar" toy.properties

The clustering result will be placed in "fileOutMembership" file provided in your properties.

Directory "dane/manual" contains examplary data set describing chemical compounds acting on 5-HT1A receptor ligands.


For any questions or assist, email me at smieja.marek [at] gmail.com.
