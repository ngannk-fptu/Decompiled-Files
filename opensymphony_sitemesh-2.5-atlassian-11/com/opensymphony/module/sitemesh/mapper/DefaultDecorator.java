/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class DefaultDecorator
implements Decorator {
    protected String page = null;
    protected String name = null;
    protected String uriPath = null;
    protected String role = null;
    protected Map parameters = null;

    public DefaultDecorator(String name, String page, Map parameters) {
        this(name, page, null, null, parameters);
    }

    public DefaultDecorator(String name, String page, String uriPath, Map parameters) {
        this(name, page, uriPath, null, parameters);
    }

    public DefaultDecorator(String name, String page, String uriPath, String role, Map parameters) {
        this.name = name;
        this.page = page;
        this.uriPath = uriPath;
        this.role = role;
        this.parameters = parameters;
    }

    public String getPage() {
        return this.page;
    }

    public String getName() {
        return this.name;
    }

    public String getURIPath() {
        return this.uriPath;
    }

    public String getRole() {
        return this.role;
    }

    public String getInitParameter(String paramName) {
        if (this.parameters == null || !this.parameters.containsKey(paramName)) {
            return null;
        }
        return (String)this.parameters.get(paramName);
    }

    public Iterator getInitParameterNames() {
        if (this.parameters == null) {
            return Collections.EMPTY_MAP.keySet().iterator();
        }
        return this.parameters.keySet().iterator();
    }
}

