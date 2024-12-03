/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerCertificateUtils;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.TDSChannel;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.X509TrustManager;

final class ServerCertificateX509TrustManager
implements X509TrustManager {
    private final Logger logger;
    private final String logContext;
    private String hostName;
    private String serverCert;

    ServerCertificateX509TrustManager(TDSChannel tdsChannel, String cert, String hostName) {
        this.logger = tdsChannel.getLogger();
        this.logContext = tdsChannel.toString() + " (ServerCertificateX509TrustManager):";
        this.hostName = hostName.toLowerCase(Locale.ENGLISH);
        this.serverCert = cert;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest(this.logContext + " Trusting client certificate (!)");
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.finest(this.logContext + " Check if server trusted.");
        }
        if (null == chain || 0 == chain.length || null == authType || authType.isEmpty()) {
            throw new IllegalArgumentException(SQLServerException.getErrString("R_illegalArgumentTrustManager"));
        }
        X509Certificate cert = null;
        try {
            X509Certificate[] x509CertificateArray = chain;
            int n = x509CertificateArray.length;
            for (int i = 0; i < n; ++i) {
                X509Certificate c;
                cert = c = x509CertificateArray[i];
                c.checkValidity();
            }
            if (null == this.serverCert) {
                SQLServerCertificateUtils.validateServerNameInCertificate(chain[0], this.hostName);
            } else {
                SQLServerCertificateUtils.validateServerCerticate(chain[0], this.serverCert);
            }
        }
        catch (CertificateNotYetValidException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverCertNotYetValid"));
            Object[] msgArgs = new Object[]{this.serverCert != null ? this.serverCert : this.hostName, e.getMessage()};
            throw new CertificateException(form.format(msgArgs));
        }
        catch (CertificateExpiredException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverCertExpired"));
            Object[] msgArgs = new Object[]{this.serverCert != null ? this.serverCert : this.hostName, e.getMessage()};
            throw new CertificateException(form.format(msgArgs));
        }
        catch (Exception e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_serverCertError"));
            Object[] msgArgs = new Object[]{e.getMessage(), this.serverCert != null ? this.serverCert : this.hostName, cert != null ? cert.toString() : ""};
            throw new CertificateException(form.format(msgArgs));
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

