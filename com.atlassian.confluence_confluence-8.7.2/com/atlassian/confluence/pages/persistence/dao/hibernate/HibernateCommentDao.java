/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.pages.persistence.CommentDaoInternal;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;

public class HibernateCommentDao
extends ConfluenceHibernateObjectDao<Comment>
implements CommentDaoInternal {
    private static final Logger log = LoggerFactory.getLogger(HibernateCommentDao.class);
    private static final int LIMIT_ITEMS_BATCH_QUERY = 1000;

    @Override
    public Class<Comment> getPersistentClass() {
        return Comment.class;
    }

    @Override
    public Comment getById(long id) {
        return this.getByClassId(id);
    }

    @Override
    protected Comment getByClassId(long id) {
        ContentEntityObject ceo = (ContentEntityObject)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
        if (!(ceo instanceof Comment)) {
            return null;
        }
        return (Comment)ceo;
    }

    @Override
    public Iterator getRecentlyUpdatedComments(long spaceId, int maxResults) {
        return (Iterator)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.content_findRecentlyModifiedCommentsBySpace");
            queryObject.setParameter("spaceId", (Object)spaceId);
            queryObject.setCacheable(true);
            if (maxResults > 0) {
                queryObject.setMaxResults(maxResults);
            }
            HibernateCommentDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list().iterator();
        });
    }

    @Override
    public List<Comment> getContainerComments(long containerId, Date since) {
        return this.findNamedQueryStringParams("confluence.content_findCommentsByContainerSinceDate", "containerId", containerId, "date", (Object)since);
    }

    @Override
    public List<Comment> getContainerComments(long containerId, Date since, String ignoreUsername) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(ignoreUsername);
        if (user == null) {
            return this.getContainerComments(containerId, since);
        }
        return this.findNamedQueryStringParams("confluence.content_findCommentsByContainerSinceDateWithoutUser", "containerId", (Object)containerId, "date", (Object)since, "ignoreUser", (Object)user);
    }

    @Override
    public Map<Searchable, Integer> countComments(Collection<? extends Searchable> searchables) {
        Objects.requireNonNull(searchables);
        if (searchables.size() == 0) {
            return Collections.emptyMap();
        }
        HashMap idToSearchableMap = Maps.newHashMap();
        HashMap results = Maps.newHashMap();
        for (Searchable searchable : searchables) {
            idToSearchableMap.put(searchable.getId(), searchable);
            results.put(searchable, 0);
        }
        List hibernateResults = this.findNamedQueryStringParam("confluence.content_countCommentsForMultiple", "contentEntities", Collections2.transform(searchables, Searchable::getId));
        for (Object[] result : hibernateResults) {
            Integer numberOfComments = (Integer)result[1];
            Long contentId = (Long)result[0];
            results.put((Searchable)idToSearchableMap.get(contentId), numberOfComments);
        }
        return ImmutableMap.copyOf((Map)results);
    }

    @Override
    public int countComments(Searchable searchable) {
        Objects.requireNonNull(searchable);
        return DataAccessUtils.intResult((Collection)this.findNamedQueryStringParam("confluence.content_countCommentsForOne", "contentId", searchable.getId()));
    }

    @Override
    public int countAllCommentVersions() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.content_countAllComments").list())));
    }

    @Override
    @Deprecated
    public PageResponse<Comment> getContainerComments(long containerId, LimitedRequest pageRequest, Depth depth, com.google.common.base.Predicate<? super Comment> ... predicates) {
        if (depth == Depth.ALL) {
            throw new UnsupportedOperationException("Call Page.getComments() instead.");
        }
        List comments = this.findNamedQueryStringParams("confluence.content_findTopLevelCommentsByContainer", HibernateObjectDao.Cacheability.CACHEABLE, pageRequest, "containerId", containerId);
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, (List)comments, (Predicate)Predicates.and((Iterable)Lists.asList((Object)Predicates.alwaysTrue(), (Object[])predicates)));
    }

    @Override
    @Deprecated
    public PageResponse<Comment> getChildren(Comment comment, LimitedRequest pageRequest, Depth depth, com.google.common.base.Predicate<? super Comment> ... predicates) {
        if (depth == Depth.ROOT) {
            List comments = this.findNamedQueryStringParams("confluence.comment_getChildren", true, pageRequest, "parentId", comment.getId());
            return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, (List)comments, (Predicate)Predicates.and((Iterable)Lists.asList((Object)Predicates.alwaysTrue(), (Object[])predicates)));
        }
        throw new NotImplementedServiceException("Can only get Comment children at ROOT depth for now.");
    }

    @Override
    public Map<Long, Integer> countUnresolvedComments(@NonNull Collection<Long> containerIds) {
        Assert.notNull(containerIds, (String)"containerIds should not be null");
        Assert.notEmpty(containerIds, (String)"containerIds should not be empty ");
        if (log.isDebugEnabled()) {
            log.debug("Querying database for unresolved comment count: {}", (Object)Arrays.toString(containerIds.toArray()));
        }
        HashMap result = Maps.newHashMap();
        Iterable partitionIdsList = Iterables.partition(containerIds, (int)1000);
        for (List subIdsList : partitionIdsList) {
            this.putUnresolvedCommentCountToMap(subIdsList, result);
        }
        return result;
    }

    private void putUnresolvedCommentCountToMap(List<Long> ids, Map<Long, Integer> map) {
        List queryResult = (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.content_unresolvedCommentCount");
            HibernateCommentDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            queryObject.setParameterList("ids", (Collection)ids);
            return queryObject.list();
        });
        if (queryResult != null) {
            for (Object obj : queryResult) {
                Object[] row = (Object[])obj;
                Long attachmentId = (Long)row[0];
                Integer unresolvedCommentCount = (Integer)row[1];
                map.put(attachmentId, unresolvedCommentCount);
            }
        }
    }
}

