/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import com.nimbusds.oauth2.sdk.auth.verifier.Context;
import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException;
import com.nimbusds.oauth2.sdk.id.ClientID;

@Deprecated
public interface ClientX509CertificateBindingVerifier<T> {
    public void verifyCertificateBinding(ClientID var1, String var2, Context<T> var3) throws InvalidClientException;
}

