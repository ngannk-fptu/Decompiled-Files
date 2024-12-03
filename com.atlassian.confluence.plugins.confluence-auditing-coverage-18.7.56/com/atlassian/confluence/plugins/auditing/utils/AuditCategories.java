/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.auditing.utils;

import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;

public class AuditCategories {
    public static final String ADMIN_CATEGORY = MessageKeyBuilder.buildCategoryKey("admin");
    public static final String AUTH_CATEGORY = MessageKeyBuilder.buildCategoryKey("auth");
    public static final String SEARCH_CATEGORY = MessageKeyBuilder.buildCategoryKey("search");

    private AuditCategories() {
    }
}

