import java.io.FileNotFoundException;
import java.util.Arrays;

public class Main {

    static int[][] dp;
    static int chainLength;

    // Function for matrix chain multiplication
    static int matrixChainMemoised(int[] p, int i, int j) {
        if (i == j) {
            return 0;
        }
        if (dp[i][j] != -1) {
            return dp[i][j];
        }
        dp[i][j] = Integer.MAX_VALUE;
        for (int k = i; k < j; k++) {
            dp[i][j] = Math.min(
                    dp[i][j], matrixChainMemoised(p, i, k)
                            + matrixChainMemoised(p, k + 1, j)
                            + p[i - 1] * p[k] * p[j]);
        }
        return dp[i][j];
    }

    static int MatrixChainOrder(int[] p, int n) {
        int i = 1, j = n - 1;
        return matrixChainMemoised(p, i, j);
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Matrix m2 = new Matrix("testMatrix1.txt");
        // Matrix m3 = new Matrix("testMatrix2.txt");
        // Matrix newMatrix = m2.naiveMultiply(m3);
        // System.out.println(newMatrix);

        // int[] chain = { 40, 20, 30, 10, 30 };
        // chainLength = chain.length;
        // dp = new int[chainLength][chainLength];
        // for (int i = 0; i < chainLength; i++)
        // Arrays.fill(dp[i], -1);

        // System.out.println(MatrixChainOrder(chain, chainLength));
    }
}