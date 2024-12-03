/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import java.lang.reflect.Type;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface RestObjectEnricher {
    public boolean isRecursive();

    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type var1);
}

