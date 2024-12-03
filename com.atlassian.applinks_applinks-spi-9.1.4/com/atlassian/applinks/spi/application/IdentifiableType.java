/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.spi.application;

import com.atlassian.applinks.spi.application.TypeId;
import javax.annotation.Nonnull;

public interface IdentifiableType {
    @Nonnull
    public TypeId getId();
}

