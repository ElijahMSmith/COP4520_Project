public class MatrixChain {

    private Matrix[] chain;

    public MatrixChain(Matrix[] chain) {
        this.chain = chain;
    }

    public MatrixChain(int length) {
        chain = new Matrix[length];
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

    private int[][] getBestMultiplicationOrdering() {
        int N = chain.length;

        if (chain.length <= 1)
            return new int[0][2];

        int[] dims = new int[N + 1];

        dims[0] = chain[0].rows();
        for (int i = 1; i < dims.length; i++)
            dims[i] = chain[i - 1].cols();

        for (int i = 0; i < dims.length; i++)
            System.out.print(dims[i] + " ");
        System.out.println();

        int[][] s = getMinimumOrdering(dims, N);
        return s;
    }

    // https://home.cse.ust.hk/~dekai/271/notes/L12/L12.pdf
    private int[][] getMinimumOrdering(int[] dims, int N) {
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
        System.out.printf("getMinimumOrdering found '%d' is the lowest total comps\n", dp[1][N]);
        return s;
    }

    // s[i][j] is the place in subchain i -> j [1, N] to insert a division
    // Take each smaller chain around the division and repeat
    // When we get to just a single matrix (i == j) use that (like mergesort)
    private Matrix multiplyOut(int[][] s, int i, int j) {
        System.out.printf("s[%d][%d] = %d\n", i, j, s[i][j]);
        if (i < j) {
            Matrix X = multiplyOut(s, i, s[i][j]);
            Matrix Y = multiplyOut(s, s[i][j] + 1, j);
            return X.multiply(Y);
        }
        // Matrix chain is 0-indexed, where other calcs are not
        return chain[i - 1];
    }

    public Matrix multiplyOut() {
        int[][] s = getBestMultiplicationOrdering();
        return multiplyOut(s, 1, chain.length);
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
