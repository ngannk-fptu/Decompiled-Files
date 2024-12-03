/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli;

import com.atlassian.secrets.cli.db.DbCipherTool;
import com.atlassian.secrets.cli.tomcat.TomcatEncryptionTool;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PasswordCipherTool {
    private static final String CONFIGURATION = "configuration";
    private static final String CONFIGURATION_SHORT = "config";
    private static final Options options = new Options();

    public static void main(String[] args) {
        PasswordCipherTool.loadOptions();
        PasswordCipherTool.validateArguments(args);
    }

    private static void loadOptions() {
        options.addOption(new Option(CONFIGURATION_SHORT, CONFIGURATION, true, "The product configuration in which the PasswordCipher will be used. The values currently supported are: \ndb: for database configuration \ntomcat: for tomcat configuration \n(Additional arguments provided will be forwarded to their consumers.)\n"));
    }

    private static void validateArguments(String[] args) {
        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            PasswordCipherTool.processConfiguration(cmd);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp("Tool to encrypt passwords using Cipher for database ot Tomcat", options);
            System.exit(1);
        }
    }

    private static void processConfiguration(CommandLine cmd) {
        String configType = cmd.getOptionValue(CONFIGURATION);
        if (configType == null) {
            System.out.println("Please provide a configuration type either '-config=db' or '-config=tomcat'.");
            System.exit(1);
        }
        switch (configType) {
            case "db": {
                System.out.println("Start database password encryption process...");
                DbCipherTool.main(new String[]{"-h"});
                String argument = System.console().readLine("Press ENTER to continue password encryption or enter an argument:", new Object[0]);
                DbCipherTool.main(new String[]{argument});
                break;
            }
            case "tomcat": {
                System.out.println("Start Tomcat password encryption process...");
                String keyFilePath = System.console().readLine("Press ENTER to continue password encryption \nor enter the path to an existing key file to encrypt password:", new Object[0]);
                TomcatEncryptionTool.main(new String[]{keyFilePath});
                break;
            }
            default: {
                System.out.println("Invalid configuration type. Please specify either '-config=db' or '-config=tomcat'.");
                System.exit(1);
            }
        }
    }
}

