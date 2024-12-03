/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.format.support.DefaultFormattingConversionService
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.support;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.support.CrudRepositoryInvoker;
import org.springframework.data.repository.support.PagingAndSortingRepositoryInvoker;
import org.springframework.data.repository.support.ReflectionRepositoryInvoker;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.util.Optionals;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.Assert;

public class DefaultRepositoryInvokerFactory
implements RepositoryInvokerFactory {
    private final Repositories repositories;
    private final ConversionService conversionService;
    private final Map<Class<?>, RepositoryInvoker> invokers;

    public DefaultRepositoryInvokerFactory(Repositories repositories) {
        this(repositories, (ConversionService)new DefaultFormattingConversionService());
    }

    public DefaultRepositoryInvokerFactory(Repositories repositories, ConversionService conversionService) {
        Assert.notNull((Object)repositories, (String)"Repositories must not be null!");
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        this.repositories = repositories;
        this.conversionService = conversionService;
        this.invokers = new ConcurrentHashMap();
    }

    @Override
    public RepositoryInvoker getInvokerFor(Class<?> domainType) {
        return this.invokers.computeIfAbsent(domainType, this::prepareInvokers);
    }

    private RepositoryInvoker prepareInvokers(Class<?> domainType) {
        Optional<RepositoryInformation> information = this.repositories.getRepositoryInformationFor(domainType);
        Optional<Object> repository = this.repositories.getRepositoryFor(domainType);
        return Optionals.mapIfAllPresent(information, repository, this::createInvoker).orElseThrow(() -> new IllegalArgumentException(String.format("No repository found for domain type: %s", domainType)));
    }

    protected RepositoryInvoker createInvoker(RepositoryInformation information, Object repository) {
        if (repository instanceof PagingAndSortingRepository) {
            return new PagingAndSortingRepositoryInvoker((PagingAndSortingRepository)repository, (RepositoryMetadata)information, this.conversionService);
        }
        if (repository instanceof CrudRepository) {
            return new CrudRepositoryInvoker((CrudRepository)repository, (RepositoryMetadata)information, this.conversionService);
        }
        return new ReflectionRepositoryInvoker(repository, information, this.conversionService);
    }
}

