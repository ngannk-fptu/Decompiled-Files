/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Arrays;

public class SymmetricEncryptionConfig {
    public static final String DEFAULT_SYMMETRIC_PASSWORD = "thepassword";
    public static final String DEFAULT_SYMMETRIC_SALT = "thesalt";
    private static final int ITERATION_COUNT = 19;
    private boolean enabled;
    private String algorithm = "PBEWithMD5AndDES";
    private String password = "thepassword";
    private String salt = "thesalt";
    private int iterationCount = 19;
    private byte[] key;

    public boolean isEnabled() {
        return this.enabled;
    }

    public SymmetricEncryptionConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public SymmetricEncryptionConfig setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public SymmetricEncryptionConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSalt() {
        return this.salt;
    }

    public SymmetricEncryptionConfig setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public int getIterationCount() {
        return this.iterationCount;
    }

    public SymmetricEncryptionConfig setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public byte[] getKey() {
        return this.cloneKey(this.key);
    }

    public SymmetricEncryptionConfig setKey(byte[] key) {
        this.key = this.cloneKey(key);
        return this;
    }

    private byte[] cloneKey(byte[] key) {
        return key != null ? Arrays.copyOf(key, key.length) : null;
    }

    public String toString() {
        return "SymmetricEncryptionConfig{enabled=" + this.enabled + ", algorithm='" + this.algorithm + '\'' + ", password='***', salt='***', iterationCount=***, key=***" + '}';
    }
}

