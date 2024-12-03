/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

public class RequestContext {
    public static final String TYPE_CONTENT = "content";
    public static final String TYPE_ATTACHMENT = "attachment";
    public static final String TYPE_SPACE = "space";
    public static final String TYPE_USER = "user/non-system";
    public static final String TYPE_ANONYMOUS_USER = "user/system/anonymous";
    public static final String TYPE_MISSING_USER = "user/missing";
    private Map<String, UriBuilder> uriBuilders;
    private final UriBuilder baseUriBuilder;
    private final User user;

    public RequestContext(User user, UriBuilder baseUriBuilder) {
        this.user = user;
        this.baseUriBuilder = baseUriBuilder;
        this.uriBuilders = new HashMap<String, UriBuilder>();
    }

    public void setUriBuilder(String type, UriBuilder uriBuilder) {
        this.uriBuilders.put(type, uriBuilder);
    }

    public UriBuilder getUriBuilder(String type) {
        return this.uriBuilders.get(type);
    }

    public UriBuilder getBaseUriBuilder() {
        return this.baseUriBuilder;
    }

    public User getUser() {
        return this.user;
    }
}

