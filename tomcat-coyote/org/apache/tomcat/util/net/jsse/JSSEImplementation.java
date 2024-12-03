/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.tomcat.util.net.jsse;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLImplementation;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.jsse.JSSESupport;
import org.apache.tomcat.util.net.jsse.JSSEUtil;

public class JSSEImplementation
extends SSLImplementation {
    public JSSEImplementation() {
        JSSESupport.init();
    }

    @Override
    @Deprecated
    public SSLSupport getSSLSupport(SSLSession session) {
        return this.getSSLSupport(session, null);
    }

    @Override
    public SSLSupport getSSLSupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }

    @Override
    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return new JSSEUtil(certificate);
    }

    @Override
    public boolean isAlpnSupported() {
        return JreCompat.isAlpnSupported();
    }
}

