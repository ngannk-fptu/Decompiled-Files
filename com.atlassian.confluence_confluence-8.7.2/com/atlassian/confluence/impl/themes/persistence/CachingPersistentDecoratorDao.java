/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.impl.themes.persistence;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.impl.themes.persistence.PersistentDecoratorCache;
import com.atlassian.confluence.impl.themes.persistence.PersistentDecoratorHibernateHelper;
import com.atlassian.confluence.themes.persistence.PersistentDecoratorDao;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.SessionFactory;

public class CachingPersistentDecoratorDao
implements PersistentDecoratorDao {
    private final PersistentDecoratorHibernateHelper hibernate;
    private final PersistentDecoratorCache cache;

    public CachingPersistentDecoratorDao(SessionFactory sessionFactory, CacheFactory cacheFactory) {
        this.hibernate = new PersistentDecoratorHibernateHelper(sessionFactory);
        this.cache = PersistentDecoratorCache.create(cacheFactory);
    }

    @VisibleForTesting
    CachingPersistentDecoratorDao(PersistentDecoratorHibernateHelper hibernate, PersistentDecoratorCache cache) {
        this.hibernate = hibernate;
        this.cache = cache;
    }

    @Override
    public void saveOrUpdate(PersistentDecorator decorator) {
        if (this.getExistingDecorator(decorator).isPresent()) {
            this.hibernate.updateDecorator(decorator);
        } else {
            this.hibernate.saveNewDecorator(decorator);
        }
        this.cache.remove(decorator);
    }

    private Optional<PersistentDecorator> getExistingDecorator(PersistentDecorator decorator) {
        return Optional.ofNullable(this.get(decorator.getSpaceKey(), decorator.getName()));
    }

    @Override
    public @Nullable PersistentDecorator get(@Nullable String spaceKey, String decoratorName) {
        if (this.areThereAnyDecoratorsAtAll()) {
            return this.cache.getDecoratorByName(spaceKey, decoratorName, this.hibernate::getDecorators).orElse(null);
        }
        return null;
    }

    @Override
    public void remove(PersistentDecorator decorator) {
        if (this.getExistingDecorator(decorator).isPresent()) {
            this.hibernate.removeDecorator(decorator);
        }
        this.cache.remove(decorator);
    }

    private boolean areThereAnyDecoratorsAtAll() {
        return this.cache.hasDecorators(this.hibernate::hasAnyDecorators);
    }
}

