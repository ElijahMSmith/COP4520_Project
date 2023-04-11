package src;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;

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
        private ConcurrentLinkedQueue<Integer> spQueue;
        private int[] dims;
        private BigInteger[][] dp;
        private int[][] s;
        private int N;
        private int ID;
        private static int ID_COUNTER = 1;

        public OrderingWorker(int[] dims, BigInteger[][] dp, int[][] s, ConcurrentLinkedQueue<Integer> spQueue) {
            N = dp.length - 1;
            this.dims = dims;
            this.dp = dp;
            this.s = s;
            this.spQueue = spQueue;
            this.ID = ID_COUNTER++;
        }

        @Override
        public void run() {
            while (true) {
                // Grab the next available length on the queue
                int l;
                try {
                    l = spQueue.poll();
                } catch (NullPointerException e) {
                    // If all subproblem lengths have been calculated, stop
                    return;
                }

                // System.out.println(ID + " polled " + l);

                // Get all matrix subchains of length l
                for (int i = 1; i <= N - l + 1; i++) {
                    int j = i + l - 1;

                    // For each possible partition location k
                    for (int k = i; k <= j - 1; k++) {
                        // Wait for other thread to finish subproblems not yet initialized
                        boolean first = true;
                        while (dp[i][k] == null) {
                            // if (first) {
                            // System.out.println("Waiting on " + i + ", " + k);
                            // first = false;
                            // }
                        }

                        first = true;
                        while (dp[k + 1][j] == null) {
                            // if (first) {
                            // System.out.println("Waiting on " + (k + 1) + ", " + j);
                            // first = false;
                            // }
                        }

                        // Calculate lowest operations to compute this chain
                        BigInteger q = dp[i][k].add(dp[k + 1][j])
                                .add(BigInteger.valueOf(dims[i - 1] * dims[k] * dims[j]));

                        // Update if first possible partition (k == i, dp[i][j] is still null)
                        // or if this partition is the best case
                        if (k == i || q.compareTo(dp[i][j]) < 0) {
                            dp[i][j] = q;
                            s[i][j] = k;
                        }

                        // System.out.println("Updated " + i + ", " + j);
                    }
                }
            }
        }
    }

    @Override
    protected int[][] getMinimumOrdering(int[] dims, int N) {
        BigInteger[][] dp = new BigInteger[N + 1][N + 1];
        int[][] s = new int[N + 1][N + 1];

        for (int i = 0; i <= N; i++)
            dp[i][i] = BigInteger.ZERO;

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
            es.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(dp[1][N]);
        return s;
    }
}
