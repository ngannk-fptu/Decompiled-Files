/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.auth.verifier.Context;
import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import java.security.PublicKey;
import java.util.List;

public interface ClientCredentialsSelector<T> {
    public List<Secret> selectClientSecrets(ClientID var1, ClientAuthenticationMethod var2, Context<T> var3) throws InvalidClientException;

    public List<? extends PublicKey> selectPublicKeys(ClientID var1, ClientAuthenticationMethod var2, JWSHeader var3, boolean var4, Context<T> var5) throws InvalidClientException;
}

