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

    @Test  // 对应测试用例1：无效单词格式
    public void testCase1_InvalidWordFormat() throws IOException {
        String input = "2\n111 the\n0\n";
        String expectedOutput = "No 111 or the in the graph!";
        runTest(input, expectedOutput);
    }

    @Test  // 对应测试用例2：无桥接词
    public void testCase2_NoBridgeWords() throws IOException {
        String input = "2\na the\n0\n";
        String expectedOutput = "No bridge words from \"a\" to \"the\"!";
        runTest(input, expectedOutput);
    }

    @Test  // 对应测试用例3：单个桥接词
    public void testCase3_SingleBridgeWord() throws IOException {
        String input = "2\nthe but\n0\n";
        String expectedOutput = "The bridge words from \"the\" to \"but\" is: \"team\".";
        runTest(input, expectedOutput);
    }

    @Test  // 对应测试用例4：两个桥接词
    public void testCase4_MultipleBridgeWords() throws IOException {
        String input = "2\nand the\n0\n";
        String[] expectedOptions = {
                "The bridge words from \"and\" to \"the\" are: \"but\", and \"shared\".",
                "The bridge words from \"and\" to \"the\" are: \"shared\", and \"but\"."
        };
        runTestMultiple(input, expectedOptions);
    }

    @Test  // 对应测试用例5：多个桥接词
    public void testCase5_MultipleBridgeWordsOrder() throws IOException {
        String input = "2\nbut team\n0\n";
        String[] expectedOptions = {
                "The bridge words from \"but\" to \"team\" are: \"the\", \"a\", and \"two\".",
                "The bridge words from \"but\" to \"team\" are: \"the\", \"two\", and \"a\".",
                "The bridge words from \"but\" to \"team\" are: \"a\", \"the\", and \"two\".",
                "The bridge words from \"but\" to \"team\" are: \"a\", \"two\", and \"the\".",
                "The bridge words from \"but\" to \"team\" are: \"two\", \"the\", and \"a\".",
                "The bridge words from \"but\" to \"team\" are: \"two\", \"a\", and \"the\"."
        };
        runTestMultiple(input, expectedOptions);
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