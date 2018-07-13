package ai.quantumsense.tgmonitor.cli;

import ai.quantumsense.tgmonitor.monitor.logincode.LoginCodeReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CliLoginCodeReader implements LoginCodeReader {
    @Override
    public String getLoginCodeFromUser() {
        System.out.print("Enter login code: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String loginCode = "";
        try {
            loginCode = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginCode;
    }
}
