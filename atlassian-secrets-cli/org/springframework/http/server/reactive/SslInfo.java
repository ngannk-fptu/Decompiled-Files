/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.security.cert.X509Certificate;
import org.springframework.lang.Nullable;

public interface SslInfo {
    @Nullable
    public String getSessionId();

    @Nullable
    public X509Certificate[] getPeerCertificates();
}

