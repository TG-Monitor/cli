package ai.quantumsense.tgmonitor.cli;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ParseCommandTest {

    private static Cli cli;

    @BeforeClass
    public static void createCli() {
        cli = new Cli(null, null, null, null, null);
    }

    @Test
    public void simple() {
        String line = "addpeer foo bar";
        List<String> expected = Arrays.asList("addpeer", "foo", "bar");
        Assert.assertEquals(expected, cli.parseCommand(line));
    }

    @Test
    public void whitespaceBetween() {
        String line = "addpeer    foo        bar";
        List<String> expected = Arrays.asList("addpeer", "foo", "bar");
        Assert.assertEquals(expected, cli.parseCommand(line));
    }

    @Test
    public void whitespaceAtEnd() {
        String line = "addpeer foo bar               ";
        List<String> expected = Arrays.asList("addpeer", "foo", "bar");
        Assert.assertEquals(expected, cli.parseCommand(line));
    }

    @Test
    public void whitespaceAtBeginning() {
        String line = "           addpeer foo bar";
        List<String> expected = Arrays.asList("addpeer", "foo", "bar");
        Assert.assertEquals(expected, cli.parseCommand(line));
    }

    @Test
    public void empty() {
        String line = "";
        Assert.assertEquals(Collections.EMPTY_LIST, cli.parseCommand(line));
    }

    @Test
    public void onlyWhitespace() {
        String line = "                           ";
        Assert.assertEquals(Collections.EMPTY_LIST, cli.parseCommand(line));
    }

}
