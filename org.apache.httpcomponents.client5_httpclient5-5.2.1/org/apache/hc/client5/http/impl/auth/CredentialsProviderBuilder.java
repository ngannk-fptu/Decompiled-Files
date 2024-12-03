/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.auth;

import java.util.HashMap;
import java.util.Map;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.FixedCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.SingleCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Args;

public final class CredentialsProviderBuilder {
    private final Map<AuthScope, Credentials> credMap = new HashMap<AuthScope, Credentials>();

    public static CredentialsProviderBuilder create() {
        return new CredentialsProviderBuilder();
    }

    public CredentialsProviderBuilder add(AuthScope authScope, Credentials credentials) {
        Args.notNull((Object)authScope, (String)"Host");
        this.credMap.put(authScope, credentials);
        return this;
    }

    public CredentialsProviderBuilder add(AuthScope authScope, String username, char[] password) {
        Args.notNull((Object)authScope, (String)"Host");
        this.credMap.put(authScope, new UsernamePasswordCredentials(username, password));
        return this;
    }

    public CredentialsProviderBuilder add(HttpHost httpHost, Credentials credentials) {
        Args.notNull((Object)httpHost, (String)"Host");
        this.credMap.put(new AuthScope(httpHost), credentials);
        return this;
    }

    public CredentialsProviderBuilder add(HttpHost httpHost, String username, char[] password) {
        Args.notNull((Object)httpHost, (String)"Host");
        this.credMap.put(new AuthScope(httpHost), new UsernamePasswordCredentials(username, password));
        return this;
    }

    public CredentialsProvider build() {
        if (this.credMap.size() == 0) {
            return new BasicCredentialsProvider();
        }
        if (this.credMap.size() == 1) {
            Map.Entry<AuthScope, Credentials> entry = this.credMap.entrySet().iterator().next();
            return new SingleCredentialsProvider(entry.getKey(), entry.getValue());
        }
        return new FixedCredentialsProvider(this.credMap);
    }

    static class Entry {
        final AuthScope authScope;
        final Credentials credentials;

        Entry(AuthScope authScope, Credentials credentials) {
            this.authScope = authScope;
            this.credentials = credentials;
        }
    }
}

