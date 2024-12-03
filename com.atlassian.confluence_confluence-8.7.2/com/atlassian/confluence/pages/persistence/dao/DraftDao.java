/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;

public interface DraftDao {
    public void saveOrUpdate(Draft var1) throws IllegalArgumentException;

    public Draft getDraft(String var1, ConfluenceUser var2, String var3, String var4);

    public Draft getDraft(long var1);

    public void remove(Draft var1);

    public List<Draft> findByCreatorName(String var1);

    public List<Draft> findAll();

    public int countDrafts(String var1);
}

