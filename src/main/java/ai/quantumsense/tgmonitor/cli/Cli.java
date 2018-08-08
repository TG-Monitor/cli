package ai.quantumsense.tgmonitor.cli;

import ai.quantumsense.tgmonitor.cli.commandparsing.CommandParserImpl;
import ai.quantumsense.tgmonitor.corefacade.CoreFacade;
import ai.quantumsense.tgmonitor.logincodeprompt.LoginCodePrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cli implements LoginCodePrompt {

    private Logger logger = LoggerFactory.getLogger(Cli.class);

    private static String VERSION;

    private CoreFacade coreFacade;
    private CommandParser parser = new CommandParserImpl();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Cli(CoreFacade coreFacade, String version) {
        logger.debug("Creating CLI");
        this.coreFacade = coreFacade;
        VERSION = version;
    }

    public void launch() {
        logger.debug("Launching CLI");
        println("TG-Monitor " + VERSION);

        if (!coreFacade.isLoggedIn()) {
            logger.debug("Not logged in");
            System.out.print("Please enter your phone number: ");
            String phoneNumber = readLine();
            coreFacade.login(phoneNumber, this);
        }
        logger.debug("Now logged in");
        println(account());
        // TODO: check if monitor is already running
        coreFacade.start();

        loop: while (true) {
            prompt();
            String line = readLine();

            // If entering Ctrl-D
            if (line == null) {
                quit();
                break;
            }

            List<String> cmdline = parser.parse(line);
            if (cmdline.isEmpty()) continue;
            
            String cmd = cmdline.get(0);
            List<String> args = cmdline.subList(1, cmdline.size());
            
            switch (cmd) {
//                case "start":
//                    start(); break;
//                case "stop":
//                    stop(); break;
//                case "status":
//                    println(status()); break;
                case "peers":
                    println(list(Entity.PEER)); break;
                case "addpeers":
                    add(Entity.PEER, new HashSet<>(args)); break;
                case "rmpeers":
                    remove(Entity.PEER, new HashSet<>(args)); break;
                case "pats":
                    println(list(Entity.PATTERN)); break;
                case "addpats":
                    add(Entity.PATTERN, new HashSet<>(args)); break;
                case "rmpats":
                    remove(Entity.PATTERN, new HashSet<>(args)); break;
                case "emails":
                    println(list(Entity.EMAIL)); break;
                case "addemails":
                    add(Entity.EMAIL, new HashSet<>(args)); break;
                case "rmemails":
                    remove(Entity.EMAIL, new HashSet<>(args)); break;
                case "account":
                    println(account()); break;
                case "help":
                    println(help()); break;
                case "quit":
                    quit(); break loop;
                case "logout":
                    logout(); break loop;
                case "":
                    break;
                default:
                    println(invalidCommand(cmd)); break;
            }
        }
    }

    private String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nUSAGE\n");
//        sb.append("  start                 Start monitor\n");
//        sb.append("  stop                  Stop monitor\n");
//        sb.append("  status                Print monitor status (whether it's running or not)\n");
        sb.append("    peers                 Print current list of peers (groups or channels)\n");
        sb.append("    addpeers <peer>...    Add one or more peers\n");
        sb.append("    rmpeers <peer>...     Remove one or more peers\n");
        sb.append("    pats                  Print current list of patterns\n");
        sb.append("    addpats <pattern>...  Add one or more patterns\n");
        sb.append("    rmpats <pattern>...   Remove one or more patterns\n");
        sb.append("    emails                Print current list of notification email addresses\n");
        sb.append("    addemails <email>...  Add one or more email addresses\n");
        sb.append("    rmemails <email>...   Remove one or more email addresses\n");
        sb.append("    account               Print information about used Telegram account\n");
        sb.append("    quit                  Stop monitor, close app, but stay logged in\n");
        sb.append("    logout                Stop monitor, close app, and log out\n");
        sb.append("    help                  Print this help message\n");
        sb.append("\nNOTES\n");
        sb.append("    * A <peer> is the username of a Telegram group or channel\n");
        sb.append("        * Example: alethena_official\n");
        sb.append("    * Usernames are case-insensitive\n");
        sb.append("        * Example: Alethena_OFFICIAL = alethena_official\n");
        sb.append("    * A <pattern> can be a single word, or multiple words enclosed in quotes\n");
        sb.append("        * Example: bitcoin ico \"initial coin offering\"\n");
        sb.append("    * Patterns are case-insensitive\n");
        sb.append("        * Example: BTC = btc\n");
        sb.append("\nEXAMPLES\n");
        sb.append("    > addpeers group1 group2 group3\n");
        sb.append("    > addpats btc bitcoin \"ico scam\" \"market crash\"\n");
        sb.append("    > addemails all@equility.ch\n");
        sb.append("\nVERSION\n");
        sb.append("    TG-Monitor " + VERSION + "\n");
        return sb.toString();
    }

//    private void start() {
//        Monitor m = monitorLocator.getService();
//        if (!m.isRunning()) m.start();
//    }
//
//    private void stop() {
//        Monitor m = monitorLocator.getService();
//        if (m.isRunning()) m.stop();
//    }
//
//    private String status() {
//        if (monitorLocator.getService().isRunning())
//            return "Running";
//        else
//            return "Stopped";
//    }

    private void add(Entity entity, Set<String> items) {
        switch (entity) {
            case PEER:
                coreFacade.addPeers(items); break;
            case PATTERN:
                coreFacade.addPatterns(items); break;
            case EMAIL:
                coreFacade.addEmails(items); break;
        }
    }

    private void remove(Entity entity, Set<String> items) {
        switch (entity) {
            case PEER:
                coreFacade.removePeers(items); break;
            case PATTERN:
                coreFacade.removePatterns(items); break;
            case EMAIL:
                coreFacade.removeEmails(items); break;
        }
    }

    private Set<String> list(Entity entity) {
        Set<String> s;
        switch (entity) {
            case PEER:
                s =  coreFacade.getPeers(); break;
            case PATTERN:
                s = coreFacade.getPatterns(); break;
            case EMAIL:
                s = coreFacade.getEmails(); break;
            default:
                s = null;
        }
        if (s.isEmpty()) s.add("<empty>");
        return s;
    }

    private String account() {
        return "Logged in with: " + coreFacade.getPhoneNumber();
    }

    private void quit() {
        coreFacade.stop();
        println("Monitor stopped");
    }

    private void logout() {
        coreFacade.stop();
        coreFacade.logout();
        println("Monitor stopped and logged out");
    }

    private void prompt() {
        print("> ");
    }

    private String invalidCommand(String cmd) {
        return "Invalid command '" + cmd + "'. Type \"help\" for usage.";
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

    private void println(Collection<String> c) {
        println(String.join("\n", c));
    }
    private void println(String s) {
        System.out.println(s);
    }
    private void print(String s) {
        System.out.print(s);
    }

    @Override
    public String promptLoginCode() {
        logger.debug("Prompting login code from user");
        print("Enter login code: ");
        String loginCode = readLine();
        logger.debug("User entered the following login code: " + loginCode);
        return readLine();
    }

    private enum Entity {
        PEER, PATTERN, EMAIL
    }
}
