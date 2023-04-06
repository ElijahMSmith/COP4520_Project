import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class MatrixGenerator {
    static final int MAX_COLS = 10;
    static final int MAX_ROWS = 10;
    static final int MAX_VALUE = 10;
    static final int MIN_MATRICES = 10;
    static final int MAX_MATRICES = 20;
    static final String outPath = "in/";

    public static void main(String[] args) throws FileNotFoundException {
        int fileNum = 1;
        File f;
        do {
            f = new File(outPath + "test" + fileNum + ".txt");
            fileNum++;
        } while (f.exists() && !f.isDirectory());

        PrintStream stream = new PrintStream(f);
        System.setOut(stream);

        int lastC = -1;

        int numMatrices = (int) (Math.random() * (MAX_MATRICES - MIN_MATRICES + 1)) + MIN_MATRICES;
        System.out.println(numMatrices);
        for (int i = 0; i < numMatrices; i++) {
            int tr = (lastC != -1 ? lastC : (int) (Math.random() * MAX_ROWS + 1));
            int tc = (int) (Math.random() * MAX_COLS + 1);

            System.out.println(tr + " " + tc);
            for (int r = 0; r < tr; r++) {
                for (int c = 0; c < tc; c++) {
                    int val = (int) (Math.random() * (MAX_VALUE + 1));
                    System.out.print(val + (c < tc - 1 ? " " : ""));
                }
                System.out.println();
            }

            lastC = tc;
        }
    }
}
