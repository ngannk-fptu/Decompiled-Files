/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.IClientSecret;
import com.microsoft.aad.msal4j.StringHelper;

final class ClientSecret
implements IClientSecret {
    private final String clientSecret;

    ClientSecret(String clientSecret) {
        if (StringHelper.isBlank(clientSecret)) {
            throw new IllegalArgumentException("clientSecret is null or empty");
        }
        this.clientSecret = clientSecret;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientSecret)) {
            return false;
        }
        ClientSecret other = (ClientSecret)o;
        String this$clientSecret = this.clientSecret();
        String other$clientSecret = other.clientSecret();
        return !(this$clientSecret == null ? other$clientSecret != null : !this$clientSecret.equals(other$clientSecret));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $clientSecret = this.clientSecret();
        result = result * 59 + ($clientSecret == null ? 43 : $clientSecret.hashCode());
        return result;
    }

    @Override
    public String clientSecret() {
        return this.clientSecret;
    }
}

