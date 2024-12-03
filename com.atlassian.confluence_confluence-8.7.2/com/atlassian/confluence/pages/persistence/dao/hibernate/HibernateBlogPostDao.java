/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.core.util.DateUtils$DateRange
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.engine.jdbc.spi.SqlExceptionHelper
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.Query
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.pages.persistence.BlogPostDaoInternal;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostStatisticsDTO;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.DateUtils;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.Query;
import org.springframework.dao.support.DataAccessUtils;

public class HibernateBlogPostDao
extends ConfluenceHibernateObjectDao<BlogPost>
implements BlogPostDaoInternal {
    @Override
    public Class<BlogPost> getPersistentClass() {
        return BlogPost.class;
    }

    @Override
    public BlogPost getById(long id) {
        return this.getByClassId(id);
    }

    @Override
    protected BlogPost getByClassId(long id) {
        ContentEntityObject ceo = (ContentEntityObject)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
        if (!(ceo instanceof BlogPost)) {
            return null;
        }
        return (BlogPost)ceo;
    }

    @Override
    public BlogPost getBlogPostByTitle(String spaceKey, String title) {
        if (spaceKey == null || title == null) {
            return null;
        }
        List posts = this.findNamedQueryStringParams("confluence.blogPost_findLatestBySpaceKeyTitle", "spaceKey", spaceKey.toLowerCase(), "title", (Object)GeneralUtil.specialToLowerCase(title), HibernateObjectDao.Cacheability.CACHEABLE);
        if (posts.size() != 1) {
            return null;
        }
        return (BlogPost)posts.get(0);
    }

    @Override
    public List<BlogPost> getBlogPostsInTrash(String spaceKey, String title) {
        if (spaceKey == null || title == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.findNamedQueryStringParams("confluence.blogPost_findTrashedBlogBySpaceKeyTitle", "spaceKey", spaceKey.toLowerCase(), "title", (Object)GeneralUtil.specialToLowerCase(title), HibernateObjectDao.Cacheability.CACHEABLE));
    }

    @Override
    public @Nullable BlogPost getBlogPost(@NonNull Space space, @NonNull String title, @NonNull Calendar day, boolean eagerLoadComments) {
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)day, (int)5);
        String lowerCaseTitle = GeneralUtil.specialToLowerCase(title);
        Timestamp startDate = new Timestamp(range.startDate.getTime());
        Timestamp endDate = new Timestamp(range.endDate.getTime());
        String theQuery = eagerLoadComments ? "confluence.blogPost_findLatestBySpaceKeyTitleAndDateRangeEagerLoadComments" : "confluence.blogPost_findLatestBySpaceKeyTitleAndDateRange";
        List blogPosts = this.findNamedQueryStringParams(theQuery, "space", space, "title", (Object)lowerCaseTitle, "startDate", (Object)startDate, "endDate", (Object)endDate, HibernateObjectDao.Cacheability.CACHEABLE);
        if (eagerLoadComments && blogPosts.isEmpty()) {
            theQuery = "confluence.blogPost_findLatestBySpaceKeyTitleAndDateRange";
            blogPosts = this.findNamedQueryStringParams(theQuery, "space", space, "title", (Object)lowerCaseTitle, "startDate", (Object)startDate, "endDate", (Object)endDate, HibernateObjectDao.Cacheability.CACHEABLE);
        }
        return (BlogPost)this.findSingleObject(blogPosts);
    }

    @Override
    public List<BlogPost> getBlogPosts(@NonNull Space space, @NonNull Calendar date, int period) {
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)date, (int)period);
        return this.findNamedQueryStringParams("confluence.blogPost_findLatestBySpaceKeyAndDateRange", "space", space, "startDate", (Object)new Timestamp(range.startDate.getTime()), "endDate", (Object)new Timestamp(range.endDate.getTime()), HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<BlogPost> getBlogPosts(@NonNull Space space, @NonNull Calendar date, int period, int startIndex, int maxResultCount) {
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)date, (int)period);
        return this.findNamedQueryStringParams("confluence.blogPost_findLatestBySpaceKeyAndDateRange", HibernateObjectDao.Cacheability.CACHEABLE, startIndex, maxResultCount, "space", space, "startDate", new Timestamp(range.startDate.getTime()), "endDate", new Timestamp(range.endDate.getTime()));
    }

    @Override
    public long getBlogPostCount(String spaceKey, Calendar date, int period) {
        if (date == null) {
            return 0L;
        }
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)date, (int)period);
        return DataAccessUtils.longResult((Collection)this.findNamedQueryStringParams("confluence.blogPost_countBlogPostDatesForSpaceAndDateRange", "spaceKey", GeneralUtil.specialToLowerCase(spaceKey), "startDate", (Object)new Timestamp(range.startDate.getTime()), "endDate", (Object)new Timestamp(range.endDate.getTime()), HibernateObjectDao.Cacheability.CACHEABLE));
    }

    @Override
    public List<Date> getBlogPostDates(@NonNull Space space) {
        return this.findNamedQueryStringParam("confluence.blogPost_findCurrentBlogPostDatesForSpace", "space", space, HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<Date> getBlogPostDates(String spaceKey, Calendar date, int period) {
        if (date == null) {
            return new ArrayList<Date>();
        }
        DateUtils.DateRange range = DateUtils.toDateRange((Calendar)date, (int)period);
        return this.findNamedQueryStringParams("confluence.blogPost_findCurrentBlogPostDatesForSpaceAndDateRange", "spaceKey", GeneralUtil.specialToLowerCase(spaceKey), "startDate", (Object)new Timestamp(range.startDate.getTime()), "endDate", (Object)new Timestamp(range.endDate.getTime()), HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public List<BlogPost> getBlogPosts(Space space, boolean currentOnly) {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = currentOnly ? session.getNamedQuery("confluence.blogPost_findCurrentBlogPostsForSpace") : session.getNamedQuery("confluence.blogPost_findBlogPostsForSpace");
            queryObject.setParameter("spaceid", (Object)space.getId());
            queryObject.setCacheable(true);
            HibernateBlogPostDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return queryObject.list();
        });
    }

    @Override
    public List<Long> getCurrentBlogPostIds() {
        return (List)this.getHibernateTemplate().execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.blogPost_findCurrentBlogPostIds");
            return queryObject.list();
        });
    }

    @Override
    public List<BlogPost> getRecentlyAddedBlogPosts(int maxCount, @Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.findNamedQuery("confluence.blogPost_findRecentlyAddedPosts", HibernateObjectDao.Cacheability.CACHEABLE, maxCount);
        }
        return this.findNamedQueryStringParam("confluence.blogPost_findRecentlyAddedPostsForSpace", "spaceKey", spaceKey.toLowerCase(), HibernateObjectDao.Cacheability.CACHEABLE, maxCount);
    }

    @Override
    public BlogPost getFirstPostBefore(String spaceKey, Date creationDate) {
        if (spaceKey == null || creationDate == null) {
            return null;
        }
        return (BlogPost)this.findSingleObject(this.findNamedQueryStringParams("confluence.blogPost_findPostsInSpaceBefore", "spaceKey", (Object)spaceKey.toLowerCase(), "creationDate", (Object)new Timestamp(creationDate.getTime()), HibernateObjectDao.Cacheability.CACHEABLE, 1));
    }

    @Override
    public BlogPost getFirstPostAfter(String spaceKey, Date creationDate) {
        if (spaceKey == null || creationDate == null) {
            return null;
        }
        return (BlogPost)this.findSingleObject(this.findNamedQueryStringParams("confluence.blogPost_findPostsInSpaceAfter", "spaceKey", (Object)spaceKey.toLowerCase(), "creationDate", (Object)new Timestamp(creationDate.getTime()), HibernateObjectDao.Cacheability.CACHEABLE, 1));
    }

    @Override
    public BlogPost getFirstPostBefore(BlogPost post) {
        if (post == null) {
            return null;
        }
        return (BlogPost)this.findSingleObject(this.findNamedQueryStringParams("confluence.blogPost_findPostsInSpaceBeforeBlog", "spaceId", (Object)post.getSpace().getId(), "creationDate", (Object)new Timestamp(post.getCreationDate().getTime()), "postId", (Object)post.getId(), HibernateObjectDao.Cacheability.CACHEABLE, 1));
    }

    @Override
    public BlogPost getFirstPostAfter(BlogPost post) {
        if (post == null) {
            return null;
        }
        return (BlogPost)this.findSingleObject(this.findNamedQueryStringParams("confluence.blogPost_findPostsInSpaceAfterBlog", "spaceId", (Object)post.getSpace().getId(), "creationDate", (Object)new Timestamp(post.getCreationDate().getTime()), "postId", (Object)post.getId(), HibernateObjectDao.Cacheability.CACHEABLE, 1));
    }

    public List getRecentlyAddedBlogPosts(int maxPosts, Date timeSince, String spaceKey) {
        if (maxPosts <= 0) {
            maxPosts = Integer.MAX_VALUE;
        }
        if (!StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            throw new IllegalStateException("Cross-space since-blogs query not yet supported");
        }
        return this.findNamedQueryStringParams("confluence.blogPost_findPostsInSpaceBackTo", "spaceKey", (Object)spaceKey.toLowerCase(), "creationDate", (Object)new Timestamp(timeSince.getTime()), HibernateObjectDao.Cacheability.CACHEABLE, maxPosts);
    }

    @Override
    public BlogPost getMostRecentBlogPost(String spaceKey) {
        if (!StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return null;
        }
        return (BlogPost)this.findSingleObject(this.findNamedQueryStringParam("confluence.blogPost_findRecentlyAddedPostsForSpace", "spaceKey", spaceKey.toLowerCase(), HibernateObjectDao.Cacheability.CACHEABLE, 1));
    }

    @Override
    public int getBlogPostCount() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.blogPost_countAll").list())));
    }

    @Override
    public int countCurrentBlogs() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.blogPost_countCurrent").list())));
    }

    @Override
    public int countDraftBlogs() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.blogPost_countDraft").list())));
    }

    @Override
    public int countBlogsWithUnpublishedChanges() {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> session.getNamedQuery("confluence.blogPost_countPostsWithUnpublishedChanges").list())));
    }

    @Override
    public Optional<BlogPostStatisticsDTO> getBlogPostStatistics() {
        return (Optional)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Connection connection = ((SessionImplementor)session).connection();
            String query = "SELECT COUNT(*) AS allBlogs, SUM(case when PREVVER is null and CONTENT_STATUS = 'current' then 1 else 0 end) AS currentBlogs, SUM(case when PREVVER is null and CONTENT_STATUS = 'draft' then 1 else 0 end) AS draftBlogs,SUM(case when PREVVER is not null and CONTENT_STATUS = 'draft' then 1 else 0 end) AS blogsWithUnpublishedChanges, SUM(case when CONTENT_STATUS = 'deleted' then 1 else 0 end) AS deletedBlogs FROM CONTENT WHERE CONTENTTYPE = 'BLOGPOST'";
            try (PreparedStatement ps = connection.prepareStatement(query);
                 ResultSet rs = ps.executeQuery();){
                if (!rs.next()) return Optional.empty();
                Optional<BlogPostStatisticsDTO> optional = Optional.of(new BlogPostStatisticsDTO(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
                return optional;
            }
            catch (SQLException ex) {
                this.logger.warn((Object)"Unable to fetch blog post statistics", (Throwable)new SqlExceptionHelper(true).convert(ex, ex.getMessage()));
            }
            return Optional.empty();
        });
    }

    @Override
    public int getCommentCountOnBlog(long blogId) {
        return DataAccessUtils.intResult((Collection)this.findNamedQueryStringParam("confluence.blogPost_countCommentsOnBlog", "blogId", blogId));
    }
}

