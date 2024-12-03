/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import org.apache.tomcat.util.net.SSLContext;

public interface SSLUtil {
    public SSLContext createSSLContext(List<String> var1) throws Exception;

    public KeyManager[] getKeyManagers() throws Exception;

    public TrustManager[] getTrustManagers() throws Exception;

    public void configureSessionContext(SSLSessionContext var1);

    public String[] getEnabledProtocols() throws IllegalArgumentException;

    public String[] getEnabledCiphers() throws IllegalArgumentException;

    public static interface ProtocolInfo {
        public String getNegotiatedProtocol();
    }
}

