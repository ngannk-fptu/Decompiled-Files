/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.manager.application.AliasAlreadyInUseException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.List;
import java.util.Map;

public interface AliasManager {
    public String findUsernameByAlias(Application var1, String var2);

    public String findAliasByUsername(Application var1, String var2);

    public List<String> search(EntityQuery var1);

    public void storeAlias(Application var1, String var2, String var3) throws AliasAlreadyInUseException;

    public void removeAlias(Application var1, String var2) throws AliasAlreadyInUseException;

    public void removeAliasesForUser(String var1);

    public Map<String, String> findAliasesByUsernames(Application var1, Iterable<String> var2);

    @ExperimentalApi
    public Map<String, String> findAllAliasesByUsernames(Application var1);
}

