/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityLinkService
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.spi.link.EntityLinkBuilderFactory;
import com.atlassian.applinks.spi.link.ReciprocalActionException;

public interface MutatingEntityLinkService
extends EntityLinkService {
    public EntityLink addEntityLink(String var1, Class<? extends EntityType> var2, EntityLink var3);

    public EntityLink addReciprocatedEntityLink(String var1, Class<? extends EntityType> var2, EntityLink var3) throws ReciprocalActionException, CredentialsRequiredException;

    public boolean deleteEntityLink(String var1, Class<? extends EntityType> var2, EntityLink var3);

    public boolean deleteReciprocatedEntityLink(String var1, Class<? extends EntityType> var2, EntityLink var3) throws ReciprocalActionException, CredentialsRequiredException;

    public void deleteEntityLinksFor(ApplicationLink var1);

    public EntityLink makePrimary(String var1, Class<? extends EntityType> var2, EntityLink var3);

    public EntityLink getEntityLink(String var1, Class<? extends EntityType> var2, String var3, Class<? extends EntityType> var4, ApplicationId var5);

    public Iterable<EntityLink> getEntityLinksForApplicationLink(ApplicationLink var1) throws TypeNotInstalledException;

    public Iterable<EntityLink> getEntityLinksForKey(String var1, Class<? extends EntityType> var2, Class<? extends EntityType> var3);

    public Iterable<EntityLink> getEntityLinksForKey(String var1, Class<? extends EntityType> var2);

    public EntityLink getPrimaryEntityLinkForKey(String var1, Class<? extends EntityType> var2, Class<? extends EntityType> var3);

    public EntityLinkBuilderFactory getEntityLinkBuilderFactory();
}

