/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.johnson.support;

import com.atlassian.johnson.event.Event;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EventExceptionTranslator {
    @Nullable
    public Event translate(@Nonnull Throwable var1);
}

