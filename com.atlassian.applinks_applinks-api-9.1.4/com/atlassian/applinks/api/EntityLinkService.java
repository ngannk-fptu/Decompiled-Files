/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;

public interface EntityLinkService {
    public Iterable<EntityLink> getEntityLinks(Object var1, Class<? extends EntityType> var2);

    public Iterable<EntityLink> getEntityLinks(Object var1);

    public EntityLink getPrimaryEntityLink(Object var1, Class<? extends EntityType> var2);
}

