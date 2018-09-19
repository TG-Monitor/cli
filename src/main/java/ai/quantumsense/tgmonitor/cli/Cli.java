package ai.quantumsense.tgmonitor.cli;

import ai.quantumsense.tgmonitor.cli.parser.ParserImpl;
import ai.quantumsense.tgmonitor.corefacade.CoreFacade;
import ai.quantumsense.tgmonitor.logincodeprompt.LoginCodePrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ai.quantumsense.tgmonitor.cli.Commands.*;
import static ai.quantumsense.tgmonitor.cli.HelpMessage.HELP_MESSAGE;

public class Cli implements LoginCodePrompt {

    private Logger logger = LoggerFactory.getLogger(Cli.class);

    private String version;
    private CoreFacade coreFacade;
    private Parser parser = new ParserImpl();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Cli(CoreFacade coreFacade, CliLifecycleHandler lifecycle, String version) {
        lifecycle.onCliCreate();
        this.coreFacade = coreFacade;
        this.version = version;
        launch();
        mainloop();
        lifecycle.onCliDestroy();
    }

    private void launch() {
        out("TG-Monitor " + version, true);
        if (!coreFacade.isLoggedIn()) {
            out("Please enter your phone number: ", false);
            String phoneNumber = readLine();
            coreFacade.login(phoneNumber, this);
            coreFacade.start();
        }
        out(formatAccountInfo(coreFacade.getPhoneNumber()), true);
        if (!coreFacade.isRunning()) {
            coreFacade.start();
        }
    }

    private void mainloop() {
        loop: while (true) {
            prompt();

            String line = readLine();
            if (line == null) break;  // If entering Ctrl-D
            List<String> cmdline = parser.parse(line);
            if (cmdline.isEmpty()) continue;
            
            String cmd = cmdline.get(0);
            List<String> args = cmdline.subList(1, cmdline.size());

            Set<String> argsSet = new HashSet<>(args);

            switch (cmd) {
                case LIST_PEERS:
                    Set<String> peers = coreFacade.getPeers();
                    out(formatSetOfStrings(peers), true);
                    break;
                case ADD_PEERS:
                    coreFacade.addPeers(argsSet);
                    break;
                case REMOVE_PEERS:
                    coreFacade.removePeers(argsSet);
                    break;
                case LIST_PATTERNS:
                    Set<String> patterns = coreFacade.getPatterns();
                    out(formatSetOfStrings(patterns), true);
                    break;
                case ADD_PATTERNS:
                    coreFacade.addPatterns(argsSet);
                    break;
                case REMOVE_PATTERNS:
                    coreFacade.removePatterns(argsSet);
                    break;
                case LIST_EMAILS:
                    Set<String> emails = coreFacade.getEmails();
                    out(formatSetOfStrings(emails), true);
                    break;
                case ADD_EMAILS:
                    coreFacade.addEmails(argsSet);
                    break;
                case REMOVE_EMAILS:
                    coreFacade.removeEmails(argsSet);
                    break;
                case ACCOUNT_INFO:
                    String phoneNumber = coreFacade.getPhoneNumber();
                    out(formatAccountInfo(phoneNumber), true);
                    break;
                case HELP:
                    out(HELP_MESSAGE, true);
                    break;
                case EXIT:
                    break loop;
                case LOGOUT:
                    coreFacade.logout();
                    out("Logged out and monitor stopped", true);
                    break loop;
                default:
                    out(formatInvalidCommand(cmd), true);
                    break;
            }
        }
    }

    @Override
    public String promptLoginCode() {
        logger.debug("Prompting login code from user");
        out("Enter login code: ", false);
        String loginCode = readLine();
        logger.debug("User entered the following login code: " + loginCode);
        return loginCode;
    }

    private void prompt() {
        out("> ", false);
    }

    private String formatAccountInfo(String phoneNumber) {
        return "Logged in with: " + phoneNumber;
    }

    private String formatInvalidCommand(String cmd) {
        return "Invalid command: " + cmd + ". Type \"help\" for a list of valid commands.";
    }

    private String formatSetOfStrings(Set<String> set) {
        if (set.isEmpty())
            return "<empty>";
        else
            return String.join("\n", set);
    }

    private void out(String s, boolean newline) {
        if (newline)
            System.out.println(s);
        else
            System.out.print(s);
    }

    private String readLine() {
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
