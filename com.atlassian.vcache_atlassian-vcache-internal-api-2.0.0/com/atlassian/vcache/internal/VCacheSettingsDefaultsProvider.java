/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.JvmCacheSettings
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.JvmCacheSettings;

public interface VCacheSettingsDefaultsProvider {
    public ExternalCacheSettings getExternalDefaults(String var1);

    public JvmCacheSettings getJvmDefaults(String var1);
}

