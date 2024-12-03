/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.dao.alias;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.model.alias.Alias;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import java.util.Map;

public interface AliasDAO {
    public List<String> search(EntityQuery<String> var1);

    public String findAliasByUsername(Application var1, String var2);

    public String findUsernameByAlias(Application var1, String var2);

    public void storeAlias(Application var1, String var2, String var3);

    public void removeAlias(Application var1, String var2);

    public void removeAliases(Application var1);

    public void removeAliasesForUser(String var1);

    public List<Alias> findAliasesForUsers(Application var1, Iterable<String> var2);

    @ExperimentalApi
    public Map<String, String> findAllAliases(Application var1);
}

