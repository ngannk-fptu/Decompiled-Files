/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentLruCache
 */
package org.springframework.data.repository.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentLruCache;

public class Repositories
implements Iterable<Class<?>> {
    static final Repositories NONE = new Repositories();
    private static final RepositoryFactoryInformation<Object, Object> EMPTY_REPOSITORY_FACTORY_INFO = EmptyRepositoryFactoryInformation.INSTANCE;
    private static final String DOMAIN_TYPE_MUST_NOT_BE_NULL = "Domain type must not be null!";
    private final Optional<BeanFactory> beanFactory;
    private final Map<Class<?>, String> repositoryBeanNames;
    private final Map<Class<?>, RepositoryFactoryInformation<Object, Object>> repositoryFactoryInfos;
    private final ConcurrentLruCache<Class<?>, Class<?>> domainTypeMapping = new ConcurrentLruCache(64, this::getRepositoryDomainTypeFor);

    private Repositories() {
        this.beanFactory = Optional.empty();
        this.repositoryBeanNames = Collections.emptyMap();
        this.repositoryFactoryInfos = Collections.emptyMap();
    }

    public Repositories(ListableBeanFactory factory) {
        Assert.notNull((Object)factory, (String)"ListableBeanFactory must not be null!");
        this.beanFactory = Optional.of(factory);
        this.repositoryFactoryInfos = new HashMap();
        this.repositoryBeanNames = new HashMap();
        this.populateRepositoryFactoryInformation(factory);
    }

    private void populateRepositoryFactoryInformation(ListableBeanFactory factory) {
        for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)factory, RepositoryFactoryInformation.class, (boolean)false, (boolean)false)) {
            this.cacheRepositoryFactory(name);
        }
    }

    private synchronized void cacheRepositoryFactory(String name) {
        RepositoryFactoryInformation repositoryFactoryInformation = (RepositoryFactoryInformation)this.beanFactory.get().getBean(name, RepositoryFactoryInformation.class);
        Class domainType = ClassUtils.getUserClass(repositoryFactoryInformation.getRepositoryInformation().getDomainType());
        RepositoryInformation information = repositoryFactoryInformation.getRepositoryInformation();
        Set<Class<?>> alternativeDomainTypes = information.getAlternativeDomainTypes();
        HashSet typesToRegister = new HashSet(alternativeDomainTypes.size() + 1);
        typesToRegister.add(domainType);
        typesToRegister.addAll(alternativeDomainTypes);
        for (Class clazz : typesToRegister) {
            this.cacheFirstOrPrimary(clazz, repositoryFactoryInformation, BeanFactoryUtils.transformedBeanName((String)name));
        }
    }

    public boolean hasRepositoryFor(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        Class userClass = (Class)this.domainTypeMapping.get(ProxyUtils.getUserClass(domainClass));
        return this.repositoryFactoryInfos.containsKey(userClass);
    }

    public Optional<Object> getRepositoryFor(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        Class userClass = (Class)this.domainTypeMapping.get(ProxyUtils.getUserClass(domainClass));
        Optional<String> repositoryBeanName = Optional.ofNullable(this.repositoryBeanNames.get(userClass));
        return this.beanFactory.flatMap(it -> repositoryBeanName.map(arg_0 -> ((BeanFactory)it).getBean(arg_0)));
    }

    private RepositoryFactoryInformation<Object, Object> getRepositoryFactoryInfoFor(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        Class userType = (Class)this.domainTypeMapping.get(ProxyUtils.getUserClass(domainClass));
        RepositoryFactoryInformation<Object, Object> repositoryInfo = this.repositoryFactoryInfos.get(userType);
        if (repositoryInfo != null) {
            return repositoryInfo;
        }
        if (!userType.equals(Object.class)) {
            return this.getRepositoryFactoryInfoFor(userType.getSuperclass());
        }
        return EMPTY_REPOSITORY_FACTORY_INFO;
    }

    public <T, S> EntityInformation<T, S> getEntityInformationFor(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        return this.getRepositoryFactoryInfoFor(domainClass).getEntityInformation();
    }

    public Optional<RepositoryInformation> getRepositoryInformationFor(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        RepositoryFactoryInformation<Object, Object> information = this.getRepositoryFactoryInfoFor(domainClass);
        return information == EMPTY_REPOSITORY_FACTORY_INFO ? Optional.empty() : Optional.of(information.getRepositoryInformation());
    }

    public RepositoryInformation getRequiredRepositoryInformation(Class<?> domainType) {
        return this.getRepositoryInformationFor(domainType).orElseThrow(() -> new IllegalArgumentException("No required RepositoryInformation found for domain type " + domainType.getName() + "!"));
    }

    public Optional<RepositoryInformation> getRepositoryInformation(Class<?> repositoryInterface) {
        return this.repositoryFactoryInfos.values().stream().map(RepositoryFactoryInformation::getRepositoryInformation).filter(information -> information.getRepositoryInterface().equals(repositoryInterface)).findFirst();
    }

    public PersistentEntity<?, ?> getPersistentEntity(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        return this.getRepositoryFactoryInfoFor(domainClass).getPersistentEntity();
    }

    public List<QueryMethod> getQueryMethodsFor(Class<?> domainClass) {
        Assert.notNull(domainClass, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        return this.getRepositoryFactoryInfoFor(domainClass).getQueryMethods();
    }

    @Override
    public Iterator<Class<?>> iterator() {
        return this.repositoryFactoryInfos.keySet().iterator();
    }

    private void cacheFirstOrPrimary(Class<?> type, RepositoryFactoryInformation information, String name) {
        Optional<ConfigurableListableBeanFactory> factoryToUse;
        Boolean presentAndPrimary;
        if (this.repositoryBeanNames.containsKey(type) && !(presentAndPrimary = (factoryToUse = this.beanFactory.map(it -> {
            if (it instanceof ConfigurableListableBeanFactory) {
                return (ConfigurableListableBeanFactory)it;
            }
            if (it instanceof ConfigurableApplicationContext) {
                return ((ConfigurableApplicationContext)it).getBeanFactory();
            }
            return null;
        })).map(it -> it.getMergedBeanDefinition(name)).map(BeanDefinition::isPrimary).orElse(false)).booleanValue()) {
            return;
        }
        this.repositoryFactoryInfos.put(type, information);
        this.repositoryBeanNames.put(type, name);
    }

    private Class<?> getRepositoryDomainTypeFor(Class<?> domainType) {
        Assert.notNull(domainType, (String)DOMAIN_TYPE_MUST_NOT_BE_NULL);
        Set<Class<?>> declaredTypes = this.repositoryBeanNames.keySet();
        if (declaredTypes.contains(domainType)) {
            return domainType;
        }
        for (Class<?> declaredType : declaredTypes) {
            if (!declaredType.isAssignableFrom(domainType)) continue;
            return declaredType;
        }
        return domainType;
    }

    private static enum EmptyRepositoryFactoryInformation implements RepositoryFactoryInformation<Object, Object>
    {
        INSTANCE;


        @Override
        public EntityInformation<Object, Object> getEntityInformation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public RepositoryInformation getRepositoryInformation() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PersistentEntity<?, ?> getPersistentEntity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<QueryMethod> getQueryMethods() {
            return Collections.emptyList();
        }
    }
}

