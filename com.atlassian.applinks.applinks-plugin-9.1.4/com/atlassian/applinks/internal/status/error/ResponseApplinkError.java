/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.ApplinkError;
import javax.annotation.Nullable;

public interface ResponseApplinkError
extends ApplinkError {
    public int getStatusCode();

    @Nullable
    public String getBody();

    @Nullable
    public String getContentType();
}

