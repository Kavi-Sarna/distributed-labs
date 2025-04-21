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
import org.apache.hadoop.io.NullWritable;
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
 * FindPattern: A MapReduce program that searches for words containing a specific pattern.
 * This program searches through words.txt and outputs any word that contains the string "fun".
 * The search is case-insensitive.
 */
public class FindPattern extends Configured implements Tool {

    /**
     * Inner Mapper class that processes input key-value pairs to generate intermediate key-value pairs
     * 
     * LongWritable: input key type (byte offset in the file)
     * Text: input value type (line of text)
     * Text: output key type (word containing the pattern)
     * IntWritable: output value type (no value needed, just the word itself)
     */
    public static class MapClass extends Mapper<LongWritable, Text, Text, NullWritable> {
        
        private static final String PATTERN = "fun";                                            // Define the pattern to search for as a constant
        private Text word = new Text();                                                         // Reusable Text object to avoid object creation overhead
        private final static NullWritable nullValue = NullWritable.get();                       // Static instance of NullWritable since it's a singleton

        // The map method is called once for each key-value pair in the input split
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            
            String line = value.toString();                                                     // Convert the input Text to a Java String
            StringTokenizer tokenizer = new StringTokenizer(line);                              // Create a StringTokenizer to split the line into words

            // Process each word in the line
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();                                           // Get the next token (word)
                
                // Check if the lowercase version of the token contains the pattern (case-insensitive search)
                if (token.toLowerCase().contains(PATTERN.toLowerCase())) {
                    
                    word.set(token);                                    // If it does, set the word Text object to this token
                    context.write(word, nullValue);                     // Emit an intermediate key-value pair: (matching word, null)
                }
            }
        }
    }


    /**
     * Inner Reducer class that processes intermediate key-value pairs to generate final output key-value pairs
     * 
     * Text: input key type (word containing the pattern)
     * IntWritable: input value type (no values)
     * Text: output key type (word containing the pattern)
     * IntWritable: output value type (no value)
     */
    public static class ReduceClass extends Reducer<Text, NullWritable, Text, NullWritable> {

        // Static instance of NullWritable since it's a singleton
        private final static NullWritable nullValue = NullWritable.get();

        // The reduce method is called once for each key and its associated values
        public void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {

            // Simply output the word, we don't need any aggregation. Since the mapper has already filtered only the words containing "fun"
            context.write(key, nullValue);
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
        Job job = Job.getInstance(conf, "findpattern");                         // Create a new MapReduce job with the configuration and a descriptive name
        job.setJarByClass(FindPattern.class);                                   // Set the jar by finding the jar containing this class

        job.setMapperClass(MapClass.class);                                     // Set the Mapper class for the job
        job.setReducerClass(ReduceClass.class);                                 // Set the Reducer class for the job

        job.setOutputKeyClass(Text.class);                                      // Set the key type for the job output
        job.setOutputValueClass(NullWritable.class);                            // Set the value type for the job output

        job.setInputFormatClass(TextInputFormat.class);                         // Set the input format class (how the input files are read)
        job.setOutputFormatClass(TextOutputFormat.class);                       // Set the output format class (how the output files are written)

        FileInputFormat.addInputPath(job, new Path(args[0]));                   // Set the input path for the job (from command-line argument)
        FileOutputFormat.setOutputPath(job, new Path(args[1]));                 // Set the output path for the job (from command-line argument)

        return job.waitForCompletion(true) ? 0 : 1;                             // Submit the job and wait for it to complete, returning 0 if successful, 1 otherwise
    }

    /**
     * Main method to run the FindPattern job
     * 
     * @param args Command-line arguments (input and output paths)
     * @throws Exception if an error occurs during job execution
     */
    public static void main(String[] args) throws Exception {

        // Run the tool with the provided arguments
        int res = ToolRunner.run(new Configuration(), new FindPattern(), args);

        // Exit with the result code from the tool
        System.exit(res);
    }
}
