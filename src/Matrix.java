package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Matrix {
    private int rows;
    private int cols;
    private long[][] values;

    public Matrix(int rows, int cols) {
        values = new long[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }

    public Matrix(long[][] values) {
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
        values = new long[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                values[i][j] = sc.nextLong();
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
