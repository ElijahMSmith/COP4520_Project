import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import src.Matrix;
import src.MatrixChain;

public class Main {
    private static final int MIN_TEST_THREAD_COUNT = 1;
    private static final int MAX_TEST_THREAD_COUNT = 20;
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

        List<long[]> arrays = new ArrayList<>();

        if (TESTING_THREAD_COUNTS) {
            for (int i = 0; i < 5; i++) {
                arrays.add(evaluateVaryingThreadCountPerformance(chain));
            }
            averageArray(arrays);
            return;
        }

        PrintStream stream = new PrintStream(new File(outFile));
        System.setOut(stream);
        Matrix res = chain.multiplyOut();
        System.out.println(res);
    }

    public static long[] evaluateVaryingThreadCountPerformance(MatrixChain matrixChain) {
        long[] threadCountRuntimes = new long[MAX_TEST_THREAD_COUNT - MIN_TEST_THREAD_COUNT + 1];

        for (int currThreadCount = MIN_TEST_THREAD_COUNT; currThreadCount <= MAX_TEST_THREAD_COUNT; currThreadCount++) {
            MatrixChain.THREAD_NO = currThreadCount;

            long startTime = System.currentTimeMillis();
            matrixChain.multiplyOut();
            long endTime = System.currentTimeMillis(); 

            threadCountRuntimes[currThreadCount - 1] = endTime - startTime;
        }

        //System.out.println(Arrays.toString(threadCountRuntimes));
        return threadCountRuntimes;
    }
    
    public static void averageArray(List<long[]> arrays) {
        double[] res = new double[20];

        for (long[] arr : arrays) {
            for (int i = 0; i < arr.length; i++) {
                res[i] += arr[i];
            }
        }

        for (int i = 0; i < res.length; i++) {
            res[i] /= 5;
        }

        System.out.println(Arrays.toString(res));
    }
}