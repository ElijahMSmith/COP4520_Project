import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class MultThread implements Runnable {
    Matrix a;
    Matrix b;
    Matrix result;
    int row;
    int col;

    MultThread(Matrix a, Matrix b, Matrix result, int i, int j) {
        this.a = a;
        this.b = b;
        this.result = result;
        row = i;
        col = j;
    }

    @Override
    public void run() {
        int resultVal = 0;
        for (int i = 0; i < a.cols(); i++) {
            resultVal += a.values[row][i] * b.values[i][col];
        }
        result.values[row][col] = resultVal;
    }
}

public class Matrix {
    private int rows;
    private int cols;
    public int[][] values;

    public Matrix(int rows, int cols) {
        values = new int[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }

    public Matrix(int[][] values) {
        this.values = values;
    }

    public Matrix(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        rows = sc.nextInt();
        cols = sc.nextInt();
        values = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = sc.nextInt();
            }
        }
        sc.close();
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    // Naive Version
    public Matrix multiply(Matrix other) {
        Matrix newMatrix = new Matrix(this.rows, other.cols);
        int innerDim = this.cols;

        for (int thisR = 0; thisR < this.rows; thisR++) {
            for (int otherC = 0; otherC < other.cols; otherC++) {
                for (int i = 0; i < innerDim; i++) {
                    newMatrix.values[thisR][otherC] += this.values[thisR][i] * other.values[i][otherC];
                }
            }
        }

        return newMatrix;
    }

    public Matrix multiply(Matrix other, ThreadPoolExecutor pool) {
        Matrix newMatrix = new Matrix(this.rows, other.cols);

        for (int thisR = 0; thisR < this.rows; thisR++) {
            for (int otherC = 0; otherC < other.cols; otherC++) {
                pool.execute(new MultThread(this, other, newMatrix, thisR, otherC));
            }
        }

        while (pool.getActiveCount() != 0)
            ;
        return newMatrix;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                build.append(values[i][j] + (j < cols - 1 ? "\t" : ""));
            if (i < rows - 1)
                build.append("\n");
        }
        return build.toString();
    }
}
