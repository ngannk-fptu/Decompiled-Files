/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.TDSChannel;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;

final class PermissiveX509TrustManager
implements X509TrustManager {
    private final Logger logger;
    private final String logContext;

    PermissiveX509TrustManager(TDSChannel tdsChannel) {
        this.logger = tdsChannel.getLogger();
        this.logContext = tdsChannel.toString() + " (PermissiveX509TrustManager):";
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.logger.isLoggable(Level.FINER)) {
            this.logger.finer(this.logContext + " Trusting client certificate (!)");
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.logger.isLoggable(Level.FINER)) {
            this.logger.finer(this.logContext + " Trusting server certificate");
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

