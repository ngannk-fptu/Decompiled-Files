/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.SetMultimap
 *  com.google.common.collect.Sets
 *  org.hibernate.Session
 *  org.hibernate.query.NativeQuery
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.impl.security.NeverPermittedContentPermissionSet;
import com.atlassian.confluence.internal.security.persistence.ContentPermissionSetDaoInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateContentPermissionSetDao
extends HibernateObjectDao<ContentPermissionSet>
implements ContentPermissionSetDaoInternal {
    private static final Logger log = LoggerFactory.getLogger(HibernateContentPermissionSetDao.class);

    @Override
    public ContentPermissionSet getById(long id) {
        return (ContentPermissionSet)this.getByClassId(id);
    }

    @Override
    public Map<Long, List<ContentPermissionSet>> getExplicitPermissionSetsFor(Collection<Long> ids) {
        HashMap result = Maps.newHashMap();
        List contentPermissionSets = (List)Objects.requireNonNull((ImmutableList)this.getHibernateTemplate().execute(session -> {
            LinkedHashSet innerContentPermissionSets = Sets.newLinkedHashSet();
            for (List innerIds : Lists.partition((List)Lists.newArrayList((Iterable)ids), (int)500)) {
                Query query = session.createQuery("select cps from ContentEntityObject as content join content.contentPermissionSets as cps join fetch cps.contentPermissions as cp where content.id in (:ids) order by content.id");
                query.setParameterList("ids", (Collection)innerIds);
                innerContentPermissionSets.addAll(query.list());
            }
            return ImmutableList.copyOf((Collection)innerContentPermissionSets);
        }));
        Map<Long, List<ContentPermissionSet>> tempResult = contentPermissionSets.stream().collect(Collectors.groupingBy(permissionSet -> permissionSet.getOwningContent().getId()));
        Iterator<Long> iterator = ids.iterator();
        while (iterator.hasNext()) {
            Long id;
            ArrayList permissionSet2 = tempResult.get(id = iterator.next());
            result.put(id, permissionSet2 == null ? new ArrayList() : permissionSet2);
        }
        return result;
    }

    @Override
    public List<ContentPermissionSet> getInheritedContentPermissionSets(Page page, String type) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query ancestorQuery = session.createQuery("select pageAncestors.id from Page as page left join page.ancestors as pageAncestors where page.id = :pageId");
            ancestorQuery.setParameter("pageId", (Object)page.getId());
            HashSet ancestors = new HashSet(ancestorQuery.list());
            if (page.getParent() != null && !ancestors.contains(page.getParent().getId())) {
                this.logAncestorsTableFailure(page);
                return Collections.singletonList(NeverPermittedContentPermissionSet.buildFrom(type, page));
            }
            if (type != null && !ancestors.isEmpty()) {
                Query cpsQuery = session.createQuery("select cps from ContentPermissionSet as cps where cps.type = :cpsType and cps.owningContent.id in (:ancestorIds)");
                cpsQuery.setParameterList("ancestorIds", ancestors);
                cpsQuery.setParameter("cpsType", (Object)type);
                return cpsQuery.list();
            }
            return Collections.emptyList();
        });
    }

    @Override
    public Map<Long, List<ContentPermissionSet>> getInheritedContentPermissionSets(Collection<Long> pageIds) {
        return (Map)this.getHibernateTemplate().execute(session -> {
            if (pageIds.isEmpty()) {
                return Collections.emptyMap();
            }
            SetMultimap<Long, Long> ancestorsByPageId = HibernateContentPermissionSetDao.getAncestors(session, pageIds);
            if (ancestorsByPageId.isEmpty()) {
                return Collections.emptyMap();
            }
            List<ContentPermissionSet> contentPermissionSets = HibernateContentPermissionSetDao.getContentPermissionSets(session, ancestorsByPageId.values());
            return (Map)pageIds.stream().collect(ImmutableMap.toImmutableMap(pageId -> pageId, pageId -> {
                Set ancestorIds = ancestorsByPageId.get(pageId);
                Predicate<ContentPermissionSet> filter = cps -> ancestorIds.contains(cps.getOwningContent().getId());
                return (List)contentPermissionSets.stream().filter(filter).collect(ImmutableList.toImmutableList());
            }));
        });
    }

    private static List<ContentPermissionSet> getContentPermissionSets(Session session, Collection<Long> pageIds) {
        return session.createQuery("select distinct cps from ContentPermissionSet as cps left join fetch cps.contentPermissions join cps.owningContent where cps.owningContent.id in (:pageIds)", ContentPermissionSet.class).setParameterList("pageIds", pageIds).setHint("hibernate.query.passDistinctThrough", (Object)false).list();
    }

    private static SetMultimap<Long, Long> getAncestors(Session session, Collection<Long> pageIds) {
        Query ancestorQuery = session.createQuery("select page.id, pageAncestors.id from Page as page join page.ancestors as pageAncestors where page.id in (:pageIds)", Object[].class);
        ancestorQuery.setParameter("pageIds", pageIds);
        return (SetMultimap)ancestorQuery.list().stream().collect(Multimaps.toMultimap(row -> Objects.requireNonNull((Long)row[0]), row -> Objects.requireNonNull((Long)row[1]), HashMultimap::create));
    }

    @Override
    public Map<Long, Set<ContentPermissionSet>> getPermissionSets(String spaceKey, List<Long> contentIds) {
        List permissionSets = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            String sql = "select {cps.*}  from CONTENT_PERM_SET {cps}, CONTENT c, SPACES sp where {cps}.CONTENT_ID = c.CONTENTID and c.SPACEID = sp.SPACEID and c.PREVVER is null and sp.SPACEKEY = :spaceKey and {cps}.CONTENT_ID in (:contentIds)";
            NativeQuery query = session.createNativeQuery("select {cps.*}  from CONTENT_PERM_SET {cps}, CONTENT c, SPACES sp where {cps}.CONTENT_ID = c.CONTENTID and c.SPACEID = sp.SPACEID and c.PREVVER is null and sp.SPACEKEY = :spaceKey and {cps}.CONTENT_ID in (:contentIds)").addEntity("cps", ContentPermissionSet.class);
            query.setString("spaceKey", spaceKey);
            query.setParameterList("contentIds", (Collection)contentIds);
            return query.list();
        }));
        return this.groupPermissionByContentId(permissionSets);
    }

    @Override
    public List<Long> getContentIdsWithPermissionSet(String spaceKey) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select page.id from Page as page join page.contentPermissionSets as cps where page.originalVersion is null and page.space.key = :spaceKey");
            query.setParameter("spaceKey", (Object)spaceKey);
            return query.list();
        });
    }

    private Map<Long, Set<ContentPermissionSet>> groupPermissionByContentId(List<ContentPermissionSet> permissionSets) {
        return permissionSets.stream().collect(Collectors.groupingBy(permission -> permission.getOwningContent().getContentId().asLong(), Collectors.toSet()));
    }

    private void logAncestorsTableFailure(Page page) {
        String message = "Detected ancestors table corruption for pageId: {}. Access to this page is blocked for all users as inherited permissions cannot be determined. To resolve this, rebuild the ancestors table. See https://confluence.atlassian.com/display/DOC/Rebuilding+the+Ancestor+Table";
        log.error("Detected ancestors table corruption for pageId: {}. Access to this page is blocked for all users as inherited permissions cannot be determined. To resolve this, rebuild the ancestors table. See https://confluence.atlassian.com/display/DOC/Rebuilding+the+Ancestor+Table", (Object)page.getId());
    }

    @Override
    public Class<ContentPermissionSet> getPersistentClass() {
        return ContentPermissionSet.class;
    }
}

