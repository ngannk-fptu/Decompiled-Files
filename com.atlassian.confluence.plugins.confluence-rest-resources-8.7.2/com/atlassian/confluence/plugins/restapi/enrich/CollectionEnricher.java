/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.restapi.enrich.RestObjectEnricher;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CollectionEnricher
extends RestObjectEnricher {
    public void enrich(@NonNull RestList var1, @NonNull SchemaType var2);
}

