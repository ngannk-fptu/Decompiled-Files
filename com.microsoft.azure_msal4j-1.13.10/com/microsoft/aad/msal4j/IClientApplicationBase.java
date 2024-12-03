/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ITokenCache;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.SilentParameters;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.net.ssl.SSLSocketFactory;

interface IClientApplicationBase {
    public static final String DEFAULT_AUTHORITY = "https://login.microsoftonline.com/common/";

    public String clientId();

    public String authority();

    public boolean validateAuthority();

    public String correlationId();

    public boolean logPii();

    public Proxy proxy();

    public SSLSocketFactory sslSocketFactory();

    public ITokenCache tokenCache();

    public URL getAuthorizationRequestUrl(AuthorizationRequestUrlParameters var1);

    public CompletableFuture<IAuthenticationResult> acquireToken(AuthorizationCodeParameters var1);

    public CompletableFuture<IAuthenticationResult> acquireToken(RefreshTokenParameters var1);

    public CompletableFuture<IAuthenticationResult> acquireTokenSilently(SilentParameters var1) throws MalformedURLException;

    public CompletableFuture<Set<IAccount>> getAccounts();

    public CompletableFuture removeAccount(IAccount var1);
}

