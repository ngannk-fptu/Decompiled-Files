/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.misc.ConcurrentConversionUtil
 *  com.atlassian.util.concurrent.Timeout
 *  com.sun.syndication.feed.synd.SyndCategory
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.LegacyRomeSyndEntry;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.util.misc.ConcurrentConversionUtil;
import com.sun.syndication.feed.synd.SyndCategory;
import io.atlassian.util.concurrent.Timeout;
import java.util.Collection;
import java.util.List;

public interface RssRenderSupport<T> {
    public String getTitle(RssRenderItem<? extends T> var1);

    public String getLink(RssRenderItem<? extends T> var1);

    @Deprecated
    default public List<SyndCategory> getCategories(RssRenderItem<? extends T> item) {
        return LegacyRomeSyndEntry.asCategories(this.getCategoryNames(item));
    }

    public Collection<String> getCategoryNames(RssRenderItem<? extends T> var1);

    @Deprecated
    default public String getRenderedContent(RssRenderItem<? extends T> item, com.atlassian.util.concurrent.Timeout timeout) {
        return this.renderedContext(item, ConcurrentConversionUtil.toIoTimeout((com.atlassian.util.concurrent.Timeout)timeout));
    }

    public String renderedContext(RssRenderItem<? extends T> var1, Timeout var2);
}

