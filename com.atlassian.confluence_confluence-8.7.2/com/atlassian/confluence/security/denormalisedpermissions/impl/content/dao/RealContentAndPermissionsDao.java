/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.time.StopWatch
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao;

import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class RealContentAndPermissionsDao {
    private static final Logger log = LoggerFactory.getLogger(RealContentAndPermissionsDao.class);
    private final HibernateTemplate hibernateTemplate;

    public RealContentAndPermissionsDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public List<SimpleContent> getSimplePageListWithIdGreaterThen(Long id, int limit) {
        return (List)this.hibernateTemplate.execute(session -> {
            Query query;
            if (id != null) {
                String hqlQuery = "select new " + SimpleContent.class.getName() + "(p.id, p.space.id, p.parent.id, p.title, p.creationDate, p.lastModificationDate, p.contentStatus, p.position)  from Page p where p.originalVersion is null and p.id > :id order by id";
                query = session.createQuery(hqlQuery, SimpleContent.class);
                query.setParameter("id", (Object)id);
            } else {
                String hqlQuery = "select new " + SimpleContent.class.getName() + "(p.id, p.space.id, p.parent.id, p.title, p.creationDate, p.lastModificationDate, p.contentStatus, p.position)  from Page p where p.originalVersion is null order by id";
                query = session.createQuery(hqlQuery, SimpleContent.class);
            }
            query.setCacheable(false);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    public Map<Long, Long> getContentPermissionSets(Collection<Long> pageIdSet, String contentPermSetType) {
        if (pageIdSet.size() == 0) {
            return Collections.emptyMap();
        }
        return (Map)this.hibernateTemplate.execute(session -> {
            StopWatch stopWatch = StopWatch.createStarted();
            String hqlQuery = "select new " + SimpleContentPermissionSet.class.getName() + "(s.id, s.owningContent.id)  from ContentPermissionSet s where s.type = :type and s.owningContent.id in (:contentIds)";
            Query query = session.createQuery(hqlQuery, SimpleContentPermissionSet.class);
            query.setParameter("type", (Object)contentPermSetType);
            query.setParameter("contentIds", (Object)pageIdSet);
            query.setCacheable(false);
            List plainRecords = query.list();
            Map<Long, Long> contentToPermissionSetMap = plainRecords.stream().collect(Collectors.toMap(SimpleContentPermissionSet::getContentPermSetId, SimpleContentPermissionSet::getContentId));
            log.trace("Content permissions ({}) sets for {} pages were retrieved in {} ms", new Object[]{contentToPermissionSetMap.size(), pageIdSet.size(), stopWatch.getTime()});
            return contentToPermissionSetMap;
        });
    }

    public Map<Long, Long> getContentPermissionSetIdsForContentPermissionIds(Set<Long> contentPermSetIds) {
        if (contentPermSetIds.size() == 0) {
            return Collections.emptyMap();
        }
        return (Map)this.hibernateTemplate.execute(session -> {
            StopWatch stopWatch = StopWatch.createStarted();
            String hqlQuery = "select new " + SimpleContentPermissionSet.class.getName() + "(s.id, s.owningContent.id)  from ContentPermissionSet s where s.id in (:cpsIds)";
            Query query = session.createQuery(hqlQuery, SimpleContentPermissionSet.class);
            query.setParameter("cpsIds", (Object)contentPermSetIds);
            query.setCacheable(false);
            List plainRecords = query.list();
            Map<Long, Long> contentToPermissionSetMap = plainRecords.stream().collect(Collectors.toMap(SimpleContentPermissionSet::getContentPermSetId, SimpleContentPermissionSet::getContentId));
            log.trace("Content permissions ({}) sets were retrieved in {} ms", (Object)plainRecords, (Object)stopWatch.getTime());
            return contentToPermissionSetMap;
        });
    }

    public List<SimpleContent> getSimpleContentList(Collection<Long> contentIds) {
        if (contentIds.size() == 0) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select new " + SimpleContent.class.getName() + "(p.id, p.space.id, p.parent.id, p.title, p.creationDate, p.lastModificationDate, p.contentStatus, p.position)  from Page p where p.id in (:ids)";
            Query query = session.createQuery(hqlQuery, SimpleContent.class);
            query.setParameter("ids", (Object)contentIds);
            query.setCacheable(false);
            return query.list();
        });
    }

    public Map<Long, List<SimpleContentPermission>> getSimpleContentPermissions(Collection<Long> contentPermSetIds) {
        if (contentPermSetIds.size() == 0) {
            return Collections.emptyMap();
        }
        return (Map)this.hibernateTemplate.execute(session -> {
            StopWatch stopWatch = StopWatch.createStarted();
            String hqlQuery = "select new " + SimpleContentPermission.class.getName() + "(p.id, p.type, p.userSubject.key, p.groupName, p.owningSet.id)  from ContentPermission p where p.owningSet.id in :contentPermSetIds";
            Query query = session.createQuery(hqlQuery, SimpleContentPermission.class);
            query.setParameter("contentPermSetIds", (Object)contentPermSetIds);
            query.setCacheable(false);
            List plainRecords = query.list();
            Map permissionSetToPermissionsMap = plainRecords.stream().collect(Collectors.groupingBy(SimpleContentPermission::getCpsId, Collectors.toList()));
            log.trace("Content permissions were retrieved in {} ms. Requested {} sets, found {} permissions for {} sets", new Object[]{stopWatch.getTime(), contentPermSetIds.size(), plainRecords.size(), permissionSetToPermissionsMap.size()});
            return permissionSetToPermissionsMap;
        });
    }

    public static class SimpleContentPermission {
        private long id;
        private String type;
        private UserKey userKey;
        private String groupName;
        private long cpsId;

        public SimpleContentPermission(long id, String type, UserKey userKey, String groupName, long cpsId) {
            this.id = id;
            this.type = type;
            this.userKey = userKey;
            this.groupName = groupName;
            this.cpsId = cpsId;
        }

        public long getId() {
            return this.id;
        }

        public String getType() {
            return this.type;
        }

        public String getUserName() {
            return this.userKey != null ? this.userKey.getStringValue() : null;
        }

        public String getGroupName() {
            return this.groupName;
        }

        public long getCpsId() {
            return this.cpsId;
        }
    }

    public static class SimpleContentPermissionSet {
        private long contentId;
        private long contentPermSetId;

        public SimpleContentPermissionSet(long contentPermSetId, long contentId) {
            this.contentPermSetId = contentPermSetId;
            this.contentId = contentId;
        }

        public long getContentId() {
            return this.contentId;
        }

        public long getContentPermSetId() {
            return this.contentPermSetId;
        }
    }
}

