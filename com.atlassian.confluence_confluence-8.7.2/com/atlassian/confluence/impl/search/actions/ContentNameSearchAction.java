/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableList
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.struts2.interceptor.ServletRequestAware
 */
package com.atlassian.confluence.impl.search.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchService;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

@Deprecated
public class ContentNameSearchAction
extends ConfluenceActionSupport
implements Beanable,
ServletRequestAware {
    private ContentNameSearchService contentNameSearchService;
    private String query;
    private String[] types;
    private String spaceKey;
    private int maxPerCategory = -1;
    private HttpServletRequest servletRequest;
    private int limit = -1;
    private ContentNameSearchResult result;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ImmutableList typesIterable = this.types == null ? Collections.emptyList() : ImmutableList.builder().add((Object[])this.types).build();
        this.result = this.contentNameSearchService.search(this.query, new ContentNameSearchContext((Iterable<String>)typesIterable, this.spaceKey, this.maxPerCategory, this.servletRequest, this.limit));
        return "success";
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setType(String[] types) {
        this.types = (String[])ArrayUtils.clone((Object[])types);
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setMaxPerCategory(int maxPerCategory) {
        this.maxPerCategory = maxPerCategory;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ContentNameSearchResult getResult() {
        return this.result;
    }

    @Override
    public Object getBean() {
        return this.getResult();
    }

    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.servletRequest = httpServletRequest;
    }

    public void setContentNameSearchService(ContentNameSearchService contentNameSearchService) {
        this.contentNameSearchService = contentNameSearchService;
    }
}

