/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.token.Token
 */
package com.atlassian.crowd.dao.token;

import com.atlassian.crowd.dao.token.SearchableTokenStorage;
import com.atlassian.crowd.dao.token.SessionTokenStorage;
import com.atlassian.crowd.model.token.Token;
import java.util.Collection;

public interface TokenDAO
extends SessionTokenStorage,
SearchableTokenStorage {
    public Collection<Token> loadAll();

    public void saveAll(Collection<Token> var1);
}

