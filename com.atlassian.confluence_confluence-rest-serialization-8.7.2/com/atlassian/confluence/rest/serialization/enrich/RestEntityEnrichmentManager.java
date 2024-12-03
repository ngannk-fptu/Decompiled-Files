/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.rest.serialization.enrich;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public interface RestEntityEnrichmentManager {
    public boolean isEnrichableList(Class var1);

    public boolean isEnrichableEntity(Class var1);

    public @NonNull Map<String, Type> getEnrichedPropertyTypes(Type var1, boolean var2);

    public Object convertAndEnrich(Object var1, SchemaType var2);
}

