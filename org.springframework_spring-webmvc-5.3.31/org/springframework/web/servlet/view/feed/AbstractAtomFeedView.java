/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.atom.Entry
 *  com.rometools.rome.feed.atom.Feed
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.feed.AbstractFeedView;

public abstract class AbstractAtomFeedView
extends AbstractFeedView<Feed> {
    public static final String DEFAULT_FEED_TYPE = "atom_1.0";
    private String feedType = "atom_1.0";

    public AbstractAtomFeedView() {
        this.setContentType("application/atom+xml");
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    @Override
    protected Feed newFeed() {
        return new Feed(this.feedType);
    }

    @Override
    protected final void buildFeedEntries(Map<String, Object> model, Feed feed, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Entry> entries = this.buildFeedEntries(model, request, response);
        feed.setEntries(entries);
    }

    protected abstract List<Entry> buildFeedEntries(Map<String, Object> var1, HttpServletRequest var2, HttpServletResponse var3) throws Exception;
}

