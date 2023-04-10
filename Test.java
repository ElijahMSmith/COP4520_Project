import java.io.FileNotFoundException;

import src.MatrixChain;
import src.ParallelOptimizationChain;

public class Test {

    public static void main(String[] args) throws FileNotFoundException {

        int numTests = 1;

        try {
            numTests = Integer.parseInt(args[0]);
        } catch (Exception e) {
        }

        for (int i = 1; i <= numTests; i++) {
            System.out.println("Running test " + i + '\n');
            String inFile = "in/dp_testing/test" + i + ".txt";
            MatrixChain naiveChain = new MatrixChain(inFile);
            MatrixChain smartChain = new ParallelOptimizationChain(inFile);

            long startNaive = System.currentTimeMillis();
            System.out.print("Naive result: ");
            naiveChain.printOptimizationResult();
            long endNaive = System.currentTimeMillis();

            long startSmart = System.currentTimeMillis();
            System.out.print("Smart result: ");
            smartChain.printOptimizationResult();
            long endSmart = System.currentTimeMillis();

            System.out.println("\nNaive time: " + (endNaive - startNaive) + "ms");
            System.out.println("Smart time: " + (endSmart - startSmart) + "ms");

            System.out.println("\n=======================\n");
        }
    }
}