/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.breadcrumbs;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BlogPostDateBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;

public class BlogPostBreadcrumb
extends AbstractBreadcrumb {
    private BlogPost blogPost;

    public BlogPostBreadcrumb(BlogPost blogPost) {
        super(blogPost.getTitle(), blogPost.getUrlPath());
        this.blogPost = blogPost;
    }

    @Override
    public Breadcrumb getParent() {
        String datePath = this.blogPost.getDatePath();
        String[] postingDateParts = datePath.split("/");
        String postingYear = postingDateParts[0];
        String postingMonthNumeric = postingDateParts[1];
        String postingDayOfMonth = postingDateParts[2];
        return new BlogPostDateBreadcrumb(this.getSpace(), postingYear, postingMonthNumeric, this.blogPost.getPostingMonth(), postingDayOfMonth);
    }

    private Space getSpace() {
        return this.blogPost.getLatestVersion().getSpace();
    }

    public BlogPost getBlogPost() {
        return this.blogPost;
    }
}

