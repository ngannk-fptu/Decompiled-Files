/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.CollectionFactory
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;

public class YamlPropertiesFactoryBean
extends YamlProcessor
implements FactoryBean<Properties>,
InitializingBean {
    private boolean singleton = true;
    @Nullable
    private Properties properties;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.isSingleton()) {
            this.properties = this.createProperties();
        }
    }

    @Override
    @Nullable
    public Properties getObject() {
        return this.properties != null ? this.properties : this.createProperties();
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    protected Properties createProperties() {
        Properties result = CollectionFactory.createStringAdaptingProperties();
        this.process((properties, map) -> result.putAll((Map<?, ?>)properties));
        return result;
    }
}

