/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli.tomcat;

import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.tomcat.cipher.EncryptionResult;
import com.atlassian.secrets.tomcat.cipher.ProductCipher;
import java.util.Optional;

public class TomcatEncryptionTool {
    public static void main(String[] args) {
        ProductCipher cipher = new ProductCipher();
        char[] password = System.console().readPassword("Enter password:", new Object[0]);
        try {
            EncryptionResult encryptionResult = cipher.encrypt(new String(password), TomcatEncryptionTool.getKeyFile(args));
            System.out.printf("Password has been successfully encrypted. Add the following attributes to your Connector in server.xml:%n\tproductEncryptionKey=<path to %s>%n\t<password name>=<path to %s>%n", encryptionResult.keyFile, encryptionResult.passwordFile);
        }
        catch (SecretStoreException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Optional<String> getKeyFile(String[] args) {
        switch (args.length) {
            case 0: {
                return Optional.empty();
            }
            case 1: {
                if (args[0].isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(args[0]);
            }
        }
        throw new IllegalArgumentException("Invalid number of arguments");
    }
}

