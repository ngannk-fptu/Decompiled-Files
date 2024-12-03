/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.sal.api.features.DarkFeatureManager;
import java.util.Objects;

public interface MappingDeconflictDarkFeature {
    public static final String KEY = "index.mapping.deconflict";
    public static final boolean DEFAULT = true;

    public static boolean isEnabled(DarkFeatureManager darkFeatureManager) {
        return darkFeatureManager.isEnabledForAllUsers(KEY).orElse(true);
    }

    public static MappingDeconflictDarkFeature create(DarkFeatureManager darkFeatureManager) {
        Objects.requireNonNull(darkFeatureManager);
        return () -> MappingDeconflictDarkFeature.isEnabled(darkFeatureManager);
    }

    public boolean isEnabled();
}

