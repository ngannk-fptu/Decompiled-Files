/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.LocalDate
 */
package com.atlassian.confluence.api.model.locator;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import java.time.LocalDate;

public class ContentLocatorBuilder {
    ContentLocatorBuilder() {
    }

    public PageLocatorBuilder forPage() {
        return new PageLocatorBuilder();
    }

    public BlogLocatorBuilder forBlog() {
        return new BlogLocatorBuilder();
    }

    public static class BlogLocatorBuilder {
        private BlogLocatorBuilder() {
        }

        public ContentLocator bySpaceKeyTitleAndPostingDay(String spaceKey, String title, LocalDate postingDay) {
            return new ContentLocator(title, spaceKey, postingDay, ContentType.BLOG_POST);
        }

        @Deprecated
        public ContentLocator bySpaceKeyTitleAndPostingDay(String spaceKey, String title, org.joda.time.LocalDate postingDay) {
            return new ContentLocator(title, spaceKey, postingDay, ContentType.BLOG_POST);
        }
    }

    public static class PageLocatorBuilder {
        private PageLocatorBuilder() {
        }

        public ContentLocator bySpaceKeyAndTitle(String spaceKey, String title) {
            return new ContentLocator(title, spaceKey, (LocalDate)null, ContentType.PAGE);
        }
    }
}

