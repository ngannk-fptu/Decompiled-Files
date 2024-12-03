/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.event.events.content;

import com.atlassian.confluence.core.ContentEntityObject;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Contented {
    public @NonNull ContentEntityObject getContent();
}

