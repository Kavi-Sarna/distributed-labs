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
 * A MapReduce program that finds aggravated assault crimes within 100 meters
 * of 3803 Forbes Avenue in Oakland and generates a KML file for Google Earth.
 */
public class OaklandCrimeStatsKML extends Configured implements Tool {

    /**
     * Mapper class that processes each line of the crime data and emits
     * crime data in KML format for crimes within 100 meters of target location
     * 
     * LongWritable: input key type (byte offset in the file)
     * Text: input value type (line of text)
     * Text: output key type (KML placemark)
     * Text: output value type (KML placemark)
     */
    public static class MapClass extends Mapper<LongWritable, Text, Text, Text> {
        // Target location coordinates (3803 Forbes Avenue)
        private final static double TARGET_X = 1354326.897;
        private final static double TARGET_Y = 411447.7828;
        // 100 meters in feet (state plane coordinates are in feet)
        private final static double METERS_100_IN_FEET = 328.084;
        
        // We'll use a single key for all output so the reducer gets all data
        private final static Text SINGLE_KEY = new Text("all_crimes");
        
        /**
         * The map method is called once for each key-value pair in the input split
         * 
         * @param key The input key (byte offset in the file)
         * @param value The input value (line of text)
         * @param context The context object to write output
         */
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            // Convert the input line to a string and split it into fields
            String line = value.toString();
            String[] fields = line.split("\t");
            
            // We need at least 9 columns for coordinates, crime type, and lat/long
            if (fields.length >= 9) {
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
                            // Get latitude and longitude from columns 8 and 9
                            String latitude = fields[7].trim();
                            String longitude = fields[8].trim();
                            
                            // Get date and time for description
                            String time = fields[2].trim();
                            String date = fields[5].trim();
                            String address = fields[3].trim();
                            
                            // Create a KML placemark for this crime
                            StringBuilder placemark = new StringBuilder();
                            placemark.append("<Placemark>\n")
                                     .append("  <name>Aggravated Assault</name>\n")
                                     .append("  <description>")
                                     .append("Date: ").append(date).append(", ")
                                     .append("Time: ").append(time).append(", ")
                                     .append("Address: ").append(address)
                                     .append("</description>\n")
                                     .append("  <Point>\n")
                                     .append("    <coordinates>")
                                     .append(longitude).append(",")
                                     .append(latitude).append(",0")
                                     .append("</coordinates>\n")
                                     .append("  </Point>\n")
                                     .append("</Placemark>");
                            
                            // Emit the placemark with a single key so the reducer gets all of them
                            context.write(SINGLE_KEY, new Text(placemark.toString()));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Skip lines that don't have proper number format
                }
            }
        }
    }

    /**
     * Reducer class that combines all placemarks into a single KML file
     * 
     * Text: input key type (fixed string for target location)
     * Text: input value type (KML placemark)
     * Text: output key type (KML file content)
     * NullWritable: output value type (not used)
     */
    public static class ReduceClass extends Reducer<Text, Text, Text, NullWritable> {
        
        private static final NullWritable nullValue = NullWritable.get();
        
        // The reduce method is called once for each key and its associated values
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // Create KML header
            StringBuilder kml = new StringBuilder();
            kml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
               .append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n")
               .append("<Document>\n")
               .append("  <name>Oakland Aggravated Assaults</name>\n")
               .append("  <description>Aggravated assault crimes within 100 meters of 3803 Forbes Avenue</description>\n");
            
            // Add Style for placemarks
            kml.append("  <Style id=\"assaultStyle\">\n")
               .append("    <IconStyle>\n")
               .append("      <color>ffff0000</color>\n") // Red
               .append("      <scale>1.0</scale>\n")
               .append("    </IconStyle>\n")
               .append("  </Style>\n");
            
            // Add all placemarks
            for (Text placemark : values) {
                kml.append(placemark.toString()).append("\n");
            }
            
            // Add KML footer
            kml.append("</Document>\n")
               .append("</kml>");
            
            // Write the entire KML file as a single output
            context.write(new Text(kml.toString()), nullValue);
        }
    }

    /**
     * The run method is called to set up the job configuration and submit it to the Hadoop cluster
     * 
     * @param args Command-line arguments (input and output paths)
     * @throws Exception if an error occurs during job setup or execution
     * @return 0 if the job completed successfully, 1 otherwise
     */
    @Override
    public int run(String[] args) throws Exception {

        Configuration conf = getConf();                                     // Get the Hadoop configuration from the Tool context
        Job job = Job.getInstance(conf, "oaklandcrimestatskml");            // Create a new MapReduce job with the configuration and a descriptive name
        job.setJarByClass(OaklandCrimeStatsKML.class);                      // Set the jar by finding the jar containing this class

        job.setMapperClass(MapClass.class);                                 // Set the Mapper class for the job
        job.setReducerClass(ReduceClass.class);                             // Set the Reducer class for the job
        
        // Force a single reducer to get all data in one file
        job.setNumReduceTasks(1);
        
        job.setMapOutputKeyClass(Text.class);                               // Set the key type for the job output
        job.setMapOutputValueClass(Text.class);                             // Set the value type for the job output
        
        job.setOutputKeyClass(Text.class);                                  // Set the key type for the job output
        job.setOutputValueClass(NullWritable.class);                        // Set the value type for the job output

        job.setInputFormatClass(TextInputFormat.class);                     // Set the input format class (how the input files are read)
        job.setOutputFormatClass(TextOutputFormat.class);                   // Set the output format class (how the output files are written)

        FileInputFormat.addInputPath(job, new Path(args[0]));               // Set the input path for the job (from command-line argument)
        FileOutputFormat.setOutputPath(job, new Path(args[1]));             // Set the output path for the job (from command-line argument)

        return job.waitForCompletion(true) ? 0 : 1;                         // Submit the job and wait for it to complete, returning 0 if successful, 1 otherwise
    }

    /**
     * Main method to run the OaklandCrimeStatsKML job
     * 
     * @param args Command-line arguments (input and output paths)
     * @throws Exception if an error occurs during job execution
     */
    public static void main(String[] args) throws Exception {

        // Run the tool with the provided arguments
        int res = ToolRunner.run(new Configuration(), new OaklandCrimeStatsKML(), args);

        // Exit with the result code from the tool
        System.exit(res);
    }
}
