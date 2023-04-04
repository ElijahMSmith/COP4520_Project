package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MatrixChain {
    public static final int THREAD_NO = 10;
    private Matrix[] chain;
    private ExecutorService executorService;

    public MatrixChain(Matrix[] chain) {
        this.chain = chain;
        executorService = Executors.newFixedThreadPool(THREAD_NO);
    }

    public MatrixChain(int length) {
        chain = new Matrix[length];
        executorService = Executors.newFixedThreadPool(THREAD_NO);
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
                long[][] temp = new long[r][c];

                for (int rx = 0; rx < r; rx++) {
                    for (int cx = 0; cx < c; cx++) {
                        temp[rx][cx] = sc.nextLong();
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
     * Ways to introduce concurrency to optimization
     * 
     * Option 1: Make DP parallel (Eli currently trying to tackle this)
     * Have each length of subproblem be a separate thread
     * Threads can line up in queues and the queue can give them the next largest
     * When they finish, they return to the queue and spin
     * Spin can spin perhaps on its own local l variable
     * When it finishes, set l to -1 then join the queue
     * When l is finally not -1, go back and calculate matrices
     * When controller runs out of l to assign, stop each thread (or set some flag
     * to let them know they should exit)
     * 
     * Option 2: Make exponential algorithm parallel
     * Start X threads and give them some part of the possible divisions at the
     * highest level (when considering the entire chain)
     * Let them compute everything required for all their assigned divisions
     * Compare the minimum between all threads
     * Have each thread store some rep of everywhere that was divided so that the
     * matrix ordering can be recovered
     */

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
                for (int k = i; k <= j - 1; k++) {
                    // Chain is being considered from indexes i -> j, with k >= i but < j and k
                    // being the division point
                    // We take the best way to computer i:k, k+1:j, and the cost of multing together
                    int q = dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                    if (q < dp[i][j]) {
                        dp[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= N; j++) {
                System.out.print(dp[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println(dp[1][N]);
        return s;
    }

    // s[i][j] is the place in subchain i -> j [1, N] to insert a division
    // Take each smaller chain around the division and repeat
    // When we get to just a single matrix (i == j) use that (like mergesort)
    protected Matrix multiplyOut(int[][] s, int i, int j) {
        if (i < j) {
            Matrix X = multiplyOut(s, i, s[i][j]);
            Matrix Y = multiplyOut(s, s[i][j] + 1, j);
            return X.multiply(Y, (ThreadPoolExecutor) executorService);
        }

        // Matrix chain is 0-indexed, where other calcs are not
        return chain[i - 1];
    }

    public Matrix multiplyOut() {
        int[][] s = getBestMultiplicationOrdering();
        Matrix retval = multiplyOut(s, 1, chain.length);
        executorService.shutdown();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Yikes!");
        }
        return retval;
    }

    /*
     * Returns the result of multiplying every matrix in this chain using the
     * optimal ordering
     * that results in the fewest number of operations.
     */
    public Matrix multiplyOutNaive() {
        // TODO: Make parallel and use optimal ordering
        for (int i = 0; i < chain.length - 1; i++)
            chain[i + 1] = chain[i].multiply(chain[i + 1]);

        chain = new Matrix[] { chain[chain.length - 1] };
        return chain[0];
    }
}
