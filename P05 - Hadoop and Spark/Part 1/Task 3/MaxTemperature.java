package edu.cmu.andrew.mm6;

// Hadoop filesystem classes
import org.apache.hadoop.fs.Path;

// Hadoop data type classes
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

// Hadoop input/output format classes
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;

// Hadoop job classes
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

// Import necessary Java classes
import java.io.*;

/**
 * MaxTemperature: A MapReduce program that finds the maximum temperature from a dataset.
 * This program reads a file containing temperature data and outputs the maximum temperature.
 * The input file should be in the format: <year>,<temperature>
 */
public class MaxTemperature {

    /**
     * Main method that sets up and runs the MapReduce job.
     * 
     * @param args Command-line arguments: input path and output path
     * @throws IOException If there is an error during job setup or execution
     */
    public static void main(String[] args) throws IOException {

        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperature <input path> <output path>");
            System.exit(-1);                                                    // Exit the program with an error code
        }

        // Create a new JobConf (job configuration) object 
        // Pass the MaxTemperature class to identify the JAR file containing this class
        JobConf conf = new JobConf(MaxTemperature.class);

        conf.setJobName("Max temperature");                                     // Set a descriptive name for the job that will appear in the Hadoop UI

        FileInputFormat.addInputPath(conf, new Path(args[0]));                  // Specify the input path for the job from the first command-line argument
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));                // Specify the output path for the job from the second command-line argument
        
        // Set the Mapper class for the job to MaxTemperatureMapper
        // This class will process the input records and emit intermediate key-value pairs
        conf.setMapperClass(MaxTemperatureMapper.class);

        // Set the Reducer class for the job to MaxTemperatureReducer
        // This class will process the intermediate key-value pairs and produce the final output
        conf.setReducerClass(MaxTemperatureReducer.class);

        // Set the output key class for the job to Text
        // The output keys will be of Text type (e.g., year or location identifiers)
        conf.setOutputKeyClass(Text.class);

        // Set the output value class for the job to IntWritable
        // The output values will be of IntWritable type (e.g., temperature values)
        conf.setOutputValueClass(IntWritable.class);

        // Submit the job to the Hadoop cluster and wait for completion
        // This blocks until the job is complete
        JobClient.runJob(conf);
    }
}
