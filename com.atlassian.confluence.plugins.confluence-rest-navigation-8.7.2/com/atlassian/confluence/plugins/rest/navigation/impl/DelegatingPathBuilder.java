/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.google.common.base.MoreObjects;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

class DelegatingPathBuilder
extends AbstractNav {
    private final AbstractNav delegatePathBuilder;
    private final String terminalPath;

    public DelegatingPathBuilder(String terminalPath, AbstractNav delegate) {
        this.delegatePathBuilder = delegate;
        this.terminalPath = terminalPath;
    }

    @Override
    protected String buildPath() {
        return UriBuilder.fromPath((String)"{arg1}{arg2}").build(new Object[]{this.delegatePathBuilder.buildPath(), this.terminalPath}).toString();
    }

    @Override
    protected void addParam(String key, Object value) {
        this.delegatePathBuilder.addParam(key, value);
    }

    @Override
    protected void setAnchor(String anchor) {
        this.delegatePathBuilder.setAnchor(anchor);
    }

    @Override
    protected String getAnchor() {
        return this.delegatePathBuilder.getAnchor();
    }

    @Override
    protected Map<String, Object> getParams() {
        return this.delegatePathBuilder.getParams();
    }

    @Override
    protected String getBaseUrl() {
        return this.delegatePathBuilder.getBaseUrl();
    }

    @Override
    protected String getContextPath() {
        return this.delegatePathBuilder.getContextPath();
    }

    @Override
    protected AbstractNav copy() {
        return new DelegatingPathBuilder(this.terminalPath, this.delegatePathBuilder.copy());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("terminalPath", (Object)this.terminalPath).add("delegate", (Object)this.delegatePathBuilder).toString();
    }
}

