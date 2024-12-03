/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.applinks.api.EntityType;

public interface SubvertedEntityLinkService
extends EntityLinkService {
    public Iterable<EntityLink> getEntityLinksNoPermissionCheck(Object var1, Class<? extends EntityType> var2);

    public Iterable<EntityLink> getEntityLinksNoPermissionCheck(Object var1);
}

