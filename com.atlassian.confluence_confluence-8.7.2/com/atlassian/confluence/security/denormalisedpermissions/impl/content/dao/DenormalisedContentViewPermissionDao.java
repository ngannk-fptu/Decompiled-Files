/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao;

import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.DenormalisedContentViewPermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.SimpleContent;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DenormalisedContentViewPermissionDao {
    private final HibernateTemplate hibernateTemplate;
    private static final int IN_BATCH_SIZE = 1000;

    public DenormalisedContentViewPermissionDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public List<Long> getSimpleContentIdsInRange(Long fromId, Long toId) {
        return (List)this.hibernateTemplate.execute(session -> {
            if (fromId != null && toId != null) {
                Query query = session.createQuery("select sc.id from SimpleContent sc where sc.id > :fromId and sc.id < :toId", Long.class);
                query.setCacheable(false);
                query.setParameter("fromId", (Object)fromId);
                query.setParameter("toId", (Object)toId);
                return query.list();
            }
            if (fromId != null) {
                Query query = session.createQuery("select sc.id from SimpleContent sc where sc.id > :fromId", Long.class);
                query.setCacheable(false);
                query.setParameter("fromId", (Object)fromId);
                return query.list();
            }
            if (toId != null) {
                Query query = session.createQuery("select sc.id from SimpleContent sc where sc.id < :toId", Long.class);
                query.setCacheable(false);
                query.setParameter("toId", (Object)toId);
                return query.list();
            }
            Query query = session.createQuery("select sc.id from SimpleContent sc", Long.class);
            query.setCacheable(false);
            return query.list();
        });
    }

    public Map<Long, Set<Long>> getAllExistingSidsForPages(Collection<Long> pageIdList) {
        if (pageIdList.size() == 0) {
            return Collections.emptyMap();
        }
        return (Map)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select new " + PageIdAndSidId.class.getName() + "(contentToSidMapId.contentId, contentToSidMapId.sidId)   from DenormalisedContentViewPermission where CONTENT_ID in (:contentIdList)";
            Query query = session.createQuery(hqlQuery, PageIdAndSidId.class);
            query.setParameter("contentIdList", (Object)pageIdList);
            query.setCacheable(false);
            return query.stream().collect(Collectors.groupingBy(PageIdAndSidId::getPageId, Collectors.mapping(PageIdAndSidId::getSidId, Collectors.toSet())));
        });
    }

    public void removeRecords(Long pageId, Set<Long> sidIds) {
        if (sidIds.size() == 0) {
            return;
        }
        this.hibernateTemplate.execute(session -> {
            String hqlQuery = "delete from DenormalisedContentViewPermission where CONTENT_ID = :pageId and SID_ID in (:sidIds)";
            Query query = session.createQuery(hqlQuery);
            query.setParameter("pageId", (Object)pageId);
            query.setParameter("sidIds", (Object)sidIds);
            query.setCacheable(false);
            query.executeUpdate();
            return null;
        });
    }

    public void removeAllDenormalisedRecordsForPages(Set<Long> pageIdsToRemove) {
        if (pageIdsToRemove.size() == 0) {
            return;
        }
        for (List pageIdBatch : Lists.partition(new ArrayList<Long>(pageIdsToRemove), (int)1000)) {
            this.hibernateTemplate.execute(session -> {
                String hqlQuery = "delete from DenormalisedContentViewPermission where CONTENT_ID in (:pageIdSet)";
                Query query = session.createQuery(hqlQuery);
                query.setParameter("pageIdSet", (Object)pageIdBatch);
                query.setCacheable(false);
                query.executeUpdate();
                return null;
            });
        }
    }

    public void removeAllSimpleContentRecordsForPages(Set<Long> pageIdsToRemove) {
        if (pageIdsToRemove.size() == 0) {
            return;
        }
        for (List pageIdBatch : Lists.partition(new ArrayList<Long>(pageIdsToRemove), (int)1000)) {
            this.hibernateTemplate.execute(session -> {
                String hqlQuery = "delete from SimpleContent where ID in (:pageIdSet)";
                Query query = session.createQuery(hqlQuery);
                query.setParameter("pageIdSet", (Object)pageIdBatch);
                query.setCacheable(false);
                query.executeUpdate();
                return null;
            });
        }
    }

    public void add(DenormalisedContentViewPermission denormalisedContentViewPermission) {
        this.hibernateTemplate.save((Object)denormalisedContentViewPermission);
    }

    public void add(List<DenormalisedContentViewPermission> permissionsToAdd) {
        permissionsToAdd.forEach(permission -> this.hibernateTemplate.save(permission));
    }

    public List<SimpleContent> getVisiblePagesFromSpace(long spaceId, Collection<Long> sidIdList, boolean permissionExempt) {
        if (sidIdList.size() == 0) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = permissionExempt ? "select page from SimpleContent page where page.spaceId = :spaceId and status = :status" : "select page from SimpleContent page where page.id in (select p.contentToSidMapId.contentId from DenormalisedContentViewPermission p where SID_ID in (:sidIdList)) and page.spaceId = :spaceId and status = :status";
            Query query = session.createQuery(hqlQuery, SimpleContent.class);
            query.setParameter("spaceId", (Object)spaceId);
            query.setParameter("status", (Object)SimpleContent.ContentStatus.CURRENT);
            if (!permissionExempt) {
                query.setParameter("sidIdList", (Object)sidIdList);
            }
            query.setCacheable(false);
            return query.list();
        });
    }

    public Map<Long, List<SimpleContent>> getAllVisibleChildren(Collection<Long> parentIds, Collection<Long> sidIdList, boolean permissionExempt) {
        if (sidIdList.isEmpty() || parentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return (Map)this.hibernateTemplate.execute(session -> {
            String hqlQuery = permissionExempt ? "select page from SimpleContent page where page.parentId in (:parentIds) and status = :status" : "select page from SimpleContent page where page.id in (select p.contentToSidMapId.contentId from DenormalisedContentViewPermission p where SID_ID in (:sidIdList)) and page.parentId in (:parentIds) and status = :status";
            Query query = session.createQuery(hqlQuery, SimpleContent.class);
            query.setParameter("parentIds", (Object)parentIds);
            if (!permissionExempt) {
                query.setParameter("sidIdList", (Object)sidIdList);
            }
            query.setParameter("status", (Object)SimpleContent.ContentStatus.CURRENT);
            query.setCacheable(false);
            return query.list().stream().collect(Collectors.groupingBy(SimpleContent::getParentId, Collectors.toList()));
        });
    }

    public List<SimpleContent> getAllVisibleTopLevelPages(long spaceId, Set<Long> sids, boolean permissionExempt) {
        if (sids.isEmpty()) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = permissionExempt ? "select page from SimpleContent page where page.parentId is null and spaceId = :spaceId and status = :status" : "select page from SimpleContent page where page.id in (select p.contentToSidMapId.contentId from DenormalisedContentViewPermission p where SID_ID in (:sids)) and page.parentId is null and spaceId = :spaceId and status = :status";
            Query query = session.createQuery(hqlQuery, SimpleContent.class);
            if (!permissionExempt) {
                query.setParameter("sids", (Object)sids);
            }
            query.setParameter("spaceId", (Object)spaceId);
            query.setParameter("status", (Object)SimpleContent.ContentStatus.CURRENT);
            query.setCacheable(false);
            return query.list();
        });
    }

    public List<SimpleContent> getDenormalisedContentList(Set<Long> pageIdSet) {
        if (pageIdSet.size() == 0) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "from SimpleContent where id in (:idSet)";
            Query query = session.createQuery(hqlQuery, SimpleContent.class);
            query.setParameter("idSet", (Object)pageIdSet);
            query.setCacheable(false);
            return query.list();
        });
    }

    public void saveSimpleContent(SimpleContent simpleContent) {
        this.hibernateTemplate.save((Object)simpleContent);
    }

    public Set<Long> getVisiblePages(Set<Long> sids, Set<Long> pageIds) {
        if (pageIds.size() == 0 || sids.size() == 0) {
            return Collections.emptySet();
        }
        List<SimpleContent> targetVisibleList = this.getVisiblePagesIgnoreInheritedPermissions(sids, pageIds);
        Set<Long> finalSetOfCompletelyVisiblePageIds = targetVisibleList.stream().filter(page -> page.getParentId() == null).map(SimpleContent::getId).collect(Collectors.toSet());
        Map<Long, List<SimpleContent>> pagesWithAncestors = targetVisibleList.stream().filter(page -> page.getParentId() != null).collect(Collectors.groupingBy(SimpleContent::getParentId, Collectors.mapping(element -> element, Collectors.toList())));
        int depthLevelLimit = 500;
        while (depthLevelLimit-- > 0) {
            Set<Long> parentIdsWaitingForCheckingTheirVisibility = pagesWithAncestors.keySet();
            if (parentIdsWaitingForCheckingTheirVisibility.size() == 0) {
                return finalSetOfCompletelyVisiblePageIds;
            }
            List<SimpleContent> visibleParents = this.getVisiblePagesIgnoreInheritedPermissions(sids, parentIdsWaitingForCheckingTheirVisibility);
            Map<Long, SimpleContent> visibleParentIdMap = visibleParents.stream().collect(Collectors.toMap(SimpleContent::getId, content -> content));
            finalSetOfCompletelyVisiblePageIds.addAll(this.getPagesReachedTheirAncestors(pagesWithAncestors, visibleParentIdMap));
            pagesWithAncestors = this.replaceParentsWithGrandParents(pagesWithAncestors, visibleParentIdMap);
        }
        throw new IllegalStateException("getVisiblePages was not able to find all pages in " + depthLevelLimit + " steps. A bug?");
    }

    private Set<Long> getPagesReachedTheirAncestors(Map<Long, List<SimpleContent>> pagesWithAncestors, Map<Long, SimpleContent> visibleParentIdMap) {
        ArrayList pagesReachedTopLevelPages = new ArrayList();
        pagesWithAncestors.forEach((key, value) -> {
            long parentId = key;
            SimpleContent grandParent = (SimpleContent)visibleParentIdMap.get(parentId);
            if (grandParent != null && grandParent.getParentId() == null) {
                pagesReachedTopLevelPages.addAll(value);
            }
        });
        return pagesReachedTopLevelPages.stream().map(SimpleContent::getId).collect(Collectors.toSet());
    }

    private Map<Long, List<SimpleContent>> replaceParentsWithGrandParents(Map<Long, List<SimpleContent>> pagesWithAncestors, Map<Long, SimpleContent> visibleParentIdMap) {
        HashMap<Long, List<SimpleContent>> newPagesWithAncestors = new HashMap<Long, List<SimpleContent>>();
        pagesWithAncestors.entrySet().stream().forEach(entry -> {
            Long grandParentId;
            Long parentId = (Long)entry.getKey();
            SimpleContent parentPage = (SimpleContent)visibleParentIdMap.get(parentId);
            Long l = grandParentId = parentPage != null ? parentPage.getParentId() : null;
            if (grandParentId != null) {
                newPagesWithAncestors.merge(grandParentId, (List)entry.getValue(), (pageList1, pageList2) -> {
                    ArrayList mergedList = new ArrayList(pageList1);
                    mergedList.addAll(pageList2);
                    return mergedList;
                });
            }
        });
        return newPagesWithAncestors;
    }

    private List<SimpleContent> getVisiblePagesIgnoreInheritedPermissions(Set<Long> sids, Set<Long> pageIds) {
        if (pageIds.size() == 0 || sids.size() == 0) {
            return Collections.emptyList();
        }
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select page from SimpleContent page where page.id in (select p.contentToSidMapId.contentId from DenormalisedContentViewPermission p where SID_ID in (:sids)) and page.id in (:pageIds)";
            Query query = session.createQuery(hqlQuery, SimpleContent.class);
            query.setParameter("sids", (Object)sids);
            query.setParameter("pageIds", (Object)pageIds);
            query.setCacheable(false);
            return query.list();
        });
    }

    private static class PageIdAndSidId {
        private final long pageId;
        private final long sidId;

        public PageIdAndSidId(long pageId, long sidId) {
            this.pageId = pageId;
            this.sidId = sidId;
        }

        public long getPageId() {
            return this.pageId;
        }

        public long getSidId() {
            return this.sidId;
        }
    }
}

