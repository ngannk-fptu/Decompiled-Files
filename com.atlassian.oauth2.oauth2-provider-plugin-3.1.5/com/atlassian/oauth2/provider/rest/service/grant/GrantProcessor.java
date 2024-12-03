/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.service.grant;

import com.atlassian.oauth2.provider.rest.exception.InvalidGrantException;
import com.atlassian.oauth2.provider.rest.model.RestToken;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;

public interface GrantProcessor {
    public RestToken execute(TokenRequestFormParams var1) throws InvalidGrantException, InterruptedException;
}

