/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.util.RSAKeys
 */
package com.atlassian.oauth.consumer.sal;

import com.atlassian.oauth.consumer.sal.KeyPairFactory;
import com.atlassian.oauth.util.RSAKeys;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

public class KeyPairFactoryImpl
implements KeyPairFactory {
    @Override
    public KeyPair newKeyPair() throws GeneralSecurityException {
        return RSAKeys.generateKeyPair();
    }
}

