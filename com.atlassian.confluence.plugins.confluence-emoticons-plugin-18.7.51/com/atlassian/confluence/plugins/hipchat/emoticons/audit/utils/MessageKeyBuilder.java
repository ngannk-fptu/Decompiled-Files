/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.utils;

import java.util.Objects;

public class MessageKeyBuilder {
    static final String AUDIT_I18N_PREFIX = "audit.logging.";
    static final String AUDIT_CATEGORY_PREFIX = "audit.logging.category.";
    static final String AUDIT_SUMMARY_PREFIX = "audit.logging.summary.";

    private MessageKeyBuilder() {
    }

    public static String buildCategoryKey(String key) {
        return AUDIT_CATEGORY_PREFIX + Objects.requireNonNull(key);
    }

    public static String buildSummaryTextKey(String summaryKey) {
        return AUDIT_SUMMARY_PREFIX + Objects.requireNonNull(summaryKey);
    }
}

