/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.auth;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

final class SingleCredentialsProvider
implements CredentialsProvider {
    private final AuthScope authScope;
    private final Credentials credentials;

    public SingleCredentialsProvider(AuthScope authScope, Credentials credentials) {
        this.authScope = (AuthScope)Args.notNull((Object)authScope, (String)"Auth scope");
        this.credentials = credentials;
    }

    public SingleCredentialsProvider(AuthScope authScope, String username, char[] password) {
        this(authScope, new UsernamePasswordCredentials(username, password));
    }

    @Override
    public Credentials getCredentials(AuthScope authScope, HttpContext context) {
        return this.authScope.match(authScope) >= 0 ? this.credentials : null;
    }

    public String toString() {
        return this.authScope.toString();
    }
}

