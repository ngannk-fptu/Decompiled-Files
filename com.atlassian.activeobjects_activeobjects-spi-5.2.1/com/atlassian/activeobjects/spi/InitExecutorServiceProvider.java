/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.tenancy.api.Tenant
 *  javax.annotation.Nonnull
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.tenancy.api.Tenant;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nonnull;

@PublicSpi
public interface InitExecutorServiceProvider {
    @Nonnull
    @Deprecated
    public ExecutorService initExecutorService(@Nonnull Tenant var1);

    default public ExecutorService initExecutorService() {
        return this.initExecutorService(null);
    }
}

