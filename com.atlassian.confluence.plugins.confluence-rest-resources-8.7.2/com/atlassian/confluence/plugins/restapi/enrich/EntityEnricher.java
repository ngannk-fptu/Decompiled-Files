/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.restapi.enrich.RestObjectEnricher;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface EntityEnricher
extends RestObjectEnricher {
    public void enrich(@NonNull RestEntity var1, @NonNull SchemaType var2);
}

