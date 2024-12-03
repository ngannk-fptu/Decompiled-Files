/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.context.support;

import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

public class ContextExposingHttpServletRequest
extends HttpServletRequestWrapper {
    private final WebApplicationContext webApplicationContext;
    @Nullable
    private final Set<String> exposedContextBeanNames;
    @Nullable
    private Set<String> explicitAttributes;

    public ContextExposingHttpServletRequest(HttpServletRequest originalRequest, WebApplicationContext context) {
        this(originalRequest, context, null);
    }

    public ContextExposingHttpServletRequest(HttpServletRequest originalRequest, WebApplicationContext context, @Nullable Set<String> exposedContextBeanNames) {
        super(originalRequest);
        Assert.notNull((Object)context, (String)"WebApplicationContext must not be null");
        this.webApplicationContext = context;
        this.exposedContextBeanNames = exposedContextBeanNames;
    }

    public final WebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }

    @Nullable
    public Object getAttribute(String name) {
        if (!(this.explicitAttributes != null && this.explicitAttributes.contains(name) || this.exposedContextBeanNames != null && !this.exposedContextBeanNames.contains(name) || !this.webApplicationContext.containsBean(name))) {
            return this.webApplicationContext.getBean(name);
        }
        return super.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        super.setAttribute(name, value);
        if (this.explicitAttributes == null) {
            this.explicitAttributes = new HashSet<String>(8);
        }
        this.explicitAttributes.add(name);
    }
}

