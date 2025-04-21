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
 * LetterCounter: A MapReduce program that counts the frequency of each letter in the input file.
 * This is a modification of the WordCount example to count individual letters instead of words.
 */
public class LetterCounter extends Configured implements Tool {

    /**
     * Inner Mapper class that processes input key-value pairs to generate intermediate key-value pairs
     * 
     * LongWritable: input key type (byte offset in the file)
     * Text: input value type (line of text)
     * Text: output key type (character)
     * IntWritable: output value type (count of 1 for each occurrence)
     */
    public static class MapClass extends Mapper<LongWritable, Text, Text, IntWritable> {
        
        private final static IntWritable one = new IntWritable(1);                          // Static final variable to avoid creating a new object for each map call
        private Text letter = new Text();                                                   // Reusable Text object to avoid object creation overhead

        // The map method is called once for each key-value pair in the input split
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String line = value.toString().toLowerCase();                                   // Convert to lowercase to count letters case-insensitively

            // Process each character in the line one by one
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);                                                    // Get the current character at position i
                
                // Only process alphabetic characters (a-z), ignore numbers, punctuation, etc.
                if (Character.isLetter(c)) {
                    letter.set(String.valueOf(c));                                          // Convert the character to a String and set it to our reusable Text object
                    context.write(letter, one);                                             // Emit an intermediate key-value pair: (letter, 1)
                }
            }
        }
    }

    /**
     * Inner Reducer class that processes intermediate key-value pairs to generate final output key-value pairs
     * 
     * Text: input key type (letter)
     * IntWritable: input value type (count of occurrences)
     * Text: output key type (letter)
     * IntWritable: output value type (total count of occurrences)
     */
    public static class ReduceClass extends Reducer<Text, IntWritable, Text, IntWritable> {

        // The reduce method is called once for each key and its associated values
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            
            int sum = 0;                                                            // Initialize the sum to zero

            // Iterate through all values for the current key (letter)
            for (IntWritable val : values) {
                sum += val.get();                                                   // Add the value (count) to the sum
            }

            context.write(key, new IntWritable(sum));                               // Emit the final key-value pair: (letter, total count)
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

        Configuration conf = getConf();                                         // Get the Hadoop configuration from the Tool context
        Job job = Job.getInstance(conf, "lettercount");                         // Create a new MapReduce job with the configuration and a descriptive name
        job.setJarByClass(LetterCounter.class);                                 // Set the jar by finding the jar containing this class

        job.setMapperClass(MapClass.class);                                     // Set the Mapper class for the job
        job.setReducerClass(ReduceClass.class);                                 // Set the Reducer class for the job

        job.setOutputKeyClass(Text.class);                                      // Set the key type for the job output
        job.setOutputValueClass(IntWritable.class);                             // Set the value type for the job output

        job.setInputFormatClass(TextInputFormat.class);                         // Set the input format class (how the input files are read)
        job.setOutputFormatClass(TextOutputFormat.class);                       // Set the output format class (how the output files are written)

        FileInputFormat.addInputPath(job, new Path(args[0]));                   // Set the input path for the job (from command-line argument)
        FileOutputFormat.setOutputPath(job, new Path(args[1]));                 // Set the output path for the job (from command-line argument)

        return job.waitForCompletion(true) ? 0 : 1;                             // Submit the job and wait for it to complete, returning 0 if successful, 1 otherwise
    }

    /**
     * Main method to run the LetterCounter job
     * 
     * @param args Command-line arguments (input and output paths)
     * @throws Exception if an error occurs during job execution
     */
    public static void main(String[] args) throws Exception {

        // Run the tool with the provided arguments
        int res = ToolRunner.run(new Configuration(), new LetterCounter(), args);

        // Exit with the result code from the tool
        System.exit(res);
    }
}
