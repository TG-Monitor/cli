package ai.quantumsense.tgmonitor.test;

import ai.quantumsense.tgmonitor.cli.Cli;
import ai.quantumsense.tgmonitor.monitor.control.MonitorControl;
import ai.quantumsense.tgmonitor.monitor.control.MonitorState;
import ai.quantumsense.tgmonitor.monitor.data.MonitorData;
import ai.quantumsense.tgmonitor.monitor.data.MonitorDataImpl;
import ai.quantumsense.tgmonitor.monitor.logincode.LoginCodeManagerFactory;
import ai.quantumsense.tgmonitor.monitor.logincode.LoginCodeManagerImplFactory;

public class CliTest {
    private static LoginCodeManagerFactory loginCodeManagerFactory = new LoginCodeManagerImplFactory();
    private static MonitorData monitorData = new MonitorDataImpl();
    private static MonitorControl monitorControl = new MonitorControl() {
        private String phoneNumber;
        @Override
        public MonitorState getState() {
            return null;
        }
        @Override
        public void login(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginCodeManagerFactory.getLoginCodeManager().getLoginCodeReader().getLoginCodeFromUser();
        }
        @Override
        public void logout() {}
        @Override
        public void start() {}
        @Override
        public void pause() {}
        @Override
        public String getPhoneNumber() {
            return phoneNumber;
        }
    };


    public static void main(String[] args) {
        Cli cli = new Cli(monitorControl, monitorData, loginCodeManagerFactory);
        cli.run();
    }
}
