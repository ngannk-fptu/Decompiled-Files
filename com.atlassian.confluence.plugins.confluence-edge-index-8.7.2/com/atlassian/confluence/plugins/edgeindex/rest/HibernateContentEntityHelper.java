/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  javax.persistence.EntityManager
 *  javax.persistence.FlushModeType
 *  javax.persistence.Query
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex.rest;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.edgeindex.rest.ContentEntityHelper;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={ContentEntityHelper.class})
public class HibernateContentEntityHelper
implements ContentEntityHelper {
    private final EntityManagerProvider entityManagerProvider;

    @Autowired
    public HibernateContentEntityHelper(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<ContentEntityObject> getContentEntities(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        FlushModeType oldFlushMode = entityManager.getFlushMode();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        LinkedList<ContentEntityObject> orderedContentEntities = new LinkedList<ContentEntityObject>();
        try {
            Query query = entityManager.createQuery("select ceo from ContentEntityObject ceo where ceo.id in (:contentIds)");
            query.setParameter("contentIds", contentIds);
            query.setHint("org.hibernate.cacheable", (Object)Boolean.TRUE);
            List queryResults = query.getResultList();
            HashMap<Long, ContentEntityObject> idToEntityMap = new HashMap<Long, ContentEntityObject>();
            for (ContentEntityObject queryResult : queryResults) {
                idToEntityMap.put(queryResult.getId(), queryResult);
            }
            for (Long contentId : contentIds) {
                ContentEntityObject contentEntity = (ContentEntityObject)idToEntityMap.get(contentId);
                if (contentEntity == null) continue;
                orderedContentEntities.add(contentEntity);
            }
        }
        finally {
            entityManager.setFlushMode(oldFlushMode);
        }
        return orderedContentEntities;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Long, Integer> getCommentCounts(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        FlushModeType oldFlushMode = entityManager.getFlushMode();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        try {
            Query query = entityManager.createQuery("select ceo.id, count(comments) from ContentEntityObject as ceo left join ceo.comments as comments where ceo.id in (:contentIds) group by ceo.id");
            query.setParameter("contentIds", contentIds);
            query.setHint("org.hibernate.cacheable", (Object)Boolean.TRUE);
            HashMap<Long, Integer> result = new HashMap<Long, Integer>();
            for (Object[] data : query.getResultList()) {
                result.put((Long)data[0], ((Number)data[1]).intValue());
            }
            HashMap<Long, Integer> hashMap = result;
            return hashMap;
        }
        finally {
            entityManager.setFlushMode(oldFlushMode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Long, Integer> getNestedCommentCounts(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        FlushModeType oldFlushMode = entityManager.getFlushMode();
        entityManager.setFlushMode(FlushModeType.COMMIT);
        try {
            Query query = entityManager.createQuery("select comment.id, count(comments) from Comment as comment left join comment.children as comments where comment.id in (:commentIds) group by comment.id");
            query.setParameter("commentIds", commentIds);
            query.setHint("org.hibernate.cacheable", (Object)Boolean.TRUE);
            HashMap<Long, Integer> result = new HashMap<Long, Integer>();
            for (Object[] data : query.getResultList()) {
                result.put((Long)data[0], ((Number)data[1]).intValue());
            }
            HashMap<Long, Integer> hashMap = result;
            return hashMap;
        }
        finally {
            entityManager.setFlushMode(oldFlushMode);
        }
    }
}

