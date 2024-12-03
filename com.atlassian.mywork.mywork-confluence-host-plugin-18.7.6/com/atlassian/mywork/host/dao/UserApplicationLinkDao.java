/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 */
package com.atlassian.mywork.host.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.mywork.host.model.UserApplicationLink;
import com.atlassian.sal.usercompatibility.UserKey;
import java.util.Map;

@Transactional
public interface UserApplicationLinkDao {
    public Map<String, UserApplicationLink> findAllByApplicationId(UserKey var1);

    public void setPingCompleted(String var1, String var2);

    public void clearPingCompleted(String var1);

    public boolean delete(long var1);
}

