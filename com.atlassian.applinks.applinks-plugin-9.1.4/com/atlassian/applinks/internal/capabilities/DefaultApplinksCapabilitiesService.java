/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.capabilities;

import com.atlassian.applinks.internal.capabilities.ApplinksCapabilitiesService;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class DefaultApplinksCapabilitiesService
implements ApplinksCapabilitiesService {
    private static boolean isDisabled(ApplinksCapabilities capability) {
        String property = System.getProperty(capability.key);
        return property != null && Boolean.FALSE.toString().equalsIgnoreCase(property);
    }

    @Override
    @Nonnull
    public Set<ApplinksCapabilities> getCapabilities() {
        EnumSet<ApplinksCapabilities> capabilities = EnumSet.allOf(ApplinksCapabilities.class);
        for (ApplinksCapabilities capability : ApplinksCapabilities.values()) {
            if (!DefaultApplinksCapabilitiesService.isDisabled(capability)) continue;
            capabilities.remove((Object)capability);
        }
        return capabilities;
    }
}

