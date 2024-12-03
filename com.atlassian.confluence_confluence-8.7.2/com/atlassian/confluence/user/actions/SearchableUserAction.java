/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.util.PairType
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.core.util.PairType;
import java.util.List;

interface SearchableUserAction<T> {
    public String doUserSearch();

    public PageRequest getPageRequest();

    public boolean isShowAll();

    public int getResultsPerPage();

    public String getSearchTerm();

    public void setSearchTerm(String var1);

    public void setResultsPerPage(int var1);

    public List<PairType> getResultsPerPageOptions();

    public void setStartIndex(int var1);

    public PageResponse<T> getPageResponse();

    public void setPageResponse(PageResponse<T> var1);
}

