/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.osgi.Bundle;
import javax.annotation.Nullable;

public interface BundleAccessor {
    public Iterable<Bundle> getBundles();

    public Iterable<Bundle> getBundles(@Nullable String var1);

    @Nullable
    public Bundle getBundle(long var1);
}

