/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ssl.TLS
 *  org.apache.hc.core5.http.ssl.TlsCiphers
 *  org.apache.hc.core5.reactor.ssl.SSLSessionInitializer
 *  org.apache.hc.core5.reactor.ssl.SSLSessionVerifier
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.conscrypt.Conscrypt
 */
package org.apache.hc.core5.http2.ssl;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http.ssl.TlsCiphers;
import org.apache.hc.core5.http2.ssl.H2TlsSupport;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.conscrypt.Conscrypt;

public final class ConscryptSupport {
    public static SSLSessionInitializer initialize(Object attachment, SSLSessionInitializer initializer) {
        return (endpoint, sslEngine) -> {
            SSLParameters sslParameters = sslEngine.getSSLParameters();
            sslParameters.setProtocols(TLS.excludeWeak((String[])sslParameters.getProtocols()));
            sslParameters.setCipherSuites(TlsCiphers.excludeH2Blacklisted((String[])sslParameters.getCipherSuites()));
            H2TlsSupport.setEnableRetransmissions(sslParameters, false);
            String[] appProtocols = H2TlsSupport.selectApplicationProtocols(attachment);
            if (Conscrypt.isConscrypt((SSLEngine)sslEngine)) {
                sslEngine.setSSLParameters(sslParameters);
                Conscrypt.setApplicationProtocols((SSLEngine)sslEngine, (String[])appProtocols);
            } else {
                sslParameters.setApplicationProtocols(appProtocols);
                sslEngine.setSSLParameters(sslParameters);
            }
            if (initializer != null) {
                initializer.initialize(endpoint, sslEngine);
            }
        };
    }

    public static SSLSessionVerifier verify(SSLSessionVerifier verifier) {
        return (endpoint, sslEngine) -> {
            TlsDetails tlsDetails;
            TlsDetails tlsDetails2 = tlsDetails = verifier != null ? verifier.verify(endpoint, sslEngine) : null;
            if (tlsDetails == null && Conscrypt.isConscrypt((SSLEngine)sslEngine)) {
                tlsDetails = new TlsDetails(sslEngine.getSession(), Conscrypt.getApplicationProtocol((SSLEngine)sslEngine));
            }
            return tlsDetails;
        };
    }
}

