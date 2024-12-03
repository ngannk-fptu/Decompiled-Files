/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.core.exception.InfrastructureException
 *  javax.persistence.PersistenceException
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.metamodel.spi.MetamodelImplementor
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.SearchableDao;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.core.exception.InfrastructureException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateSearchableDao
implements SearchableDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateSearchableDao.class);
    private final HibernateTemplate hibernateTemplate;

    public HibernateSearchableDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public List<List<HibernateHandle>> getLatestSearchableHandlesGroupedByType() {
        return this.getLatestSearchableHandlesGroupedByType(Optional.empty());
    }

    @Override
    @Transactional(readOnly=true)
    public List<List<HibernateHandle>> getLatestSearchableHandlesGroupedByType(Optional<String> spaceKey) {
        List<Class<? extends Searchable>> implementingClasses = this.getClassesImplementingSearchable();
        LinkedList<List<HibernateHandle>> result = new LinkedList<List<HibernateHandle>>();
        for (Class<? extends Searchable> clazz : implementingClasses) {
            List<HibernateHandle> handles = this.getLatestSearchableHandles(clazz, spaceKey);
            if (handles.size() <= 0) continue;
            result.add(handles);
        }
        return result;
    }

    @Override
    @Transactional(readOnly=true)
    public List<HibernateHandle> getLatestSearchableHandles(Class<? extends Searchable> clazz) {
        return this.getLatestSearchableHandles(clazz, Optional.empty());
    }

    @Override
    @Transactional(readOnly=true)
    public List<HibernateHandle> getLatestSearchableHandles(Class<? extends Searchable> clazz, Optional<String> spaceKeyOptional) {
        if (spaceKeyOptional.isPresent() && !this.isSpaceContentEntitySubClassOrComment(clazz)) {
            return Collections.emptyList();
        }
        try (Cleanup ignored = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            List<Class<? extends Searchable>> implementingClasses;
            if (Comment.class.isAssignableFrom(clazz)) {
                List list = (List)this.hibernateTemplate.execute(session -> {
                    Query query = session.createQuery(this.getHqlCommentQuery(clazz, spaceKeyOptional));
                    spaceKeyOptional.ifPresent(spaceKey -> query.setParameter("spaceKey", spaceKey));
                    return this.findHandles(query, clazz);
                });
                return list;
            }
            List<Class<Object>> list = spaceKeyOptional.isPresent() ? this.getClassesImplementingSearchable().stream().filter(SpaceContentEntityObject.class::isAssignableFrom).collect(Collectors.toList()) : (implementingClasses = this.getClassesImplementingSearchable());
            if (!implementingClasses.contains(clazz)) {
                throw new IllegalArgumentException(clazz + " is not a hibernate entity class");
            }
            List list2 = (List)this.hibernateTemplate.execute(session -> {
                Query query = session.createQuery(this.getHqlQuery(clazz, spaceKeyOptional));
                spaceKeyOptional.ifPresent(spaceKey -> query.setParameter("spaceKey", spaceKey));
                return this.findHandles(query, clazz);
            });
            return list2;
        }
    }

    private boolean isSpaceContentEntitySubClassOrComment(Class<? extends Searchable> clazz) {
        return SpaceContentEntityObject.class.isAssignableFrom(clazz) || Comment.class.isAssignableFrom(clazz);
    }

    private List<HibernateHandle> findHandles(Query query, Class<? extends Searchable> clazz) {
        query.setCacheable(false);
        List ids = query.list();
        LinkedList<HibernateHandle> result = new LinkedList<HibernateHandle>();
        for (Long id : ids) {
            result.add(new HibernateHandle(clazz.getName(), id));
        }
        return result;
    }

    @Override
    @Transactional(readOnly=true)
    public int getCountOfLatestSearchables() {
        List<Class<? extends Searchable>> implementingClasses = this.getClassesImplementingSearchable();
        return Objects.requireNonNull((Integer)this.hibernateTemplate.execute(session -> {
            int result = 0;
            for (Class clazz : implementingClasses) {
                result += (int)((Long)session.createQuery(this.getHqlCountQuery(clazz, Optional.empty())).iterate().next()).longValue();
            }
            return result;
        }));
    }

    @Override
    @Transactional(readOnly=true)
    public int getCountOfLatestSearchables(String spaceKey) {
        List implementingClasses = this.getClassesImplementingSearchable().stream().filter(SpaceContentEntityObject.class::isAssignableFrom).collect(Collectors.toList());
        int count = Objects.requireNonNull((Integer)this.hibernateTemplate.execute(session -> {
            int result = 0;
            for (Class clazz : implementingClasses) {
                result += (int)((Long)session.createQuery(this.getHqlCountQuery(clazz, Optional.ofNullable(spaceKey))).setParameter("spaceKey", (Object)spaceKey).iterate().next()).longValue();
            }
            return result;
        }));
        return count += Objects.requireNonNull((Integer)this.hibernateTemplate.execute(session -> (int)((Long)session.createQuery(this.getHqlCommentCountQuery(Comment.class, Optional.ofNullable(spaceKey))).setParameter("spaceKey", (Object)spaceKey).iterate().next()).longValue())).intValue();
    }

    @Override
    @Transactional(readOnly=true)
    public int getCountOfLatestSearchables(Class<? extends Searchable> clazz) {
        List<Class<? extends Searchable>> implementingClasses = this.getClassesImplementingSearchable();
        if (!implementingClasses.contains(clazz)) {
            throw new IllegalArgumentException(clazz + " is not a hibernate entity class");
        }
        return (int)((Long)Objects.requireNonNull(this.hibernateTemplate.execute(session -> session.createQuery(this.getHqlCountQuery(clazz, Optional.empty())).iterate().next()))).longValue();
    }

    @Override
    public int getCountOfLatestSearchables(String spaceKey, Class<? extends Searchable> clazz) {
        if (Comment.class.isAssignableFrom(clazz)) {
            return (int)((Long)Objects.requireNonNull(this.hibernateTemplate.execute(session -> session.createQuery(this.getHqlCommentCountQuery(clazz, Optional.ofNullable(spaceKey))).setParameter("spaceKey", (Object)spaceKey).iterate().next()))).longValue();
        }
        if (!SpaceContentEntityObject.class.isAssignableFrom(clazz)) {
            return 0;
        }
        List implementingClasses = this.getClassesImplementingSearchable().stream().filter(SpaceContentEntityObject.class::isAssignableFrom).collect(Collectors.toList());
        if (!implementingClasses.contains(clazz)) {
            throw new IllegalArgumentException(clazz + " is not a hibernate entity class");
        }
        return (int)((Long)Objects.requireNonNull(this.hibernateTemplate.execute(session -> session.createQuery(this.getHqlCountQuery(clazz, Optional.ofNullable(spaceKey))).setParameter("spaceKey", (Object)spaceKey).iterate().next()))).longValue();
    }

    private String getHqlQuery(Class<? extends Searchable> clazz, Optional<String> spaceKeyOptional) {
        StringBuilder query = new StringBuilder();
        if (Versioned.class.isAssignableFrom(clazz)) {
            query.append(String.format("select searchable.id from %s searchable where searchable.originalVersion is null", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" and searchable.space.key = :spaceKey"));
        } else {
            query.append(String.format("select searchable.id from %s searchable", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" where searchable.space.key = :spaceKey"));
        }
        return query.toString();
    }

    private String getHqlCommentQuery(Class<? extends Searchable> clazz, Optional<String> spaceKeyOptional) {
        StringBuilder query = new StringBuilder();
        if (Versioned.class.isAssignableFrom(clazz)) {
            query.append(String.format("select comment.id from %s comment where comment.originalVersion is null", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" and comment.containerContent.space.key = :spaceKey"));
        } else {
            query.append(String.format("select comment.id from %s comment", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" where comment.containerContent.space.key = :spaceKey"));
        }
        return query.toString();
    }

    private String getHqlCommentCountQuery(Class<? extends Searchable> clazz, Optional<String> spaceKeyOptional) {
        StringBuilder query = new StringBuilder();
        if (Versioned.class.isAssignableFrom(clazz)) {
            query.append(String.format("select count(*) from %s comment where comment.originalVersion is null", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" and comment.containerContent.space.key = :spaceKey"));
        } else {
            query.append(String.format("select count(*) from %s comment", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" where comment.containerContent.space.key =  :spaceKey"));
        }
        return query.toString();
    }

    private String getHqlCountQuery(Class<? extends Searchable> clazz, Optional<String> spaceKeyOptional) {
        StringBuilder query = new StringBuilder();
        if (Versioned.class.isAssignableFrom(clazz)) {
            query.append(String.format("select count(*) from %s searchable where searchable.originalVersion is null", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" and searchable.space.key = :spaceKey"));
        } else {
            query.append(String.format("select count(*) from %s searchable", clazz.getName()));
            spaceKeyOptional.ifPresent(spaceKey -> query.append(" where searchable.space.key =  :spaceKey"));
        }
        return query.toString();
    }

    private List<Class<? extends Searchable>> getClassesImplementingSearchable() {
        LinkedList<Class<? extends Searchable>> implementingClasses = new LinkedList<Class<? extends Searchable>>();
        try {
            SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)this.hibernateTemplate.getSessionFactory();
            MetamodelImplementor metamodel = Objects.requireNonNull(sessionFactory).getMetamodel();
            for (String entityName : metamodel.getAllEntityNames()) {
                EntityPersister metadata = metamodel.entityPersister(entityName);
                Class clazz = metadata.getMappedClass();
                if (!Searchable.class.isAssignableFrom(clazz) || !HibernateSearchableDao.isConcrete(clazz)) continue;
                implementingClasses.add(clazz);
            }
        }
        catch (PersistenceException e) {
            throw new InfrastructureException("Unable to determine classes implementing Searchable");
        }
        if (log.isDebugEnabled()) {
            log.debug("Classes implementing Searchable: " + implementingClasses);
        }
        return implementingClasses;
    }

    private static boolean isConcrete(Class<? extends Searchable> clazz) {
        return !Modifier.isAbstract(clazz.getModifiers());
    }
}

