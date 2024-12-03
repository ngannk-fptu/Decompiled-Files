/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.annotations.Internal;

@Internal
public interface CollaborativeEditingHelper {
    public static final String COLLABORATIVE_MODE = "collaborative";
    public static final String LEGACY_MODE = "legacy";
    @Deprecated
    public static final String LIMITED_MODE = "limited";
    public static final String SHARED_DRAFTS_DARK_FEATURE = "shared-drafts";
    public static final String SITE_WIDE_SHARED_DRAFTS_DARK_FEATURE = "site-wide.shared-drafts";
    @Deprecated
    public static final String SYNCHRONY_DARK_FEATURE = "synchrony";
    @Deprecated
    public static final String SITE_WIDE_SYNCHRONY_DARK_FEATURE = "site-wide.synchrony";
    public static final String USER_LIMIT_DARK_FEATURE_DISABLE = "confluence.collab.edit.user.limit.disable";

    public boolean isSharedDraftsFeatureEnabled(String var1);

    public boolean isUpgraded();

    @Deprecated
    public boolean isLimitedModeEnabled(String var1);

    public String getEditMode(String var1);

    public boolean isOverLimit(int var1);

    public int getUserLimit();
}

