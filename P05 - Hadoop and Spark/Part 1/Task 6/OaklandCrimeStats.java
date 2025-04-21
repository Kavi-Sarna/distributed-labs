/**
 * @author      Kavi Sarna
 * @andrewId    ksarna
 */
package org.myorg;

// Import necessary Java classes
import java.io.IOException;

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
 * A MapReduce program that counts the total number of aggravated assault crimes 
 * that occurred within 100 meters of 3803 Forbes Avenue in Oakland.
 */
public class OaklandCrimeStats extends Configured implements Tool {

    /**
     * Mapper class that processes each line of the crime data and emits
     * a count of 1 for each aggravated assault within 100 meters of target location
     * 
     * LongWritable: input key type (byte offset in the file)
     * Text: input value type (line of text)
     * Text: output key type (fixed string for target location)
     * IntWritable: output value type (count of 1 for each occurrence)
     */
    public static class MapClass extends Mapper<LongWritable, Text, Text, IntWritable> {
        // Constants
        private final static IntWritable one = new IntWritable(1);
        private final static Text result = new Text("Total aggravated assaults near 3803 Forbes Ave");
        // Target location coordinates (3803 Forbes Avenue)
        private final static double TARGET_X = 1354326.897;
        private final static double TARGET_Y = 411447.7828;
        // 100 meters in feet (state plane coordinates are in feet)
        private final static double METERS_100_IN_FEET = 328.084;
        

        /**
         * The map method is called once for each key-value pair in the input split
         * 
         * @param key The input key (byte offset in the file)
         * @param value The input value (line of text)
         * @param context The context object to write output
         * @throws IOException If an I/O error occurs
         * @throws InterruptedException If the task is interrupted
         */
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] fields = line.split("\t");
            
            // Need at least 5 columns for coordinates and crime type
            if (fields.length >= 5) {
                try {
                    // Get X and Y coordinates from first two columns
                    double x = Double.parseDouble(fields[0].trim());
                    double y = Double.parseDouble(fields[1].trim());
                    
                    // Check if the crime is aggravated assault
                    String crime = fields[4].trim().toLowerCase();
                    
                    if (crime.contains("aggravated assault")) {
                        // Calculate distance using Pythagorean theorem
                        double distance = Math.sqrt(
                            Math.pow(x - TARGET_X, 2) + 
                            Math.pow(y - TARGET_Y, 2)
                        );
                        
                        // Check if distance is less than 100 meters (in feet)
                        if (distance <= METERS_100_IN_FEET) {
                            context.write(result, one);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Skip lines that don't have proper number format
                    // This handles header rows or malformed data
                }
            }
        }
    }

    /**
     * Reducer class that sums up the counts for qualifying crimes
     * 
     * Text: input key type (fixed string for target location)
     * IntWritable: input value type (count of 1 for each occurrence)
     * Text: output key type (fixed string for target location)
     * IntWritable: output value type (total count of aggravated assaults)
     */
    public static class ReduceClass extends Reducer<Text, IntWritable, Text, IntWritable> {
        
        // The reduce method is called once for each key and its associated values
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int sum = 0;                                                                // Initialize the sum to zero

            // Iterate through all values for the current key (fixed string)
            for (IntWritable val : values) {
                sum += val.get();                                                       // Add the value (count) to the sum
            }

            context.write(key, new IntWritable(sum));                                   // Emit the final key-value pair: (fixed string, total count)
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

        Configuration conf = getConf();                                                                 // Get the Hadoop configuration from the Tool context
        Job job = Job.getInstance(conf, "oaklandcrimestats");                                           // Create a new MapReduce job with the configuration and a descriptive name
        job.setJarByClass(OaklandCrimeStats.class);                                                     // Set the jar by finding the jar containing this class

        job.setMapperClass(MapClass.class);                                                             // Set the Mapper class for the job
        job.setReducerClass(ReduceClass.class);                                                         // Set the Reducer class for the job
        job.setCombinerClass(ReduceClass.class);                                                        // Set the Reducer class for the job

        job.setOutputKeyClass(Text.class);                                                              // Set the key type for the job output
        job.setOutputValueClass(IntWritable.class);                                                     // Set the value type for the job output

        job.setInputFormatClass(TextInputFormat.class);                                                 // Set the input format class (how the input files are read)
        job.setOutputFormatClass(TextOutputFormat.class);                                               // Set the output format class (how the output files are written)

        FileInputFormat.addInputPath(job, new Path(args[0]));                                           // Set the input path for the job (from command-line argument)
        FileOutputFormat.setOutputPath(job, new Path(args[1]));                                         // Set the output path for the job (from command-line argument)

        return job.waitForCompletion(true) ? 0 : 1;                                                     // Submit the job and wait for it to complete, returning 0 if successful, 1 otherwise
    }

    /**
     * Main method to run the OaklandCrimeStats job
     * 
     * @param args Command-line arguments (input and output paths)
     * @throws Exception If an error occurs during job execution
     */
    public static void main(String[] args) throws Exception {

        // Run the tool with the provided arguments
        int res = ToolRunner.run(new Configuration(), new OaklandCrimeStats(), args);

        // Exit with the result code from the tool
        System.exit(res);
    }
}
