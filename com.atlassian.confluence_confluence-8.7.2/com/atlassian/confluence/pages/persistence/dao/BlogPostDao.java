/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.core.persistence.VersionedObjectDao;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.BlogPostStatisticsDTO;
import com.atlassian.confluence.spaces.Space;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface BlogPostDao
extends VersionedObjectDao<BlogPost> {
    public BlogPost getById(long var1);

    public BlogPost getBlogPostByTitle(String var1, String var2);

    public List<BlogPost> getBlogPostsInTrash(String var1, String var2);

    public @Nullable BlogPost getBlogPost(@NonNull Space var1, @NonNull String var2, @NonNull Calendar var3, boolean var4);

    public List<BlogPost> getBlogPosts(@NonNull Space var1, @NonNull Calendar var2, int var3);

    public List<BlogPost> getBlogPosts(Space var1, boolean var2);

    public List<Long> getCurrentBlogPostIds();

    public List<Date> getBlogPostDates(@NonNull Space var1);

    public List<Date> getBlogPostDates(String var1, Calendar var2, int var3);

    public List<BlogPost> getRecentlyAddedBlogPosts(int var1, @Nullable String var2);

    public BlogPost getFirstPostBefore(String var1, Date var2);

    public BlogPost getFirstPostAfter(String var1, Date var2);

    public BlogPost getFirstPostBefore(BlogPost var1);

    public BlogPost getFirstPostAfter(BlogPost var1);

    public List<BlogPost> getRecentlyAddedBlogPosts(int var1, Date var2, String var3);

    public BlogPost getMostRecentBlogPost(String var1);

    public List<BlogPost> getBlogPosts(@NonNull Space var1, @NonNull Calendar var2, int var3, int var4, int var5);

    public long getBlogPostCount(String var1, Calendar var2, int var3);

    public int getBlogPostCount();

    public int countCurrentBlogs();

    public int countDraftBlogs();

    public int countBlogsWithUnpublishedChanges();

    public int getCommentCountOnBlog(long var1);

    public Optional<BlogPostStatisticsDTO> getBlogPostStatistics();
}

