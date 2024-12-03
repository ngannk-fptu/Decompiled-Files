/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.UDecoder
 */
package org.apache.catalina.valves;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.servlet.ServletException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UDecoder;

public class SSLValve
extends ValveBase {
    private static final Log log = LogFactory.getLog(SSLValve.class);
    private String sslClientCertHeader = "ssl_client_cert";
    private String sslClientEscapedCertHeader = "ssl_client_escaped_cert";
    private String sslCipherHeader = "ssl_cipher";
    private String sslSessionIdHeader = "ssl_session_id";
    private String sslCipherUserKeySizeHeader = "ssl_cipher_usekeysize";

    public SSLValve() {
        super(true);
    }

    public String getSslClientCertHeader() {
        return this.sslClientCertHeader;
    }

    public void setSslClientCertHeader(String sslClientCertHeader) {
        this.sslClientCertHeader = sslClientCertHeader;
    }

    public String getSslClientEscapedCertHeader() {
        return this.sslClientEscapedCertHeader;
    }

    public void setSslClientEscapedCertHeader(String sslClientEscapedCertHeader) {
        this.sslClientEscapedCertHeader = sslClientEscapedCertHeader;
    }

    public String getSslCipherHeader() {
        return this.sslCipherHeader;
    }

    public void setSslCipherHeader(String sslCipherHeader) {
        this.sslCipherHeader = sslCipherHeader;
    }

    public String getSslSessionIdHeader() {
        return this.sslSessionIdHeader;
    }

    public void setSslSessionIdHeader(String sslSessionIdHeader) {
        this.sslSessionIdHeader = sslSessionIdHeader;
    }

    public String getSslCipherUserKeySizeHeader() {
        return this.sslCipherUserKeySizeHeader;
    }

    public void setSslCipherUserKeySizeHeader(String sslCipherUserKeySizeHeader) {
        this.sslCipherUserKeySizeHeader = sslCipherUserKeySizeHeader;
    }

    public String mygetHeader(Request request, String header) {
        String strcert0 = request.getHeader(header);
        if (strcert0 == null) {
            return null;
        }
        if ("(null)".equals(strcert0)) {
            return null;
        }
        return strcert0;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String headerEscapedValue = this.mygetHeader(request, this.sslClientEscapedCertHeader);
        String headerValue = headerEscapedValue != null ? UDecoder.URLDecode((String)headerEscapedValue, null) : this.mygetHeader(request, this.sslClientCertHeader);
        if (headerValue != null && (headerValue = headerValue.trim()).length() > 27) {
            String body = headerValue.substring(27);
            String header = "-----BEGIN CERTIFICATE-----\n";
            String strcerts = header.concat(body);
            ByteArrayInputStream bais = new ByteArrayInputStream(strcerts.getBytes(StandardCharsets.ISO_8859_1));
            X509Certificate[] jsseCerts = null;
            String providerName = (String)request.getConnector().getProperty("clientCertProvider");
            try {
                CertificateFactory cf = providerName == null ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance("X.509", providerName);
                X509Certificate cert = (X509Certificate)cf.generateCertificate(bais);
                jsseCerts = new X509Certificate[]{cert};
            }
            catch (CertificateException e) {
                log.warn((Object)sm.getString("sslValve.certError", new Object[]{strcerts}), (Throwable)e);
            }
            catch (NoSuchProviderException e) {
                log.error((Object)sm.getString("sslValve.invalidProvider", new Object[]{providerName}), (Throwable)e);
            }
            request.setAttribute("javax.servlet.request.X509Certificate", jsseCerts);
        }
        if ((headerValue = this.mygetHeader(request, this.sslCipherHeader)) != null) {
            request.setAttribute("javax.servlet.request.cipher_suite", headerValue);
        }
        if ((headerValue = this.mygetHeader(request, this.sslSessionIdHeader)) != null) {
            request.setAttribute("javax.servlet.request.ssl_session_id", headerValue);
        }
        if ((headerValue = this.mygetHeader(request, this.sslCipherUserKeySizeHeader)) != null) {
            request.setAttribute("javax.servlet.request.key_size", Integer.valueOf(headerValue));
        }
        this.getNext().invoke(request, response);
    }
}

