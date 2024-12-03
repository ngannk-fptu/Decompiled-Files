/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.content.entity;

import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import java.util.Date;

public class ConfluenceCustomEmoticon
implements CustomEmoticon {
    private long id;
    private final String shortcut;
    private final String name;
    private String url;
    private final String creatorUserId;
    private final Date createdDate;

    public static ConfluenceCustomEmoticon newlyConfluenceCustomEmoticon(String shortcut, String name) {
        return new ConfluenceCustomEmoticon(-1L, shortcut, name);
    }

    public ConfluenceCustomEmoticon(long id, String shortcut, String name) {
        this(id, shortcut, name, null);
    }

    public ConfluenceCustomEmoticon(long id, String shortcut, String name, String url) {
        this(id, shortcut, name, url, null, null);
    }

    public ConfluenceCustomEmoticon(long id, String shortcut, String name, String url, String creatorUserId, Date createdDate) {
        this.id = id;
        this.shortcut = shortcut;
        this.name = name;
        this.url = url;
        this.creatorUserId = creatorUserId;
        this.createdDate = createdDate;
    }

    @Override
    public String getShortcut() {
        return this.shortcut;
    }

    @Override
    public String getURL() {
        return this.url;
    }

    protected void setURL(String url) {
        this.url = url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getCreatorUserId() {
        return this.creatorUserId;
    }

    @Override
    public Date getCreatedDate() {
        return this.createdDate;
    }
}

