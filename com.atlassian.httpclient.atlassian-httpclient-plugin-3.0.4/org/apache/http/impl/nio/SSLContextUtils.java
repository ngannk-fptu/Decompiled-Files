/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

class SSLContextUtils {
    SSLContextUtils() {
    }

    static SSLContext getDefault() {
        SSLContext sslContext;
        try {
            try {
                sslContext = SSLContext.getInstance("Default");
            }
            catch (NoSuchAlgorithmException ex) {
                sslContext = SSLContext.getInstance("TLS");
            }
            sslContext.init(null, null, null);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failure initializing default SSL context", ex);
        }
        return sslContext;
    }
}

