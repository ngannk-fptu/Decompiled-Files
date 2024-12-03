/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.search.contentnames;

import javax.servlet.http.HttpServletRequest;

public class ContentNameSearchContext {
    private final Iterable<String> types;
    private final String spaceKey;
    private final int maxPerCategory;
    private final HttpServletRequest httpServletRequest;
    private final int limit;

    public ContentNameSearchContext(Iterable<String> types, String spaceKey, int maxPerCategory, HttpServletRequest httpServletRequest, int limit) {
        this.types = types;
        this.spaceKey = spaceKey;
        this.maxPerCategory = maxPerCategory;
        this.httpServletRequest = httpServletRequest;
        this.limit = limit;
    }

    public Iterable<String> getTypes() {
        return this.types;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public int getMaxPerCategory() {
        return this.maxPerCategory;
    }

    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    public int getLimit() {
        return this.limit;
    }
}

