/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class BasicCredentialsProvider
implements CredentialsProvider {
    private final ConcurrentHashMap<AuthScope, Credentials> credMap = new ConcurrentHashMap();

    @Override
    public void setCredentials(AuthScope authscope, Credentials credentials) {
        Args.notNull((Object)authscope, (String)"Authentication scope");
        this.credMap.put(authscope, credentials);
    }

    private static Credentials matchCredentials(Map<AuthScope, Credentials> map, AuthScope authscope) {
        Credentials creds = map.get(authscope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (AuthScope current : map.keySet()) {
                int factor = authscope.match(current);
                if (factor <= bestMatchFactor) continue;
                bestMatchFactor = factor;
                bestMatch = current;
            }
            if (bestMatch != null) {
                creds = map.get(bestMatch);
            }
        }
        return creds;
    }

    @Override
    public Credentials getCredentials(AuthScope authscope) {
        Args.notNull((Object)authscope, (String)"Authentication scope");
        return BasicCredentialsProvider.matchCredentials(this.credMap, authscope);
    }

    @Override
    public void clear() {
        this.credMap.clear();
    }

    public String toString() {
        return this.credMap.toString();
    }
}

