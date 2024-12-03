/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.security.trust;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface CurrentApplicationIdProvider {
    public @NonNull String getCurrentApplicationId();
}

