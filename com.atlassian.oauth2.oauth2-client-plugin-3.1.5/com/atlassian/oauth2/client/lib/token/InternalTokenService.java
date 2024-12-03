/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.token.TokenServiceException
 */
package com.atlassian.oauth2.client.lib.token;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.token.TokenServiceException;

public interface InternalTokenService {
    public ClientToken getAccessTokenFromAuthorizationCode(ClientConfiguration var1, String var2) throws TokenServiceException;
}

