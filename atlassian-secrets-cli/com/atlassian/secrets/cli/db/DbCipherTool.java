/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli.db;

import com.atlassian.secrets.DefaultSecretStoreProvider;
import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.cli.db.CliArgs;
import com.atlassian.secrets.cli.db.CliArgsProvider;
import com.atlassian.secrets.cli.db.Product;
import com.atlassian.secrets.cli.db.SecretStoreOutput;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class DbCipherTool {
    public static void main(String[] args) {
        CliArgsProvider cliArgsProvider = new CliArgsProvider();
        Optional<Object> cipherArgsOpt = Optional.empty();
        try {
            cipherArgsOpt = cliArgsProvider.getCipherProviderCliOptions(args);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        if (!cipherArgsOpt.isPresent() || ((CliArgs)cipherArgsOpt.get()).isHelp()) {
            new HelpFormatter().printHelp("Tool to encrypt password using Cipher", cliArgsProvider.getOptions());
            return;
        }
        CliArgs cipherArgs = (CliArgs)cipherArgsOpt.get();
        SecretStoreOutput secretStoreOutput = DbCipherTool.getOutputData(cipherArgs.getClassName(), cipherArgs.getPassword(), cipherArgs.isEncryptionMode());
        if (cipherArgs.isSilent()) {
            System.out.print(secretStoreOutput.getSecretValue());
        } else if (cipherArgs.isEncryptionMode()) {
            String lineSeparator = System.getProperty("line.separator");
            System.out.println(String.format("Success!%n", new Object[0]) + Arrays.stream(Product.values()).map(p -> p.configInstructions(secretStoreOutput.getSecretStoreClass(), secretStoreOutput.getSecretValue())).collect(Collectors.joining(lineSeparator + lineSeparator)));
        } else {
            System.out.println("Success! Decrypted password using cipher provider: " + secretStoreOutput.getSecretStoreClass() + " decrypted password: " + secretStoreOutput.getSecretValue());
        }
    }

    public static SecretStoreOutput getOutputData(String className, String password, boolean isEncryptionMode) {
        DefaultSecretStoreProvider provider = new DefaultSecretStoreProvider();
        if (className == null || className.isEmpty()) {
            className = provider.getDefaultSecretStoreClassName();
        }
        String finalClassName = className;
        return provider.getInstance(className).map(c -> new SecretStoreOutput(isEncryptionMode ? c.store(password) : c.get(password), finalClassName)).orElseThrow(() -> new SecretStoreException(String.format("Couldn't process the password, see logs for more info. In case of %s, add your library to class path when running this lib. You can use -cp param", ClassNotFoundException.class.toString())));
    }
}

