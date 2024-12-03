/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.utils;

import com.atlassian.confluence.plugins.hipchat.emoticons.audit.utils.MessageKeyBuilder;

public class AuditCategories {
    public static final String PAGES_CATEGORY = MessageKeyBuilder.buildCategoryKey("pages");
    public static final String PERMISSIONS_CATEGORY = MessageKeyBuilder.buildCategoryKey("permissions");

    private AuditCategories() {
    }
}

