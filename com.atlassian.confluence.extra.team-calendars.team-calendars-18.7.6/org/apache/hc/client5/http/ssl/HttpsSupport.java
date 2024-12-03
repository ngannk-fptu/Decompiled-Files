/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.ssl;

import java.security.AccessController;
import javax.net.ssl.HostnameVerifier;
import org.apache.hc.client5.http.psl.PublicSuffixMatcherLoader;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.core5.util.TextUtils;

public final class HttpsSupport {
    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    private static String getProperty(String key) {
        return AccessController.doPrivileged(() -> System.getProperty(key));
    }

    public static String[] getSystemProtocols() {
        return HttpsSupport.split(HttpsSupport.getProperty("https.protocols"));
    }

    public static String[] getSystemCipherSuits() {
        return HttpsSupport.split(HttpsSupport.getProperty("https.cipherSuites"));
    }

    public static HostnameVerifier getDefaultHostnameVerifier() {
        return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
    }
}

