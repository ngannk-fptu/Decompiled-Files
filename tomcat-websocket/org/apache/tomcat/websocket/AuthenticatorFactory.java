/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.websocket;

import java.util.ServiceLoader;
import org.apache.tomcat.websocket.Authenticator;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.apache.tomcat.websocket.DigestAuthenticator;

public class AuthenticatorFactory {
    public static Authenticator getAuthenticator(String authScheme) {
        Authenticator auth = null;
        switch (authScheme.toLowerCase()) {
            case "basic": {
                auth = new BasicAuthenticator();
                break;
            }
            case "digest": {
                auth = new DigestAuthenticator();
                break;
            }
            default: {
                auth = AuthenticatorFactory.loadAuthenticators(authScheme);
            }
        }
        return auth;
    }

    private static Authenticator loadAuthenticators(String authScheme) {
        ServiceLoader<Authenticator> serviceLoader = ServiceLoader.load(Authenticator.class);
        for (Authenticator auth : serviceLoader) {
            if (!auth.getSchemeName().equalsIgnoreCase(authScheme)) continue;
            return auth;
        }
        return null;
    }
}

