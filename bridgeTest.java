import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class bridgeTest {

    /**@Test
    public void testCase1_BridgeWordsExistSingle() throws IOException {
        String input = "2\nthe but\n0\n";
        String expectedOutput = "The bridge words from \"the\" to \"but\" is: \"team\".";
        runTest(input, expectedOutput);
    }

    @Test
    public void testCase2_BridgeWordsExistMultiple() throws IOException {
        String input = "2\nbut team\n0\n";
        // 允许两种可能的输出顺序
        String expectedOutput1 = "The bridge words from \"but\" to \"team\" are: \"the\", and \"a\".";
        String expectedOutput2 = "The bridge words from \"but\" to \"team\" are: \"a\", and \"the\".";
        runTestMultiple(input, expectedOutput1, expectedOutput2);
    }

    @Test
    public void testCase3_NoBridgeWords() throws IOException {
        String input = "2\na the\n0\n";
        String expectedOutput = "No bridge words from \"a\" to \"the\"!";
        runTest(input, expectedOutput);
    }

    @Test
    public void testCase4_WordNotInGraph() throws IOException {
        String input = "2\nyes the\n0\n";
        String expectedOutput = "No yes or the in the graph!";
        runTest(input, expectedOutput);
    }

    @Test
    public void testCase5_InvalidWordFormat() throws IOException {
        String input = "2\n111 the\n0\n";
        String expectedOutput = "No 111 or the in the graph!";
        runTest(input, expectedOutput);
    }*/

   @Test
    public void testCase6_InvalidInputCount() throws IOException {
        String input = "2\nthe\n0\n";
        String expectedOutput = "Invalid input!";
        runTest(input, expectedOutput);
    }

    private void runTest(String input, String expectedOutput) throws IOException {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            Main.main(new String[]{});

            String actualOutput = out.toString();
            assertTrue("Expected output not found: " + expectedOutput,
                    actualOutput.contains(expectedOutput));

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    private void runTestMultiple(String input, String... expectedOptions) throws IOException {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            InputStream in = new ByteArrayInputStream(input.getBytes());
            System.setIn(in);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));

            Main.main(new String[]{});

            String actualOutput = out.toString();
            boolean found = false;
            for (String expected : expectedOptions) {
                if (actualOutput.contains(expected)) {
                    found = true;
                    break;
                }
            }
            assertTrue("None of the expected outputs matched: " + String.join(" OR ", expectedOptions), found);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}