

# 95-702 Distributed Systems       

## Distributed Computation

## lab9-MapReduceAndSpark

### Part 1. HDFS and MapReduce

The purpose of Part 1 is for the student to establish credentials
on the Heinz College High Performance Computing Cluster and to run
a simple MapReduce job.

You will find your login ID on Canvas. Look in the area where your grades are
normally posted. Look under "Cluster Login ID". There you will find a one or two
or three digit number. To form your login ID you must concatenate the string
'student' with the number expanded to exactly three digits. Here are some
examples:

If your number is 1 then your login ID is student001.
If you number is 17 then your login ID is student017.
If your number is 98 then your login ID is student098.
If you number is 102 then your login ID is student102.

Your initial password will be provided in class.

Here are two links to examine what is going on on the cluster:

On port 9870, there is an overview of the cluster:

http://jumbo2.heinz.cmu.local:9870/

On port 8050, we can examine nodes, scheduler, and tools:

http://jumbo2.heinz.cmu.local:8050/


Output from System.out.println() statements is available in log files.  
To view the logs, visit Tools/Local Logs on the the ResourceManager page at this URL:

http://jumbo2.heinz.cmu.local:8050/


Find your completed job in the list. On the left, click on the job ID.
Select the map or reduce task.
Select the task number and then the task logs.


Before doing this lab, you will need to have the ability to run a secure
shell. You may use putty or some other secure telnet client.

To work from home, you will first need to install Cisco's AnyConnect
available from CMU's software download page shown next.

https://www.cmu.edu/computing/services/endpoint/network-access/vpn/how-to/


The TA's will be using time stamps on files to verify on-time or late submissions.
So, please do not perform any unintended work after the deadline for an assignment.

Before beginning, I highly recommend that you make a bulk replacement in this file. That
is, I would suggest that you change every occurrence of the string "mm6" with the
user ID that you have been assigned, e.g., student003. You may do this with a local editor. In that way
the commands that you will use below will be tailored to your machine. For example,
student110 would change every occurrence of the string "mm6" to the string
"student110". This will prevent common typing errors. It will allow you to copy and
paste commands.

Note that the tab key is useful for showing your command history. After using the tab
key, you may hit return to execute commands that you previously ran.

Note too that there is a Unix help sheet on Canvas. It is named "UNIX Commands Quick List".
Note too that we have provided a short linux tutorial. See Canvas and look for
LinuxTutorial.mp4.

1.  Log in to the Hadoop cluster by using SSH to connect. Note:
the ID "mm6" is my ID. You need to change "mm6" to your ID.

```
ssh -l mm6  jumbo2.heinz.cmu.local

```

Note: If this ssh fails, it may be a problem with your DNS configuration. Use an IP address instead of the name jumbo2.heinz.cmu.local. Ask a colleague to ping the name (jumbo2.heinz.cmu.local) to see the IP address.

On a MAC, if you receive the error message
"No Matching Host Key Type Found"
then try the following (replace mm6 with your own ID):
```
ssh -oHostKeyAlgorithms=+ssh-rsa mm6@jumbo2.heinz.cmu.local
```

You must change your password now. Use the "passwd" command. PLEASE remember this
new password. Again, your initial password is provided in class.

Your password requires a capital letter, a number and one of
the following characters: !@#$%^&*()_-+=.

### :checkered_flag:** Now, complete the 'quiz' that is on Canvas so that your password is available to the TA's for the course. WE NEED THIS TO GRADE YOUR PROJECT 5. The quiz is called "Cluster Password Quiz".**

It is highly suggested that you continue and do the following steps.

2.  Your current directory is /home/mm6. Verify this with the "pwd" command.

3.  Create a directory named "input" and one named "output" in your
    /home/mm6 directory.

```
mkdir input
mkdir output
```

Verify this with the "ls" command.

4.  For testing, calculate PI with the following commands:

```

hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar pi 10 100000

```
Twenty mappers may be employed with the following:
```
hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar pi 20 100000

```

Verify: Did the system compute a reasonable value of PI?
How did we use parallelization to compute Pi? See the course slides.

Verify: Check port 8050. Did your job run to completion?

5.  Normally, to upload files, we will use "scp" (secure copy) to transfer files
    to the cluster.

6.  For now, simply construct a text file (pico or vi) and place it under your
    /home/mm6/input directory.

```
cd input

```
Verify with "pwd".

Run the pico editor and create a file called "test".

```
pico test
```

Copy and paste this file into test. By "this file" we mean the document you
are currently reading. In pico, use ^o followed by ^x. The ^ symbol is the control key.

^o  is used to write out the file to disk.
^x is used to exit the pico editor.

7.  Copy the directory where the input resides to HDFS. ***Note: This is a different file system.***

```
cd ..
hdfs dfs -copyFromLocal /home/mm6/input/test /user/mm6/input/test


```

8. Look in the HDFS input directory and see if test is there.

```
hdfs dfs -ls /user/mm6/input

```
9. You can view the file on HDFS with this command:

```
hdfs dfs -cat /user/mm6/input/test

```
10. Delete the output directory on HDFS:

```
hdfs dfs -rm -r /user/mm6/output

```

11. Run word count using MapReduce:

```
hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.2.1.jar wordcount /user/mm6/input /user/mm6/output


```
Note: we are counting all of the words in all of the files in the input directory.
In this case, we only have one file, i.e., the file named 'test'.

If you get an error then you may have to remove an old output directory with

```
hdfs dfs -rm -r /user/mm6/output

```

And run word count again.

12. We want to see the output. See if the result files have been produced:

```
hdfs dfs -ls /user/mm6/output

```
13. View the output file:
```
hdfs dfs -cat /user/mm6/output/part-r-00000
```
14. But we want to copy the output to our directory - not the directory that HDFS provides. Place the results in the output folder in your /home/mm6/output directory.
```
hdfs dfs -getmerge /user/mm6/output /home/mm6/output/mergedfile.txt

```

15. Examine the results:
```
cat /home/mm6/output/mergedfile.txt

```

16. How many times did the word 'the' appear in the file?

17. How many time did the word 'you' appear in the file?

18. You may view what jobs are running on the cluster with the command:

```
  mapred job -list



```

19. You can kill a job that is not making progress. Do this if you need to. You will need
the Job ID from the mapred job -list command.

```
mapred job -kill <job_id>


```

### Part 2. Apache Spark on IntelliJ

For the Spark part of this lab, we have had more success running JDK 8 than JDK 17.
JDK 17 has been tried several times with no luck.

Please download and install JDK 8 for the remainder of this lab.

There is a Canvas quiz named Lab9_Quiz that needs to be completed. You may complete the Lab9_Quiz when you have finished with this part of the lab.

0. Run IntelliJ and select File New project
1. Name the project Spark-Example-Lab9
2. Choose Java as the language
3. Choose Maven for the Build System
4. Use JDK 8 or JDK 1.8 (Same thing!)
5. Select Create and do the following to install the Spark library.
   a. Right click the project node and select Open Module Settings
   b. Choose Libraries/+/From Maven
   c. Enter org.apache.spark:spark-core_2.10:1.0.0
      Leave Transitive dependancies checked.
   d. Select OK, OK, Apply, OK
6. Drill down into src and right click the Java node and select new Java class
7. Create a Java class named WordCounter.java with the following content:


```

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class WordCounter {

    private static void wordCount(String fileName) {

        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("JD Word Counter");

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> inputFile = sparkContext.textFile(fileName);

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split(" ")));

        JavaPairRDD countData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);

        countData.saveAsTextFile("CountData");
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No files provided.");
            System.exit(0);
        }

        wordCount(args[0]);
    }
}
```

Note: If you get an error on the import of java.utils.Array, select File/Invalidate Caches, select all of the options, and then select "Invalidate and Restart".

8. We need a file to process. Right click the project node and select "New File".
Give the file the name hadoop-lab.txt.

9. Use the file that you are currently reading and copy it to hadoop-lab.txt.

10. We need to specify the file name as a command line parameter.
Select the project node. From the Run menu, select Edit Configurations.

11. You may need to "Add new run configuration...".
    Choose "Application".
    The main class is WordCounter.
    Set the command line argument (CLI arguments to your application) to the name of this file: hadoop-lab.txt
    Check that the working directory points to the directory holding hadoop-lab.txt.
    The configuration's Build and Run should show java 8 SDK and the working
    directory will provide a path to hadoop-lab.txt.

12. Compile and run the Java application.

13. The output file will be in a directory named CountData in your working directory.

14. If you get a "file already exists exception", be sure to delete the output directory
named "CountData" in your working directory. Spark does this so that a large output file is
not accidentally erased.


### :checkered_flag: Answer the 5 questions in the Canvas quiz named Lab9_Quiz.
