/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.ldap.test;

public class PageSizeValidator {
    private static final int MINIMAL_PAGE_SIZE = 100;

    public static void checkPageSize(Boolean pageResults, Integer pageSize) {
        if (pageResults.booleanValue() && (pageSize == null || pageSize < 100)) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize);
        }
    }
}

