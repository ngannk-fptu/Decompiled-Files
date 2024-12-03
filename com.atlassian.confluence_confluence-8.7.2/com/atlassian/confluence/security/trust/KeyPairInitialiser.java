/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.trust;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface KeyPairInitialiser {
    public void initConfluenceKey() throws NoSuchProviderException, NoSuchAlgorithmException;
}

