import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Matrix {
    private int rows;
    private int cols;
    private int[][] values;

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
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                build.append(values[i][j] + (j < cols - 1 ? "\t" : ""));
            if (i < rows - 1)
                build.append("\n");
        }
        return build.toString();
    }
}
