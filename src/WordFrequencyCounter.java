import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * The WordFrequencyCounter class counts the frequency of words in a given text.
 */
public class WordFrequencyCounter {
    private static final Logger logger = Logger.getLogger(WordFrequencyCounter.class.getName());
    private final ConcurrentHashMap<String, Integer> wordCountMap = new ConcurrentHashMap<>();

    /**
     * Counts the frequency of words in the given text.
     *
     * @param text The text to count word frequencies from.
     */
    public void countWords(String text) {
        String[] parts = text.split("(?<=\\G.{100})");
        ExecutorService executor = Executors.newFixedThreadPool(parts.length);
        List<Future<Void>> futures = new ArrayList<>();

        for (String part : parts) {
            futures.add(executor.submit(new WordCounterTask(part)));
        }

        for (Future<Void> future : futures) {
            try {
                if (future.isDone()) {
                    future.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.severe("Error processing text part: " + e.getMessage());
            }
        }

        executor.shutdown();
        logger.info("Word counting completed.");
        printWordCounts();
    }

    /**
     * Prints the word frequency results.
     */
    private void printWordCounts() {
        wordCountMap.forEach((word, count) -> {
            logger.info(String.format("Word: '%s', Count: %d", word, count));
        });
    }

    /**
     * Inner class to perform word counting for a part of the text.
     */
    private class WordCounterTask implements Callable<Void> {
        private final String text;

        public WordCounterTask(String text) {
            this.text = text;
        }

        @Override
        public Void call() {
            String[] words = text.split("\\W+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    wordCountMap.merge(word.toLowerCase(), 1, Integer::sum);
                }
            }
            return null;
        }
    }

    /**
     * Main method to test the WordFrequencyCounter class.
     */
    public static void main(String[] args) {
String sampleText = "I am currently a third-year student at Kyiv Polytechnic Institute (KPI), studying Software Engineering. "
            + "Throughout my academic journey at KPI, I have learned numerous essential subjects, including programming, algorithms, databases, and more. "
            + "Right now, we are focusing on asynchronous programming, which is the core topic of this lab assignment. "
            + "I am working on this lab as part of the 'Asynchronous Programming in Java' course, and it is being supervised by Professor Ksenia. "
            + "This topic has intrigued me greatly, as asynchronous programming is a key aspect of modern technology, enhancing the efficiency and performance of software applications. "
            + "Asynchronous programming allows multiple tasks to run simultaneously without blocking the main execution thread. "
            + "This enables the creation of more efficient and faster programs, particularly when dealing with large amounts of data or network-related tasks. "
            + "In this lab, I am focusing on using Callable and Future to manage task execution in multiple threads. "
            + "Additionally, I am exploring the use of ExecutorService to control threads and allow the program to function asynchronously, avoiding the need to wait for lengthy operations like input/output or database queries to complete. "
            + "Over the course of this class, I’ve learned several new concepts, such as multithreading, synchronization mechanisms, and the use of various tools to manage thread execution. "
            + "During each lesson, we discuss different approaches to solving problems related to asynchronicity, which has given me a much deeper understanding of how modern applications function, especially those capable of processing multiple requests at once. "
            + "The lab I am working on is focused on asynchronously counting the frequency of words in a text. "
            + "I have created a Java program that utilizes a ConcurrentHashMap to count the frequency of words in a large text, which is divided into several parts. "
            + "By using Callable, I can distribute the task of word counting across multiple threads. "
            + "With Future, I gather the results from each thread once the tasks are completed. "
            + "The use of multithreading and asynchronous execution greatly accelerates the text processing, which is particularly important in real-world scenarios where applications must handle large volumes of data in a short period.";

        WordFrequencyCounter counter = new WordFrequencyCounter();
        counter.countWords(sampleText);
    }
}
