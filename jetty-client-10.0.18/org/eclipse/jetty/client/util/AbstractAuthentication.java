/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.util;

import java.net.URI;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Authentication;

public abstract class AbstractAuthentication
implements Authentication {
    private final URI uri;
    private final String realm;

    public AbstractAuthentication(URI uri, String realm) {
        this.uri = uri;
        this.realm = realm;
    }

    public abstract String getType();

    public URI getURI() {
        return this.uri;
    }

    public String getRealm() {
        return this.realm;
    }

    @Override
    public boolean matches(String type, URI uri, String realm) {
        if (!this.getType().equalsIgnoreCase(type)) {
            return false;
        }
        if (!this.realm.equals("<<ANY_REALM>>") && !this.realm.equals(realm)) {
            return false;
        }
        return AbstractAuthentication.matchesURI(this.uri, uri);
    }

    public static boolean matchesURI(URI uri1, URI uri2) {
        int thatPort;
        int thisPort;
        String scheme = uri1.getScheme();
        if (scheme.equalsIgnoreCase(uri2.getScheme()) && uri1.getHost().equalsIgnoreCase(uri2.getHost()) && (thisPort = HttpClient.normalizePort(scheme, uri1.getPort())) == (thatPort = HttpClient.normalizePort(scheme, uri2.getPort()))) {
            return uri2.getPath().startsWith(uri1.getPath());
        }
        return false;
    }
}

