/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.api;

import java.net.URI;
import org.eclipse.jetty.client.api.Authentication;

public interface AuthenticationStore {
    public void addAuthentication(Authentication var1);

    public void removeAuthentication(Authentication var1);

    public void clearAuthentications();

    public Authentication findAuthentication(String var1, URI var2, String var3);

    public void addAuthenticationResult(Authentication.Result var1);

    public void removeAuthenticationResult(Authentication.Result var1);

    public void clearAuthenticationResults();

    public Authentication.Result findAuthenticationResult(URI var1);

    default public boolean hasAuthenticationResults() {
        return true;
    }
}

