/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.analytics.client.base;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="analytics.confluence.base.data")
public class ConfluenceBaseDataEvent {
    private final int numUsers;
    private final int numSpaces;
    private final int numPages;
    private final int numBlogPosts;
    private final int numComments;
    private final String serverKey;

    public ConfluenceBaseDataEvent(int numUsers, int numSpaces, int numPages, int numBlogPosts, int numComments, String serverKey) {
        this.numUsers = numUsers;
        this.numSpaces = numSpaces;
        this.numPages = numPages;
        this.numBlogPosts = numBlogPosts;
        this.numComments = numComments;
        this.serverKey = serverKey;
    }

    public int getNumUsers() {
        return this.numUsers;
    }

    public int getNumSpaces() {
        return this.numSpaces;
    }

    public int getNumPages() {
        return this.numPages;
    }

    public int getNumBlogPosts() {
        return this.numBlogPosts;
    }

    public int getNumComments() {
        return this.numComments;
    }

    public String getServerKey() {
        return this.serverKey;
    }
}

