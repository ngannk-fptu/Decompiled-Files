/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.config.JohnsonConfig
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.spring.johnson;

import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.config.JohnsonConfig;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.web.context.ServletContextAware;

public class JohnsonConfigFactoryBean
extends AbstractFactoryBean<JohnsonConfig>
implements ServletContextAware {
    private ServletContext servletContext;

    public Class getObjectType() {
        return JohnsonConfig.class;
    }

    protected JohnsonConfig createInstance() throws Exception {
        return Johnson.getConfig((ServletContext)this.servletContext);
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (this.servletContext == null) {
            throw new IllegalStateException("Servlet context must NOT be null");
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

