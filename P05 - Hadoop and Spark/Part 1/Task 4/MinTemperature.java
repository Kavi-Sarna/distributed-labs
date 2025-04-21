/**
 * @author      Kavi Sarna
 * @andrewId    ksarna
 */
package edu.cmu.andrew.mm6;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

/**
 * MinTemperature - Driver program to find minimum temperature by year
 * Modified from MaxTemperature to use MinTemperatureMapper and MinTemperatureReducer
 */
public class MinTemperature {

    /**
     * main - The main method to run the job
     * 
     * @param args - The input and output paths
     * @throws IOException - If there is an error in the job configuration or execution
     */
    public static void main(String[] args) throws IOException {

        // Check for the correct number of arguments
        if (args.length != 2) {
            System.err.println("Usage: MinTemperature <input path> <output path>");
            System.exit(-1);
        }
        
        // Create a new JobConf (job configuration) object 
        // Pass the MaxTemperature class to identify the JAR file containing this class
        JobConf conf = new JobConf(MinTemperature.class);

        conf.setJobName("Min temperature");                                         // Set a descriptive name for the job that will appear in the Hadoop UI
        
        FileInputFormat.addInputPath(conf, new Path(args[0]));                      // Set the input path for the job (from command-line argument)
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));                    // Set the output path for the job (from command-line argument)
        
        conf.setMapperClass(MinTemperatureMapper.class);                            // Set the Mapper class for the job
        conf.setReducerClass(MinTemperatureReducer.class);                          // Set the Reducer class for the job
        
        conf.setOutputKeyClass(Text.class);                                         // Set the key type for the job output
        conf.setOutputValueClass(IntWritable.class);                                // Set the value type for the job output
        
        // Submit the job to the Hadoop cluster and wait for completion
        // This blocks until the job is complete
        JobClient.runJob(conf);
    }
}
