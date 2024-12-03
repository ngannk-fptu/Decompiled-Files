/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.rss.Channel
 *  com.rometools.rome.feed.rss.Item
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.feed.AbstractFeedView;

public abstract class AbstractRssFeedView
extends AbstractFeedView<Channel> {
    public AbstractRssFeedView() {
        this.setContentType("application/rss+xml");
    }

    @Override
    protected Channel newFeed() {
        return new Channel("rss_2.0");
    }

    @Override
    protected final void buildFeedEntries(Map<String, Object> model, Channel channel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Item> items = this.buildFeedItems(model, request, response);
        channel.setItems(items);
    }

    protected abstract List<Item> buildFeedItems(Map<String, Object> var1, HttpServletRequest var2, HttpServletResponse var3) throws Exception;
}

