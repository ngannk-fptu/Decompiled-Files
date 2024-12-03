/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.security.cert.X509Certificate;

public interface SSLSupport {
    public static final String CIPHER_SUITE_KEY = "javax.servlet.request.cipher_suite";
    public static final String KEY_SIZE_KEY = "javax.servlet.request.key_size";
    public static final String CERTIFICATE_KEY = "javax.servlet.request.X509Certificate";
    public static final String SESSION_ID_KEY = "javax.servlet.request.ssl_session_id";
    public static final String SESSION_MGR = "javax.servlet.request.ssl_session_mgr";
    public static final String PROTOCOL_VERSION_KEY = "org.apache.tomcat.util.net.secure_protocol_version";
    public static final String REQUESTED_CIPHERS_KEY = "org.apache.tomcat.util.net.secure_requested_ciphers";
    public static final String REQUESTED_PROTOCOL_VERSIONS_KEY = "org.apache.tomcat.util.net.secure_requested_protocol_versions";

    public String getCipherSuite() throws IOException;

    public X509Certificate[] getPeerCertificateChain() throws IOException;

    default public X509Certificate[] getLocalCertificateChain() {
        return null;
    }

    public Integer getKeySize() throws IOException;

    public String getSessionId() throws IOException;

    public String getProtocol() throws IOException;

    public String getRequestedProtocols() throws IOException;

    public String getRequestedCiphers() throws IOException;
}

