/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.AlertTrigger$Builder
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class AlertTriggerResolver {
    private final CallingBundleResolver callingBundleResolver;
    private final String systemVersion;

    public AlertTriggerResolver(@Nonnull CallingBundleResolver callingBundleResolver, @Nonnull String systemVersion) {
        this.callingBundleResolver = Objects.requireNonNull(callingBundleResolver, "callingBundleResolver");
        this.systemVersion = Objects.requireNonNull(systemVersion, "systemVersion");
    }

    public AlertTrigger triggerForCallingBundle(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Bundle bundle = this.callingBundleResolver.getCallingBundle().orElseGet(() -> FrameworkUtil.getBundle((Class)clazz));
        return this.triggerForBundle(clazz, bundle);
    }

    public AlertTrigger triggerForBundle(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Bundle bundle = FrameworkUtil.getBundle((Class)clazz);
        return this.triggerForBundle(clazz, bundle);
    }

    private AlertTrigger triggerForBundle(Class clazz, Bundle bundle) {
        AlertTrigger.Builder builder = new AlertTrigger.Builder().module(clazz.getName());
        if (bundle != null && bundle.getBundleId() != 0L) {
            builder.plugin(OsgiHeaderUtil.getPluginKey((Bundle)bundle), bundle.getVersion().toString());
        } else {
            builder.plugin("System", this.systemVersion);
        }
        return builder.build();
    }
}

