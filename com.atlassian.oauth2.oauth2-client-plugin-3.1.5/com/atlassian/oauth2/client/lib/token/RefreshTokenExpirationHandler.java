/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 */
package com.atlassian.oauth2.client.lib.token;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.time.Instant;

public interface RefreshTokenExpirationHandler {
    public Instant getExpirationTimeForToken(ClientConfiguration var1, Instant var2, RefreshToken var3);
}

