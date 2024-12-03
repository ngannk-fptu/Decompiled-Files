/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.service.token;

import java.util.Date;

public interface ExpirableUserTokenService {
    public boolean removeExpiredTokens(Date var1);
}

