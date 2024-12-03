/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.OperationTrigger;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface OperationContext<TRIGGER extends OperationTrigger> {
    public boolean isEventSuppressed();

    public boolean isSuppressNotifications();

    public @NonNull TRIGGER getUpdateTrigger();
}

