package ai.quantumsense.tgmonitor.cli;

public class Test {

    private final String s = makeHelpMessage();

    @org.junit.Test
    public void test() {
        System.out.println(HelpMessage.HELP_MESSAGE);
    }

    private String makeHelpMessage() {
        return (new StringBuilder())
                .append("foo\n")
                .append("bar")
                .toString();
    }
}
