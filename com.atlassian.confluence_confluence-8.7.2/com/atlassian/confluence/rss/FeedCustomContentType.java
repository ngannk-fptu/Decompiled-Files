/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.List;

public interface FeedCustomContentType {
    public String getIdentifier();

    public String getI18nKey();

    public List<FeedCustomContentType> getSubTypes();

    public SearchQuery toSearchQuery();
}

