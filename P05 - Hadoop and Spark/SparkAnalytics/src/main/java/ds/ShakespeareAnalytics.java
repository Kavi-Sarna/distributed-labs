package ds;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * ShakespeareAnalytics.java
 * Author: [Your Name]
 *
 * This program uses Apache Spark to analyze Shakespeare's "All's Well That Ends Well"
 * performing various text analytics such as counting lines, words, distinct words,
 * symbols, distinct symbols, distinct letters, and providing a case-sensitive search function.
 */
public class ShakespeareAnalytics {

    public static void main(String[] args) {
        // Set log level to reduce verbosity
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);

        // Configure Spark
        SparkConf conf = new SparkConf().setAppName("Shakespeare Analytics").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the input file
        String inputFile = "All's Well That Ends Well.txt"; // Make sure this file is in your working directory
        JavaRDD<String> inputRDD = sc.textFile(inputFile);

        // Task 0: Count number of lines
        long lineCount = inputRDD.count();
        System.out.println("Task 0 - Number of lines: " + lineCount);

        // Task 1: Count number of words
        // Split each line into words using regex [^a-zA-Z]+ as the delimiter
        JavaRDD<String> wordsRDD = inputRDD
                .flatMap(line -> Arrays.asList(line.split("[^a-zA-Z]+")))
                .filter(word -> !word.isEmpty());

        long wordCount = wordsRDD.count();
        System.out.println("Task 1 - Number of words: " + wordCount);

        // Task 2: Count number of distinct words
        long distinctWordCount = wordsRDD.distinct().count();
        System.out.println("Task 2 - Number of distinct words: " + distinctWordCount);

        // Task 3: Count number of symbols (including newlines)
        // To properly count all symbols including newlines, we need to process each character individually
        // and add a newline character between each line
        JavaRDD<Character> symbolsRDD = inputRDD.zipWithIndex().flatMap(
                tuple -> {
                    String line = tuple._1();
                    long lineIndex = tuple._2();

                    List<Character> characters = new ArrayList<>();

                    // Add each character from the line
                    for (char c : line.toCharArray()) {
                        characters.add(c);
                    }

                    // Add a newline character at the end of each line (except potentially the last one)
                    // In a text file, each line ends with a newline
                    if (line.isEmpty())
                        characters.add('\n');

                    return characters;
                }
        );

        long symbolCount = symbolsRDD.count();
        System.out.println("Task 3 - Number of symbols: " + symbolCount);

        // Task 4: Count number of distinct symbols
        long distinctSymbolCount = symbolsRDD.distinct().count();
        System.out.println("Task 4 - Number of distinct symbols: " + distinctSymbolCount);

        // Task 5: Count number of distinct letters
        // Filter to only include letters and count distinct ones
        JavaRDD<Character> lettersRDD = symbolsRDD
                .filter(symbol -> Character.isLetter(symbol));

        long distinctLetterCount = lettersRDD.distinct().count();
        System.out.println("Task 5 - Number of distinct letters: " + distinctLetterCount);

        // Task 6: Interactive search for lines containing a word (case-sensitive)
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a word to search for (case-sensitive): ");
        String searchWord = scanner.nextLine();

        // Filter lines that contain the search word (case-sensitive)
        JavaRDD<String> matchingLines = inputRDD
                .filter(line -> line.contains(searchWord));

        System.out.println("\nLines containing '" + searchWord + "':");
        matchingLines.collect().forEach(System.out::println);

        // Close resources
        scanner.close();
//        sc.close();
    }
}
