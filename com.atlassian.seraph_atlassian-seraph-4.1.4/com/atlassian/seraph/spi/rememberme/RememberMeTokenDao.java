/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.spi.rememberme;

import com.atlassian.seraph.service.rememberme.RememberMeToken;
import java.util.List;

public interface RememberMeTokenDao {
    public RememberMeToken findById(Long var1);

    public RememberMeToken save(RememberMeToken var1);

    public List<RememberMeToken> findForUserName(String var1);

    public void remove(Long var1);

    public void removeAllForUser(String var1);

    public void removeAll();
}

