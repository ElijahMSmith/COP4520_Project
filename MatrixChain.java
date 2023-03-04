import java.util.Arrays;

public class MatrixChain {

    private Matrix[] chain;

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

    public int[][] getBestMultiplicationOrdering(int N) {
        // TODO: Can we make this faster by running in parallel (or by brute forcing in
        // parallel?)
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
        for (int l = 2; l <= N; l++) {
            for (int i = 1; i <= N - l + 1; i++) {
                int j = i + l - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k <= j - 1; k++) {
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

    private Matrix multiplyOutSmart(int[][] s, int i, int j) {
        if (i < j) {
            Matrix X = multiplyOutSmart(s, i, s[i][j]);
            Matrix Y = multiplyOutSmart(s, s[i][j] + 1, j);
            return X.multiply(Y);
        }
        return chain[i];
    }

    public Matrix multiplyOut() {
        int N = chain.length;
        int[][] s = getBestMultiplicationOrdering(N);
        return multiplyOutSmart(s, 1, N);
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
