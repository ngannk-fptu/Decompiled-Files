/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.internal.ExternalCacheDetails;
import com.atlassian.vcache.internal.JvmCacheDetails;
import com.atlassian.vcache.internal.RequestCacheDetails;
import java.util.Map;

public interface VCacheManagement {
    public Map<String, JvmCacheDetails> allJvmCacheDetails();

    public Map<String, RequestCacheDetails> allRequestCacheDetails();

    public Map<String, ExternalCacheDetails> allExternalCacheDetails();
}

