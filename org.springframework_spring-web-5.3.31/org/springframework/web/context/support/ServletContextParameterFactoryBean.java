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

public class ServletContextParameterFactoryBean
implements FactoryBean<String>,
ServletContextAware {
    @Nullable
    private String initParamName;
    @Nullable
    private String paramValue;

    public void setInitParamName(String initParamName) {
        this.initParamName = initParamName;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        if (this.initParamName == null) {
            throw new IllegalArgumentException("initParamName is required");
        }
        this.paramValue = servletContext.getInitParameter(this.initParamName);
        if (this.paramValue == null) {
            throw new IllegalStateException("No ServletContext init parameter '" + this.initParamName + "' found");
        }
    }

    @Nullable
    public String getObject() {
        return this.paramValue;
    }

    public Class<String> getObjectType() {
        return String.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

