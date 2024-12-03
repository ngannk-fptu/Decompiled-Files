/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.util.breadcrumbs.spaceia;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.BlogPostDateBreadcrumb;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class BlogPostBreadcrumb
extends AbstractBreadcrumb {
    private final BlogPost blogPost;
    private final DateFormatter dateFormatter;

    public BlogPostBreadcrumb(BlogPost blogPost) {
        this(blogPost, null);
    }

    public BlogPostBreadcrumb(BlogPost blogPost, @Nullable DateFormatter dateFormatter) {
        super(blogPost.getTitle(), blogPost.getUrlPath());
        this.blogPost = blogPost;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public Breadcrumb getParent() {
        if (this.blogPost.getCreationDate() != null && this.dateFormatter != null) {
            return BlogPostBreadcrumb.createBlogPostDateBreadcrumb(this.getSpace(), this::formatCreationDate);
        }
        return this.createBlogPostDateBreadcrumb();
    }

    private String formatCreationDate(String dateFormat) {
        return this.dateFormatter.formatGivenString(dateFormat, this.blogPost.getCreationDate());
    }

    private Breadcrumb createBlogPostDateBreadcrumb() {
        String datePath = this.blogPost.getDatePath();
        String[] postingDateParts = datePath.split("/");
        String postingYear = postingDateParts[0];
        String postingMonthNumeric = postingDateParts[1];
        String postingDayOfMonth = postingDateParts[2];
        return new BlogPostDateBreadcrumb(this.getSpace(), postingYear, postingMonthNumeric, this.blogPost.getPostingMonth(this.dateFormatter), postingDayOfMonth);
    }

    private static Breadcrumb createBlogPostDateBreadcrumb(Space space, UnaryOperator<String> creationDateFormatter) {
        return new BlogPostDateBreadcrumb(space, (String)creationDateFormatter.apply("yyyy"), (String)creationDateFormatter.apply("MM"), (String)creationDateFormatter.apply("MMMM"), (String)creationDateFormatter.apply("d"));
    }

    private Space getSpace() {
        return this.blogPost.getLatestVersion().getSpace();
    }
}

