/**
 * @author      Kavi Sarna
 * @andrewId    ksarna
 */
package edu.cmu.andrew.mm6;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * MinTemperatureReducer - Takes a year and list of temperatures and finds minimum
 * Modified from MaxTemperatureReducer to find minimum instead of maximum
 */
public class MinTemperatureReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
    
    /**
     * reduce - Takes a year and list of temperatures and finds minimum
     * @param key - The key is the year
     * @param values - The list of temperatures for that year
     * @param output - The output collector to emit the year and minimum temperature
     * @param reporter - The reporter to report progress
     */
    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        
        // from the list of values, find the minimum
        int minValue = Integer.MAX_VALUE;
        
        // iterate through the list of temperatures for that year and find the minimum
        while (values.hasNext()) {
            minValue = Math.min(minValue, values.next().get());
        }

        // emit (key = year, value = minTemp = min for year)
        output.collect(key, new IntWritable(minValue));
    }
}
