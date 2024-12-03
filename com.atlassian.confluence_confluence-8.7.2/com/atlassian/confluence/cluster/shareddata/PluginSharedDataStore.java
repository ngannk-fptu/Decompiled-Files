/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster.shareddata;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.shareddata.PluginSharedDataKey;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import com.atlassian.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.2", forRemoval=true)
@Internal
public interface PluginSharedDataStore {
    public @NonNull SharedData getPluginSharedData(PluginSharedDataKey var1, Plugin var2);

    public void unregisterPluginSharedData(Plugin var1);
}

