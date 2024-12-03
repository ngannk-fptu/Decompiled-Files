/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;

public class MailSearchContentTypeDescriptor
implements ContentTypeSearchDescriptor {
    public String getIdentifier() {
        return "com.atlassian.confluence.plugins.confluence-mail-archiving:mail";
    }

    public String getI18NKey() {
        return "mail.searchtype.name";
    }

    public boolean isIncludedInDefaultSearch() {
        return false;
    }

    public SearchQuery getQuery() {
        return new CustomContentTypeQuery(new String[]{"com.atlassian.confluence.plugins.confluence-mail-archiving:mail"});
    }
}

