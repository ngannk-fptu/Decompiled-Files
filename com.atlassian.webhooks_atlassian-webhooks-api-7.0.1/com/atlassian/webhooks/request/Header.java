/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.request;

import javax.annotation.Nonnull;

public interface Header {
    @Nonnull
    public String getName();

    @Nonnull
    public String getValue();
}

