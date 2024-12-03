/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.init;

import javax.annotation.Nonnull;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.RepositoryPopulator;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractRepositoryPopulatorFactoryBean
extends AbstractFactoryBean<ResourceReaderRepositoryPopulator>
implements ApplicationListener<ContextRefreshedEvent>,
ApplicationContextAware {
    private Resource[] resources = new Resource[0];
    @Nullable
    private RepositoryPopulator populator;
    @Nullable
    private ApplicationContext context;

    public void setResources(Resource[] resources) {
        Assert.notNull((Object)resources, (String)"Resources must not be null!");
        this.resources = (Resource[])resources.clone();
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Nonnull
    public Class<?> getObjectType() {
        return ResourceReaderRepositoryPopulator.class;
    }

    protected ResourceReaderRepositoryPopulator createInstance() {
        ResourceReaderRepositoryPopulator initializer = new ResourceReaderRepositoryPopulator(this.getResourceReader());
        initializer.setResources(this.resources);
        if (this.context != null) {
            initializer.setApplicationEventPublisher((ApplicationEventPublisher)this.context);
        }
        this.populator = initializer;
        return initializer;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        RepositoryPopulator populator = this.populator;
        if (populator == null) {
            throw new IllegalStateException("RepositoryPopulator was not properly initialized!");
        }
        if (event.getApplicationContext().equals(this.context)) {
            Repositories repositories = new Repositories((ListableBeanFactory)event.getApplicationContext());
            populator.populate(repositories);
        }
    }

    protected abstract ResourceReader getResourceReader();

    public void afterPropertiesSet() throws Exception {
        Assert.state((this.resources != null ? 1 : 0) != 0, (String)"Resources must not be null!");
        super.afterPropertiesSet();
    }
}

