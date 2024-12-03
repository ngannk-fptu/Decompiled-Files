/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.ssl.SSLInitializationException
 */
package com.amazonaws.internal;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLInitializationException;

public class SdkSSLContext {
    public static final SSLContext getPreferredSSLContext(SecureRandom secureRandom) {
        return SdkSSLContext.getPreferredSSLContext(null, secureRandom);
    }

    public static final SSLContext getPreferredSSLContext(KeyManager[] keyManagers, SecureRandom secureRandom) {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(keyManagers, null, secureRandom);
            return sslcontext;
        }
        catch (NoSuchAlgorithmException ex) {
            throw new SSLInitializationException(ex.getMessage(), (Throwable)ex);
        }
        catch (KeyManagementException ex) {
            throw new SSLInitializationException(ex.getMessage(), (Throwable)ex);
        }
    }
}

