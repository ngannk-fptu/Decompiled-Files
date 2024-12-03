/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import java.util.concurrent.CompletableFuture;

public interface IBroker {
    default public boolean isAvailable() {
        return false;
    }

    default public IAuthenticationResult acquireToken(PublicClientApplication application, SilentParameters requestParameters) {
        throw new MsalClientException("Broker implementation missing", "missing_broker");
    }

    default public IAuthenticationResult acquireToken(PublicClientApplication application, InteractiveRequestParameters requestParameters) {
        throw new MsalClientException("Broker implementation missing", "missing_broker");
    }

    default public IAuthenticationResult acquireToken(PublicClientApplication application, UserNamePasswordParameters requestParameters) {
        throw new MsalClientException("Broker implementation missing", "missing_broker");
    }

    default public CompletableFuture removeAccount(IAccount account) {
        throw new MsalClientException("Broker implementation missing", "missing_broker");
    }
}

