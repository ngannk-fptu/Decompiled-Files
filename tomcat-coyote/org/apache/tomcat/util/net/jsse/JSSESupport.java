/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.jsse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.net.SSLSessionManager;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import org.apache.tomcat.util.res.StringManager;

public class JSSESupport
implements SSLSupport,
SSLSessionManager {
    private static final Log log = LogFactory.getLog(JSSESupport.class);
    private static final StringManager sm = StringManager.getManager(JSSESupport.class);
    private static final Map<String, Integer> keySizeCache = new HashMap<String, Integer>();
    private SSLSession session;
    private Map<String, List<String>> additionalAttributes;

    static void init() {
    }

    @Deprecated
    public JSSESupport(SSLSession session) {
        this(session, null);
    }

    public JSSESupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        this.session = session;
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    public String getCipherSuite() throws IOException {
        if (this.session == null) {
            return null;
        }
        return this.session.getCipherSuite();
    }

    @Override
    public X509Certificate[] getLocalCertificateChain() {
        if (this.session == null) {
            return null;
        }
        return JSSESupport.convertCertificates(this.session.getLocalCertificates());
    }

    @Override
    public X509Certificate[] getPeerCertificateChain() throws IOException {
        if (this.session == null) {
            return null;
        }
        Certificate[] certs = null;
        try {
            certs = this.session.getPeerCertificates();
        }
        catch (Throwable t) {
            log.debug((Object)sm.getString("jsseSupport.clientCertError"), t);
            return null;
        }
        return JSSESupport.convertCertificates(certs);
    }

    private static X509Certificate[] convertCertificates(Certificate[] certs) {
        if (certs == null) {
            return null;
        }
        X509Certificate[] x509Certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; ++i) {
            if (certs[i] instanceof X509Certificate) {
                x509Certs[i] = (X509Certificate)certs[i];
            } else {
                try {
                    byte[] buffer = certs[i].getEncoded();
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
                    x509Certs[i] = (X509Certificate)cf.generateCertificate(stream);
                }
                catch (Exception ex) {
                    log.info((Object)sm.getString("jsseSupport.certTranslationError", new Object[]{certs[i]}), (Throwable)ex);
                    return null;
                }
            }
            if (!log.isTraceEnabled()) continue;
            log.trace((Object)("Cert #" + i + " = " + x509Certs[i]));
        }
        if (x509Certs.length < 1) {
            return null;
        }
        return x509Certs;
    }

    @Override
    public Integer getKeySize() throws IOException {
        if (this.session == null) {
            return null;
        }
        return keySizeCache.get(this.session.getCipherSuite());
    }

    @Override
    public String getSessionId() throws IOException {
        if (this.session == null) {
            return null;
        }
        byte[] ssl_session = this.session.getId();
        if (ssl_session == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : ssl_session) {
            String digit = Integer.toHexString(b);
            if (digit.length() < 2) {
                buf.append('0');
            }
            if (digit.length() > 2) {
                digit = digit.substring(digit.length() - 2);
            }
            buf.append(digit);
        }
        return buf.toString();
    }

    public void setSession(SSLSession session) {
        this.session = session;
    }

    @Override
    public void invalidateSession() {
        this.session.invalidate();
    }

    @Override
    public String getProtocol() throws IOException {
        if (this.session == null) {
            return null;
        }
        return this.session.getProtocol();
    }

    @Override
    public String getRequestedProtocols() throws IOException {
        if (this.additionalAttributes == null) {
            return null;
        }
        return StringUtils.join((Collection)this.additionalAttributes.get("org.apache.tomcat.util.net.secure_requested_protocol_versions"));
    }

    @Override
    public String getRequestedCiphers() throws IOException {
        if (this.additionalAttributes == null) {
            return null;
        }
        return StringUtils.join((Collection)this.additionalAttributes.get("org.apache.tomcat.util.net.secure_requested_ciphers"));
    }

    static {
        for (Cipher cipher : Cipher.values()) {
            for (String jsseName : cipher.getJsseNames()) {
                keySizeCache.put(jsseName, cipher.getStrength_bits());
            }
        }
    }
}

