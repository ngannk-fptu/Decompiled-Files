/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.serviceprovider.internal;

public interface NonceChecker {
    public boolean isNonceUnique(String var1, String var2);

    public void addNonce(String var1, String var2);

    public static String generateCacheKeyFrom(String consumerKey, String nonce) {
        return consumerKey + ":" + nonce;
    }
}

