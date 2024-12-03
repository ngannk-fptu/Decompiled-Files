/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.applinks.core.webfragment;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

public class WebFragmentContext {
    public static final String APPLICATION_LINK = "applicationLink";
    public static final String ENTITY_LINK = "entityLink";
    private final Map<String, Object> contextMap;

    private WebFragmentContext(Map<String, Object> contextMap) {
        this.contextMap = ImmutableMap.copyOf(contextMap);
    }

    public Map<String, Object> getContextMap() {
        return this.contextMap;
    }

    public static class Builder {
        private Map<String, Object> contextMap = new HashMap<String, Object>();

        public Builder applicationLink(ApplicationLink applicationLink) {
            this.contextMap.put(WebFragmentContext.APPLICATION_LINK, applicationLink);
            return this;
        }

        public Builder entityLink(EntityLink entityLink) {
            this.contextMap.put(WebFragmentContext.ENTITY_LINK, entityLink);
            return this;
        }

        public WebFragmentContext build() {
            WebFragmentContext context = new WebFragmentContext(this.contextMap);
            this.contextMap = ImmutableMap.of();
            return context;
        }
    }
}

