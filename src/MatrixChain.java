package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatrixChain {
    public static int THREAD_NO = 10;
    private Matrix[] chain;
    private ExecutorService executorService;

    public MatrixChain(Matrix[] chain) {
        this.chain = chain;
    }

    public MatrixChain(int length) {
        chain = new Matrix[length];
    }

    public MatrixChain(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        int inputLen = sc.nextInt();
        chain = new Matrix[inputLen];

        try {
            int i = 0;
            while (i < inputLen) {
                int r = sc.nextInt();
                int c = sc.nextInt();
                BigInteger[][] temp = new BigInteger[r][c];

                for (int rx = 0; rx < r; rx++) {
                    for (int cx = 0; cx < c; cx++) {
                        temp[rx][cx] = new BigInteger(sc.next().trim());
                    }
                }

                chain[i] = new Matrix(temp);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*
     * Returns the ordering of matrix multiplications for the entire chain
     * that results in the lowest possible number of operations.
     * 
     * The return value is an array of integers, where the integer at index i
     * represents the
     * index in the chain of the left array in the ith multiplication pair to
     * achieve this minimum
     */
    protected int[][] getBestMultiplicationOrdering() {
        int N = chain.length;

        if (chain.length <= 1)
            return new int[0][2];

        int[] dims = new int[N + 1];

        dims[0] = chain[0].rows();
        for (int i = 1; i < dims.length; i++)
            dims[i] = chain[i - 1].cols();

        int[][] s = getMinimumOrdering(dims, N);
        return s;
    }

    // https://home.cse.ust.hk/~dekai/271/notes/L12/L12.pdf
    protected int[][] getMinimumOrdering(int[] dims, int N) {
        int[][] dp = new int[N + 1][N + 1];
        int[][] s = new int[N + 1][N + 1];
        // l is the length of each matrix chain subproblem we tackle this iteration
        // Start smaller so that when we divide larger chains, answer is already done
        for (int l = 2; l <= N; l++) {
            for (int i = 1; i <= N - l + 1; i++) {
                int j = i + l - 1;
                dp[i][j] = Integer.MAX_VALUE;
                // For each possible partition point in this chain
                for (int k = i; k <= j - 1; k++) {
                    // Total operations if we use this partition is the cost of multing left and
                    // right + cost of combining at partition
                    int q = dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    if (q < dp[i][j]) {
                        dp[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        // Print minimum number of operations required to out file
        System.out.println(dp[1][N]);
        return s;
    }

    // s[i][j] is the place in subchain i -> j [1, N] to insert a division
    // Take each smaller chain around the division and repeat
    // When we get to just a single matrix (i == j) use that (like mergesort)
    protected Matrix multiplyOutSmartThreaded(int[][] s, int i, int j) {
        if (i < j) {
            Matrix X = multiplyOutSmartThreaded(s, i, s[i][j]);
            Matrix Y = multiplyOutSmartThreaded(s, s[i][j] + 1, j);

            // Return resulting matrix of multithreaded multiplication operation
            return X.multiply(Y, (ThreadPoolExecutor) executorService);
        }

        // Matrix chain is 0-indexed, where other calcs are not
        return chain[i - 1];
    }

    // Same as above, but performed on a single thread
    protected Matrix multiplyOutSmartSync(int[][] s, int i, int j) {
        if (i < j) {
            Matrix X = multiplyOutSmartSync(s, i, s[i][j]);
            Matrix Y = multiplyOutSmartSync(s, s[i][j] + 1, j);
            return X.multiply(Y);
        }

        return chain[i - 1];
    }

    /*
     * Returns the result of multiplying every matrix in this chain using the
     * optimal ordering that results in the fewest number of operations.
     */
    public Matrix multiplyOutBruteForce() {
        for (int i = 0; i < chain.length - 1; i++)
            chain[i + 1] = chain[i].multiply(chain[i + 1]);

        chain = new Matrix[] { chain[chain.length - 1] };
        return chain[0];
    }

    /* Root function called to multiply out chain of matrices */
    public Matrix multiplyOut(boolean useMultipleThreads) {
        int[][] s = getBestMultiplicationOrdering();
        Matrix retval;

        if (useMultipleThreads) {
            // Setup thread pool with fixed number of threads to be used on individual
            // matrix multiplication operations
            executorService = Executors.newFixedThreadPool(THREAD_NO);

            // Multiply matrices on multiple threads and use efficient ordering
            retval = multiplyOutSmartThreaded(s, 1, chain.length);

            // Done using thread pool so shut it down and make sure it waits till
            // resources for thread pool have closed before returning
            executorService.shutdown();
            try {
                executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (Exception e) {
                System.out.println("Executor took longer than one minute :(");
            }
        } else {
            // Multiply matrix chain on single thread but still use efficient ordering
            retval = multiplyOutSmartSync(s, 1, chain.length);
        }

        return retval;
    }
}
