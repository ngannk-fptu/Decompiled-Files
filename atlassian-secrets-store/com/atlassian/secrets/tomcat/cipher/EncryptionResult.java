/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.tomcat.cipher;

public class EncryptionResult {
    public final String keyFile;
    public final String passwordFile;

    public EncryptionResult(String keyFile, String passwordFile) {
        this.keyFile = keyFile;
        this.passwordFile = passwordFile;
    }
}

