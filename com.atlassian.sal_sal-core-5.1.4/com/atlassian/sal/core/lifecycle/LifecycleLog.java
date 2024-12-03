/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.core.lifecycle;

import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

class LifecycleLog {
    LifecycleLog() {
    }

    @Nonnull
    static String getPluginKeyFromBundle(@Nullable Bundle bundle) {
        return bundle == null ? "<stale service reference>" : OsgiHeaderUtil.getPluginKey((Bundle)bundle);
    }

    @Nonnull
    static <T> String listPluginKeys(@Nonnull Collection<ServiceReference<T>> services) {
        Iterable pluginKeys = services.stream().map(service -> LifecycleLog.getPluginKeyFromBundle(service.getBundle())).collect(Collectors.toList());
        return String.format("[%s]", String.join((CharSequence)", ", pluginKeys));
    }
}

