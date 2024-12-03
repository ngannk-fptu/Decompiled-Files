/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

public interface DatabaseNameHelper {
    public boolean isQueryDslReady();

    public String getCaseSensitiveTableName(String var1);

    public String getCaseSensitiveColumnName(String var1, String var2);
}

