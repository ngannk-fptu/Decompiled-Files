/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.search.parameter;

public interface SearchParameter {
    public static final String MAXHITS = "maxhits";
    public static final String APPLICATION = "application";
    public static final String PROJECT = "project";

    public String getName();

    public String getValue();

    public String buildQueryString();
}

