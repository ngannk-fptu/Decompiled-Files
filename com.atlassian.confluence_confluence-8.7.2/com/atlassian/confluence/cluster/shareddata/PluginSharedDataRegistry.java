/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster.shareddata;

import com.atlassian.confluence.cluster.shareddata.PluginSharedDataKey;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.2", forRemoval=true)
public interface PluginSharedDataRegistry {
    public @NonNull SharedData getPluginSharedData(PluginSharedDataKey var1);
}

