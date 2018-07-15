package ai.quantumsense.tgmonitor.cli;

import ai.quantumsense.tgmonitor.entities.Emails;
import ai.quantumsense.tgmonitor.entities.Patterns;
import ai.quantumsense.tgmonitor.entities.Peers;
import ai.quantumsense.tgmonitor.monitor.LoginCodePrompt;
import ai.quantumsense.tgmonitor.monitor.Monitor;
import ai.quantumsense.tgmonitor.servicelocator.ServiceLocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Cli implements LoginCodePrompt {

    private ServiceLocator<Peers> peersLocator;
    private ServiceLocator<Patterns> patternsLocator;
    private ServiceLocator<Emails> emailsLocator;
    private ServiceLocator<Monitor> monitorLocator;

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

    public void start() {
        System.out.print("Please enter your phone number: ");
        String phoneNumber = readLine();
        monitorLocator.getService().login(phoneNumber);
        System.out.println("You are logged in with " + phoneNumber);

        List<String> cmd;

        loop: while (true) {
            prompt();
            cmd = parseCommand(readLine());
            if (cmd.isEmpty()) continue;

            switch (cmd.get(0)) {
                case "peers":
                    list(Type.PEER);
                    break;
                case "addpeer":
                    add(Type.PEER, cmd.get(1));
                    break;
                case "rmpeer":
                    remove(Type.PEER, cmd.get(1));
                    break;
                case "pats":
                    list(Type.PATTERN);
                    break;
                case "addpat":
                    add(Type.PATTERN, cmd.get(1));
                    break;
                case "rmpat":
                    remove(Type.PATTERN, cmd.get(1));
                    break;
                case "emails":
                    list(Type.EMAIL);
                    break;
                case "addemail":
                    add(Type.EMAIL, cmd.get(1));
                    break;
                case "rmemail":
                    remove(Type.EMAIL, cmd.get(1));
                    break;
                case "whoami":
                    account();
                    break;
                case "help":
                    help();
                    break;
                case "quit":
                    break loop;
                case "":
                    break;
                default:
                    invalidCommand();
                    break;
            }
        }
    }

    private void help() {
        StringBuilder sb = new StringBuilder();
        sb.append("peers              show peers (groups or channels) that are currently monitored\n");
        sb.append("addpeer <peer>     add a new peer (username, e.g. \"alethena_official\")\n");
        sb.append("rmpeer <peer>      remove a peer (username)\n");
        sb.append("pats               show the current list of patterns\n");
        sb.append("addpat <pat>       add a new pattern\n");
        sb.append("rmpat <pat>        remove a pattern\n");
        sb.append("emails             show the current list of notification email addresses\n");
        sb.append("addemail <email>   add a new email address\n");
        sb.append("rmemail <email>    remove an email address\n");
        sb.append("whoami             print information about your account\n");
        sb.append("quit               stop all monitors and log out\n");
        sb.append("help               show this help message");
        System.out.println(sb.toString());
    }

    private void add(Type type, String item) {
        switch (type) {
            case PEER:
                peersLocator.getService().addPeer(item);
                break;
            case PATTERN:
                patternsLocator.getService().addPattern(item);
                break;
            case EMAIL:
                emailsLocator.getService().addEmail(item);
                break;
        }
    }

    private void remove(Type type, String item) {
        switch (type) {
            case PEER:
                peersLocator.getService().removePeer(item);
                break;
            case PATTERN:
                patternsLocator.getService().removePattern(item);
                break;
            case EMAIL:
                emailsLocator.getService().removeEmail(item);
                break;
        }
    }

    private void list(Type type) {
        Set<String> items = null;
        switch (type) {
            case PEER:
                items = peersLocator.getService().getPeers();
                break;
            case PATTERN:
                items = patternsLocator.getService().getPatterns();
                break;
            case EMAIL:
                items = emailsLocator.getService().getEmails();
                break;
        }
        for (String i : items)
            System.out.println(i);
    }

    private void account() {
        System.out.println("You are logged in with " + monitorLocator.getService().getPhoneNumber());
    }

    private void invalidCommand() {
        System.out.println("Invalid command: type \"help\" for usage.");
    }

    private void prompt() {
        System.out.print("> ");
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

     List<String> parseCommand(String line) {
        List<String> cmd = new LinkedList<>(Arrays.asList(line.split("\\s+")));
        while (cmd.remove(""));
        return cmd;
    }

    @Override
    public String promptLoginCode() {
        System.out.print("Enter login code: ");
        return readLine();
    }

    private enum Type {
        PEER, PATTERN, EMAIL
    }
}
