/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.writer.convert;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ValueConverter {
    @Nullable
    public Object convert(@Nullable Object var1);
}

