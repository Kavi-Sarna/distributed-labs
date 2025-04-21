package edu.cmu.andrew.mm6;

// Import necessary Java classes
import java.io.IOException;
import java.util.Iterator;

// Hadoop data type classes
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

// Hadoop Reducer classes
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Reducer;

// Hadoop input/output format classes
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


/**
 * MaxTemperatureReducer: A Reducer class for the MaxTemperature MapReduce program.
 * This class processes intermediate key-value pairs emitted by the Mapper
 * and produces the final output.
 * 
 * Text: input key type (year)
 * IntWritable: input value type (temperature)
 * Text: output key type (year)
 * IntWritable: output value type (maximum temperature for the year)
 */
public class MaxTemperatureReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

    /**
     * The reduce method is called once for each unique key (year).
     * It processes the list of values (temperatures) and emits the maximum temperature for that year.
     * 
     * @param key Input key (year)
     * @param values Input values (list of temperatures for that year)
     * @param output Output collector to emit final key-value pairs
     * @param reporter Reporter to report progress and status
     */
    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

        // from the list of values, find the maximum
        int maxValue = Integer.MIN_VALUE;

        // Iterate through the list of temperatures for the year and find the maximum temperature
        while (values.hasNext()) {
            maxValue = Math.max(maxValue, values.next().get());
        }
        
        // emit (key = year, value = maxTemp = max for year)
        output.collect(key, new IntWritable(maxValue));
    }
}
