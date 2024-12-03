/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.template.Configuration
 *  freemarker.template.TemplateException
 */
package org.springframework.ui.freemarker;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

public class FreeMarkerConfigurationFactoryBean
extends FreeMarkerConfigurationFactory
implements FactoryBean<Configuration>,
InitializingBean,
ResourceLoaderAware {
    @Nullable
    private Configuration configuration;

    @Override
    public void afterPropertiesSet() throws IOException, TemplateException {
        this.configuration = this.createConfiguration();
    }

    @Override
    @Nullable
    public Configuration getObject() {
        return this.configuration;
    }

    @Override
    public Class<? extends Configuration> getObjectType() {
        return Configuration.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

