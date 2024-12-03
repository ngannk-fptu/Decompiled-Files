/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.search.query;

public interface SearchQuery {
    public static final String PARAMETER_SEPARATOR = "&";

    public SearchQuery setParameter(String var1, String var2);

    public String getParameter(String var1);

    public SearchQuery append(String var1);

    public String buildQueryString();

    public String getSearchString();

    public int getParameter(String var1, int var2);
}

