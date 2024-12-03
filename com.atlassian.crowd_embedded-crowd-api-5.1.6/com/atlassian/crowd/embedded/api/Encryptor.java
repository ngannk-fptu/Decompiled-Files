/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

public interface Encryptor {
    public String encrypt(String var1);

    public String decrypt(String var1);

    default public boolean changeEncryptionKey() {
        return false;
    }
}

