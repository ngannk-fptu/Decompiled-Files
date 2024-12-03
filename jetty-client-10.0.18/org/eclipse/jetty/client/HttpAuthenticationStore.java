/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.util.AbstractAuthentication;

public class HttpAuthenticationStore
implements AuthenticationStore {
    private final List<Authentication> authentications = new CopyOnWriteArrayList<Authentication>();
    private final Map<URI, Authentication.Result> results = new ConcurrentHashMap<URI, Authentication.Result>();

    @Override
    public void addAuthentication(Authentication authentication) {
        this.authentications.add(authentication);
    }

    @Override
    public void removeAuthentication(Authentication authentication) {
        this.authentications.remove(authentication);
    }

    @Override
    public void clearAuthentications() {
        this.authentications.clear();
    }

    @Override
    public Authentication findAuthentication(String type, URI uri, String realm) {
        for (Authentication authentication : this.authentications) {
            if (!authentication.matches(type, uri, realm)) continue;
            return authentication;
        }
        return null;
    }

    @Override
    public void addAuthenticationResult(Authentication.Result result) {
        URI uri = result.getURI();
        if (uri != null) {
            this.results.put(uri, result);
        }
    }

    @Override
    public void removeAuthenticationResult(Authentication.Result result) {
        this.results.remove(result.getURI());
    }

    @Override
    public void clearAuthenticationResults() {
        this.results.clear();
    }

    @Override
    public Authentication.Result findAuthenticationResult(URI uri) {
        for (Map.Entry<URI, Authentication.Result> entry : this.results.entrySet()) {
            if (!AbstractAuthentication.matchesURI(entry.getKey(), uri)) continue;
            return entry.getValue();
        }
        return null;
    }

    @Override
    public boolean hasAuthenticationResults() {
        return !this.results.isEmpty();
    }
}

