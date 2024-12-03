/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.realm;

import java.security.cert.X509Certificate;

public interface X509UsernameRetriever {
    public String getUsername(X509Certificate var1);
}

