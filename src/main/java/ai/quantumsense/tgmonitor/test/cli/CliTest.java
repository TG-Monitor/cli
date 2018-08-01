package ai.quantumsense.tgmonitor.test.cli;

import ai.quantumsense.tgmonitor.cli.Cli;
import ai.quantumsense.tgmonitor.corefacade.CoreFacade;
import ai.quantumsense.tgmonitor.corefacade.CoreFacadeImpl;
import ai.quantumsense.tgmonitor.entities.Emails;
import ai.quantumsense.tgmonitor.entities.EmailsImpl;
import ai.quantumsense.tgmonitor.entities.Patterns;
import ai.quantumsense.tgmonitor.entities.PatternsImpl;
import ai.quantumsense.tgmonitor.entities.Peers;
import ai.quantumsense.tgmonitor.entities.PeersImpl;
import ai.quantumsense.tgmonitor.logincodeprompt.LoginCodePrompt;
import ai.quantumsense.tgmonitor.monitor.Monitor;
import ai.quantumsense.tgmonitor.servicelocator.ServiceLocator;

/**
 * Dry-running the CLI. There is a fake login procedure at each startup.
 */
public class CliTest {

    private static Cli cli;
    static {
        ServiceLocator<Peers> peersLocator = new ServiceLocator<Peers>() {
            Peers peers = new PeersImpl(this);
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
        ServiceLocator<Monitor> monitorLocator = new ServiceLocator<Monitor>() {
            Monitor monitor = new Monitor() {
                private String phoneNumber = null;
                private boolean isRunning = false;
                @Override
                public void login(String phoneNumber, LoginCodePrompt loginCodePrompt) {
                    this.phoneNumber = phoneNumber;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loginCodePrompt.promptLoginCode();
                }
                @Override
                public void logout() {}
                @Override
                public boolean isLoggedIn() {
                    return phoneNumber != null;
                }
                @Override
                public void start() {
                    isRunning = true;
                }
                @Override
                public void stop() {
                    isRunning = false;
                }
                @Override
                public boolean isRunning() {
                    return isRunning;
                }
                @Override
                public String getPhoneNumber() {
                    if (phoneNumber == null)
                        phoneNumber = "+417891234567";
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
        CoreFacade monitorFacade = new CoreFacadeImpl(monitorLocator, peersLocator, patternsLocator, emailsLocator);
        cli = new Cli(monitorFacade, "0.0.1");
    }

    public static void main(String[] args) {
        cli.launch();
    }
}
