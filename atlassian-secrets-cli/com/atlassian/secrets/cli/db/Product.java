/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli.db;

import com.atlassian.secrets.store.algorithm.AesOnlyAlgorithmSecretStore;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Product {
    JIRA("Jira", "dbconfig.xml", "<atlassian-password-cipher-provider>%s</atlassian-password-cipher-provider>%n<password>%s</password>"),
    BITBUCKET("Bitbucket", "bitbucket.properties", "encrypted-property.cipher.classname=%s%njdbc.password=%s"),
    BAMBOO("Bamboo", "bamboo.cfg.xml", "<property name=\"jdbc.password.decrypter.classname\">%s</property>%n<property name=\"hibernate.connection.password\">%s</property>"),
    CONFLUENCE("Confluence", "confluence.cfg.xml", "<property name=\"jdbc.password.decrypter.classname\">%s</property>%n<property name=\"hibernate.connection.password\">%s</property>"),
    CROWD("Crowd", "crowd.cfg.xml", "<property name=\"jdbc.password.decrypter.classname\">%s</property>%n<property name=\"hibernate.connection.password\">%s</property>", AesOnlyAlgorithmSecretStore.class.getName());

    private final String productName;
    private final String configFileName;
    private final String configFileContent;
    private final Set<String> allowedCiphers;

    private Product(String productName, String configFileName, String configFileContent) {
        this(productName, configFileName, configFileContent, new String[0]);
    }

    private Product(String productName, String configFileName, String configFileContent, String ... allowedCiphers) {
        this.productName = productName;
        this.configFileName = configFileName;
        this.configFileContent = configFileContent;
        this.allowedCiphers = Arrays.stream(allowedCiphers).collect(Collectors.toSet());
    }

    public String configInstructions(String cipherClass, String password) {
        if (this.allowedCiphers.isEmpty() || this.allowedCiphers.contains(cipherClass)) {
            return String.format("For %s, set the following properties in %s:%n%n" + this.configFileContent, this.productName, this.configFileName, cipherClass, password);
        }
        return String.format("This password cannot be used in %s as %s only accepts passwords provided by one of the following secret stores: %s", this.productName, this.productName, this.allowedCiphers);
    }
}

