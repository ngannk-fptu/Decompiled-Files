/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.authentication;

import com.atlassian.crowd.directory.authentication.MsGraphApiAuthenticator;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;

public interface MsGraphApiAuthenticatorFactory {
    public MsGraphApiAuthenticator create(String var1, String var2, String var3, AzureApiUriResolver var4);
}

