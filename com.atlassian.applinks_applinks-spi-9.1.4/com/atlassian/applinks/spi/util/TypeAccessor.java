/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 */
package com.atlassian.applinks.spi.util;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.spi.application.TypeId;

public interface TypeAccessor {
    public <T extends EntityType> T getEntityType(Class<T> var1);

    public EntityType loadEntityType(TypeId var1);

    public <T extends ApplicationType> T getApplicationType(Class<T> var1);

    public ApplicationType loadApplicationType(TypeId var1);

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass(String var1);

    public Iterable<? extends EntityType> getEnabledEntityTypes();

    public Iterable<? extends EntityType> getEnabledEntityTypesForApplicationType(ApplicationType var1);

    public Iterable<? extends ApplicationType> getEnabledApplicationTypes();
}

