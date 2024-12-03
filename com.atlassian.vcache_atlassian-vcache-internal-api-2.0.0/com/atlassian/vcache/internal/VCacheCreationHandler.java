/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.VCacheException
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.VCacheException;
import com.atlassian.vcache.internal.ExternalCacheDetails;
import com.atlassian.vcache.internal.JvmCacheDetails;

public interface VCacheCreationHandler {
    public JvmCacheSettings jvmCacheCreation(JvmCacheDetails var1) throws VCacheException;

    public void requestCacheCreation(String var1);

    public ExternalCacheSettings externalCacheCreation(ExternalCacheDetails var1);
}

