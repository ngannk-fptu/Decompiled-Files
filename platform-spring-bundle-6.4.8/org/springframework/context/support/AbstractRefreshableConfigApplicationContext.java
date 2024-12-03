/*
 * Decompiled with CFR 0.152.
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
        this.setConfigLocations(StringUtils.tokenizeToStringArray(location, ",; \t\n"));
    }

    public void setConfigLocations(String ... locations) {
        if (locations != null) {
            Assert.noNullElements((Object[])locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i2 = 0; i2 < locations.length; ++i2) {
                this.configLocations[i2] = this.resolvePath(locations[i2]).trim();
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

    @Override
    public void setBeanName(String name) {
        if (!this.setIdCalled) {
            super.setId(name);
            this.setDisplayName("ApplicationContext '" + name + "'");
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (!this.isActive()) {
            this.refresh();
        }
    }
}

