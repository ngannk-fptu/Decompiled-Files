/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.http;

import com.amazonaws.http.AbstractFileTlsKeyManagersProvider;
import java.io.File;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SystemPropertyTlsKeyManagersProvider
extends AbstractFileTlsKeyManagersProvider {
    private static final Log log = LogFactory.getLog(SystemPropertyTlsKeyManagersProvider.class);
    private static final String KEY_STORE_PROPERTY = "javax.net.ssl.keyStore";
    private static final String KEY_STORE_PASSWORD_PROPERTY = "javax.net.ssl.keyStorePassword";
    private static final String KEY_STORE_TYPE_PROPERTY = "javax.net.ssl.keyStoreType";

    @Override
    public KeyManager[] getKeyManagers() {
        String keyStorePath = SystemPropertyTlsKeyManagersProvider.getKeyStore();
        if (keyStorePath == null) {
            return null;
        }
        String type = SystemPropertyTlsKeyManagersProvider.getKeyStoreType();
        String password = SystemPropertyTlsKeyManagersProvider.getKeyStorePassword();
        char[] passwordChars = null;
        if (password != null) {
            passwordChars = password.toCharArray();
        }
        try {
            return this.createKeyManagers(new File(keyStorePath), type, passwordChars);
        }
        catch (Exception e) {
            log.warn((Object)"Unable to load KeyManager from system properties", (Throwable)e);
            return null;
        }
    }

    private static String getKeyStore() {
        return System.getProperty(KEY_STORE_PROPERTY);
    }

    private static String getKeyStoreType() {
        return System.getProperty(KEY_STORE_TYPE_PROPERTY, KeyStore.getDefaultType());
    }

    private static String getKeyStorePassword() {
        return System.getProperty(KEY_STORE_PASSWORD_PROPERTY);
    }
}

