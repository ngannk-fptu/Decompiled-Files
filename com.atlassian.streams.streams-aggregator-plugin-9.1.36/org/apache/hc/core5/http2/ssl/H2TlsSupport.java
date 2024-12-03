/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.ssl;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http.ssl.TlsCiphers;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.util.ReflectionUtils;

public final class H2TlsSupport {
    public static void setEnableRetransmissions(SSLParameters sslParameters, boolean value) {
        ReflectionUtils.callSetter(sslParameters, "EnableRetransmissions", Boolean.TYPE, value);
    }

    public static void setApplicationProtocols(SSLParameters sslParameters, String[] values) {
        ReflectionUtils.callSetter(sslParameters, "ApplicationProtocols", String[].class, values);
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

    public static SSLSessionInitializer enforceRequirements(final Object attachment, final SSLSessionInitializer initializer) {
        return new SSLSessionInitializer(){

            @Override
            public void initialize(NamedEndpoint endpoint, SSLEngine sslEngine) {
                SSLParameters sslParameters = sslEngine.getSSLParameters();
                sslParameters.setProtocols(TLS.excludeWeak(sslParameters.getProtocols()));
                sslParameters.setCipherSuites(TlsCiphers.excludeH2Blacklisted(sslParameters.getCipherSuites()));
                H2TlsSupport.setEnableRetransmissions(sslParameters, false);
                H2TlsSupport.setApplicationProtocols(sslParameters, H2TlsSupport.selectApplicationProtocols(attachment));
                sslEngine.setSSLParameters(sslParameters);
                if (initializer != null) {
                    initializer.initialize(endpoint, sslEngine);
                }
            }
        };
    }
}

