/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

