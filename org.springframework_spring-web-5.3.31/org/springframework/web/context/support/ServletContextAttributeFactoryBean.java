/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.context.support;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

public class ServletContextAttributeFactoryBean
implements FactoryBean<Object>,
ServletContextAware {
    @Nullable
    private String attributeName;
    @Nullable
    private Object attribute;

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        if (this.attributeName == null) {
            throw new IllegalArgumentException("Property 'attributeName' is required");
        }
        this.attribute = servletContext.getAttribute(this.attributeName);
        if (this.attribute == null) {
            throw new IllegalStateException("No ServletContext attribute '" + this.attributeName + "' found");
        }
    }

    @Nullable
    public Object getObject() throws Exception {
        return this.attribute;
    }

    public Class<?> getObjectType() {
        return this.attribute != null ? this.attribute.getClass() : null;
    }

    public boolean isSingleton() {
        return true;
    }
}

