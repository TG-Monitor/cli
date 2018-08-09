package ai.quantumsense.tgmonitor.cli.parser;

import ai.quantumsense.tgmonitor.cli.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserImpl implements Parser {

    private static final String REGEX = "\'([^\']*)\'|\"([^\"]*)\"|(\\S+)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);


    @Override
    public List<String> parse(String line) {
        List<String> l = new ArrayList<>();
        Matcher m = PATTERN.matcher(line);
        while (m.find()) {
            if (m.group(1) != null) l.add(m.group(1));
            else if (m.group(2) != null) l.add(m.group(2));
            else if (m.group(3) != null) l.add(m.group(3));
        }
        return l;
    }
}
