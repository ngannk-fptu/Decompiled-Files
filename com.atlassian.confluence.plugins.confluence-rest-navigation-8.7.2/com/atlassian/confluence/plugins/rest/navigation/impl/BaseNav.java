/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import java.util.Map;

public abstract class BaseNav
extends AbstractNav {
    protected final String baseUrl;
    protected final String contextPath;
    private String anchor;
    private final Map<String, Object> params = Maps.newLinkedHashMap();

    public BaseNav(String baseUrl, String context) {
        this.baseUrl = baseUrl;
        this.contextPath = context;
    }

    @Override
    protected final void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    @Override
    protected final void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @Override
    protected final String getAnchor() {
        return this.anchor;
    }

    @Override
    protected final Map<String, Object> getParams() {
        return this.params;
    }

    @Override
    protected String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    protected String getContextPath() {
        return this.contextPath;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("baseUrl", (Object)this.baseUrl).add("contextPath", (Object)this.contextPath).add("anchor", (Object)this.anchor).add("params", this.params).toString();
    }
}

