/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.core;

import javax.annotation.Nonnull;
import org.osgi.framework.Version;

public interface AppLinkPluginUtil {
    @Nonnull
    public String getPluginKey();

    @Nonnull
    public String completeModuleKey(@Nonnull String var1);

    @Nonnull
    public Version getVersion();
}

