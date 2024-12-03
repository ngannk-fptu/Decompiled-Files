/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.BlogPostReferenceParser;
import java.text.ParseException;

public class BlogPostReference {
    private final BlogPostReferenceParser parser;
    private final String spaceKey;
    private BlogPost blogPost;

    public BlogPostReference(String reference, String spaceKey, PageManager pageManager) throws ParseException {
        this.parser = new BlogPostReferenceParser(reference);
        this.spaceKey = spaceKey;
        if (this.parser.getEntityName() != null && this.parser.getPostingDay() != null) {
            this.blogPost = pageManager.getBlogPost(spaceKey, this.parser.getEntityName(), this.parser.getCalendarPostingDay());
        }
    }

    public String getPostingDay() {
        return this.parser.getPostingDay();
    }

    public String getPostingYear() {
        return this.parser.getPostingYear();
    }

    public String getPostingMonth() {
        return this.parser.getPostingMonth();
    }

    public String getEntityName() {
        return this.parser.getEntityName();
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public BlogPost getBlogPost() {
        return this.blogPost;
    }
}

