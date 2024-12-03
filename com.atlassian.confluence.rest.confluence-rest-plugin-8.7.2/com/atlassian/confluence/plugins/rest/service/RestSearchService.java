/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.service;

import com.atlassian.confluence.plugins.rest.entities.SearchResultEntityList;
import com.atlassian.confluence.plugins.rest.service.RestSearchParameters;
import com.atlassian.confluence.plugins.rest.service.SearchServiceException;

public interface RestSearchService {
    public SearchResultEntityList userSearch(String var1, Integer var2) throws SearchServiceException;

    public SearchResultEntityList userSearch(String var1, Integer var2, boolean var3) throws SearchServiceException;

    public SearchResultEntityList groupSearch(String var1, Integer var2) throws SearchServiceException;

    public SearchResultEntityList fullSearch(RestSearchParameters var1, Integer var2, Integer var3) throws SearchServiceException;

    public SearchResultEntityList nameSearch(RestSearchParameters var1, boolean var2, int var3, Integer var4, Integer var5) throws SearchServiceException;
}

