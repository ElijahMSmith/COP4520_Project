import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import src.Matrix;
import src.MatrixChain;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int testno = 1;

        try {
            testno = Integer.parseInt(args[0]);
        } catch (Exception e) {
        }

        String inFile = "in/test" + testno + ".txt";
        String outFile = "out/test" + testno + ".txt";
        MatrixChain chain = new MatrixChain(inFile);

        PrintStream stream = new PrintStream(new File(outFile));
        System.setOut(stream);
        Matrix res = chain.multiplyOut();
        System.out.println(res);
    }
}