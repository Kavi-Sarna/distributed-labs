package edu.cmu.andrew.mm6;

// Import necessary Java classes
import java.io.IOException;

// Hadoop data type classes
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

// Hadoop Mapper classes
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;

// Hadoop input/output format classes
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * MaxTemperatureMapper: A Mapper class for the MaxTemperature MapReduce program.
 * This class processes input records and emits intermediate key-value pairs.
 * 
 * LongWritable: input key type (byte offset in the file)
 * Text: input value type (line of text)
 * Text: output key type (year)
 * IntWritable: output value type (temperature)
 */
public class MaxTemperatureMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    
    // Constant to represent missing temperature values in the input data
    private static final int MISSING = 9999;

    /**
     * The map method is called once for each key-value pair in the input split.
     * It processes the input record and emits intermediate key-value pairs.
     * 
     * @param key Input key (byte offset in the file)
     * @param value Input value (line of text)
     * @param output Output collector to emit intermediate key-value pairs
     * @param reporter Reporter to report progress and status
     */
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

        // Get line from input file. This was passed in by Hadoop as value.
        // We have no use for the key (file offset) so we are ignoring it.

        // Convert the input Text to a Java String
        // This line represents a single record of weather data
        String line = value.toString();

        // Get year when weather data was collected. The year is in positions 15-18.
        // This field is at a fixed position within a line.
        String year = line.substring(15, 19);

        // Get the temperature too.
        int airTemperature;

        if (line.charAt(87) == '+') {                                       // parseInt doesn't like leading plus signs
            airTemperature = Integer.parseInt(line.substring(88, 92));
        } else {
            airTemperature = Integer.parseInt(line.substring(87, 92));
        }

        // Get quality of reading. If not missing and of good quality then produce intermediate (year, temp).
        String quality = line.substring(92, 93);


        if (airTemperature != MISSING && quality.matches("[01459]")) {

            // for each year in input, reduce will be called with (year,[temp,temp,temp,…])
            // They key is year and the list of temps will be placed in an iterator.
            output.collect(new Text(year), new IntWritable(airTemperature)); 
        }
    }
}
