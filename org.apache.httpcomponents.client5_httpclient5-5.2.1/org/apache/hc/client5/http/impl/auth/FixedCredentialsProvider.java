/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.impl.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.impl.auth.CredentialsMatcher;
import org.apache.hc.core5.http.protocol.HttpContext;

final class FixedCredentialsProvider
implements CredentialsProvider {
    private final Map<AuthScope, Credentials> credMap;

    public FixedCredentialsProvider(Map<AuthScope, Credentials> credMap) {
        this.credMap = Collections.unmodifiableMap(new HashMap<AuthScope, Credentials>(credMap));
    }

    @Override
    public Credentials getCredentials(AuthScope authScope, HttpContext context) {
        return CredentialsMatcher.matchCredentials(this.credMap, authScope);
    }

    public String toString() {
        return this.credMap.keySet().toString();
    }
}

