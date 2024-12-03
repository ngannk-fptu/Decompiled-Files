/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.ContentEntityObject;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Contained<T extends ContentEntityObject> {
    public @Nullable T getContainer();
}

