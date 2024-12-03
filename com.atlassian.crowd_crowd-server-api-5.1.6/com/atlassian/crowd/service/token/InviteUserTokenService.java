/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectAlreadyExistsException
 *  com.atlassian.crowd.model.token.ExpirableUserToken
 */
package com.atlassian.crowd.service.token;

import com.atlassian.crowd.exception.ObjectAlreadyExistsException;
import com.atlassian.crowd.model.token.ExpirableUserToken;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface InviteUserTokenService {
    public static final int DEFAULT_TOKEN_EXPIRY_SECONDS = (int)TimeUnit.DAYS.toSeconds(7L);

    public ExpirableUserToken createAndStoreToken(String var1, long var2, int var4) throws ObjectAlreadyExistsException;

    public Optional<ExpirableUserToken> findByToken(String var1);

    public boolean removeToken(String var1);
}

