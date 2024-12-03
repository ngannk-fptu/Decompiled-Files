/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.application;

public class AliasAlreadyInUseException
extends Exception {
    private final String applicationName;
    private final String aliasName;
    private final String username;

    public AliasAlreadyInUseException(String applicationName, String aliasName, String username) {
        super("Alias [" + aliasName + "] already in use for application [" + applicationName + "] by user [" + username + "]");
        this.applicationName = applicationName;
        this.aliasName = aliasName;
        this.username = username;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public String getUsername() {
        return this.username;
    }
}

