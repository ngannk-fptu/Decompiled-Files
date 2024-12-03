/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.services.RestNavigation$RestBuilder
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.rest.api.services.RestNavigation;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

public abstract class AbstractNav
implements RestNavigation.RestBuilder {
    protected abstract String buildPath();

    protected abstract void addParam(String var1, Object var2);

    protected abstract void setAnchor(String var1);

    protected abstract String getAnchor();

    protected abstract Map<String, Object> getParams();

    protected abstract String getBaseUrl();

    protected abstract String getContextPath();

    protected abstract AbstractNav copy();

    public String buildAbsolute() {
        return this.toAbsoluteUriBuilder().build(new Object[0]).toString();
    }

    public String buildCanonicalAbsolute() {
        return this.buildAbsolute();
    }

    public UriBuilder toAbsoluteUriBuilder() {
        UriBuilder builder = UriBuilder.fromUri((String)this.getBaseUrl()).path(this.buildPath());
        this.addParams(builder);
        this.addAnchor(builder);
        return builder;
    }

    public String buildRelative() {
        UriBuilder builder = UriBuilder.fromPath((String)this.buildPath());
        this.addParams(builder);
        this.addAnchor(builder);
        return builder.build(new Object[0]).toString();
    }

    private void addAnchor(UriBuilder builder) {
        builder.fragment(this.getAnchor());
    }

    private void addParams(UriBuilder builder) {
        for (Map.Entry<String, Object> param : this.getParams().entrySet()) {
            Object val = param.getValue();
            if (val.getClass().isArray()) {
                builder.queryParam(param.getKey(), (Object[])param.getValue());
                continue;
            }
            builder.queryParam(param.getKey(), new Object[]{param.getValue()});
        }
    }

    public String buildRelativeWithContext() {
        return this.getContextPath() + this.buildRelative();
    }
}

