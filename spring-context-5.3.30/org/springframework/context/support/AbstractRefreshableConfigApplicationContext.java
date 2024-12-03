/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractRefreshableConfigApplicationContext
extends AbstractRefreshableApplicationContext
implements BeanNameAware,
InitializingBean {
    @Nullable
    private String[] configLocations;
    private boolean setIdCalled = false;

    public AbstractRefreshableConfigApplicationContext() {
    }

    public AbstractRefreshableConfigApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    public void setConfigLocation(String location) {
        this.setConfigLocations(StringUtils.tokenizeToStringArray((String)location, (String)",; \t\n"));
    }

    public void setConfigLocations(String ... locations) {
        if (locations != null) {
            Assert.noNullElements((Object[])locations, (String)"Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; ++i) {
                this.configLocations[i] = this.resolvePath(locations[i]).trim();
            }
        } else {
            this.configLocations = null;
        }
    }

    @Nullable
    protected String[] getConfigLocations() {
        return this.configLocations != null ? this.configLocations : this.getDefaultConfigLocations();
    }

    @Nullable
    protected String[] getDefaultConfigLocations() {
        return null;
    }

    protected String resolvePath(String path) {
        return this.getEnvironment().resolveRequiredPlaceholders(path);
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        this.setIdCalled = true;
    }

    public void setBeanName(String name) {
        if (!this.setIdCalled) {
            super.setId(name);
            this.setDisplayName("ApplicationContext '" + name + "'");
        }
    }

    public void afterPropertiesSet() {
        if (!this.isActive()) {
            this.refresh();
        }
    }
}

