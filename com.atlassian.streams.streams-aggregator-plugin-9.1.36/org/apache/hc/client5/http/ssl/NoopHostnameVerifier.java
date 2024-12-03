/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.STATELESS)
public class NoopHostnameVerifier
implements HostnameVerifier {
    public static final NoopHostnameVerifier INSTANCE = new NoopHostnameVerifier();

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }

    public final String toString() {
        return "NO_OP";
    }
}

