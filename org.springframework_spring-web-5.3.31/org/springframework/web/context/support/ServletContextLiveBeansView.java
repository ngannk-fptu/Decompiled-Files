/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.context.support.LiveBeansView
 *  org.springframework.util.Assert
 */
package org.springframework.web.context.support;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.LiveBeansView;
import org.springframework.util.Assert;

@Deprecated
public class ServletContextLiveBeansView
extends LiveBeansView {
    private final ServletContext servletContext;

    public ServletContextLiveBeansView(ServletContext servletContext) {
        Assert.notNull((Object)servletContext, (String)"ServletContext must not be null");
        this.servletContext = servletContext;
    }

    protected Set<ConfigurableApplicationContext> findApplicationContexts() {
        LinkedHashSet<ConfigurableApplicationContext> contexts = new LinkedHashSet<ConfigurableApplicationContext>();
        Enumeration attrNames = this.servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String)attrNames.nextElement();
            Object attrValue = this.servletContext.getAttribute(attrName);
            if (!(attrValue instanceof ConfigurableApplicationContext)) continue;
            contexts.add((ConfigurableApplicationContext)attrValue);
        }
        return contexts;
    }
}

