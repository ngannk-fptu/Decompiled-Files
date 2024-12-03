/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.themes.persistence;

import com.atlassian.confluence.core.PersistentDecorator;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

class PersistentDecoratorHibernateHelper {
    private final HibernateTemplate hibernate;

    PersistentDecoratorHibernateHelper(SessionFactory sessionFactory) {
        this.hibernate = new HibernateTemplate(sessionFactory);
    }

    public boolean hasAnyDecorators() {
        return !Objects.requireNonNull((List)this.hibernate.execute(session -> session.createQuery("from PersistentDecorator").setMaxResults(1).list())).isEmpty();
    }

    public List<PersistentDecorator> getDecorators(@Nullable String spaceKey) {
        return Optional.ofNullable(spaceKey).map(this::getDecoratorsBySpace).orElseGet(this::getGlobalDecorators);
    }

    private List<PersistentDecorator> getGlobalDecorators() {
        return this.hibernate.findByNamedQuery("confluence.persistentdecorator_findGlobalDecorators", new Object[0]);
    }

    private List<PersistentDecorator> getDecoratorsBySpace(String spaceKey) {
        return this.hibernate.findByNamedQueryAndNamedParam("confluence.persistentdecorator_findBySpaceKey", "spaceKey", (Object)spaceKey);
    }

    public void saveNewDecorator(PersistentDecorator decorator) {
        this.hibernate.save((Object)decorator);
    }

    public void removeDecorator(PersistentDecorator decorator) {
        this.hibernate.delete((Object)decorator);
    }

    private PersistentDecorator getNamedDecorator(@Nullable String spaceKey, String decoratorName) {
        return (PersistentDecorator)this.hibernate.execute(session -> DataAccessUtils.singleResult((Collection)PersistentDecoratorHibernateHelper.createDecoratorFetchQuery(spaceKey, decoratorName, session).list()));
    }

    public void updateDecorator(PersistentDecorator decorator) {
        this.getNamedDecorator(decorator.getSpaceKey(), decorator.getName()).setBody(decorator.getBody());
    }

    private static Query createDecoratorFetchQuery(@Nullable String spaceKey, String decoratorName, Session session) {
        Query query;
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            query = session.getNamedQuery("confluence.persistentdecorator_findByNameAndSpaceKey");
            query.setParameter("spaceKey", (Object)spaceKey);
        } else {
            query = session.getNamedQuery("confluence.persistentdecorator_findGlobalDecoratorByName");
        }
        query.setParameter("name", (Object)decoratorName);
        return query;
    }
}

