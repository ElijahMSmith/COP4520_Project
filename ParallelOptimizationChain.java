import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelOptimizationChain extends MatrixChain {

    private Matrix[] chain;

    public ParallelOptimizationChain(Matrix[] chain) {
        super(chain);
    }

    public ParallelOptimizationChain(int length) {
        super(length);
    }

    public ParallelOptimizationChain(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    /*
     * Ways to introduce concurrency to optimization
     * 
     * Option 1: Make DP parallel
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

    private static class OrderingWorker extends Thread {
        private final int EMPTY = 0;

        private ConcurrentLinkedQueue<Integer> spQueue;
        private int[] dims;
        private int[][] dp;
        private int[][] s;
        private int N;

        public OrderingWorker(int[] dims, int[][] dp, int[][] s, ConcurrentLinkedQueue<Integer> spQueue) {
            N = dp.length - 1;
            this.dims = dims;
            this.dp = dp;
            this.s = s;
            this.spQueue = spQueue;
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

                // Get all matrix subchains of length l from [i, j]
                for (int i = 1; i <= N - l + 1; i++) {
                    int j = i + l - 1;
                    dp[i][j] = Integer.MAX_VALUE;

                    // For each possible partition location k
                    for (int k = i; k <= j - 1; k++) {
                        while (dp[i][k] == EMPTY || dp[k + 1][j] == EMPTY) {
                        }

                        int q = dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j];
                        if (q < dp[i][j]) {
                            dp[i][j] = q;
                            s[i][j] = k;
                        }
                    }
                }
            }
        }
    }

    @Override
    private int[][] getMinimumOrdering(int[] dims, int N) throws InterruptedException {
        int[][] dp = new int[N + 1][N + 1];
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
        es.awaitTermination(2, TimeUnit.MINUTES);

        System.out.printf("getMinimumOrdering found '%d' is the lowest total comps\n", dp[1][N]);
        return s;
    }
}
