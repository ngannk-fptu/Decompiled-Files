/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rss.FeedCustomContentType
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.rss.FeedCustomContentType;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import java.util.Collections;
import java.util.List;

public class MailFeedContentType
implements FeedCustomContentType {
    public String getIdentifier() {
        return "atlassian-mail";
    }

    public String getI18nKey() {
        return "list.element.mail";
    }

    public List<FeedCustomContentType> getSubTypes() {
        return Collections.emptyList();
    }

    public SearchQuery toSearchQuery() {
        return new CustomContentTypeQuery(new String[]{"com.atlassian.confluence.plugins.confluence-mail-archiving:mail"});
    }
}

