/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.support.PropertiesLoaderSupport
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.lang.Nullable;

public class PropertiesFactoryBean
extends PropertiesLoaderSupport
implements FactoryBean<Properties>,
InitializingBean {
    private boolean singleton = true;
    @Nullable
    private Properties singletonInstance;

    public final void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public final boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public final void afterPropertiesSet() throws IOException {
        if (this.singleton) {
            this.singletonInstance = this.createProperties();
        }
    }

    @Override
    @Nullable
    public final Properties getObject() throws IOException {
        if (this.singleton) {
            return this.singletonInstance;
        }
        return this.createProperties();
    }

    @Override
    public Class<Properties> getObjectType() {
        return Properties.class;
    }

    protected Properties createProperties() throws IOException {
        return this.mergeProperties();
    }
}

