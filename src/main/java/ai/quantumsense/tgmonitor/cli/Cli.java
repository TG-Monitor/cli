package ai.quantumsense.tgmonitor.cli;

import ai.quantumsense.tgmonitor.cli.parsing.CommandParserImpl;
import ai.quantumsense.tgmonitor.entities.Emails;
import ai.quantumsense.tgmonitor.entities.Patterns;
import ai.quantumsense.tgmonitor.entities.Peers;
import ai.quantumsense.tgmonitor.monitor.LoginCodePrompt;
import ai.quantumsense.tgmonitor.monitor.Monitor;
import ai.quantumsense.tgmonitor.servicelocator.ServiceLocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cli implements LoginCodePrompt {

    private ServiceLocator<Peers> peersLocator;
    private ServiceLocator<Patterns> patternsLocator;
    private ServiceLocator<Emails> emailsLocator;
    private ServiceLocator<Monitor> monitorLocator;

    private CommandParser parser = new CommandParserImpl();

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Cli(ServiceLocator<Peers> peersLocator,
               ServiceLocator<Patterns> patternsLocator,
               ServiceLocator<Emails> emailsLocator,
               ServiceLocator<Monitor> monitorLocator,
               ServiceLocator<LoginCodePrompt> loginCodePromptLocator) {
        this.peersLocator = peersLocator;
        this.patternsLocator = patternsLocator;
        this.emailsLocator = emailsLocator;
        this.monitorLocator = monitorLocator;
        if (loginCodePromptLocator != null)
            loginCodePromptLocator.registerService(this);
    }

    public void launch() {

        if (!monitorLocator.getService().isLoggedIn()) {
            System.out.print("Please enter your phone number: ");
            String phoneNumber = readLine();
            monitorLocator.getService().login(phoneNumber);
        }
        println(account());
        monitorLocator.getService().start();

        List<String> cmdline;
        loop: while (true) {
            prompt();
            
            cmdline = parser.parse(readLine());
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
        sb.append("USAGE\n\n");
//        sb.append("  start                 Start monitor\n");
//        sb.append("  stop                  Stop monitor\n");
//        sb.append("  status                Print monitor status (whether it's running or not)\n");
        sb.append("  peers                 Print current list of peers (groups or channels)\n");
        sb.append("  addpeers <peer>...    Add one or more new peers\n");
        sb.append("  rmpeers <peer>...     Remove one or more peers\n");
        sb.append("  pats                  Print current list of patterns\n");
        sb.append("  addpats <pattern>...  Add one or more new patterns\n");
        sb.append("  rmpats <pattern>...   Remove one or more patterns\n");
        sb.append("  emails                Print current list of notification email addresses\n");
        sb.append("  addemails <email>...  Add one or more new email addresses\n");
        sb.append("  rmemails <email>...   Remove one or more email addresses\n");
        sb.append("  account               Print information about used Telegram account\n");
        sb.append("  quit                  Stop monitor, close app, but stay logged in\n");
        sb.append("  logout                Stop monitor, close app, and log out\n");
        sb.append("  help                  Print this help message\n");
        sb.append("\nNOTES\n\n");
        sb.append("  - <peer> refers to the username of a Telegram group or channel.\n");
        sb.append("      * For example: alethena_official\n");
        sb.append("  - <peer> is case-insensitive.\n");
        sb.append("      * For example: Alethena_OFFICIAL = alethena_official\n");
        sb.append("  - A single <pattern> may consist of one or multiple words. If it's multiple\n");
        sb.append("    words, then <pattern> must be enclosed with single or double quotes.\n");
        sb.append("       * For example: \"ico scam\"\n");
        sb.append("  - Patterns are case insensitive.\n");
        sb.append("      * For example: BTC = btc\n");
        sb.append("\nEXAMPLES\n\n");
        sb.append("  > addpeers group1 group2 group3\n");
        sb.append("  > addpats ethereum btc \"falling down\" 'ico scam'\n");
        sb.append("  > rmpats 'falling down'");
        return sb.toString();
    }

    private void start() {
        Monitor m = monitorLocator.getService();
        if (!m.isRunning()) m.start();
    }

    private void stop() {
        Monitor m = monitorLocator.getService();
        if (m.isRunning()) m.stop();
    }

    private String status() {
        if (monitorLocator.getService().isRunning())
            return "Running";
        else
            return "Stopped";
    }

    private void add(Entity entity, Set<String> items) {
        switch (entity) {
            case PEER:
                peersLocator.getService().addPeers(items); break;
            case PATTERN:
                patternsLocator.getService().addPatterns(items); break;
            case EMAIL:
                emailsLocator.getService().addEmails(items); break;
        }
    }

    private void remove(Entity entity, Set<String> items) {
        switch (entity) {
            case PEER:
                peersLocator.getService().removePeers(items); break;
            case PATTERN:
                patternsLocator.getService().removePatterns(items); break;
            case EMAIL:
                emailsLocator.getService().removeEmails(items); break;
        }
    }

    private Set<String> list(Entity entity) {
        Set<String> s;
        switch (entity) {
            case PEER:
                s =  peersLocator.getService().getPeers(); break;
            case PATTERN:
                s = patternsLocator.getService().getPatterns(); break;
            case EMAIL:
                s = emailsLocator.getService().getEmails(); break;
            default:
                s = null;
        }
        if (s.isEmpty()) s.add("<empty>");
        return s;
    }

    private String account() {
        return "Logged in with: " + monitorLocator.getService().getPhoneNumber();
    }

    private void quit() {
        monitorLocator.getService().stop();
        println("Monitor stopped");
    }

    private void logout() {
        monitorLocator.getService().stop();
        monitorLocator.getService().logout();
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
        print("Enter login code: ");
        return readLine();
    }

    private enum Entity {
        PEER, PATTERN, EMAIL
    }
}
