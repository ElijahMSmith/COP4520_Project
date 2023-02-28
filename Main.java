import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Matrix m1 = new Matrix("in/testMatrix1.txt");
        Matrix m2 = new Matrix("in/testMatrix2.txt");
        Matrix m3 = new Matrix("in/testMatrix3.txt");
        Matrix m4 = new Matrix("in/testMatrix4.txt");

        // Matrix newMatrix = m1.multiply(m2);
        // System.out.println(newMatrix);

        MatrixChain chain = new MatrixChain(new Matrix[] { m1, m2, m3, m4 });
        chain.getBestMultiplicationOrdering();

        System.out.println(chain.multiplyOut());
    }
}