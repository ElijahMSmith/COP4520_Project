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
        this.row = i;
        this.col = j;
    }

    // Responsible for multiplying row "row" of matrix "a" with column "col" of matrix "b" and
    // storing the result at position ("row", "col") in matrix "result"
    @Override
    public void run() {
        BigInteger resultVal = new BigInteger("0");

        // Iterate through specified row/column of matrix a and b, multiplying each matching cell and adding to
        // the result sum
        for (int i = 0; i < a.cols(); i++) {
            resultVal = resultVal.add(a.values[row][i].multiply(b.values[i][col]));
        }

        // Assign this row/column multiplication result into specified position in the result matrix
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

    // Multithreaded Version
    public Matrix multiply(Matrix other, ThreadPoolExecutor pool) {
        // Create empty result matrix
        Matrix newMatrix = new Matrix(this.rows, other.cols);

        // Get the number of tasks that the thread pool completed before getting to this multiplication step
        long tasksDoneBefore = pool.getCompletedTaskCount();

        // Get number of individual row/column multiplications that will need to be completed to get result matrix
        long tasksToBeComputed = this.rows * other.cols;

        // Iterate through each cell of result matrix to get its value
        for (int thisR = 0; thisR < this.rows; thisR++) {
            for (int otherC = 0; otherC < other.cols; otherC++) {
                // Spawn a task for the thread pool that will multiply the current row and column of the input matrices
                // and assign this value to the current (row, column) cell of the result matrix
                pool.execute(new MultThread(this, other, newMatrix, thisR, otherC));
            }
        }

        // Wait until all tasks for the result matrix have been completed
        while (pool.getCompletedTaskCount() - tasksDoneBefore < tasksToBeComputed)
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
