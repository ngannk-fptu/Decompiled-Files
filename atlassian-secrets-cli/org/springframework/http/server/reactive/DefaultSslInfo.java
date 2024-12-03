/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.net.ssl.SSLSession;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

final class DefaultSslInfo
implements SslInfo {
    @Nullable
    private final String sessionId;
    @Nullable
    private final X509Certificate[] peerCertificates;

    DefaultSslInfo(@Nullable String sessionId, X509Certificate[] peerCertificates) {
        Assert.notNull((Object)peerCertificates, "No SSL certificates");
        this.sessionId = sessionId;
        this.peerCertificates = peerCertificates;
    }

    DefaultSslInfo(SSLSession session) {
        Assert.notNull((Object)session, "SSLSession is required");
        this.sessionId = DefaultSslInfo.initSessionId(session);
        this.peerCertificates = DefaultSslInfo.initCertificates(session);
    }

    @Override
    @Nullable
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    @Nullable
    public X509Certificate[] getPeerCertificates() {
        return this.peerCertificates;
    }

    @Nullable
    private static String initSessionId(SSLSession session) {
        byte[] bytes = session.getId();
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String digit = Integer.toHexString(b);
            if (digit.length() < 2) {
                sb.append('0');
            }
            if (digit.length() > 2) {
                digit = digit.substring(digit.length() - 2);
            }
            sb.append(digit);
        }
        return sb.toString();
    }

    @Nullable
    private static X509Certificate[] initCertificates(SSLSession session) {
        Certificate[] certificates;
        try {
            certificates = session.getPeerCertificates();
        }
        catch (Throwable ex) {
            return null;
        }
        ArrayList<X509Certificate> result = new ArrayList<X509Certificate>(certificates.length);
        for (Certificate certificate : certificates) {
            if (!(certificate instanceof X509Certificate)) continue;
            result.add((X509Certificate)certificate);
        }
        return !result.isEmpty() ? result.toArray(new X509Certificate[0]) : null;
    }
}

