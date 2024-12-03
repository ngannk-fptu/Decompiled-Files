/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.analytics.client.extractor;

import com.google.common.collect.ImmutableMap;

public interface PropertyContributor {
    public void contribute(ImmutableMap.Builder<String, Object> var1, String var2, Object var3);
}

