import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import src.Matrix;
import src.MatrixChain;

public class Main {
    // Parameters to test performance of varying thread counts
    private static final int MIN_TEST_THREAD_COUNT = 1;
    private static final int MAX_TEST_THREAD_COUNT = 20;
    private static final int NUM_THREAD_COUNT_TEST_TRIALS = 5;
    private static final boolean TESTING_THREAD_COUNTS = true;

    public static void main(String[] args) throws FileNotFoundException {
        int testno = 1;

        try {
            testno = Integer.parseInt(args[0]);
        } catch (Exception e) {
        }

        String inFile = "in/test" + testno + ".txt";
        String outFile = "out/test" + testno + ".txt";
        MatrixChain chain = new MatrixChain(inFile);

        if (TESTING_THREAD_COUNTS) {
            // Run multiple trials for varying thread counts on same test case
            List<long[]> runtimeTrialArrays = new ArrayList<>();
            for (int i = 0; i < NUM_THREAD_COUNT_TEST_TRIALS; i++) {
                runtimeTrialArrays.add(evaluateVaryingThreadCountPerformance(chain));
            }

            // Take average of all trials testing various thread counts and print
            printAverageArray(runtimeTrialArrays);

            return;
        }

        PrintStream stream = new PrintStream(new File(outFile));
        System.setOut(stream);
        Matrix res = chain.multiplyOut(true);
        System.out.println(res);
    }

    // Runs multithreaded matrix chain multiplication algorithm on the same matrix chain for varying number of thread counts and records
    // the time it took to multiply the chain for each unique thread count tested
    public static long[] evaluateVaryingThreadCountPerformance(MatrixChain matrixChain) {
        long[] threadCountRuntimes = new long[MAX_TEST_THREAD_COUNT - MIN_TEST_THREAD_COUNT + 1];

        // Test multiplication algorithm on thread counts ranging from min to max parameter set above
        for (int currThreadCount = MIN_TEST_THREAD_COUNT; currThreadCount <= MAX_TEST_THREAD_COUNT; currThreadCount++) {
            // Assigns matrix chain class a new thread count to use for the thread pool that will
            // handle the multithreaded matrix chain multiplication
            MatrixChain.THREAD_NO = currThreadCount;

            // Record how long it took the multithreaded matrix chain multiplication to run with a thread pool using
            // the current number of threads that is being tested
            long startTime = System.currentTimeMillis();
            matrixChain.multiplyOut(true);
            long endTime = System.currentTimeMillis(); 

            // Record this elapsed time in the result array
            threadCountRuntimes[currThreadCount - MIN_TEST_THREAD_COUNT] = endTime - startTime;
        }

        return threadCountRuntimes;
    }
    
    // Helper function to print average of multiple arrays of the same size
    private static void printAverageArray(List<long[]> arrays) {
        if (arrays == null || arrays.isEmpty()) {
            return;
        }

        double[] res = new double[arrays.get(0).length];

        // Sum up all values for each array into the single result array at each index
        for (long[] arr : arrays) {
            for (int i = 0; i < arr.length; i++) {
                res[i] += arr[i];
            }
        }

        // Divide each item in result array by the total number of arrays to get average
        // for each index
        for (int i = 0; i < res.length; i++) {
            res[i] /= arrays.size();
        }

        // Print result array
        System.out.println(Arrays.toString(res));
    }
}