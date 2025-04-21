/**
 * @author      Kavi Sarna
 * @andrewId    ksarna
 */
package edu.cmu.andrew.mm6;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * MinTemperatureMapper - Processes weather data and emits year as key with temperature as value
 * Modified from MaxTemperatureMapper to be used for finding minimum temperatures
 */
public class MinTemperatureMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
    
    // The missing temperature value is represented by 9999 in the input data
    private static final int MISSING = 9999;
    
    /**
     * map - Processes each line of the input file and emits the year and temperature
     * @param key - The key is the file offset, which we ignore
     * @param value - The line of text from the input file
     * @param output - The output collector to emit the year and temperature
     * @param reporter - The reporter to report progress
     */
    @Override
    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        
        // Get line from input file. This was passed in by Hadoop as value.
        // We have no use for the key (file offset) so we are ignoring it.
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
        
        // Get quality of reading. If not missing and of good quality then produce intermediate (year,temp).
        String quality = line.substring(92, 93);

        // Check if the temperature is missing or not. If it is missing, we will not emit anything.
        if (airTemperature != MISSING && quality.matches("[01459]")) {
            
            // for each year in input, reduce will be called with (year,[temp,temp,temp, ...])
            // They key is year and the list of temps will be placed in an iterator.
            output.collect(new Text(year), new IntWritable(airTemperature));
        }
    }
}
