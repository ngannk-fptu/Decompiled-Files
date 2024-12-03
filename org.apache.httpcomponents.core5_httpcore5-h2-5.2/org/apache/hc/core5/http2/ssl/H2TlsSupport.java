/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ssl.TLS
 *  org.apache.hc.core5.http.ssl.TlsCiphers
 *  org.apache.hc.core5.reactor.ssl.SSLSessionInitializer
 *  org.apache.hc.core5.util.ReflectionUtils
 */
package org.apache.hc.core5.http2.ssl;

import javax.net.ssl.SSLParameters;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http.ssl.TlsCiphers;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.util.ReflectionUtils;

public final class H2TlsSupport {
    public static void setEnableRetransmissions(SSLParameters sslParameters, boolean value) {
        ReflectionUtils.callSetter((Object)sslParameters, (String)"EnableRetransmissions", Boolean.TYPE, (Object)value);
    }

    @Deprecated
    public static void setApplicationProtocols(SSLParameters sslParameters, String[] values) {
        ReflectionUtils.callSetter((Object)sslParameters, (String)"ApplicationProtocols", String[].class, (Object)values);
    }

    public static String[] selectApplicationProtocols(Object attachment) {
        HttpVersionPolicy versionPolicy = attachment instanceof HttpVersionPolicy ? (HttpVersionPolicy)((Object)attachment) : HttpVersionPolicy.NEGOTIATE;
        switch (versionPolicy) {
            case FORCE_HTTP_1: {
                return new String[]{ApplicationProtocol.HTTP_1_1.id};
            }
            case FORCE_HTTP_2: {
                return new String[]{ApplicationProtocol.HTTP_2.id};
            }
        }
        return new String[]{ApplicationProtocol.HTTP_2.id, ApplicationProtocol.HTTP_1_1.id};
    }

    public static SSLSessionInitializer enforceRequirements(Object attachment, SSLSessionInitializer initializer) {
        return (endpoint, sslEngine) -> {
            SSLParameters sslParameters = sslEngine.getSSLParameters();
            sslParameters.setProtocols(TLS.excludeWeak((String[])sslParameters.getProtocols()));
            sslParameters.setCipherSuites(TlsCiphers.excludeH2Blacklisted((String[])sslParameters.getCipherSuites()));
            H2TlsSupport.setEnableRetransmissions(sslParameters, false);
            sslParameters.setApplicationProtocols(H2TlsSupport.selectApplicationProtocols(attachment));
            sslEngine.setSSLParameters(sslParameters);
            if (initializer != null) {
                initializer.initialize(endpoint, sslEngine);
            }
        };
    }
}

