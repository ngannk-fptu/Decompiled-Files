/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.api.lib.token;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.token.TokenServiceException;
import java.time.Duration;

public interface TokenService {
    public ClientToken forceRefresh(ClientConfiguration var1, ClientToken var2) throws TokenServiceException, IllegalArgumentException;

    public ClientToken refreshIfNeeded(ClientConfiguration var1, ClientToken var2, Duration var3) throws TokenServiceException;

    public boolean isRefreshNeeded(ClientToken var1, Duration var2);
}

