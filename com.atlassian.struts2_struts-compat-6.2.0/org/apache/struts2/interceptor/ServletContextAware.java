/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.struts2.action.ServletContextAware
 */
package org.apache.struts2.interceptor;

import javax.servlet.ServletContext;

@Deprecated(since="1.0.0", forRemoval=true)
public interface ServletContextAware
extends org.apache.struts2.action.ServletContextAware {
    public void setServletContext(ServletContext var1);

    default public void withServletContext(ServletContext context) {
        this.setServletContext(context);
    }
}

