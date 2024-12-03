/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface AsymmetricKeyFactory {
    public KeyPair getNewKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException;
}

