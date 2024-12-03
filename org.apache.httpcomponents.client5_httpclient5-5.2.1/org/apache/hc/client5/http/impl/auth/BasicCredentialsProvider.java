/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.auth;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.impl.auth.CredentialsMatcher;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class BasicCredentialsProvider
implements CredentialsStore {
    private final ConcurrentHashMap<AuthScope, Credentials> credMap = new ConcurrentHashMap();

    @Override
    public void setCredentials(AuthScope authScope, Credentials credentials) {
        Args.notNull((Object)authScope, (String)"Authentication scope");
        this.credMap.put(authScope, credentials);
    }

    @Override
    public Credentials getCredentials(AuthScope authScope, HttpContext context) {
        return CredentialsMatcher.matchCredentials(this.credMap, authScope);
    }

    @Override
    public void clear() {
        this.credMap.clear();
    }

    public String toString() {
        return ((ConcurrentHashMap.CollectionView)((Object)this.credMap.keySet())).toString();
    }
}

