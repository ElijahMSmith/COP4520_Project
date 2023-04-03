package src;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelOptimizationChain extends MatrixChain {

    public ParallelOptimizationChain(Matrix[] chain) {
        super(chain);
    }

    public ParallelOptimizationChain(int length) {
        super(length);
    }

    public ParallelOptimizationChain(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    private static class OrderingWorker extends Thread {
        private final int EMPTY = 0;

        private ConcurrentLinkedQueue<Integer> spQueue;
        private int[] dims;
        private long[][] dp;
        private int[][] s;
        private int N;

        private static int ID_COUNTER = 0;
        private int ID;

        public OrderingWorker(int[] dims, long[][] dp, int[][] s, ConcurrentLinkedQueue<Integer> spQueue) {
            N = dp.length - 1;
            this.dims = dims;
            this.dp = dp;
            this.s = s;
            this.spQueue = spQueue;
            ID = ID_COUNTER;
            ID_COUNTER++;
        }

        private boolean willBeFilled(int r, int c) {
            return !(r == 0 || c == 0 || c <= r);
        }

        @Override
        public void run() {
            while (true) {
                // Grab the next available length on the queue
                int l;
                try {
                    l = spQueue.poll();
                    // System.out.println("Thread " + ID + " grabbed l = " + l);
                } catch (NullPointerException e) {
                    // If all subproblem lengths have been calculated, stop
                    return;
                }

                // Get all matrix subchains of length l from [i, j]
                for (int i = 1; i <= N - l + 1; i++) {
                    int j = i + l - 1;
                    dp[i][j] = Integer.MAX_VALUE;

                    // For each possible partition location k
                    for (int k = i; k <= j - 1; k++) {
                        boolean ikWillFill = willBeFilled(i, k);
                        boolean kjWillFill = willBeFilled(k + 1, j);

                        // Don't wait on slots that will always be zero and aren't important
                        while (dp[i][k] == EMPTY && ikWillFill) {
                            // System.out.printf("i = %d, k = %d, dp[i][k] = %d\n", i, k, dp[i][k]);

                        }
                        while (dp[k + 1][j] == EMPTY && kjWillFill) {
                            // System.out.printf("k + 1 = %d, j = %d, dp[k+1][j] = %d\n", k + 1, j, dp[k +
                            // 1][j]);
                        }

                        long q = dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                        if (q < dp[i][j]) {
                            dp[i][j] = q;
                            s[i][j] = k;
                        }

                        if (dp[i][j] == EMPTY)
                            System.out.println("\t\t -> dp[" + i + "][" + j + "] = " + dp[i][j]);
                    }
                }
            }
        }
    }

    @Override
    protected int[][] getMinimumOrdering(int[] dims, int N) {
        long[][] dp = new long[N + 1][N + 1];
        int[][] s = new int[N + 1][N + 1];

        // l is the length of each matrix chain subproblem we tackle this iteration
        // Start smaller so that when we divide larger chains, answer is already done
        ConcurrentLinkedQueue<Integer> spQueue = new ConcurrentLinkedQueue<>();
        for (int l = 2; l <= N; l++)
            spQueue.add(l);

        ExecutorService es = Executors.newCachedThreadPool();

        final int NUM_THREADS = 2;
        for (int i = 0; i < NUM_THREADS; i++) {
            OrderingWorker worker = new OrderingWorker(dims, dp, s, spQueue);
            es.execute(worker);
        }

        es.shutdown();
        try {
            es.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("getMinimumOrdering found '%d' is the lowest total operations\n", dp[1][N]);
        return s;
    }
}
