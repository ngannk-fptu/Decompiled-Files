/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.init;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.init.RepositoriesPopulatedEvent;
import org.springframework.data.repository.init.RepositoryPopulator;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.data.repository.support.DefaultRepositoryInvokerFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ResourceReaderRepositoryPopulator
implements RepositoryPopulator,
ApplicationEventPublisherAware {
    private static final Log logger = LogFactory.getLog(ResourceReaderRepositoryPopulator.class);
    private final ResourceReader reader;
    @Nullable
    private final ClassLoader classLoader;
    private final ResourcePatternResolver resolver;
    @Nullable
    private ApplicationEventPublisher publisher;
    private Collection<Resource> resources = Collections.emptySet();

    public ResourceReaderRepositoryPopulator(ResourceReader reader) {
        this(reader, null);
    }

    public ResourceReaderRepositoryPopulator(ResourceReader reader, @Nullable ClassLoader classLoader) {
        Assert.notNull((Object)reader, (String)"Reader must not be null!");
        this.reader = reader;
        this.classLoader = classLoader;
        this.resolver = classLoader == null ? new PathMatchingResourcePatternResolver() : new PathMatchingResourcePatternResolver(classLoader);
    }

    public void setResourceLocation(String location) throws IOException {
        Assert.hasText((String)location, (String)"Location must not be null!");
        this.setResources(this.resolver.getResources(location));
    }

    public void setResources(Resource ... resources) {
        this.resources = Arrays.asList(resources);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void populate(Repositories repositories) {
        Assert.notNull((Object)repositories, (String)"Repositories must not be null!");
        DefaultRepositoryInvokerFactory invokerFactory = new DefaultRepositoryInvokerFactory(repositories);
        for (Resource resource : this.resources) {
            logger.info((Object)String.format("Reading resource: %s", resource));
            Object result = this.readObjectFrom(resource);
            if (result instanceof Collection) {
                for (Object element : (Collection)result) {
                    if (element != null) {
                        this.persist(element, invokerFactory);
                        continue;
                    }
                    logger.info((Object)"Skipping null element found in unmarshal result!");
                }
                continue;
            }
            this.persist(result, invokerFactory);
        }
        if (this.publisher != null) {
            this.publisher.publishEvent((ApplicationEvent)new RepositoriesPopulatedEvent(this, repositories));
        }
    }

    private Object readObjectFrom(Resource resource) {
        try {
            return this.reader.readFrom(resource, this.classLoader);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void persist(Object object, RepositoryInvokerFactory invokerFactory) {
        RepositoryInvoker invoker = invokerFactory.getInvokerFor(object.getClass());
        logger.debug((Object)String.format("Persisting %s using repository %s", object, invoker));
        invoker.invokeSave(object);
    }
}

