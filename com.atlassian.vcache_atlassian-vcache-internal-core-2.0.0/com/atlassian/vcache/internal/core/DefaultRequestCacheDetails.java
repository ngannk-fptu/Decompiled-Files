/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.RequestCacheDetails
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.RequestCacheDetails;
import java.util.Objects;

public class DefaultRequestCacheDetails
implements RequestCacheDetails {
    private final String name;

    public DefaultRequestCacheDetails(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return this.name;
    }
}

