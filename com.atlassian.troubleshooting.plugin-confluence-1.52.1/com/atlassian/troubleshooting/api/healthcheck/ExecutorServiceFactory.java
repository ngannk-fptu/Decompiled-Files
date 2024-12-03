/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.util.concurrent.ExecutorService;
import javax.annotation.Nonnull;

public interface ExecutorServiceFactory {
    @Nonnull
    public ExecutorService newFixedSizeThreadPool(int var1, @Nonnull String var2);
}

