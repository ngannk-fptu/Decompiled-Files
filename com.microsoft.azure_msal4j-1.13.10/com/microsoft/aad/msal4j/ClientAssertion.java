/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.IClientAssertion;
import com.microsoft.aad.msal4j.StringHelper;

final class ClientAssertion
implements IClientAssertion {
    static final String assertionType = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private final String assertion;

    ClientAssertion(String assertion) {
        if (StringHelper.isBlank(assertion)) {
            throw new NullPointerException("assertion");
        }
        this.assertion = assertion;
    }

    @Override
    public String assertion() {
        return this.assertion;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientAssertion)) {
            return false;
        }
        ClientAssertion other = (ClientAssertion)o;
        String this$assertion = this.assertion();
        String other$assertion = other.assertion();
        return !(this$assertion == null ? other$assertion != null : !this$assertion.equals(other$assertion));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $assertion = this.assertion();
        result = result * 59 + ($assertion == null ? 43 : $assertion.hashCode());
        return result;
    }
}

