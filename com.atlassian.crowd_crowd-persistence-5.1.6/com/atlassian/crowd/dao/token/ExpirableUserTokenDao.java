/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectAlreadyExistsException
 *  com.atlassian.crowd.model.token.ExpirableUserToken
 *  com.atlassian.crowd.model.token.ExpirableUserTokenType
 */
package com.atlassian.crowd.dao.token;

import com.atlassian.crowd.exception.ObjectAlreadyExistsException;
import com.atlassian.crowd.model.token.ExpirableUserToken;
import com.atlassian.crowd.model.token.ExpirableUserTokenType;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface ExpirableUserTokenDao {
    public Optional<ExpirableUserToken> findByToken(String var1);

    public ExpirableUserToken add(ExpirableUserToken var1) throws ObjectAlreadyExistsException;

    public boolean removeByToken(String var1);

    public boolean removeExpiredTokens(Date var1);

    public boolean removeByDirectoryAndUsername(long var1, String var3, ExpirableUserTokenType var4);

    public Set<ExpirableUserToken> findAllTokens(long var1, String var3, ExpirableUserTokenType var4);
}

