/**
 * @author      Kavi Sarna
 * @andrewId    ksarna
 */
package org.myorg;

// Import necessary Java classes
import java.io.IOException;
import java.util.StringTokenizer;

// Hadoop configuration classes
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

// Hadoop filesystem classes
import org.apache.hadoop.fs.Path;

// Hadoop data type classes
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

// Hadoop job classes
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

// Hadoop input/output format classes
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

// Hadoop utility classes
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * A MapReduce program that counts the total number of aggravated assaults and robberies from crime data in Pittsburgh.
 */
public class AggrAssaultPlusRobbery extends Configured implements Tool {

    /**
     * Mapper class that processes each line of the crime data and emits
     * a count of 1 for each crime that is either an aggravated assault or robbery
     * 
     * LongWritable: input key type (byte offset in the file)
     * Text: input value type (line of text)
     * Text: output key type (crime type)
     * IntWritable: output value type (count of 1 for each occurrence)
     */
    public static class MapClass extends Mapper<LongWritable, Text, Text, IntWritable> {
        // Define a static instance of IntWritable to represent the count of 1
        private final static IntWritable one = new IntWritable(1);
        private Text crimeType = new Text("Total");                     // Key for the output, representing the total count of aggravated assaults and robberies
        
        /** 
         * The map method is called once for each key-value pair in the input split
         * 
         * @param key The input key (byte offset in the file)
         * @param value The input value (line of text)
         * @param context The context object for writing output key-value pairs
         * @throws IOException if an I/O error occurs
         * @throws InterruptedException if the task is interrupted
        */
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString();                                                         // Convert the input Text to a Java String
            String[] fields = line.split("\t");                                                     // Split the line into fields using tab as the delimiter
            
            // The crime type is in the 5th column (index 4)
            if (fields.length > 4) {
                String crime = fields[4].trim().toLowerCase();                                      // Get the crime type and convert it to lowercase for case-insensitive comparison
                
                // Check if the crime is either aggravated assault or robbery
                if (crime.contains("aggravated assault") || crime.contains("robbery")) {

                    context.write(crimeType, one);                                                  // Emit an intermediate key-value pair: (matching word, null)
                }
            }
        }
    }

    /**
     * Inner Reducer class that processes intermediate key-value pairs to generate final output key-value pairs
     * 
     * Text: input key type (crime type)
     * IntWritable: input value type (count of 1 for each occurrence)
     * Text: output key type (fixed string for target location)
     * IntWritable: output value type (total count of aggravated assaults and robberies)
     */
    public static class ReduceClass extends Reducer<Text, IntWritable, Text, IntWritable> {
        
        // The reduce method is called once for each key and its associated values
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int sum = 0;                                                    // Initialize the sum to zero

            // Iterate through all values for the current key (letter)
            for (IntWritable val : values) {
                sum += val.get();                                           // Add the value (count) to the sum
            }

            context.write(key, new IntWritable(sum));                       // Emit the final key-value pair: (letter, total count)
        }
    }

    /**
     * The run method is called to set up the job configuration and submit it to the Hadoop cluster
     * 
     * @param args Command-line arguments (input and output paths)
     * @return 0 if the job completed successfully, 1 otherwise
     */
    @Override
    public int run(String[] args) throws Exception {

        Configuration conf = getConf();                                                         // Get the Hadoop configuration from the Tool context
        Job job = Job.getInstance(conf, "aggrvatedassaultsplusrobberies");                      // Create a new MapReduce job with the configuration and a descriptive name
        job.setJarByClass(AggrAssaultPlusRobbery.class);                                        // Set the jar by finding the jar containing this class

        job.setMapperClass(MapClass.class);                                                     // Set the Mapper class for the job
        job.setReducerClass(ReduceClass.class);                                                 // Set the Reducer class for the job
        job.setCombinerClass(ReduceClass.class);                                                // Set the Reducer class for the job

        job.setOutputKeyClass(Text.class);                                                      // Set the key type for the job output
        job.setOutputValueClass(IntWritable.class);                                             // Set the value type for the job output

        job.setInputFormatClass(TextInputFormat.class);                                         // Set the input format class (how the input files are read)
        job.setOutputFormatClass(TextOutputFormat.class);                                       // Set the output format class (how the output files are written)

        FileInputFormat.addInputPath(job, new Path(args[0]));                                   // Set the input path for the job (from command-line argument)
        FileOutputFormat.setOutputPath(job, new Path(args[1]));                                 // Set the output path for the job (from command-line argument)

        return job.waitForCompletion(true) ? 0 : 1;                                             // Submit the job and wait for it to complete, returning 0 if successful, 1 otherwise
    }

    /**
     * Main method to run the AggrAssaultPlusRobbery job
     * 
     * @param args Command-line arguments (input and output paths)
     * @throws Exception if an error occurs during job execution
     */
    public static void main(String[] args) throws Exception {

        // Run the tool with the provided arguments
        int res = ToolRunner.run(new Configuration(), new AggrAssaultPlusRobbery(), args);

        // Exit with the result code from the tool
        System.exit(res);
    }
}
