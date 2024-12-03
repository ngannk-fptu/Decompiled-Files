/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.ContentCursor
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.core.db.JDBCUtils
 *  com.atlassian.core.util.DateUtils$DateRange
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.engine.jdbc.spi.SqlExceptionHelper
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.NativeQuery
 *  org.hibernate.query.Query
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.jdbc.core.namedparam.MapSqlParameterSource
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 *  org.springframework.jdbc.core.namedparam.SqlParameterSource
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionSummary;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.internal.persistence.hibernate.AbstractContentEntityObjectHibernateDao;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageStatisticsDTO;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.core.db.JDBCUtils;
import com.atlassian.core.util.DateUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class HibernatePageDao
extends AbstractContentEntityObjectHibernateDao<Page>
implements PageDaoInternal {
    private static Integer numberPermissionPagesPerLoop = Integer.getInteger("NumberPermissionPagesPerLoop", 300);
    private static final String DELETED = "deleted";
    private static final int UNLIMITED_RESULTS = -1;
    private static final String PAGE = "Page";
    private static final String BLOGPOST = "BlogPost";

    @Override
    public Page getPageById(long id) {
        return (Page)this.getByClassId(id);
    }

    @Override
    public List<Page> getPagesByIds(Iterable<Long> ids) {
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterable partition = Iterables.partition(ids, (int)1000);
        for (List idList : partition) {
            builder.addAll((Iterable)this.findNamedQueryStringParam("confluence.page_getByIds", "ids", idList));
        }
        return builder.build();
    }

    @Override
    public int getCommentCountOnPage(long id) {
        return DataAccessUtils.intResult((Collection)this.findNamedQueryStringParam("confluence.page_countCommentsOnPage", "pageId", id));
    }

    @Override
    public Page getPageByIdWithComments(long id) {
        List pages = this.findNamedQueryStringParam("confluence.page_findLatestByIdOptimisedForComments", "pageId", id, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        if (pages.size() == 0) {
            pages = this.findNamedQueryStringParam("confluence.page_findLatestById", "pageId", id, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        }
        return this.getFirstPage(pages);
    }

    public Page getPage(Space space, String pageTitle, boolean eagerLoadComments) {
        if (space == null || pageTitle == null) {
            return null;
        }
        String lowerCasePageTitle = GeneralUtil.specialToLowerCase(pageTitle);
        String theQueryToUse = eagerLoadComments ? "confluence.page_findLatestBySpaceIdAndTitleOptimisedForComments" : "confluence.page_findLatestBySpaceIdAndTitle";
        List pages = this.findNamedQueryStringParams(theQueryToUse, "spaceId", space.getId(), "pageTitle", (Object)lowerCasePageTitle, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        if (eagerLoadComments && pages.size() == 0) {
            theQueryToUse = "confluence.page_findLatestBySpaceIdAndTitle";
            pages = this.findNamedQueryStringParams(theQueryToUse, "spaceId", space.getId(), "pageTitle", (Object)lowerCasePageTitle, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        }
        return this.getFirstPage(pages);
    }

    @Override
    public Page getPageWithComments(Space space, String pageTitle) {
        return this.getPage(space, pageTitle, true);
    }

    @Override
    public Page getPage(Space space, String pageTitle) {
        return this.getPage(space, pageTitle, false);
    }

    @Override
    public List<Page> getPages(@Nullable Space space, boolean currentOnly) {
        return (List)this.getHibernateTemplate().execute(session -> {
            if (space == null) {
                return Collections.emptyList();
            }
            Query queryObject = currentOnly ? session.getNamedQuery("confluence.page_findCurrentPagesForSpace") : session.getNamedQuery("confluence.page_findPagesForSpace");
            queryObject.setParameter("spaceid", (Object)space.getId());
            queryObject.setCacheable(true);
            HibernatePageDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        });
    }

    @Override
    public List<Page> getPagesWithPermissions(@NonNull Space space) {
        Objects.requireNonNull(space);
        return this.getSessionFactory().getCurrentSession().createNamedQuery("confluence.page_findCurrentPagesForSpaceWithPermissions", Page.class).setParameter("spaceid", (Object)space.getId()).list();
    }

    @Override
    public List<Page> getPages(LimitedRequest pageRequest) {
        return this.findNamedQueryStringParams("confluence.page_findCurrentPages", true, pageRequest, new Object[0]);
    }

    @Override
    public List<Page> getPages(Space space, LimitedRequest pageRequest) {
        return this.findNamedQueryStringParams("confluence.page_findCurrentPagesForSpace", true, pageRequest, "spaceid", space.getId());
    }

    @Override
    public List<BlogPost> getBlogPosts(Space space, LimitedRequest limitedRequest) {
        return this.findNamedQueryStringParams("confluence.blogPost_findCurrentBlogPostsForSpace", true, limitedRequest, "spaceid", space.getId());
    }

    @Override
    public List<AbstractPage> getAbstractPages(Space space, List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        Preconditions.checkNotNull((Object)space, (Object)"Space should not be null");
        Preconditions.checkArgument((contentTypes != null && !contentTypes.isEmpty() ? 1 : 0) != 0, (Object)"Types should not be null or empty");
        Preconditions.checkArgument((statuses != null && !statuses.isEmpty() ? 1 : 0) != 0, (Object)"Statuses should not be null or empty");
        Collection<Class<? extends ContentEntityObject>> entityTypes = HibernatePageDao.getContentEntityTypes(contentTypes);
        List<String> statusNames = this.getStatusNames(statuses);
        return this.findNamedQueryStringParams("confluence.abstractpage_findBySpaceAndStatuses", true, pageRequest, "spaceid", space.getId(), "types", entityTypes, "statuses", statusNames);
    }

    @Override
    public List<AbstractPage> getAbstractPages(List<ContentType> contentTypes, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        Preconditions.checkArgument((contentTypes != null && !contentTypes.isEmpty() ? 1 : 0) != 0, (Object)"Types should not be null or empty");
        Preconditions.checkArgument((statuses != null && !statuses.isEmpty() ? 1 : 0) != 0, (Object)"Statuses should not be null or empty");
        Collection<Class<? extends ContentEntityObject>> entityTypes = HibernatePageDao.getContentEntityTypes(contentTypes);
        List<String> statusNames = this.getStatusNames(statuses);
        return this.findNamedQueryStringParams("confluence.abstractpage_findByStatuses", true, pageRequest, "types", entityTypes, "statuses", statusNames);
    }

    @Override
    public Collection<Long> getPageIds(Space space) {
        Preconditions.checkNotNull((Object)space, (Object)"space cannot be null");
        return this.findNamedQueryStringParam("confluence.page_findPageIdsForSpace", "spaceId", space.getId());
    }

    @Override
    public List<AbstractPage> getAbstractPagesByTitle(String title, LimitedRequest limitedRequest) {
        Preconditions.checkNotNull((Object)title, (Object)"title cannot be null");
        String lowerTitle = GeneralUtil.specialToLowerCase(title);
        return this.findNamedQueryStringParams("confluence.abstractpage_findByTitle", true, limitedRequest, "title", lowerTitle);
    }

    @Override
    public List<AbstractPage> getAbstractPagesByCreationDate(DateUtils.DateRange range, LimitedRequest pageRequest) {
        return this.findNamedQueryStringParams("confluence.abstractpage_findByDateRange", true, pageRequest, "startDate", new Timestamp(range.startDate.getTime()), "endDate", new Timestamp(range.endDate.getTime()));
    }

    @Override
    public List<AbstractPage> getAbstractPages(Space space, String title, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        Preconditions.checkNotNull((Object)space, (Object)"Space should not be null");
        Preconditions.checkNotNull((Object)title, (Object)"Title should not be null");
        Preconditions.checkArgument((statuses != null && !statuses.isEmpty() ? 1 : 0) != 0, (Object)"Statuses should not be null or empty");
        String lowerTitle = GeneralUtil.specialToLowerCase(title);
        List<String> statusNames = this.getStatusNames(statuses);
        return this.findNamedQueryStringParams("confluence.abstractpage_findBySpaceAndTitleAndStatuses", true, pageRequest, "title", lowerTitle, "spaceid", space.getId(), "statuses", statusNames);
    }

    @Override
    public List<AbstractPage> getAbstractPages(String title, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        Preconditions.checkNotNull((Object)title, (Object)"Title should not be null");
        Preconditions.checkArgument((statuses != null && !statuses.isEmpty() ? 1 : 0) != 0, (Object)"Statuses should not be null or empty");
        String lowerTitle = GeneralUtil.specialToLowerCase(title);
        List<String> statusNames = this.getStatusNames(statuses);
        return this.findNamedQueryStringParams("confluence.abstractpage_findByTitleAndStatuses", true, pageRequest, "title", lowerTitle, "statuses", statusNames);
    }

    @Override
    public List<AbstractPage> getAbstractPages(DateUtils.DateRange range, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        Preconditions.checkArgument((statuses != null && !statuses.isEmpty() ? 1 : 0) != 0, (Object)"Statuses should not be null or empty");
        List<String> statusNames = this.getStatusNames(statuses);
        return this.findNamedQueryStringParams("confluence.abstractpage_findByDateRangeAndStatuses", true, pageRequest, "startDate", new Timestamp(range.startDate.getTime()), "endDate", new Timestamp(range.endDate.getTime()), "statuses", statusNames);
    }

    @Override
    public List<Page> getPagesStartingWith(Space space, String s) {
        return (List)this.getHibernateTemplate().execute(session -> {
            if (space == null) {
                return Collections.emptyList();
            }
            Query queryObject = session.getNamedQuery("confluence.page_findPagesStartingWithForSpace");
            queryObject.setParameter("spaceid", (Object)space.getId());
            queryObject.setParameter("string", (Object)GeneralUtil.specialToLowerCase(s).concat("%"));
            queryObject.setCacheable(true);
            HibernatePageDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        });
    }

    private Page getFirstPage(List<Page> pages) {
        if (pages.size() == 1) {
            return pages.get(0);
        }
        Page firstPage = null;
        for (Page page : pages) {
            if (firstPage != null && firstPage.getId() <= page.getId()) continue;
            firstPage = page;
        }
        return firstPage;
    }

    @Override
    public List<Page> getRecentlyAddedPages(int maxCount, @Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.findNamedQuery("confluence.page_findRecentlyAddedPages", HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxCount);
        }
        return this.findNamedQueryStringParam("confluence.page_findRecentlyAddedPagesForSpace", "spaceKey", spaceKey.toLowerCase(), HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxCount);
    }

    @Override
    public List<Page> getRecentlyUpdatedPages(int maxCount, @Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.findNamedQuery("confluence.page_findRecentlyUpdatedPages", HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxCount);
        }
        return this.findNamedQueryStringParam("confluence.page_findRecentlyUpdatedPagesForSpace", "spaceKey", spaceKey.toLowerCase(), HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxCount);
    }

    @Override
    public List<Page> getOrphanedPages(@Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.findNamedQuery("confluence.page_findOrphanedPages");
        }
        return this.findNamedQueryStringParam("confluence.page_findOrphanedPagesForSpace", "spaceKey", spaceKey.toLowerCase());
    }

    @Override
    @Deprecated
    public List<Page> getUndefinedPages(@Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.findNamedQuery("confluence.page_findUndefinedPages");
        }
        return this.findNamedQueryStringParam("confluence.page_findUndefinedPagesForSpace", "spaceKey", spaceKey.toLowerCase());
    }

    @Override
    public final List<OutgoingLink> getUndefinedLinks(@Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.findNamedQuery("confluence.page_findUndefinedPages");
        }
        return this.findNamedQueryStringParam("confluence.page_findUndefinedPagesForSpace", "spaceKey", spaceKey.toLowerCase());
    }

    @Override
    public List<Page> getPermissionPages(Space space) {
        ArrayList<Page> resultPages = new ArrayList<Page>();
        long totalItemCount = this.getPermissionPagesCount(space);
        int itemPerLoop = numberPermissionPagesPerLoop;
        int i = 0;
        while ((long)i < totalItemCount) {
            int limit = itemPerLoop;
            int nextPageOffset = i + itemPerLoop;
            if ((long)nextPageOffset > totalItemCount) {
                limit = (int)totalItemCount - i;
            }
            LimitedRequest limitedRequest = LimitedRequestImpl.create((int)i, (int)limit, (int)limit);
            resultPages.addAll(this.getPermissionPages(space, limitedRequest));
            i += itemPerLoop;
        }
        return resultPages;
    }

    @Override
    public Collection<Page> getPermissionPages(Space space, LimitedRequest limitedRequest) {
        Objects.requireNonNull(limitedRequest);
        List paginationIdsResult = this.getSessionFactory().getCurrentSession().getNamedQuery("confluence.page_findIdsCurrentPagesForSpaceHavePermissions").setParameter("spaceid", (Object)space.getId()).setFirstResult(limitedRequest.getStart()).setMaxResults(limitedRequest.getLimit()).list();
        if (paginationIdsResult == null || paginationIdsResult.size() == 0) {
            return Collections.emptyList();
        }
        List pages = this.getSessionFactory().getCurrentSession().getNamedQuery("confluence.page_findCurrentPagesForSpaceHavePermissions").setParameter("spaceid", (Object)space.getId()).setParameter("pageids", (Object)paginationIdsResult).list();
        LinkedHashSet<Page> uniquePages = new LinkedHashSet<Page>(pages);
        return uniquePages;
    }

    @Override
    public long getPermissionPagesCount(Space space) {
        return DataAccessUtils.longResult((Collection)this.findNamedQueryStringParam("confluence.page_countCurrentPagesForSpaceHavePermissions", "spaceid", space.getId()));
    }

    @Override
    public int getAuthoredPagesCountByUser(String username) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        return DataAccessUtils.intResult((Collection)this.findNamedQueryStringParam("confluence.page_findAuthoredPagesCountByUser", "creator", user));
    }

    @Override
    public List<Page> getRecentlyAuthoredPagesByUser(String username, int maxCount) {
        ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.page_findRecentlyAuthoredPagesByUser", "user", user, HibernateObjectDao.Cacheability.NOT_CACHEABLE, maxCount);
    }

    @Override
    public List<Page> getPagesCreatedOrUpdatedSinceDate(Date previousLoginDate) {
        if (previousLoginDate == null) {
            previousLoginDate = new Date();
        }
        return this.findNamedQueryStringParam("confluence.page_findPagesCreatedOrUpdatedSinceDate", "date", previousLoginDate, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public Class getPersistentClass() {
        return Page.class;
    }

    @Override
    public List<AbstractPage> findPagesWithCurrentOrHistoricalTitleInPermittedSpace(SpacePermissionQueryBuilder permissionQueryBuilder, String pageTitle, Space space, int maxResultCount) {
        return this.findPagesWithCurrentOrHistoricalTitle(permissionQueryBuilder, pageTitle, space, maxResultCount, PAGE, true, true);
    }

    @Override
    public List<AbstractPage> findBlogsWithCurrentOrHistoricalTitleInPermittedSpace(SpacePermissionQueryBuilder permissionQueryBuilder, String blogTitle, Space space, int maxResultCount) {
        return this.findPagesWithCurrentOrHistoricalTitle(permissionQueryBuilder, blogTitle, space, maxResultCount, BLOGPOST, true, true);
    }

    @Override
    public List<AbstractPage> findBlogsWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(SpacePermissionQueryBuilder permissionQueryBuilder, String blogTitle, Space space, int maxResultCount) {
        return this.findPagesWithCurrentOrHistoricalTitle(permissionQueryBuilder, blogTitle, space, maxResultCount, BLOGPOST, false, true);
    }

    @Override
    public List<AbstractPage> findPagesWithCurrentOrHistoricalTitleInAllPermittedSpacesExcept(SpacePermissionQueryBuilder permissionQueryBuilder, String pageTitle, Space space, int maxResultCount) {
        return this.findPagesWithCurrentOrHistoricalTitle(permissionQueryBuilder, pageTitle, space, maxResultCount, PAGE, false, true);
    }

    @Override
    public List<ContentPermissionSummary> findContentPermissionSummaryByIds(Collection<Long> ids) {
        return (List)this.getHibernateTemplate().execute(session -> {
            ArrayList innerContentPermissionSummary = new ArrayList();
            for (List innerIds : Lists.partition((List)Lists.newArrayList((Iterable)ids), (int)500)) {
                Query query = session.createNamedQuery("confluence.content_findContentPermissionSummaryByIds", ContentPermissionSummary.class);
                query.setParameterList("ids", (Collection)innerIds);
                innerContentPermissionSummary.addAll(query.list());
            }
            return innerContentPermissionSummary;
        });
    }

    private static Collection<Class<? extends ContentEntityObject>> getContentEntityTypes(Collection<ContentType> contentTypes) {
        return contentTypes.stream().map(contentType -> HibernatePageDao.getEntityType(contentType)).collect(Collectors.toSet());
    }

    private static Class<? extends ContentEntityObject> getEntityType(ContentType contentType) {
        if (ContentType.PAGE.equals((Object)contentType)) {
            return Page.class;
        }
        if (ContentType.BLOG_POST.equals((Object)contentType)) {
            return BlogPost.class;
        }
        throw new IllegalArgumentException("Types cannot reference to any other content that pages or blog posts");
    }

    private List<String> getStatusNames(List<ContentStatus> statuses) {
        return statuses.stream().map(contentStatus -> ContentStatus.TRASHED.equals(contentStatus) ? DELETED : contentStatus.getValue()).collect(Collectors.toList());
    }

    private List<AbstractPage> findPagesWithCurrentOrHistoricalTitle(SpacePermissionQueryBuilder spacePermissionQueryBuilder, String pageTitle, Space space, int maxResultCount, String pageType, boolean findInSpace, boolean includeLatestVersions) {
        LinkedHashSet result = Sets.newLinkedHashSet();
        if (includeLatestVersions) {
            String currentClause = findInSpace ? this.getCurrentInSpaceClause() : this.getCurrentOutOfSpaceClause(space);
            result.addAll(this.findCurrentPages(pageType, currentClause, pageTitle, space, maxResultCount, spacePermissionQueryBuilder));
        }
        if (maxResultCount > result.size() || maxResultCount == -1) {
            String historicalClause = findInSpace ? this.getHistoricalTitleInSpaceClause(pageType) : this.getHistoricalTitleOutOfSpaceClause(space, pageType);
            result.addAll(this.findCurrentPages(pageType, historicalClause, pageTitle, space, maxResultCount, spacePermissionQueryBuilder));
        }
        return this.asList(result, maxResultCount);
    }

    @Override
    public int countCurrentPages() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.page_countCurrentPages").list())));
    }

    @Override
    public int countDraftPages() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.page_countDraftPages").list())));
    }

    @Override
    public int countPagesWithUnpublishedChanges() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.page_countPagesWithUnpublishedChanges").list())));
    }

    @Override
    public long getPageCount(@NonNull String spaceKey) {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.page_countAllPagesForSpace").setParameter("spaceKey", (Object)spaceKey.toLowerCase()).list())));
    }

    @Override
    public long getPageCount(@NonNull String spaceKey, List<ContentStatus> statuses) {
        List<String> statusNames = this.getStatusNames(statuses);
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.page_countPagesForSpace").setParameter("spaceKey", (Object)spaceKey.toLowerCase()).setParameter("statuses", (Object)statusNames).list())));
    }

    @Override
    public Optional<PageStatisticsDTO> getPageStatistics() {
        return (Optional)this.getHibernateTemplate().executeWithNativeSession(session -> {
            ResultSet rs;
            PreparedStatement ps;
            block4: {
                Optional<PageStatisticsDTO> optional;
                ps = null;
                rs = null;
                try {
                    Connection connection = ((SessionImplementor)session).connection();
                    String query = "SELECT COUNT(*) AS allPages, SUM(case when PREVVER is null and CONTENT_STATUS = 'current' then 1 else 0 end) AS currentPages, SUM(case when PREVVER is null and CONTENT_STATUS = 'draft' then 1 else 0 end) AS draftPages,SUM(case when PREVVER is not null and CONTENT_STATUS = 'draft' then 1 else 0 end) AS pagesWithUnpublishedChanges, SUM(case when CONTENT_STATUS = 'deleted' then 1 else 0 end) AS deletedPages FROM CONTENT WHERE CONTENTTYPE = 'PAGE'";
                    ps = connection.prepareStatement(query);
                    rs = ps.executeQuery();
                    if (!rs.next()) break block4;
                    optional = Optional.of(new PageStatisticsDTO(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
                }
                catch (SQLException ex) {
                    try {
                        this.logger.warn((Object)"Unable to fetch page statistics", (Throwable)new SqlExceptionHelper(true).convert(ex, ex.getMessage()));
                    }
                    catch (Throwable throwable) {
                        JDBCUtils.close(rs);
                        JDBCUtils.close(ps);
                        throw throwable;
                    }
                    JDBCUtils.close((ResultSet)rs);
                    JDBCUtils.close((Statement)ps);
                }
                JDBCUtils.close((ResultSet)rs);
                JDBCUtils.close((Statement)ps);
                return optional;
            }
            JDBCUtils.close((ResultSet)rs);
            JDBCUtils.close((Statement)ps);
            return Optional.empty();
        });
    }

    @Override
    public int countAllPages() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.page_countAllPages").list())));
    }

    private List<AbstractPage> asList(Collection<AbstractPage> pages, int maxResultCount) {
        if (maxResultCount == -1) {
            return Lists.newArrayList(pages);
        }
        ArrayList list = Lists.newArrayListWithCapacity((int)Math.min(maxResultCount, pages.size()));
        Iterables.addAll((Collection)list, (Iterable)Iterables.limit(pages, (int)maxResultCount));
        return list;
    }

    private List<AbstractPage> findCurrentPages(String pageType, String clause, String pageTitle, Space space, int maxResultCount, SpacePermissionQueryBuilder spacePermissionQueryBuilder) {
        String hql = "select latest from " + pageType + " latest where latest.originalVersion is null and latest.contentStatus = 'current' " + clause + "and exists (select perm.id from SpacePermission perm where latest.space = perm.space and " + spacePermissionQueryBuilder.getHqlPermissionFilterString("perm") + ") order by latest.lastModificationDate desc";
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.createQuery(hql);
            queryObject.setParameter("pageTitle", (Object)pageTitle);
            if (space != null) {
                queryObject.setParameter("spaceKey", (Object)space.getKey());
            }
            spacePermissionQueryBuilder.substituteHqlQueryParameters(queryObject);
            queryObject.setCacheable(true);
            if (maxResultCount != -1) {
                queryObject.setMaxResults(maxResultCount);
            }
            HibernatePageDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        });
    }

    private String getHistoricalTitleInSpaceClause(String pageType) {
        return "and latest.space.key = :spaceKey and latest.id in (select distinct historical.originalVersion.id from " + pageType + " historical where historical.title = :pageTitle and historical.originalVersion is not null and historical.contentStatus = 'current') ";
    }

    private String getHistoricalTitleOutOfSpaceClause(Space space, String pageType) {
        return (space != null ? "and latest.space.key != :spaceKey " : "") + "and latest.id in (select distinct historical.originalVersion.id from " + pageType + " historical where historical.title = :pageTitle and historical.originalVersion is not null and historical.contentStatus = 'current') ";
    }

    private String getCurrentInSpaceClause() {
        return "and latest.space.key = :spaceKey and latest.title = :pageTitle ";
    }

    private String getCurrentOutOfSpaceClause(Space space) {
        return (space != null ? "and latest.space.key != :spaceKey " : "") + "and latest.title = :pageTitle ";
    }

    @Override
    public List<Page> getPageInTrash(String spaceKey, String title) {
        if (spaceKey == null || title == null) {
            return null;
        }
        return this.findNamedQueryStringParams("confluence.page_findTrashedPageBySpaceKeyTitle", "spaceKey", spaceKey.toLowerCase(), "pageTitle", (Object)GeneralUtil.specialToLowerCase(title), HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<Page> getDescendants(Page page) {
        List descendants = (List)this.getHibernateTemplate().execute(session -> {
            NativeQuery query = session.createNativeQuery("select {confpage.*} from CONFANCESTORS ancestors, CONTENT {confpage} where {confpage}.CONTENTID = ancestors.DESCENDENTID and ancestors.ANCESTORID = :ancestorId order by {confpage}.CONTENTID").addEntity("confpage", Page.class);
            query.setCacheable(true);
            query.setParameter("ancestorId", (Object)page.getId());
            return query.list();
        });
        LinkedHashMap distinctDescendants = Maps.newLinkedHashMap();
        for (Page descendant : Objects.requireNonNull(descendants)) {
            distinctDescendants.put(descendant.getId(), descendant);
        }
        return Lists.newArrayList(distinctDescendants.values());
    }

    @Override
    public int countPagesInSubtree(@NonNull Page page) {
        return 1 + this.countDescendants(page.getId());
    }

    private int countDescendants(long pageId) {
        return Objects.requireNonNull((Integer)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Connection c = ((SessionImplementor)session).connection();
            String query = "select count(*) from (CONFANCESTORS inner join CONTENT on CONFANCESTORS.DESCENDENTID = CONTENT.CONTENTID) where ANCESTORID = ? AND CONTENT.CONTENT_STATUS = 'current'";
            try (PreparedStatement ps = c.prepareStatement("select count(*) from (CONFANCESTORS inner join CONTENT on CONFANCESTORS.DESCENDENTID = CONTENT.CONTENTID) where ANCESTORID = ? AND CONTENT.CONTENT_STATUS = 'current'");){
                Integer n;
                block14: {
                    ps.setLong(1, pageId);
                    ResultSet rs = ps.executeQuery();
                    try {
                        rs.next();
                        n = rs.getInt(1);
                        if (rs == null) break block14;
                    }
                    catch (Throwable throwable) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    rs.close();
                }
                return n;
            }
            catch (SQLException ex) {
                throw new SqlExceptionHelper(true).convert(ex, ex.getMessage());
            }
        }));
    }

    @Override
    public Map<Long, List<Long>> getAncestorsFor(Collection<Long> ids) {
        List queryResult = this.findNamedQueryStringParam("confluence.page_findPagesAncestorIds", "ids", ids);
        HashMap result = Maps.newHashMap();
        for (Object[] signedQueryResult : queryResult) {
            Long pageId = (Long)signedQueryResult[0];
            Long ancestorId = (Long)signedQueryResult[1];
            ArrayList<Long> ancestorList = (ArrayList<Long>)result.get(pageId);
            if (ancestorList == null) {
                ancestorList = new ArrayList<Long>();
            }
            if (ancestorId != null) {
                ancestorList.add(ancestorId);
            }
            result.put(pageId, ancestorList);
        }
        return result;
    }

    @Override
    public List<String> getDescendantTitles(Page page) {
        return this.findNamedQueryStringParam("confluence.page_findDescendantTitles", "pageId", page.getId());
    }

    @Override
    public List<Long> getDescendantIds(Page page) {
        return this.getDescendantIds(page, ContentStatus.CURRENT);
    }

    @Override
    public List<Long> getDescendantIds(Page page, ContentStatus ... contentStatus) {
        return (List)this.getHibernateTemplate().executeWithNativeSession(session -> {
            boolean hasContentStatus;
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate((DataSource)new SingleConnectionDataSource(((SessionImplementor)session).connection(), true));
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("ancestorId", (Object)page.getId());
            boolean bl = hasContentStatus = contentStatus != null && contentStatus.length > 0;
            if (hasContentStatus) {
                parameters.addValue("statuses", Arrays.stream(contentStatus).map(status -> status.getValue()).collect(Collectors.toList()));
                return template.queryForList("select DESCENDENTID from (CONFANCESTORS inner join CONTENT on CONFANCESTORS.DESCENDENTID = CONTENT.CONTENTID) where ANCESTORID = :ancestorId and CONTENT.CONTENT_STATUS in (:statuses)", (SqlParameterSource)parameters, Long.TYPE);
            }
            return template.queryForList("select DESCENDENTID from (CONFANCESTORS inner join CONTENT on CONFANCESTORS.DESCENDENTID = CONTENT.CONTENTID) where ANCESTORID = :ancestorId", (SqlParameterSource)parameters, Long.TYPE);
        });
    }

    @Override
    public List<Page> getTopLevelPages(Space space) {
        return this.findNamedQueryStringParam("confluence.page_findTopLevelPagesBySpace", "spaceid", space.getId(), HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<Page> getTopLevelPages(Space space, LimitedRequest limitedRequest) {
        return this.findNamedQueryStringParams("confluence.page_findTopLevelPagesBySpace", HibernateObjectDao.Cacheability.CACHEABLE, limitedRequest, "spaceid", space.getId());
    }

    @Override
    public List<Page> getChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        if (depth == Depth.ALL) {
            throw new NotImplementedServiceException("Page children is currently only supported for direct children");
        }
        return this.findNamedQueryStringParams("confluence.page_getChildren", HibernateObjectDao.Cacheability.CACHEABLE, pageRequest, "parentId", page.getId(), "status", "current");
    }

    @Override
    public PageResponse<Page> getDraftChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        if (depth == Depth.ALL) {
            throw new NotImplementedServiceException("Page children is currently only supported for direct children");
        }
        List pages = this.findNamedQueryStringParams("confluence.page_getDraftChildren", HibernateObjectDao.Cacheability.CACHEABLE, pageRequest, "parentId", page.getId());
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, (List)pages, null);
    }

    @Override
    public PageResponse<Page> getAllChildren(Page page, LimitedRequest pageRequest, Depth depth) {
        if (depth == Depth.ALL) {
            throw new NotImplementedServiceException("Page children is currently only supported for direct children");
        }
        List pages = this.findNamedQueryStringParams("confluence.page_getAllChildren", HibernateObjectDao.Cacheability.CACHEABLE, pageRequest, "parentId", page.getId());
        return PageResponseImpl.filteredResponse((LimitedRequest)pageRequest, (List)pages, null);
    }

    @Override
    public Integer getMaxSiblingPosition(Page page) {
        List result = page.getParent() == null ? this.findNamedQueryStringParam("confluence.page_findTopLevelMaxSiblingPosition", "spaceid", page.getSpace().getId()) : this.findNamedQueryStringParam("confluence.page_findMaxChildPosition", "pageid", page.getParent().getId());
        if (result == null || result.isEmpty()) {
            return null;
        }
        return (Integer)result.get(0);
    }

    @Override
    public Date getOldestPageCreationDate() {
        List results = this.findNamedQuery("page_findOldestPageCreationDate");
        if (results == null || results.isEmpty()) {
            return null;
        }
        return (Date)results.get(0);
    }

    @Override
    public void saveRawWithoutReindex(EntityObject objectToSave) {
        this.getHibernateTemplate().saveOrUpdate((Object)objectToSave);
    }

    @Override
    public List<Page> scanFilteredPages(List<ContentStatus> statuses, LimitedRequest pageRequest) {
        List<String> contentStatusStringList = this.getContentStatusStringList(statuses);
        Function<LimitedRequest, List> pageSearch = request -> {
            ContentCursor contentCursor = (ContentCursor)request.getCursor();
            if (contentCursor.getContentId() == null) {
                String queryName = contentCursor.isReverse() ? "confluence.page_findPagesByCursorFirstReverse" : "confluence.page_findPagesByCursorFirstForward";
                return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, (LimitedRequest)request, (Object)"statuses", (Object)contentStatusStringList);
            }
            String queryName = contentCursor.isReverse() ? "confluence.page_findPagesByCursorReverse" : "confluence.page_findPagesByCursorForward";
            return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, (LimitedRequest)request, (Object)"pageid", (Object)contentCursor.getContentId(), (Object)"statuses", (Object)contentStatusStringList);
        };
        return pageSearch.apply(pageRequest);
    }

    @Override
    public List<Page> scanFilteredPages(Space space, List<ContentStatus> statuses, LimitedRequest pageRequest) {
        List<String> contentStatusStringList = this.getContentStatusStringList(statuses);
        Function<LimitedRequest, List> pageSearch = request -> {
            ContentCursor contentCursor = (ContentCursor)request.getCursor();
            if (contentCursor.getContentId() == null) {
                String queryName = contentCursor.isReverse() ? "confluence.page_findPagesForSpaceByCursorFirstReverse" : "confluence.page_findPagesForSpaceByCursorFirstForward";
                return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, (LimitedRequest)request, (Object)"spaceid", (Object)space.getId(), (Object)"statuses", (Object)contentStatusStringList);
            }
            String queryName = contentCursor.isReverse() ? "confluence.page_findPagesForSpaceByCursorReverse" : "confluence.page_findPagesForSpaceByCursorForward";
            return this.findNamedQueryStringParams(queryName, HibernateObjectDao.Cacheability.NOT_CACHEABLE, (LimitedRequest)request, (Object)"spaceid", (Object)space.getId(), (Object)"pageid", (Object)contentCursor.getContentId(), (Object)"statuses", (Object)contentStatusStringList);
        };
        return pageSearch.apply(pageRequest);
    }

    private List<String> getContentStatusStringList(List<ContentStatus> statuses) {
        if (statuses.isEmpty()) {
            ArrayList<String> strContentStatus = new ArrayList<String>();
            strContentStatus.add(ContentStatus.CURRENT.getValue());
            return strContentStatus;
        }
        return this.getStatusNames(statuses);
    }
}

