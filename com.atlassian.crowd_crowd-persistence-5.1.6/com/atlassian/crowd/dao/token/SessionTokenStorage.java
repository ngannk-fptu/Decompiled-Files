/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectAlreadyExistsException
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.token.Token
 */
package com.atlassian.crowd.dao.token;

import com.atlassian.crowd.exception.ObjectAlreadyExistsException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.token.Token;
import java.util.Date;

public interface SessionTokenStorage {
    public Token findByRandomHash(String var1) throws ObjectNotFoundException;

    public Token findByIdentifierHash(String var1) throws ObjectNotFoundException;

    public Token add(Token var1) throws ObjectAlreadyExistsException;

    public Token update(Token var1) throws ObjectNotFoundException;

    public void remove(Token var1);

    public void remove(long var1, String var3);

    public void removeExcept(long var1, String var3, String var4);

    public void removeAll(long var1);

    public void removeExpiredTokens(Date var1, long var2);

    public void removeAll();
}

