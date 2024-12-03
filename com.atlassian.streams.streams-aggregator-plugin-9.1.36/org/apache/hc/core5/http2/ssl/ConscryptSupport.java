/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.conscrypt.Conscrypt
 */
package org.apache.hc.core5.http2.ssl;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http.ssl.TlsCiphers;
import org.apache.hc.core5.http2.ssl.H2TlsSupport;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.conscrypt.Conscrypt;

public final class ConscryptSupport {
    public static SSLSessionInitializer initialize(final Object attachment, final SSLSessionInitializer initializer) {
        return new SSLSessionInitializer(){

            @Override
            public void initialize(NamedEndpoint endpoint, SSLEngine sslEngine) {
                SSLParameters sslParameters = sslEngine.getSSLParameters();
                sslParameters.setProtocols(TLS.excludeWeak(sslParameters.getProtocols()));
                sslParameters.setCipherSuites(TlsCiphers.excludeH2Blacklisted(sslParameters.getCipherSuites()));
                H2TlsSupport.setEnableRetransmissions(sslParameters, false);
                String[] appProtocols = H2TlsSupport.selectApplicationProtocols(attachment);
                if (Conscrypt.isConscrypt((SSLEngine)sslEngine)) {
                    sslEngine.setSSLParameters(sslParameters);
                    Conscrypt.setApplicationProtocols((SSLEngine)sslEngine, (String[])appProtocols);
                } else {
                    H2TlsSupport.setApplicationProtocols(sslParameters, appProtocols);
                    sslEngine.setSSLParameters(sslParameters);
                }
                if (initializer != null) {
                    initializer.initialize(endpoint, sslEngine);
                }
            }
        };
    }

    public static SSLSessionVerifier verify(final SSLSessionVerifier verifier) {
        return new SSLSessionVerifier(){

            @Override
            public TlsDetails verify(NamedEndpoint endpoint, SSLEngine sslEngine) throws SSLException {
                TlsDetails tlsDetails;
                TlsDetails tlsDetails2 = tlsDetails = verifier != null ? verifier.verify(endpoint, sslEngine) : null;
                if (tlsDetails == null && Conscrypt.isConscrypt((SSLEngine)sslEngine)) {
                    tlsDetails = new TlsDetails(sslEngine.getSession(), Conscrypt.getApplicationProtocol((SSLEngine)sslEngine));
                }
                return tlsDetails;
            }
        };
    }
}

