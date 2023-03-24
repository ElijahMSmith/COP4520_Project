import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import src.MatrixChain;
import src.ParallelOptimizationChain;

public class Test {

    public static void main(String[] args) throws FileNotFoundException {
        int testno = 1;

        try {
            testno = Integer.parseInt(args[0]);
        } catch (Exception e) {
        }

        String inFile = "in/test" + testno + ".txt";
        MatrixChain chain = new ParallelOptimizationChain(inFile);

        System.out.println(chain.multiplyOut());
    }
}