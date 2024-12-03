/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.google.common.collect.Iterables;
import java.util.List;

@Deprecated
public final class LegacySingleSsoConfigUtil {
    private LegacySingleSsoConfigUtil() {
    }

    public static IdpConfig getOnlySsoConfig(List<IdpConfig> ssoConfigs) {
        return (IdpConfig)Iterables.getFirst(ssoConfigs, null);
    }

    public static boolean isSsoEnabled(List<IdpConfig> idpConfigs) {
        IdpConfig onlyConfig = LegacySingleSsoConfigUtil.getOnlySsoConfig(idpConfigs);
        return onlyConfig != null && onlyConfig.isEnabled();
    }
}

