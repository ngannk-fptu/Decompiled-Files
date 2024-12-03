/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.consumer.sal;

import java.security.GeneralSecurityException;
import java.security.KeyPair;

public interface KeyPairFactory {
    public KeyPair newKeyPair() throws GeneralSecurityException;
}

