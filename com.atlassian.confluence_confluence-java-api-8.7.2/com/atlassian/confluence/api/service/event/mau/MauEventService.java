/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.service.event.mau;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.event.mau.MauApplicationKey;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
public interface MauEventService {
    public void addApplicationActivity(@NonNull MauApplicationKey var1);

    public void clearApplicationActivities();

    public void sendMauEvents();
}

