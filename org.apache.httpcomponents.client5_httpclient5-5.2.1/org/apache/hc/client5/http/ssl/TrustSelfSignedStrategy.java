/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.ssl.TrustStrategy
 */
package org.apache.hc.client5.http.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.ssl.TrustStrategy;

@Contract(threading=ThreadingBehavior.STATELESS)
public class TrustSelfSignedStrategy
implements TrustStrategy {
    public static final TrustSelfSignedStrategy INSTANCE = new TrustSelfSignedStrategy();

    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        return chain.length == 1;
    }
}

