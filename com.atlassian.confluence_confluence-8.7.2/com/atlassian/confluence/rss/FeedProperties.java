/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;

public class FeedProperties {
    private final String title;
    private final String description;
    private final boolean showContent;
    private final boolean publicFeed;
    public static final String DEFAULT_FEED_TITLE = "Confluence RSS Feed";

    public FeedProperties(String title, String description, boolean showContent, boolean publicFeed) {
        if (StringUtils.isBlank((CharSequence)description)) {
            throw new IllegalArgumentException("Description is required.");
        }
        this.title = StringUtils.isNotBlank((CharSequence)title) ? this.cleanUpTitle(title) : DEFAULT_FEED_TITLE;
        this.description = description;
        this.showContent = showContent;
        this.publicFeed = publicFeed;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isShowContent() {
        return this.showContent;
    }

    public boolean isPublicFeed() {
        return this.publicFeed;
    }

    private String cleanUpTitle(String title) {
        return GeneralUtil.removeEmailsFromString(UrlUtils.removeUrlsFromString(title));
    }
}

