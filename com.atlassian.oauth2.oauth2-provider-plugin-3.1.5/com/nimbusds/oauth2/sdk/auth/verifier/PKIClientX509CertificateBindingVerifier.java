/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import com.nimbusds.oauth2.sdk.auth.verifier.Context;
import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import java.security.cert.X509Certificate;

public interface PKIClientX509CertificateBindingVerifier<T> {
    public void verifyCertificateBinding(ClientID var1, X509Certificate var2, Context<T> var3) throws InvalidClientException;
}

