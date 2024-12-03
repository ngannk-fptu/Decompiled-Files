/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io.ssl;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.apache.hc.core5.http.HttpHost;

public interface SSLSessionVerifier {
    public void verify(HttpHost var1, SSLSession var2) throws SSLException;
}

