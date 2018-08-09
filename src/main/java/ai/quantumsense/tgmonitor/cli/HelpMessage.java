package ai.quantumsense.tgmonitor.cli;

import static ai.quantumsense.tgmonitor.cli.Commands.*;

public class HelpMessage {

    private static final int INDENT_WIDTH = 4;
    private static final int CMD_COL_WIDTH = 22;

    static final String HELP_MESSAGE = (new StringBuilder())
                .append(emptyLine())
                .append(simpleLine("USAGE"))
                .append(cmdLine(LIST_PEERS,                         "Print current list of peers"))
                .append(cmdLine(ADD_PEERS + " <peer>...",           "Add one or more peers"))
                .append(cmdLine(REMOVE_PEERS + " <peer>...",        "Remove one or more peers"))
                .append(cmdLine(LIST_PATTERNS,                      "Print current list of patterns"))
                .append(cmdLine(ADD_PATTERNS + " <pattern>...",     "Add one or more patterns"))
                .append(cmdLine(REMOVE_PATTERNS + " <pattern>...",  "Remove one or more patterns"))
                .append(cmdLine(LIST_EMAILS,                        "Print current list of notification email addresses"))
                .append(cmdLine(ADD_EMAILS + " <email>...",         "Add one or more notification email addresses"))
                .append(cmdLine(REMOVE_EMAILS + " <email>...",      "Remove one or more notification email addresses"))
                .append(cmdLine(ACCOUNT_INFO,                       "Print login information"))
                .append(cmdLine(LOGOUT,                             "Log out, stop monitor, and exit CLI"))
                .append(cmdLine(EXIT,                               "Exit CLI"))
                .append(cmdLine(HELP,                               "Print this help message"))
                .append(emptyLine())
                .append(simpleLine("NOTES"))
                .append(simpleLineWithIndent("* <peer>: case-insensitive username of a public Telegram group or channel"))
                .append(simpleLineWithIndent("    * Examples:"))
                .append(simpleLineWithIndent("        * alethena_official"))
                .append(simpleLineWithIndent("        * Alethena_OFFICIAL"))
                .append(simpleLineWithIndent("* <pattern>: arbitrary case-insensitive string (quote if containing whitespace)"))
                .append(simpleLineWithIndent("    * Examples:"))
                .append(simpleLineWithIndent("        * btc"))
                .append(simpleLineWithIndent("        * BTC"))
                .append(simpleLineWithIndent("        * \"ico scam\""))
                .append(simpleLineWithIndent("        * \"ICO Scam\""))
                .append(emptyLine())
                .append(simpleLine("EXAMPLE SESSION"))
                .append(simpleLineWithIndent("> " + ADD_PEERS + " icocountdown crypto_experts_signals"))
                .append(simpleLineWithIndent("> " + ADD_PATTERNS + " btc \"ico scam\""))
                .append(simpleLineWithIndent("> " + ADD_EMAILS + " admin@equility.ch"))
                .append(simpleLineWithIndent("> exit"))
                .toString();

    private static String emptyLine() {
        return "\n";
    }
    private static String simpleLine(String s) {
        return s + "\n";
    }
    private static String simpleLineWithIndent(String s) {
        return String.format("%-" + INDENT_WIDTH + "s%s\n", "", s);
    }
    private static String cmdLine(String col1, String col2) {
        return String.format("%-" + INDENT_WIDTH + "s%-" + CMD_COL_WIDTH +"s%s\n", "", col1, col2);
    }
}
