/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.apache.tomcat.util.net.AprEndpoint;
import org.apache.tomcat.util.net.SSLSupport;

@Deprecated
public class AprSSLSupport
implements SSLSupport {
    private final AprEndpoint.AprSocketWrapper socketWrapper;
    private final String clientCertProvider;

    public AprSSLSupport(AprEndpoint.AprSocketWrapper socketWrapper, String clientCertProvider) {
        this.socketWrapper = socketWrapper;
        this.clientCertProvider = clientCertProvider;
    }

    @Override
    public String getCipherSuite() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(2);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public X509Certificate[] getPeerCertificateChain() throws IOException {
        try {
            int certLength = this.socketWrapper.getSSLInfoI(1024);
            byte[] clientCert = this.socketWrapper.getSSLInfoB(263);
            X509Certificate[] certs = null;
            if (clientCert != null) {
                if (certLength < 0) {
                    certLength = 0;
                }
                certs = new X509Certificate[certLength + 1];
                CertificateFactory cf = this.clientCertProvider == null ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance("X.509", this.clientCertProvider);
                certs[0] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(clientCert));
                for (int i = 0; i < certLength; ++i) {
                    byte[] data = this.socketWrapper.getSSLInfoB(1024 + i);
                    certs[i + 1] = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(data));
                }
            }
            return certs;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Integer getKeySize() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoI(3);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getSessionId() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(1);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getProtocol() throws IOException {
        try {
            return this.socketWrapper.getSSLInfoS(7);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getRequestedProtocols() throws IOException {
        return null;
    }

    @Override
    public String getRequestedCiphers() throws IOException {
        return null;
    }
}

