package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;

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
        BigInteger resultVal = new BigInteger("0");
        for (int i = 0; i < a.cols(); i++) {
            resultVal = resultVal.add(a.values[row][i].multiply(b.values[i][col]));
        }
        result.values[row][col] = resultVal;
    }
}

public class Matrix {
    private int rows;
    private int cols;
    public BigInteger[][] values;

    public Matrix(int rows, int cols) {
        values = new BigInteger[rows][cols];
        this.rows = rows;
        this.cols = cols;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = new BigInteger("0");
            }
        }
    }

    public Matrix(BigInteger[][] values) {
        this.values = values;
        this.rows = values.length;
        if (rows == 0)
            this.cols = 0;
        else
            this.cols = values[0].length;
    }

    public Matrix(String fileName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        rows = sc.nextInt();
        cols = sc.nextInt();
        values = new BigInteger[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = new BigInteger(sc.next().trim());
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
                    newMatrix.values[thisR][otherC] = newMatrix.values[thisR][otherC].add(this.values[thisR][i].multiply(other.values[i][otherC]));
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
        build.append(rows + " " + cols + "\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                build.append(values[i][j] + (j < cols - 1 ? "\t" : ""));
            if (i < rows - 1)
                build.append("\n");
        }
        return build.toString();
    }
}
