package ai.quantumsense.tgmonitor.test.cli;

import ai.quantumsense.tgmonitor.cli.Cli;
import ai.quantumsense.tgmonitor.cli.CliLifecycleHandler;
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
 * Dry-run of the CLI.
 */
public class CliTest {

    private static String version = "0.0.5";
    private static CoreFacade coreFacade;
    private static CliLifecycleHandler lifecycle = new CliLifecycleHandler() {
        @Override
        public void onCliCreate() {}
        @Override
        public void onCliDestroy() {}
    };
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
                private boolean isRunning = true;
                @Override
                public void login(String phoneNumber, LoginCodePrompt loginCodePrompt) {
                    this.phoneNumber = phoneNumber;
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
        coreFacade = new CoreFacadeImpl(monitorLocator, peersLocator, patternsLocator, emailsLocator);
    }

    public static void main(String[] args) {
        new Cli(coreFacade, lifecycle, version);
    }
}
