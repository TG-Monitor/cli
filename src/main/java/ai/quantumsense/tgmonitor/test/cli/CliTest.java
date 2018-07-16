package ai.quantumsense.tgmonitor.test.cli;

import ai.quantumsense.tgmonitor.cli.Cli;
import ai.quantumsense.tgmonitor.entities.Emails;
import ai.quantumsense.tgmonitor.entities.EmailsImpl;
import ai.quantumsense.tgmonitor.entities.Patterns;
import ai.quantumsense.tgmonitor.entities.PatternsImpl;
import ai.quantumsense.tgmonitor.entities.Peers;
import ai.quantumsense.tgmonitor.entities.PeersImpl;
import ai.quantumsense.tgmonitor.monitor.LoginCodePrompt;
import ai.quantumsense.tgmonitor.monitor.Monitor;
import ai.quantumsense.tgmonitor.servicelocator.ServiceLocator;

/**
 * Dry run of the CLI. The login and the starting of monitors (with "addpeer"
 * command) is mocked (doing nothing).
 */
public class CliTest {

    private static Cli cli;
    static {
        ServiceLocator<Peers> peersLocator = new ServiceLocator<Peers>() {
            Peers peers = new PeersImpl(set -> {}, this);
            @Override
            public void registerService(Peers peers) {}
            @Override
            public Peers getService() {
                return peers;
            }
        };
        ServiceLocator<Patterns> patternsLocator = new ServiceLocator<Patterns>() {
            Patterns patterns = new PatternsImpl(this);
            @Override
            public void registerService(Patterns patterns) {}
            @Override
            public Patterns getService() {
                return patterns;
            }
        };
        ServiceLocator<Emails> emailsLocator = new ServiceLocator<Emails>() {
            Emails emails = new EmailsImpl(this);
            @Override
            public void registerService(Emails emails) {}
            @Override
            public Emails getService() {
                return emails;
            }
        };
        ServiceLocator<LoginCodePrompt> loginCodePromptLocator = new ServiceLocator<LoginCodePrompt>() {
            LoginCodePrompt loginCodePrompt = null;
            @Override
            public void registerService(LoginCodePrompt loginCodePrompt) {
                this.loginCodePrompt = loginCodePrompt;
            }
            @Override
            public LoginCodePrompt getService() {
                return loginCodePrompt;
            }
        };
        ServiceLocator<Monitor> monitorLocator = new ServiceLocator<Monitor>() {
            Monitor monitor = new Monitor() {
                String phoneNumber = null;
                @Override
                public void login(String phoneNumber) {
                    this.phoneNumber = phoneNumber;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loginCodePromptLocator.getService().promptLoginCode();
                }
                @Override
                public void logout() {}

                @Override
                public boolean isLoggedIn() {
                    return phoneNumber == null;
                }

                @Override
                public String getPhoneNumber() {
                    return phoneNumber;
                }
            };
            @Override
            public void registerService(Monitor monitor) {}
            @Override
            public Monitor getService() {
                return monitor;
            }
        };
        cli = new Cli(peersLocator, patternsLocator, emailsLocator, monitorLocator, loginCodePromptLocator);
    }

    public static void main(String[] args) {
        cli.start();
    }
}
