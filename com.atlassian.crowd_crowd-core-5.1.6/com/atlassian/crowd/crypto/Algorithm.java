/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.crypto;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Algorithm {
    AES("AES_CBC_PKCS5Padding", true),
    DES("DES_CBC_PKCS5Padding", false),
    DESEDE("DESede_CBC_PKCS5Padding", false);

    private final String key;
    private final boolean isSecure;

    private Algorithm(String key, boolean isSecure) {
        this.key = key;
        this.isSecure = isSecure;
    }

    public static Algorithm getByKey(String key) {
        return Arrays.stream(Algorithm.values()).filter(encryptor -> encryptor.key.equals(key)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown encryptor " + key));
    }

    public static Set<String> getSecureKeySet() {
        return (Set)Arrays.stream(Algorithm.values()).filter(Algorithm::isSecure).map(Algorithm::getKey).collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    public String getKey() {
        return this.key;
    }

    public boolean isSecure() {
        return this.isSecure;
    }

    public String toString() {
        return this.key;
    }
}

