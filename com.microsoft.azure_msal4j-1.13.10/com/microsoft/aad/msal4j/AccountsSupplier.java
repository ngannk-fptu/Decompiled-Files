/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractClientApplicationBase;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.LogHelper;
import com.microsoft.aad.msal4j.MsalRequest;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

class AccountsSupplier
implements Supplier<Set<IAccount>> {
    AbstractClientApplicationBase clientApplication;
    MsalRequest msalRequest;

    AccountsSupplier(AbstractClientApplicationBase clientApplication, MsalRequest msalRequest) {
        this.clientApplication = clientApplication;
        this.msalRequest = msalRequest;
    }

    @Override
    public Set<IAccount> get() {
        try {
            return this.clientApplication.tokenCache.getAccounts(this.clientApplication.clientId());
        }
        catch (Exception ex) {
            this.clientApplication.log.error(LogHelper.createMessage("Execution of " + this.getClass() + " failed.", this.msalRequest.headers().getHeaderCorrelationIdValue()), (Throwable)ex);
            throw new CompletionException(ex);
        }
    }
}

