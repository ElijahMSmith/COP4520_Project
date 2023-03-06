import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        String inFolder = "test1/";
        Matrix m1 = new Matrix("in/" + inFolder + "testMatrix1.txt");
        Matrix m2 = new Matrix("in/" + inFolder + "testMatrix2.txt");
        Matrix m3 = new Matrix("in/" + inFolder + "testMatrix3.txt");
        Matrix m4 = new Matrix("in/" + inFolder + "testMatrix4.txt");

        // ((AB)C)D
        MatrixChain chain = new MatrixChain(new Matrix[] { m1, m2, m3, m4 });
        System.out.println(chain.multiplyOut());
    }
}