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

public class newtextTest {


    /* 测试用例1：多位置插入桥接词 */
    @Test
    public void testCase1_MultipleInsertions() throws IOException {
        String input = "3\nmore wrote detailed with\n0\n";
        String expected = "New text: more data wrote a detailed report with";
        runTest(input, expected);
    }

    /* 测试用例2：无桥接词 */
    @Test
    public void testCase2_ConsecutiveInsertions() throws IOException {
        String input = "3\na b c d\n0\n";
        String expected = "New text: a b c d";
        runTest(input, expected);
    }

    /* 测试用例3：句子全部都小写 */
    @Test
    public void testCase3_RandomSelection() throws IOException {
        String input = "3\nbut scientist\n0\n";
        String[] expectedOptions = {
                "New text: but the scientist",
                "New text: but a scientist"
        };
        runTestMultiple(input, expectedOptions);
    }

    /* 测试用例4：句子存在大写 */
    @Test
    public void testCase4_CaseInsensitive() throws IOException {
        String input = "3\nBut Scientist It\n0\n";
        String expected = "New text: but the scientist analyzed it";
        runTest(input, expected);
    }

    /* 测试用例5：存在字母外符号 */
    @Test
    public void testCase5_SpecialCharacters() throws IOException {
        String input = "3\nit111but\n0\n";
        String expected = "New text: it again but";
        runTest(input, expected);
    }

    /* 测试用例6：输入空 */
    @Test
    public void testCase6_InvalidInput() throws IOException {
        String input = "3\n\n0\n";
        String expected = "Invalid input!";
        runTest(input, expected);
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