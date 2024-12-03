/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.service.rememberme.RememberMeToken
 *  com.atlassian.seraph.spi.rememberme.RememberMeTokenDao
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user.rememberme;

import com.atlassian.seraph.service.rememberme.RememberMeToken;
import com.atlassian.seraph.spi.rememberme.RememberMeTokenDao;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ConfluenceRememberMeTokenDao
extends RememberMeTokenDao {
    public void removeExpiredTokens();

    @Transactional(readOnly=true)
    public RememberMeToken findById(Long var1);

    public RememberMeToken save(RememberMeToken var1);

    @Transactional(readOnly=true)
    public List<RememberMeToken> findForUserName(String var1);

    public void remove(Long var1);

    public void removeAllForUser(String var1);

    public void removeAll();
}

