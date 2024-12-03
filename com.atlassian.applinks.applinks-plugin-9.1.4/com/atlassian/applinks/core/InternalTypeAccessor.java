/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.util.TypeAccessor
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.util.TypeAccessor;

public interface InternalTypeAccessor
extends TypeAccessor {
    public ApplicationType loadApplicationType(String var1);

    public EntityType loadEntityType(String var1);

    public Iterable<? extends EntityType> getEntityTypesForApplicationType(TypeId var1);
}

