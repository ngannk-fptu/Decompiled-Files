/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.mobile.hibernate;

import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class FindFavouriteSpacesHibernateQueryFactory
implements HibernateContentQueryFactory {
    public Query getQuery(EntityManager entityManager, Object ... parameters) throws PersistenceException {
        Query query = entityManager.createQuery("select space from Space space, Labelling labelling, Label label where space.spaceStatus = 'CURRENT' and space.description = labelling.content and labelling.label = label and (label.name = 'favourite' or label.name = 'favorite') and label.owningUser.lowerName = :lowerOwnerName and label.namespace = 'my' order by labelling.lastModificationDate");
        query.setParameter("lowerOwnerName", parameters[0]);
        return query;
    }
}

