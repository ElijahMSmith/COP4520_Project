import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import src.Matrix;
import src.MatrixChain;

public class Main {
    // Parameters to test performance of varying thread counts
    private static final int MIN_TEST_THREAD_COUNT = 1;
    private static final int MAX_TEST_THREAD_COUNT = 20;
    private static final int NUM_THREAD_COUNT_TEST_TRIALS = 5;
    private static final boolean TESTING_THREAD_COUNTS = false;

    // Parameters to test performance of three approaches for multiplying matrix chain
    private static final int NUM_PERFORMANCE_TRIALS = 1;
    private static final boolean TESTING_PERFORMANCE = true;

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
        else if (TESTING_PERFORMANCE) {
            Scanner in = new Scanner(System.in);

            // Let user choose which appraoch to multiply matrix chain with
            System.out.println("Which approach do you want to use (0 for brute force, 1 for single thread, 2 for multi-thread)?");
            int approachChosen = in.nextInt();
            
            in.close();

            if (approachChosen == 0) {
                long bruteForceRuntimes = 0;
                for (int i = 0; i < NUM_PERFORMANCE_TRIALS; i++) {
                    // Multiply chain out using brute force approach and record runtime
                    long startTimeBruteForce = System.currentTimeMillis();
                    Matrix resBruteForce = chain.multiplyOutBruteForce();
                    long endTimeBruteForce = System.currentTimeMillis();
                    bruteForceRuntimes += endTimeBruteForce - startTimeBruteForce;
                }

                // Average out the runtimes for the trials
                double avgBruteForceRuntime = bruteForceRuntimes / (double) NUM_PERFORMANCE_TRIALS;

                // Print results
                System.out.println("Average brute force runtime: " + avgBruteForceRuntime + "ms");
            }
            else if (approachChosen == 1) {
                long syncSmartOrderingRuntimes = 0;
                for (int i = 0; i < NUM_PERFORMANCE_TRIALS; i++) {
                    // Multiply chain out using efficient ordering and single thread approach and record runtime
                    long startTimeSyncSmartOrdering = System.currentTimeMillis();
                    Matrix resSyncSmartOrdering = chain.multiplyOut(false);
                    long endTimeSyncSmartOrdering = System.currentTimeMillis();
                    syncSmartOrderingRuntimes += endTimeSyncSmartOrdering - startTimeSyncSmartOrdering;
                }

                // Average out the runtimes for the trials
                double avgSyncSmartOrderingRuntime = syncSmartOrderingRuntimes / (double) NUM_PERFORMANCE_TRIALS;

                // Print results
                System.out.println("Average synchronous runtime with efficient ordering: " + avgSyncSmartOrderingRuntime + "ms");
            }
            else {
                long multiThreadedSmartOrderingRuntimes = 0;
                for (int i = 0; i < NUM_PERFORMANCE_TRIALS; i++) {
                    // Multiply chain out using efficient ordering and multiple threads approach and record runtime
                    long startTimeMultiThreadedSmartOrdering = System.currentTimeMillis();
                    Matrix resMultiThreadedSmartOrdering = chain.multiplyOut(true);
                    long endTimeMultiThreadedSmartOrdering = System.currentTimeMillis();
                    multiThreadedSmartOrderingRuntimes += endTimeMultiThreadedSmartOrdering - startTimeMultiThreadedSmartOrdering;
                }

                // Average out the runtimes for the trials
                double avgMultiThreadedSmartOrderingRuntime = multiThreadedSmartOrderingRuntimes / (double) NUM_PERFORMANCE_TRIALS;

                // Print results
                System.out.println("Average multithreaded runtime with efficient ordering: " + avgMultiThreadedSmartOrderingRuntime + "ms");
            }
                // if (!matricesAreEqual(resBruteForce, resSyncSmartOrdering, resMultiThreadedSmartOrdering)) {
                //     System.out.println("Incorrect results from matrix multiplication.");
                //     return;
                // }
                // if (!matricesAreEqual(resSyncSmartOrdering, resSyncSmartOrdering, resMultiThreadedSmartOrdering)) {
                //     System.out.println("Incorrect results from matrix multiplication.");
                //     return;
                // }
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

    // Helper function to confirm/verify that three matrices contain identical values
    private static boolean matricesAreEqual(Matrix matrix1, Matrix matrix2, Matrix matrix3) {
        for (int i = 0; i < matrix1.rows(); i++) {
            for (int j = 0; j < matrix1.cols(); j++) {
                if (!matrix1.values[i][j].equals(matrix2.values[i][j]) || !matrix2.values[i][j].equals(matrix3.values[i][j])) {
                    return false;
                }
            }
        }

        return true;
    }
}