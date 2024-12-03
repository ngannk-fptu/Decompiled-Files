/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 */
package com.atlassian.applinks.host.spi;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.HostApplication;
import java.net.URI;

public interface InternalHostApplication
extends HostApplication {
    public URI getDocumentationBaseUrl();

    public String getName();

    public ApplicationType getType();

    public Iterable<Class<? extends AuthenticationProvider>> getSupportedInboundAuthenticationTypes();

    public Iterable<Class<? extends AuthenticationProvider>> getSupportedOutboundAuthenticationTypes();

    public Iterable<EntityReference> getLocalEntities();

    public boolean doesEntityExist(String var1, Class<? extends EntityType> var2);

    public boolean doesEntityExistNoPermissionCheck(String var1, Class<? extends EntityType> var2);

    public EntityReference toEntityReference(Object var1);

    public EntityReference toEntityReference(String var1, Class<? extends EntityType> var2);

    public boolean canManageEntityLinksFor(EntityReference var1);

    public boolean hasPublicSignup();
}

