/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.streams.spi.Evictor
 *  com.google.common.base.Preconditions
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.streams.spi.Evictor;
import com.google.common.base.Preconditions;
import java.util.Optional;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceEvictor
implements Evictor<ConfluenceEntityObject> {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceEvictor.class);
    private final EntityManagerProvider entityManagerProvider;

    public ConfluenceEvictor(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = (EntityManagerProvider)Preconditions.checkNotNull((Object)entityManagerProvider);
    }

    public Void apply(ConfluenceEntityObject entity) {
        if (!(entity instanceof AbstractPage)) {
            return null;
        }
        try {
            EntityManagerFactory entityManagerFactory = this.entityManagerProvider.getEntityManager().getEntityManagerFactory();
            Optional.ofNullable(entityManagerFactory.getCache()).ifPresent(cache -> cache.evict(entity.getClass(), (Object)entity));
        }
        catch (IllegalStateException | PersistenceException e) {
            log.warn("Failed to evict the entity from the session", e);
        }
        return null;
    }
}

