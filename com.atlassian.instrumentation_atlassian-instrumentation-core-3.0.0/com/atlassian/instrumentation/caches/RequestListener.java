/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.instrumentation.caches;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

public interface RequestListener {
    public String getName();

    public void onRequestStart();

    default public String getLoggingKey() {
        return "other";
    }

    public Map<String, Object> onRequestEnd();

    default public List<String> getTags() {
        return ImmutableList.of();
    }

    public boolean isEnabled();

    public void setEnabled(boolean var1);
}

