package ai.quantumsense.tgmonitor.cli;

import java.util.List;

public interface CommandParser {

    /**
     * Parse a command-line into a tokenized list of the command and its
     * arguments.
     *
     * Arguments wrapped in single or double quotes are supported. A double
     * quoted argument may contain single quotes and a single quoted argument
     * may contain double quotes.
     *
     * The following is a list of valid arguments and their parsed value:
     *
     * - foo              ==> [foo]
     * - "foo bar"        ==> [foo bar]
     * - 'foo bar'        ==> [foo bar]
     * - "foo 'bar' baz"  ==> [foo 'bar' baz]
     * - 'foo "bar" baz'  ==> [foo "bar" baz]
     * - "that's ok"      ==> [that's ok]
     * - 'say "yes"'      ==> [say "yes"]
     * - ""               ==> []
     * - ''               ==> []
     * - "foo"            ==> [foo]
     * - 'foo'            ==> [foo]
     * - '"foo"'          ==> ["foo"]
     * - "'foo'"          ==> ['foo']
     *
     * Escaping single or double quotes within a singly or doubly quoted
     * argument, respectively, is NOT supported. That is, arguments like
     * "say \"yes\"" or 'that\'s ok' will NOT be parsed correctly.
     *
     * @param line Command line
     *
     * @return A list consisting of the command and its arguments.
     */
    List<String> parse(String line);
}
