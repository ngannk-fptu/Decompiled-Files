/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net.openssl;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.jsse.JSSESupport;
import org.apache.tomcat.util.net.openssl.OpenSSLUtil;

public class OpenSSLImplementation
extends SSLImplementation {
    @Override
    @Deprecated
    public SSLSupport getSSLSupport(SSLSession session) {
        return new JSSESupport(session);
    }

    @Override
    public SSLSupport getSSLSupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }

    @Override
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return new OpenSSLUtil(certificate);
    }

    @Override
    public boolean isAlpnSupported() {
        return true;
    }
}

