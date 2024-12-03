/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.crowd.model.user.User;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface AuditHelper {
    public static final String AUDIT_I18N_PREFIX = "audit.logging.";
    public static final String AUDIT_SUMMARY_PREFIX = "audit.logging.summary.";
    public static final String AUDIT_DESCRIPTION_PREFIX = "audit.logging.description.";
    public static final String AUDIT_CHANGED_VALUE_PREFIX = "audit.logging.changed.value.";
    public static final String AUDIT_EXTRA_ATTRIBUTE_PREFIX = "audit.logging.extra.attribute.key.";

    public static String buildTextKey(String key) {
        return AUDIT_I18N_PREFIX + Objects.requireNonNull(key);
    }

    public static String buildSummaryTextKey(String summaryKey) {
        return AUDIT_SUMMARY_PREFIX + Objects.requireNonNull(summaryKey);
    }

    public static String buildChangedValueTextKey(String changedValueKey) {
        return AUDIT_CHANGED_VALUE_PREFIX + Objects.requireNonNull(changedValueKey);
    }

    public static String buildDescriptionTextKey(String descriptionKey) {
        return AUDIT_DESCRIPTION_PREFIX + descriptionKey;
    }

    public static String buildExtraAttribute(String extraAttributeKey) {
        return AUDIT_EXTRA_ATTRIBUTE_PREFIX + extraAttributeKey;
    }

    public String translate(String var1);

    public @Nullable String fetchSpaceId(@Nullable String var1);

    public @Nullable String fetchSpaceDisplayName(@Nullable String var1);

    public @Nullable String fetchUserKey(@Nullable ConfluenceUser var1);

    public @Nullable String fetchUserKey(@Nullable User var1);

    public @Nullable String fetchUserFullName(@Nullable ConfluenceUser var1);

    public @Nullable String fetchUserFullName(@Nullable User var1);

    public @Nullable String fetchUserKey(@Nullable String var1);

    public @Nullable String fetchUserFullName(@Nullable String var1);
}

