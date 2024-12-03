/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.johnson.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public interface JohnsonEventSerializer {
    public @NonNull Json toJson(Event var1);
}

