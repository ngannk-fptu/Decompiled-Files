/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.authentication;

import com.atlassian.crowd.directory.authentication.UserCredentialVerifier;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;

public interface UserCredentialVerifierFactory {
    public UserCredentialVerifier create(AzureApiUriResolver var1, String var2, String var3);
}

