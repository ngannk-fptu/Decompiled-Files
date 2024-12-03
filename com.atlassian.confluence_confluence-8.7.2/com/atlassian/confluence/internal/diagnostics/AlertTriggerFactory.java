/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.AlertTrigger$Builder
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import java.util.Objects;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

class AlertTriggerFactory {
    private final CallingBundleResolver callingBundleResolver;

    public AlertTriggerFactory(CallingBundleResolver callingBundleResolver) {
        this.callingBundleResolver = Objects.requireNonNull(callingBundleResolver);
    }

    public AlertTrigger create(Class<?> type) {
        AlertTrigger.Builder builder = new AlertTrigger.Builder().module(type.getName());
        Bundle bundle = this.callingBundleResolver.getCallingBundle().orElseGet(() -> FrameworkUtil.getBundle((Class)type));
        if (bundle != null && bundle.getBundleId() != 0L) {
            builder.plugin(OsgiHeaderUtil.getPluginKey((Bundle)bundle), bundle.getVersion().toString());
        } else {
            builder.plugin("System", null);
        }
        return builder.build();
    }
}

