/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.auth;

import java.util.Map;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;

final class CredentialsMatcher {
    CredentialsMatcher() {
    }

    static Credentials matchCredentials(Map<AuthScope, Credentials> map, AuthScope authScope) {
        Credentials creds = map.get(authScope);
        if (creds == null) {
            int bestMatchFactor = -1;
            AuthScope bestMatch = null;
            for (AuthScope current : map.keySet()) {
                int factor = authScope.match(current);
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
}

