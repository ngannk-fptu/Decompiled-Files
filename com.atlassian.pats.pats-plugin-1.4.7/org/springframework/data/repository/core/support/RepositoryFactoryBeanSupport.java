/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.EventPublishingRepositoryProxyPostProcessor;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.ExtensionAwareQueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;

public abstract class RepositoryFactoryBeanSupport<T extends Repository<S, ID>, S, ID>
implements InitializingBean,
RepositoryFactoryInformation<S, ID>,
FactoryBean<T>,
BeanClassLoaderAware,
BeanFactoryAware,
ApplicationEventPublisherAware {
    private final Class<? extends T> repositoryInterface;
    private RepositoryFactorySupport factory;
    private QueryLookupStrategy.Key queryLookupStrategyKey;
    private Optional<Class<?>> repositoryBaseClass = Optional.empty();
    private Optional<Object> customImplementation = Optional.empty();
    private Optional<RepositoryComposition.RepositoryFragments> repositoryFragments = Optional.empty();
    private NamedQueries namedQueries;
    private Optional<MappingContext<?, ?>> mappingContext = Optional.empty();
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private boolean lazyInit = false;
    private Optional<QueryMethodEvaluationContextProvider> evaluationContextProvider = Optional.empty();
    private List<RepositoryFactoryCustomizer> repositoryFactoryCustomizers = new ArrayList<RepositoryFactoryCustomizer>();
    private ApplicationEventPublisher publisher;
    private Lazy<T> repository;
    private RepositoryMetadata repositoryMetadata;

    protected RepositoryFactoryBeanSupport(Class<? extends T> repositoryInterface) {
        Assert.notNull(repositoryInterface, (String)"Repository interface must not be null!");
        this.repositoryInterface = repositoryInterface;
    }

    public void setRepositoryBaseClass(Class<?> repositoryBaseClass) {
        this.repositoryBaseClass = Optional.ofNullable(repositoryBaseClass);
    }

    public void setQueryLookupStrategyKey(QueryLookupStrategy.Key queryLookupStrategyKey) {
        this.queryLookupStrategyKey = queryLookupStrategyKey;
    }

    public void setCustomImplementation(Object customImplementation) {
        this.customImplementation = Optional.of(customImplementation);
    }

    public void setRepositoryFragments(RepositoryComposition.RepositoryFragments repositoryFragments) {
        this.repositoryFragments = Optional.of(repositoryFragments);
    }

    public void setNamedQueries(NamedQueries namedQueries) {
        this.namedQueries = namedQueries;
    }

    protected void setMappingContext(MappingContext<?, ?> mappingContext) {
        this.mappingContext = Optional.of(mappingContext);
    }

    public void setEvaluationContextProvider(QueryMethodEvaluationContextProvider evaluationContextProvider) {
        this.evaluationContextProvider = Optional.of(evaluationContextProvider);
    }

    public void addRepositoryFactoryCustomizer(RepositoryFactoryCustomizer customizer) {
        Assert.notNull((Object)customizer, (String)"RepositoryFactoryCustomizer must not be null");
        this.repositoryFactoryCustomizers.add(customizer);
    }

    public void setLazyInit(boolean lazy) {
        this.lazyInit = lazy;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (!this.evaluationContextProvider.isPresent() && ListableBeanFactory.class.isInstance(beanFactory)) {
            this.evaluationContextProvider = this.createDefaultQueryMethodEvaluationContextProvider((ListableBeanFactory)beanFactory);
        }
    }

    protected Optional<QueryMethodEvaluationContextProvider> createDefaultQueryMethodEvaluationContextProvider(ListableBeanFactory beanFactory) {
        return Optional.of(new ExtensionAwareQueryMethodEvaluationContextProvider(beanFactory));
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public EntityInformation<S, ID> getEntityInformation() {
        return this.factory.getEntityInformation(this.repositoryMetadata.getDomainType());
    }

    @Override
    public RepositoryInformation getRepositoryInformation() {
        RepositoryComposition.RepositoryFragments fragments = this.customImplementation.map(xva$0 -> RepositoryComposition.RepositoryFragments.just(xva$0)).orElse(RepositoryComposition.RepositoryFragments.empty());
        return this.factory.getRepositoryInformation(this.repositoryMetadata, fragments);
    }

    @Override
    public PersistentEntity<?, ?> getPersistentEntity() {
        return this.mappingContext.orElseThrow(() -> new IllegalStateException("No MappingContext available!")).getRequiredPersistentEntity(this.repositoryMetadata.getDomainType());
    }

    @Override
    public List<QueryMethod> getQueryMethods() {
        return this.factory.getQueryMethods();
    }

    @Nonnull
    public T getObject() {
        return (T)((Repository)this.repository.get());
    }

    @Nonnull
    public Class<? extends T> getObjectType() {
        return this.repositoryInterface;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() {
        this.factory = this.createRepositoryFactory();
        this.factory.setQueryLookupStrategyKey(this.queryLookupStrategyKey);
        this.factory.setNamedQueries(this.namedQueries);
        this.factory.setEvaluationContextProvider(this.evaluationContextProvider.orElseGet(() -> QueryMethodEvaluationContextProvider.DEFAULT));
        this.factory.setBeanClassLoader(this.classLoader);
        this.factory.setBeanFactory(this.beanFactory);
        if (this.publisher != null) {
            this.factory.addRepositoryProxyPostProcessor(new EventPublishingRepositoryProxyPostProcessor(this.publisher));
        }
        this.repositoryBaseClass.ifPresent(this.factory::setRepositoryBaseClass);
        this.repositoryFactoryCustomizers.forEach(customizer -> customizer.customize(this.factory));
        RepositoryComposition.RepositoryFragments customImplementationFragment = this.customImplementation.map(xva$0 -> RepositoryComposition.RepositoryFragments.just(xva$0)).orElseGet(RepositoryComposition.RepositoryFragments::empty);
        RepositoryComposition.RepositoryFragments repositoryFragmentsToUse = this.repositoryFragments.orElseGet(RepositoryComposition.RepositoryFragments::empty).append(customImplementationFragment);
        this.repositoryMetadata = this.factory.getRepositoryMetadata(this.repositoryInterface);
        this.repository = Lazy.of(() -> (Repository)this.factory.getRepository(this.repositoryInterface, repositoryFragmentsToUse));
        this.mappingContext.ifPresent(it -> it.getPersistentEntity(this.repositoryMetadata.getDomainType()));
        if (!this.lazyInit) {
            this.repository.get();
        }
    }

    protected abstract RepositoryFactorySupport createRepositoryFactory();
}

