import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import src.Matrix;
import src.MatrixChain;
import src.ParallelOptimizationChain;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int testno = 1;

        try {
            testno = Integer.parseInt(args[0]);
        } catch (Exception e) {
        }

        String inFile = "in/test" + testno + ".txt";
        String outFile = "out/test" + testno + ".txt";
        MatrixChain chain = new ParallelOptimizationChain(inFile);

        PrintStream stream = new PrintStream(new File(outFile));
        Matrix res = chain.multiplyOut();
        System.setOut(stream);
        System.out.println(res);
    }
}