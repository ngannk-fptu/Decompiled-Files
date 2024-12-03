/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.osgi;

import com.atlassian.upm.osgi.Service;
import javax.annotation.Nullable;

public interface ServiceAccessor {
    public Iterable<Service> getServices();

    @Nullable
    public Service getService(long var1);
}

