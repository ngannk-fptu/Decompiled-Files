/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerCertificateUtils;
import com.microsoft.sqlserver.jdbc.TDSChannel;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;

final class HostNameOverrideX509TrustManager
implements X509TrustManager {
    private final Logger logger;
    private final String logContext;
    private final X509TrustManager defaultTrustManager;
    private String hostName;

    HostNameOverrideX509TrustManager(TDSChannel tdsChannel, X509TrustManager tm, String hostName) {
        this.logger = tdsChannel.getLogger();
        this.logContext = tdsChannel.toString() + " (HostNameOverrideX509TrustManager):";
        this.defaultTrustManager = tm;
        this.hostName = hostName.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest(this.logContext + " Forwarding ClientTrusted.");
        }
        this.defaultTrustManager.checkClientTrusted(chain, authType);
        for (X509Certificate cert : chain) {
            cert.checkValidity();
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest(this.logContext + " Forwarding Trusting server certificate");
        }
        this.defaultTrustManager.checkServerTrusted(chain, authType);
        for (X509Certificate cert : chain) {
            cert.checkValidity();
        }
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest(this.logContext + " Default serverTrusted succeeded proceeding with server name validation");
        }
        SQLServerCertificateUtils.validateServerNameInCertificate(chain[0], this.hostName);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.defaultTrustManager.getAcceptedIssuers();
    }
}

