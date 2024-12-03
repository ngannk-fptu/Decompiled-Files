/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.db.JDBCUtils
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Pair
 *  javax.persistence.PersistenceException
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.NativeQuery
 *  org.hibernate.query.Query
 *  org.hibernate.transform.Transformers
 *  org.hibernate.type.LongType
 *  org.hibernate.type.StringType
 *  org.hibernate.type.TimestampType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.internal.persistence.hibernate;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.core.AbstractVersionedEntityObject;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContributionStatus;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.VersionHistorySummaryCollaborator;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.VersionedHibernateObjectDao;
import com.atlassian.confluence.impl.content.DefaultContentEntityManager;
import com.atlassian.confluence.impl.contributors.VersionContributorSummary;
import com.atlassian.confluence.internal.persistence.ContentEntityObjectDaoInternal;
import com.atlassian.confluence.internal.relations.dao.User2ContentRelationEntity;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.db.JDBCUtils;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.atlassian.fugue.Pair;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;

public abstract class AbstractContentEntityObjectHibernateDao<T extends ContentEntityObject>
extends VersionedHibernateObjectDao<T>
implements ContentEntityObjectDaoInternal<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractContentEntityObjectHibernateDao.class);
    private final RetentionFeatureChecker retentionFeatureChecker;

    public AbstractContentEntityObjectHibernateDao(RetentionFeatureChecker retentionFeatureChecker) {
        this.retentionFeatureChecker = Objects.requireNonNull(retentionFeatureChecker);
    }

    public AbstractContentEntityObjectHibernateDao() {
        this.retentionFeatureChecker = null;
    }

    @Override
    public T getById(long id) {
        return (T)this.getByClassId(id);
    }

    @Override
    protected T getByClassId(long id) {
        if (id == 0L) {
            return null;
        }
        ContentEntityObject ceo = (ContentEntityObject)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
        if (this.getPersistentClass().isInstance(ceo)) {
            return (T)((ContentEntityObject)this.getPersistentClass().cast(ceo));
        }
        return null;
    }

    @Override
    public List<ContentEntityObject> getContentAuthoredByUser(String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.content_findContentAuthoredByUser", "user", user, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public Iterator<SpaceContentEntityObject> getAllCurrentEntities() {
        return (Iterator)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findAllCurrentEntities", SpaceContentEntityObject.class);
            queryObject.setCacheable(true);
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.iterate();
        });
    }

    @Override
    public Iterator<ContentEntityObject> getRecentlyAddedEntities(String spaceKey, int maxResults) {
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return (Iterator)this.getHibernateTemplate().execute(session -> {
                Query queryObject = session.createNamedQuery("confluence.content_findRecentlyAddedBySpace", ContentEntityObject.class);
                queryObject.setParameter("spaceKey", (Object)spaceKey.toLowerCase());
                queryObject.setCacheable(true);
                if (maxResults > 0) {
                    queryObject.setMaxResults(maxResults);
                }
                AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
                return queryObject.list().iterator();
            });
        }
        return (Iterator)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findRecentlyAdded", ContentEntityObject.class);
            queryObject.setCacheable(true);
            if (maxResults > 0) {
                queryObject.setMaxResults(maxResults);
            }
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.iterate();
        });
    }

    @Override
    public Iterator<ContentEntityObject> getRecentlyModifiedEntities(int maxResults) {
        return (Iterator)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findRecentlyModified", ContentEntityObject.class);
            queryObject.setCacheable(true);
            if (maxResults > 0) {
                queryObject.setMaxResults(maxResults);
            }
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.iterate();
        });
    }

    @Override
    public Iterator<SpaceContentEntityObject> getRecentlyModifiedEntities(String spaceKey, int maxResults) {
        return (Iterator)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findRecentlyModifiedBySpace", SpaceContentEntityObject.class);
            queryObject.setParameter("spaceKey", (Object)spaceKey.toLowerCase());
            queryObject.setCacheable(true);
            if (maxResults > 0) {
                queryObject.setMaxResults(maxResults);
            }
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list().iterator();
        });
    }

    @Override
    public Iterator<ContentEntityObject> getRecentlyModifiedEntitiesForUser(String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            return Collections.emptyIterator();
        }
        return (Iterator)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findRecentlyModifiedByUser", ContentEntityObject.class);
            queryObject.setCacheable(true);
            queryObject.setParameter("user", (Object)user);
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.iterate();
        });
    }

    @Override
    public ContentEntityObject getFirstVersionBefore(long originalVersionContentId, int version) {
        return (ContentEntityObject)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findPreviousVersion", ContentEntityObject.class);
            queryObject.setParameter("originalVersionId", (Object)originalVersionContentId);
            queryObject.setParameter("version", (Object)version);
            queryObject.setCacheable(true);
            queryObject.setMaxResults(1);
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return (ContentEntityObject)queryObject.uniqueResult();
        });
    }

    @Override
    public ContentEntityObject getFirstVersionAfter(long originalVersionContentId, int version) {
        return (ContentEntityObject)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findNextVersion", ContentEntityObject.class);
            queryObject.setParameter("originalVersionId", (Object)originalVersionContentId);
            queryObject.setParameter("version", (Object)version);
            queryObject.setCacheable(true);
            queryObject.setMaxResults(1);
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return (ContentEntityObject)queryObject.uniqueResult();
        });
    }

    @Override
    public ContentEntityObject getVersion(long originalVersionContentId, int version) {
        return (ContentEntityObject)this.findSingleObject(this.findNamedQueryStringParams("confluence.content_findByVersion", "originalVersionId", originalVersionContentId, "version", (Object)version, HibernateObjectDao.Cacheability.CACHEABLE));
    }

    @Override
    public List<ContentEntityObject> getRecentlyModifiedForChangeDigest(Date fromDate) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findRecentlyModifiedForChangeDigest", ContentEntityObject.class);
            queryObject.setParameter("fromDate", (Object)fromDate, (Type)TimestampType.INSTANCE);
            AbstractContentEntityObjectHibernateDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        });
    }

    @Override
    public List<T> getLastEditedVersionsOf(T content) {
        if (content == null) {
            throw new IllegalArgumentException("content is required.");
        }
        if (!((AbstractVersionedEntityObject)content).isLatestVersion()) {
            throw new IllegalArgumentException("Content must be latest version. " + content);
        }
        return this.findNamedQueryStringParam("confluence.content_getLastEditedVersionsOfContent", "latestContentId", content.getId(), HibernateObjectDao.Cacheability.CACHEABLE).stream().filter(ContentEntityObject::isCurrent).collect(Collectors.toList());
    }

    @Override
    public List<ContentEntityObject> getTrashedContent(String spaceKey) {
        return this.findNamedQueryStringParam("confluence.content_findTrashedContent", "spaceKey", spaceKey.toLowerCase());
    }

    @Override
    public List<ContentEntityObject> getTrashedContents(String spaceKey, int offset, int limit) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createNamedQuery("confluence.content_findTrashedContent", ContentEntityObject.class);
            query.setParameter("spaceKey", (Object)spaceKey.toLowerCase());
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            return query.list();
        });
    }

    @Override
    public PageResponse<ContentEntityObject> getTrashedContents(String spaceKey, LimitedRequest pageRequest, Predicate<? super ContentEntityObject> filter) {
        Function<LimitedRequest, List<T>> pageSearch = request -> {
            ContentCursor contentCursor = (ContentCursor)request.getCursor();
            if (contentCursor.getContentId() == null) {
                String queryName = contentCursor.isReverse() ? "confluence.content_findTrashedContentByCursorFirstReverse" : "confluence.content_findTrashedContentByCursorFirstForward";
                return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, (LimitedRequest)request, (Object)"spaceKey", (Object)spaceKey.toLowerCase());
            }
            String queryName = contentCursor.isReverse() ? "confluence.content_findTrashedContentByCursorReverse" : "confluence.content_findTrashedContentByCursorForward";
            return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, (LimitedRequest)request, (Object)"spaceKey", (Object)spaceKey.toLowerCase(), (Object)"contentid", (Object)contentCursor.getContentId());
        };
        return this.getPagesByCursor(pageSearch, pageRequest, filter);
    }

    protected <T extends ConfluenceEntityObject> PageResponse<T> getPagesByCursor(Function<LimitedRequest, List<T>> searchPages, LimitedRequest originalRequest, Predicate<? super T> filter) {
        List<T> pages = searchPages.apply(originalRequest);
        return DefaultContentEntityManager.filteredResponseWithCursor(originalRequest, filter, pages);
    }

    @Override
    public List<SpaceContentEntityObject> getTrashedEntities(long contentIdOffset, int limit) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query trashQuery = session.createNamedQuery("confluence.content_findAllTrashedContent", SpaceContentEntityObject.class);
            trashQuery.setParameter("contentIdOffset", (Object)contentIdOffset);
            trashQuery.setMaxResults(limit);
            return trashQuery.list();
        });
    }

    @Override
    public List<SpaceContentEntityObject> findContentBySpaceIdAndStatus(long spaceId, String status, int offset, int count) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createNamedQuery("confluence.content_findBySpaceKeyAndStatus", SpaceContentEntityObject.class);
            queryObject.setParameter("spaceId", (Object)spaceId, (Type)LongType.INSTANCE);
            queryObject.setParameter("contentStatus", (Object)status, (Type)StringType.INSTANCE);
            queryObject.setFirstResult(offset);
            queryObject.setMaxResults(count);
            return queryObject.list();
        });
    }

    @Override
    @Deprecated
    public PageResponse<SpaceContentEntityObject> findContentBySpaceIdAndStatus(long spaceId, String status, LimitedRequest limitedRequest, com.google.common.base.Predicate<? super SpaceContentEntityObject> predicate) {
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)this.findNamedQueryStringParams("confluence.content_findBySpaceKeyAndStatus", false, limitedRequest, "spaceId", spaceId, "contentStatus", status), predicate);
    }

    @Override
    public int countContentBySpaceIdAndStatus(long spaceId, String status) {
        return DataAccessUtils.intResult((Collection)this.findNamedQueryStringParams("confluence.content_countBySpaceKeyAndStatus", "spaceId", spaceId, "contentStatus", (Object)status));
    }

    @Override
    public String getObjectType(long id) {
        String string;
        ResultSet rs;
        PreparedStatement s;
        block5: {
            s = null;
            rs = null;
            Connection conn = ((SessionImplementor)this.getSessionFactory().getCurrentSession()).connection();
            s = conn.prepareStatement("select CONTENTTYPE from CONTENT where CONTENTID = ?");
            s.setLong(1, id);
            rs = s.executeQuery();
            if (!rs.next()) break block5;
            String string2 = rs.getString(1);
            JDBCUtils.close((ResultSet)rs);
            JDBCUtils.close((Statement)s);
            return string2;
        }
        try {
            string = null;
        }
        catch (SQLException | PersistenceException e) {
            try {
                throw new InfrastructureException("Error finding type of content with id " + id, e);
            }
            catch (Throwable throwable) {
                JDBCUtils.close(rs);
                JDBCUtils.close(s);
                throw throwable;
            }
        }
        JDBCUtils.close((ResultSet)rs);
        JDBCUtils.close((Statement)s);
        return string;
    }

    @Override
    public Map<Long, ContentEntityObject> getVersionsLastEditedByUser(Collection<Long> contentIds, UserKey userKey) {
        if (contentIds.size() == 0 || userKey == null) {
            return Collections.emptyMap();
        }
        List results = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            NativeQuery query = session.createNativeQuery("select {lc.*} from CONTENT {lc} left join CONTENT c    on coalesce({lc}.PREVVER, {lc}.CONTENTID) = coalesce(c.PREVVER, c.CONTENTID) and {lc}.LASTMODIFIER = c.LASTMODIFIER and    {lc}.VERSION < c.VERSION and c.CONTENT_STATUS = :currentStatus  where coalesce(c.PREVVER, c.CONTENTID) is null  and {lc}.LASTMODIFIER = :userKey  and coalesce({lc}.PREVVER, {lc}.CONTENTID) in (:contentIds)  and {lc}.CONTENT_STATUS = :currentStatus  order by {lc}.LASTMODDATE desc").addEntity("lc", ContentEntityObject.class);
            query.setCacheable(true);
            query.setParameter("userKey", (Object)userKey.toString(), (Type)StringType.INSTANCE);
            query.setParameterList("contentIds", contentIds);
            query.setParameter("currentStatus", (Object)"current", (Type)StringType.INSTANCE);
            return query.list();
        }));
        return Maps.uniqueIndex((Iterable)results, ContentEntityObject::getLatestVersionId);
    }

    @Override
    @Deprecated
    public Map<Long, ContentEntityObject> getVersionsLastEditedByUserNew(Collection<Long> contentIds, UserKey userKey) {
        if (contentIds.size() == 0 || userKey == null) {
            return Collections.emptyMap();
        }
        List results = Objects.requireNonNull((List)this.getHibernateTemplate().execute(session -> {
            String sqlString = "SELECT abstract_page.* FROM (         SELECT lc.CONTENTID AS CONTENTID,         MAX(content_union.LASTMODDATE) AS LASTMODDATE         from (SELECT  c.LASTMODDATE as LASTMODDATE,                       c.CONTENTID as CONTENTID               FROM CONTENT c                       WHERE c.LASTMODIFIER = :userKey                       AND c.CONTENT_STATUS IN ('current')                       AND c.CONTENTTYPE in :contentTypes         ) content_union         LEFT JOIN CONTENT lc         ON lc.CONTENTID = content_union.CONTENTID         GROUP BY lc.CONTENTID ) r LEFT JOIN CONTENT abstract_page ON abstract_page.CONTENTID = r.CONTENTID WHERE (abstract_page.CONTENTID in :contentIds OR abstract_page.PREVVER in :contentIds)";
            NativeQuery query = session.createNativeQuery("SELECT abstract_page.* FROM (         SELECT lc.CONTENTID AS CONTENTID,         MAX(content_union.LASTMODDATE) AS LASTMODDATE         from (SELECT  c.LASTMODDATE as LASTMODDATE,                       c.CONTENTID as CONTENTID               FROM CONTENT c                       WHERE c.LASTMODIFIER = :userKey                       AND c.CONTENT_STATUS IN ('current')                       AND c.CONTENTTYPE in :contentTypes         ) content_union         LEFT JOIN CONTENT lc         ON lc.CONTENTID = content_union.CONTENTID         GROUP BY lc.CONTENTID ) r LEFT JOIN CONTENT abstract_page ON abstract_page.CONTENTID = r.CONTENTID WHERE (abstract_page.CONTENTID in :contentIds OR abstract_page.PREVVER in :contentIds)").addEntity("abstract_page", AbstractPage.class);
            query.setCacheable(false);
            query.setParameter("userKey", (Object)userKey.toString(), (Type)StringType.INSTANCE);
            query.setParameterList("contentIds", contentIds);
            query.setParameterList("contentTypes", (Collection)ImmutableList.of((Object)"PAGE", (Object)"BLOGPOST"));
            return query.list();
        }));
        return results.stream().collect(Collectors.toMap(ContentEntityObject::getLatestVersionId, Function.identity(), (pageOne, pageTwo) -> {
            Date pageTwoLastModDate;
            Date pageOneLastModDate = pageOne.getLastModificationDate();
            int result = pageOneLastModDate.compareTo(pageTwoLastModDate = pageTwo.getLastModificationDate());
            if (result > 0) {
                return pageOne;
            }
            if (result < 0) {
                return pageTwo;
            }
            log.debug("timestamp collision on pages {} and {}. Choosing the second", pageOne, pageTwo);
            return pageTwo;
        }));
    }

    @Override
    public PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUser(UserKey userKey, LimitedRequest limitedRequest) {
        List results = (List)this.getHibernateTemplate().execute(session -> {
            NativeQuery query = session.createNativeQuery("select {lc.*} from CONTENT {lc} left join CONTENT c    on coalesce({lc}.PREVVER, {lc}.CONTENTID) = coalesce(c.PREVVER, c.CONTENTID) and {lc}.LASTMODIFIER = c.LASTMODIFIER and    {lc}.VERSION < c.VERSION and c.CONTENT_STATUS = :currentStatus  where coalesce(c.PREVVER, c.CONTENTID) is null  and {lc}.CONTENT_STATUS = :currentStatus  and {lc}.LASTMODIFIER " + (userKey == null ? "is null" : "= :userKey") + "  and {lc}.CONTENTTYPE in (:contentTypes)  order by {lc}.LASTMODDATE desc, {lc}.CONTENTID").addEntity("lc", AbstractPage.class);
            query.setCacheable(false);
            if (userKey != null) {
                query.setParameter("userKey", (Object)userKey.toString(), (Type)StringType.INSTANCE);
            }
            query.setParameterList("contentTypes", (Collection)ImmutableList.of((Object)"PAGE", (Object)"BLOGPOST"));
            query.setParameter("currentStatus", (Object)"current", (Type)StringType.INSTANCE);
            query.setMaxResults(limitedRequest.getLimit() + 1);
            query.setFirstResult(limitedRequest.getStart());
            return query.list();
        });
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, (List)results, null);
    }

    @Override
    public PageResponse<AbstractPage> getPageAndBlogPostsVersionsLastEditedByUserIncludingDrafts(UserKey userKey, LimitedRequest limitedRequest) {
        if (userKey == null) {
            return PageResponseImpl.empty((boolean)false);
        }
        List<AbstractPage> results = this.getPageAndBlogPostsVersionsQuery(userKey, limitedRequest);
        return PageResponseImpl.filteredResponse((LimitedRequest)limitedRequest, results, null);
    }

    private List<AbstractPage> getPageAndBlogPostsVersionsQuery(@NonNull UserKey userKey, LimitedRequest limitedRequest) {
        return (List)this.getHibernateTemplate().execute(session -> {
            String sqlString = "SELECT abstract_page.* FROM (         SELECT rc.CONTENTID AS CONTENTID,         MAX(content_union.LASTMODDATE) AS LASTMODDATE         from (SELECT  r.LASTMODDATE as LASTMODDATE,                 r.TARGETCONTENTID as CONTENTID                         FROM USERCONTENT_RELATION r                         WHERE r.RELATIONNAME = 'touched'                         AND r.SOURCEUSER = :userKey             UNION ALL                 SELECT  c.LASTMODDATE as LASTMODDATE,                         c.CONTENTID as CONTENTID                 FROM CONTENT c                         WHERE c.LASTMODIFIER = :userKey                         AND c.CONTENT_STATUS IN ('current')                         AND c.CONTENTTYPE in :contentTypes         ) content_union         LEFT JOIN CONTENT lc         ON lc.CONTENTID = content_union.CONTENTID         LEFT JOIN CONTENT rc         ON coalesce(lc.PREVVER, lc.CONTENTID) = rc.CONTENTID         WHERE rc.CONTENT_STATUS in ('current', 'draft')            AND rc.CONTENTTYPE in :contentTypes         GROUP BY rc.CONTENTID ) r LEFT JOIN CONTENT abstract_page ON abstract_page.CONTENTID = r.CONTENTID ORDER BY r.LASTMODDATE DESC";
            NativeQuery query = session.createNativeQuery("SELECT abstract_page.* FROM (         SELECT rc.CONTENTID AS CONTENTID,         MAX(content_union.LASTMODDATE) AS LASTMODDATE         from (SELECT  r.LASTMODDATE as LASTMODDATE,                 r.TARGETCONTENTID as CONTENTID                         FROM USERCONTENT_RELATION r                         WHERE r.RELATIONNAME = 'touched'                         AND r.SOURCEUSER = :userKey             UNION ALL                 SELECT  c.LASTMODDATE as LASTMODDATE,                         c.CONTENTID as CONTENTID                 FROM CONTENT c                         WHERE c.LASTMODIFIER = :userKey                         AND c.CONTENT_STATUS IN ('current')                         AND c.CONTENTTYPE in :contentTypes         ) content_union         LEFT JOIN CONTENT lc         ON lc.CONTENTID = content_union.CONTENTID         LEFT JOIN CONTENT rc         ON coalesce(lc.PREVVER, lc.CONTENTID) = rc.CONTENTID         WHERE rc.CONTENT_STATUS in ('current', 'draft')            AND rc.CONTENTTYPE in :contentTypes         GROUP BY rc.CONTENTID ) r LEFT JOIN CONTENT abstract_page ON abstract_page.CONTENTID = r.CONTENTID ORDER BY r.LASTMODDATE DESC").addEntity("abstract_page", AbstractPage.class);
            query.setCacheable(false);
            query.setFirstResult(limitedRequest.getStart());
            query.setMaxResults(limitedRequest.getLimit());
            query.setParameterList("contentTypes", Arrays.asList("PAGE", "BLOGPOST"));
            query.setParameter("userKey", (Object)userKey.toString(), (Type)StringType.INSTANCE);
            return query.list();
        });
    }

    @Override
    public Map<Long, ContributionStatus> getContributionStatusByUser(Collection<ContentId> contentIds, UserKey userKey) {
        if (userKey == null) {
            return Collections.emptyMap();
        }
        List<ContributionStatus> touchedResults = this.queryContributionStatusByTouch(contentIds, userKey);
        return touchedResults.stream().collect(Collectors.toMap(ContributionStatus::getContentId, status -> status, (previous, current) -> current));
    }

    private List<ContributionStatus> queryContributionStatusByTouch(Collection<ContentId> contentIds, UserKey userKey) {
        return (List)this.getHibernateTemplate().execute(session -> {
            if (log.isTraceEnabled()) {
                List contentRelationJoinResults = session.createNativeQuery("SELECT ucr.LASTMODDATE as touchdate, ucr.RELATIONID, c.LASTMODDATE, c.CONTENTID, c.TITLE, c.CONTENT_STATUS, coalesce(c.PREVVER, c.CONTENTID) as currentId, c.PREVVER as draftprevver FROM CONTENT c LEFT JOIN USERCONTENT_RELATION ucr ON c.CONTENTID = ucr.TARGETCONTENTID WHERE ucr.RELATIONNAME = 'touched' AND ucr.LASTMODIFIER = :userKey     and c.CONTENTTYPE in (:contentTypes)     and c.CONTENT_STATUS in (:contentStatuses) ").setParameterList("contentTypes", Arrays.asList("PAGE", "BLOGPOST")).setParameterList("contentStatuses", Arrays.asList("current", "draft")).setParameter("userKey", (Object)userKey.getStringValue()).getResultList();
                log.trace("content touch relations for user: {}: {} rows. First 10 results: {}", new Object[]{userKey, contentRelationJoinResults.size(), contentRelationJoinResults.stream().limit(10L).map(Arrays::asList).collect(Collectors.toList())});
                List idList = contentIds.stream().map(ContentId::asLong).collect(Collectors.toList());
                List matchingContentResults = session.createNativeQuery("SELECT lt.CONTENTID, lt.LASTMODDATE, lt.CONTENT_STATUS FROM CONTENT lt WHERE lt.CONTENTID in (:contentIds)").setParameterList("contentIds", idList).getResultList();
                log.trace("content requested: {}: {} rows. First 10 results: {}", new Object[]{idList, matchingContentResults.size(), matchingContentResults.stream().limit(10L).map(Arrays::asList).collect(Collectors.toList())});
            }
            String sqlString = "SELECT rt.currentId as contentId, rt.draftprevver as latestVersionid, rt.RELATIONID as relationId, CASE WHEN lt.LASTMODDATE < rt.touchdate THEN rt.CONTENT_STATUS ELSE lt.CONTENT_STATUS END as contentStatus, rt.touchdate as lastModifiedDate FROM CONTENT lt JOIN ( SELECT ucr.LASTMODDATE as touchdate, ucr.RELATIONID, c.LASTMODDATE, c.CONTENTID, c.TITLE, c.CONTENT_STATUS, coalesce(c.PREVVER, c.CONTENTID) as currentId, c.PREVVER as draftprevver FROM CONTENT c LEFT JOIN USERCONTENT_RELATION ucr ON c.CONTENTID = ucr.TARGETCONTENTID WHERE ucr.RELATIONNAME = 'touched' AND ucr.LASTMODIFIER = :userKey     and c.CONTENTTYPE in (:contentTypes)     and c.CONTENT_STATUS in (:contentStatuses) ) rt on lt.CONTENTID = rt.currentId WHERE lt.CONTENTID in (:contentIds)";
            Query query = session.createNativeQuery("SELECT rt.currentId as contentId, rt.draftprevver as latestVersionid, rt.RELATIONID as relationId, CASE WHEN lt.LASTMODDATE < rt.touchdate THEN rt.CONTENT_STATUS ELSE lt.CONTENT_STATUS END as contentStatus, rt.touchdate as lastModifiedDate FROM CONTENT lt JOIN ( SELECT ucr.LASTMODDATE as touchdate, ucr.RELATIONID, c.LASTMODDATE, c.CONTENTID, c.TITLE, c.CONTENT_STATUS, coalesce(c.PREVVER, c.CONTENTID) as currentId, c.PREVVER as draftprevver FROM CONTENT c LEFT JOIN USERCONTENT_RELATION ucr ON c.CONTENTID = ucr.TARGETCONTENTID WHERE ucr.RELATIONNAME = 'touched' AND ucr.LASTMODIFIER = :userKey     and c.CONTENTTYPE in (:contentTypes)     and c.CONTENT_STATUS in (:contentStatuses) ) rt on lt.CONTENTID = rt.currentId WHERE lt.CONTENTID in (:contentIds)").addScalar("contentId", (Type)LongType.INSTANCE).addScalar("latestVersionId", (Type)LongType.INSTANCE).addScalar("relationId", (Type)LongType.INSTANCE).addScalar("contentStatus", (Type)StringType.INSTANCE).addScalar("lastModifiedDate", (Type)TimestampType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ContributionStatus.class));
            query.setCacheable(false);
            query.setParameterList("contentTypes", Arrays.asList("PAGE", "BLOGPOST"));
            query.setParameterList("contentIds", (Collection)contentIds.stream().map(ContentId::asLong).collect(Collectors.toList()));
            query.setParameterList("contentStatuses", Arrays.asList("current", "draft"));
            query.setParameter("userKey", (Object)userKey.getStringValue());
            return query.list();
        });
    }

    @Override
    public List<VersionHistorySummary> getVersionHistorySummary(long originalContentId) {
        List collaboratorHistories = this.findNamedQueryStringParam("confluence.content_findVersionHistoryCollaborators", "originalVersionId", originalContentId);
        List historySummaries = this.findNamedQueryStringParam("confluence.content_findVersionHistory", "originalVersionId", originalContentId);
        return this.consolidateCollaborators(collaboratorHistories, historySummaries);
    }

    @Override
    public Map<Long, Set<ConfluenceUser>> getAllModifiers(Collection<Long> contentIds) {
        List contributors;
        if (contentIds.isEmpty()) {
            return new HashMap<Long, Set<ConfluenceUser>>();
        }
        List contentVersions = this.getSessionFactory().getCurrentSession().createQuery("from ContentEntityObject content join fetch content.lastModifier where (content.originalVersion.id in (:contentIds) or content.id in (:contentIds)) and content.contentStatus = 'current'", ContentEntityObject.class).setParameter("contentIds", contentIds).list();
        List modifiers = Lists.transform((List)contentVersions, ceo -> Pair.pair((Object)ceo.getLatestVersionId(), (Object)ceo.getLastModifier()));
        if (this.isRetentionFeatureAvailable()) {
            List relations = this.getSessionFactory().getCurrentSession().createQuery("from User2ContentRelationEntity relation join fetch relation.sourceContent where (relation.targetContent.id in (:contentIds)) and relation.relationName = 'contributor'", User2ContentRelationEntity.class).setParameter("contentIds", contentIds).list();
            contributors = Lists.transform((List)relations, relation -> Pair.pair((Object)((ContentEntityObject)relation.getTargetContent()).getId(), (Object)((ConfluenceUser)relation.getSourceContent())));
        } else {
            contributors = Collections.emptyList();
        }
        return Stream.concat(modifiers.stream(), contributors.stream()).collect(Collectors.groupingBy(Pair::left, Collectors.mapping(Pair::right, Collectors.toSet())));
    }

    private boolean isRetentionFeatureAvailable() {
        return this.retentionFeatureChecker != null && this.retentionFeatureChecker.isFeatureAvailable();
    }

    private List<VersionHistorySummary> consolidateCollaborators(@NonNull List<VersionHistorySummaryCollaborator> collaboratorHistories, @NonNull List<VersionHistorySummary> summaries) {
        HashMap<Long, VersionHistorySummary.Builder> baseHistory = new HashMap<Long, VersionHistorySummary.Builder>();
        TreeMap<Long, VersionHistorySummary.Builder> historySummaryMap = new TreeMap<Long, VersionHistorySummary.Builder>((a, b) -> ((VersionHistorySummary.Builder)baseHistory.get(b)).getVersion() - ((VersionHistorySummary.Builder)baseHistory.get(a)).getVersion());
        for (VersionHistorySummaryCollaborator collaboratorHistory : collaboratorHistories) {
            VersionHistorySummary.Builder item2 = (VersionHistorySummary.Builder)baseHistory.get(collaboratorHistory.getId());
            if (item2 == null) {
                baseHistory.put(collaboratorHistory.getId(), new VersionHistorySummary.Builder(collaboratorHistory));
                continue;
            }
            item2.withContributor(collaboratorHistory.getCollaborator());
        }
        summaries.stream().filter(item -> !baseHistory.containsKey(item.getId())).forEach(item -> baseHistory.put(item.getId(), new VersionHistorySummary.Builder((VersionHistorySummary)item)));
        historySummaryMap.putAll(baseHistory);
        return historySummaryMap.values().stream().map(VersionHistorySummary.Builder::build).collect(Collectors.toList());
    }

    @Override
    public PageResponse<VersionHistorySummary> getVersionHistorySummary(long originalContentId, LimitedRequest request) {
        List limitedResult = this.findNamedQueryStringParams("confluence.content_findVersionHistory", HibernateObjectDao.Cacheability.CACHEABLE, request, "originalVersionId", originalContentId);
        return PageResponseImpl.filteredResponse((LimitedRequest)request, (List)limitedResult, null);
    }

    @Override
    public Map<Long, List<ConfluenceUser>> getVersionEditContributors(Iterable<T> originalVersions) {
        if (Iterables.isEmpty(originalVersions)) {
            return Collections.emptyMap();
        }
        List summaries = this.findNamedQueryStringParam("confluence.content_findEditContributors", "originalVersionIds", Lists.newArrayList((Iterable)Iterables.transform(originalVersions, EntityObject::getId)), HibernateObjectDao.Cacheability.CACHEABLE);
        Map<Long, List<ConfluenceUser>> map = this.groupByContent(summaries);
        this.addContentCreatorsIfRequired(originalVersions, map);
        return map;
    }

    private Map<Long, List<ConfluenceUser>> groupByContent(List<VersionContributorSummary> summaries) {
        return summaries.stream().collect(Collectors.groupingBy(VersionContributorSummary::getContentId, Collectors.mapping(VersionContributorSummary::getContributor, Collectors.toList())));
    }

    private void addContentCreatorsIfRequired(Iterable<T> originalVersions, Map<Long, List<ConfluenceUser>> map) {
        for (ContentEntityObject originalVersion : originalVersions) {
            List<ConfluenceUser> list = map.get(originalVersion.getId());
            if (list == null || list.contains(originalVersion.getCreator())) continue;
            list.add(originalVersion.getCreator());
            list.sort(Comparator.comparing(user -> user == null ? "" : user.getName()));
        }
    }

    @Override
    public List<ContentEntityObject> findPreviousVersions(long originalContentId) {
        return this.findNamedQueryStringParam("confluence.content_findPreviousVersions", "originalVersionId", originalContentId, HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<ContentEntityObject> findHistoricalVersionsAfterVersion(long originalContentId, int version) {
        return this.findNamedQueryStringParams("confluence.content_findHistoricalVersionsAfterVersion", "originalVersionId", originalContentId, "version", (Object)version, HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public Date getOldestPageCreationDate() {
        List results = this.findNamedQuery("page_findOldestCeoCreationDate");
        if (results == null) {
            return null;
        }
        return (Date)results.get(0);
    }

    @Override
    public List<ContentEntityObject> findAllDraftsFor(long contentId) {
        List drafts = this.findNamedQueryStringParam("confluence.content_findDraftForContent", "contentId", contentId);
        if (drafts == null || drafts.isEmpty()) {
            return Collections.emptyList();
        }
        return drafts;
    }

    @Override
    public List<Draft> findAllLegacyDraftsFor(long contentId) {
        List drafts = this.findNamedQueryStringParam("confluence.draft_findByPageId", "pageId", Long.toString(contentId));
        if (drafts == null || drafts.isEmpty()) {
            return Collections.emptyList();
        }
        return drafts;
    }

    @Override
    public ContentEntityObject findDraftFor(long contentId) {
        List<ContentEntityObject> drafts = this.findAllDraftsFor(contentId);
        if (drafts == null || drafts.isEmpty()) {
            return null;
        }
        if (drafts.size() > 1) {
            log.debug("More that one draft found for content with id " + contentId);
        }
        return drafts.get(0);
    }

    @Override
    public List<ContentEntityObject> findUnpublishedContentWithUserContributions(String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        List allDrafts = this.findNamedQueryStringParam("confluence.content_findDraftsForUser", "user", user);
        return allDrafts.stream().filter(draft -> draft.isUnpublished() || DraftsTransitionHelper.isLegacyDraft(draft)).collect(Collectors.toList());
    }

    @Override
    public List<ContentEntityObject> findDraftsWithUnpublishedChangesForUser(String creatorName) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(creatorName);
        return this.findNamedQueryStringParam("confluence.content_findDraftsWithUnpublishedChangesForUser", "user", user);
    }
}

