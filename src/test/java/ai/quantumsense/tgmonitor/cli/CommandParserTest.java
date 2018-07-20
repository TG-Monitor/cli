package ai.quantumsense.tgmonitor.cli;

import ai.quantumsense.tgmonitor.cli.commandparsing.CommandParserImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandParserTest {

    private static CommandParser parser;

    @BeforeClass
    public static void createCli() {
        parser = new CommandParserImpl();
    }

    @Test
    public void simple() {
        String line = "cmd foo bar";
        List<String> expected = Arrays.asList("cmd", "foo", "bar");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void whitespaceBetween() {
        String line = "cmd    foo        bar";
        List<String> expected = Arrays.asList("cmd", "foo", "bar");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void whitespaceAtEnd() {
        String line = "cmd foo bar               ";
        List<String> expected = Arrays.asList("cmd", "foo", "bar");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void whitespaceAtBeginning() {
        String line = "           cmd foo bar";
        List<String> expected = Arrays.asList("cmd", "foo", "bar");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void empty() {
        String line = "";
        Assert.assertEquals(Collections.EMPTY_LIST, parser.parse(line));
    }

    @Test
    public void onlyWhitespace() {
        String line = "                           ";
        Assert.assertEquals(Collections.EMPTY_LIST, parser.parse(line));
    }

    @Test
    public void doubleQuotes() {
        String line = "cmd    \"foo bar\"    hello    \"1  2   3\"";
        List<String> expected = Arrays.asList("cmd", "foo bar", "hello", "1  2   3");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void doubleQuotesWithIncludedSingleQuotes() {
        String line = "cmd    \"that's bad, it's too bad\"    \"it's ok\"";
        List<String> expected = Arrays.asList("cmd", "that's bad, it's too bad", "it's ok");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void singleQuotes() {
        String line = "cmd    'foo bar'    hello    '1  2   3'";
        List<String> expected = Arrays.asList("cmd", "foo bar", "hello", "1  2   3");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void singleQuotesWithIncludedDoubleQuotes() {
        String line = "cmd    'she said \"yes\" and \"of course\"'    '\"'";
        List<String> expected = Arrays.asList("cmd", "she said \"yes\" and \"of course\"", "\"");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void emptyDoubleQuotes() {
        String line = "cmd \"\"";
        List<String> expected = Arrays.asList("cmd", "");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void emptySingleQuotes() {
        String line = "cmd ''";
        List<String> expected = Arrays.asList("cmd", "");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void failOnEscapedDoubleQuotes() {
        String line = "cmd \"say \\\"yes\\\"";
        List<String> wronglyExpected = Arrays.asList("cmd", "say \"yes\"");
        Assert.assertFalse(parser.parse(line).equals(wronglyExpected));
    }

    @Test
    public void failOnEscapedSingleQuotes() {
        String line = "cmd 'it\\'s ok";
        List<String> wronglyExpected = Arrays.asList("cmd", "it's ok");
        Assert.assertFalse(parser.parse(line).equals(wronglyExpected));
    }

    @Test
    public void oddNumberOfDoubleQuotesInSpaceDelimitedArg() {
        String line = "cmd a\"b";
        List<String> expected = Arrays.asList("cmd", "a\"b");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void evemNumberOfDoubleQuotesInSpaceDelimitedArg() {
        String line = "cmd a\"bb\"c";
        List<String> expected = Arrays.asList("cmd", "a\"bb\"c");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void evenNumberOfDoubleQuotesInSpaceDelimitedArg() {
        String line = "cmd a'bb'c";
        List<String> expected = Arrays.asList("cmd", "a'bb'c");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void oddNumberOfSingleQuotesInSpaceDelimitedArg() {
        String line = "cmd it's";
        List<String> expected = Arrays.asList("cmd", "it's");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void evenNumberOfSingleQuotesInSpaceDelimitedArg() {
        String line = "cmd a'bb'c";
        List<String> expected = Arrays.asList("cmd", "a'bb'c");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void protectingDoubleQuotesAroundArg() {
        String line = "cmd    '\"foo\"'    '\"foo bar\"'";
        List<String> expected = Arrays.asList("cmd", "\"foo\"", "\"foo bar\"");
        Assert.assertEquals(expected, parser.parse(line));
    }

    @Test
    public void protectingSingleQuotesAroundArg() {
        String line = "cmd    \"'foo'\"    \"'foo bar'\"";
        List<String> expected = Arrays.asList("cmd", "'foo'", "'foo bar'");
        Assert.assertEquals(expected, parser.parse(line));
    }

}
