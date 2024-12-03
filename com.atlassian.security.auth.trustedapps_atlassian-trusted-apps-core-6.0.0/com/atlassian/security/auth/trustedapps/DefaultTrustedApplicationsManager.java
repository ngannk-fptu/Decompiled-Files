/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.BouncyCastleEncryptionProvider;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.DefaultCurrentApplication;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

public class DefaultTrustedApplicationsManager
implements TrustedApplicationsManager {
    protected final CurrentApplication application;
    protected final Map<String, TrustedApplication> trustedApps;

    public DefaultTrustedApplicationsManager(CurrentApplication application, Map<String, TrustedApplication> trustedApps) {
        Null.not("application", application);
        Null.not("trustedApps", trustedApps);
        this.application = application;
        this.trustedApps = trustedApps;
    }

    public DefaultTrustedApplicationsManager() {
        this(new BouncyCastleEncryptionProvider());
    }

    public DefaultTrustedApplicationsManager(EncryptionProvider encryptionProvider) {
        try {
            KeyPair keyPair = encryptionProvider.generateNewKeyPair();
            Null.not("keyPair", keyPair);
            this.application = new DefaultCurrentApplication(encryptionProvider, keyPair.getPublic(), keyPair.getPrivate(), encryptionProvider.generateUID());
            this.trustedApps = new HashMap<String, TrustedApplication>();
        }
        catch (NoSuchAlgorithmException e) {
            throw new AssertionError((Object)e);
        }
        catch (NoSuchProviderException e) {
            throw new AssertionError((Object)e);
        }
    }

    @Override
    public CurrentApplication getCurrentApplication() {
        return this.application;
    }

    @Override
    public TrustedApplication getTrustedApplication(String id) {
        return this.trustedApps.get(id);
    }
}

