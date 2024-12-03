/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli.db;

import com.atlassian.secrets.cli.db.CliArgs;
import com.atlassian.secrets.store.base64.Base64SecretStore;
import java.util.Optional;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliArgsProvider {
    private static final String PASSWORD = "password";
    private static final String PASSWORD_SHORT = "p";
    private static final String CLASS = "class";
    private static final String CLASS_SHORT = "c";
    private static final String SILENT = "silent";
    private static final String SILENT_SHORT = "s";
    private static final String HELP = "help";
    private static final String HELP_SHORT = "h";
    private static final String MODE = "mode";
    private static final String MODE_SHORT = "m";
    private final Options options = this.createOptions();

    public Options getOptions() {
        return this.options;
    }

    private Options createOptions() {
        Options opts = new Options();
        Option password = new Option(PASSWORD_SHORT, PASSWORD, true, "Plain text password, which you want to encrypt. If you omit this param console will ask you to type password.");
        Option clazz = new Option(CLASS_SHORT, CLASS, true, "Canonical class name of the Cipher provider, leave empty to use default: " + Base64SecretStore.class.getCanonicalName());
        Option silent = new Option(SILENT_SHORT, SILENT, false, "Log minimum info");
        Option mode = new Option(MODE_SHORT, MODE, true, "'encrypt' or 'decrypt' provided password.");
        Option help = new Option(HELP_SHORT, HELP, false, "Print this message");
        opts.addOption(password);
        opts.addOption(clazz);
        opts.addOption(silent);
        opts.addOption(mode);
        opts.addOption(help);
        return opts;
    }

    public Optional<CliArgs> getCipherProviderCliOptions(String[] args) throws ParseException {
        CommandLine cmd = new DefaultParser().parse(this.options, args);
        if (cmd == null) {
            return Optional.empty();
        }
        if (cmd.hasOption(HELP)) {
            return Optional.of(new CliArgs.CliArgsBuilder().setIsHelp(cmd.hasOption(HELP)).build());
        }
        char[] password = cmd.hasOption(PASSWORD) ? cmd.getOptionValue(PASSWORD).toCharArray() : System.console().readPassword("Enter password:", new Object[0]);
        return Optional.of(new CliArgs.CliArgsBuilder().setIsHelp(cmd.hasOption(HELP)).setPassword(new String(password)).setClassName(cmd.getOptionValue(CLASS)).setIsSilent(cmd.hasOption(SILENT)).isEncryptionMode(!"decrypt".equals(cmd.getOptionValue(MODE))).build());
    }
}

