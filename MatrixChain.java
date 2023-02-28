import java.util.Arrays;

public class MatrixChain {

    private Matrix[] chain;
    private int[][] dp;

    public MatrixChain(Matrix[] chain) {
        this.chain = chain;
    }

    public MatrixChain(int length) {
        chain = new Matrix[length];
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
    public int[][] getBestMultiplicationOrdering() {
        // TODO: Can we make this faster by running in parallel (or by brute forcing in parallel?)
        if (chain.length <= 1)
            return new int[0][2];

        int[] dims = new int[chain.length + 1];
        int N = dims.length;

        dims[0] = chain[0].rows();
        for (int i = 1; i < dims.length; i++)
            dims[i] = chain[i - 1].cols();

        for (int i = 0; i < dims.length; i++)
            System.out.print(dims[i] + " ");
        System.out.println();

        int minTotalOperations = getMinimumOperations(dims, N);
        System.out.println("minTotalOperations: " + minTotalOperations);
        return new int[0][2]; // TODO: Recover this ordering
    }

    private int getMinimumOperations(int[] dims, int N) {
        int i = 1, j = N - 1;
        dp = new int[N][N];
        for (int x = 0; x < dp.length; x++)
            Arrays.fill(dp[x], -1);
        return getMinimumOperations(dims, i, j);
    }

    private int getMinimumOperations(int[] dims, int i, int j) {
        if (i == j) {
            return 0;
        }

        if (dp[i][j] != -1) {
            return dp[i][j];
        }

        dp[i][j] = Integer.MAX_VALUE;
        for (int k = i; k < j; k++) {
            dp[i][j] = Math.min(
                    dp[i][j],
                    getMinimumOperations(
                            dims, i, k)
                            + getMinimumOperations(
                                    dims, k + 1, j)
                            + dims[i - 1] * dims[k] * dims[j]);
        }
        return dp[i][j];
    }

    /*
     * Returns the result of multiplying every matrix in this chain using the
     * optimal ordering
     * that results in the fewest number of operations.
     */
    public Matrix multiplyOut() {
        // TODO: Make parallel and use optimal ordering
        for (int i = 0; i < chain.length - 1; i++)
            chain[i + 1] = chain[i].multiply(chain[i + 1]);

        chain = new Matrix[] { chain[chain.length - 1] };
        return chain[0];
    }
}
