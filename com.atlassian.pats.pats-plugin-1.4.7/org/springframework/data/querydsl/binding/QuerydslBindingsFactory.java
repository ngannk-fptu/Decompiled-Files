/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.data.querydsl.binding;

import com.querydsl.core.types.EntityPath;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizerDefaults;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public class QuerydslBindingsFactory
implements ApplicationContextAware {
    private static final String INVALID_DOMAIN_TYPE = "Unable to find Querydsl root type for detected domain type %s! User @%s's root attribute to define the domain type manually!";
    private final EntityPathResolver entityPathResolver;
    private final Map<TypeInformation<?>, EntityPath<?>> entityPaths;
    private Optional<AutowireCapableBeanFactory> beanFactory;
    private Optional<Repositories> repositories;
    private QuerydslBinderCustomizer<EntityPath<?>> defaultCustomizer;

    public QuerydslBindingsFactory(EntityPathResolver entityPathResolver) {
        Assert.notNull((Object)entityPathResolver, (String)"EntityPathResolver must not be null!");
        this.entityPathResolver = entityPathResolver;
        this.entityPaths = new ConcurrentReferenceHashMap();
        this.beanFactory = Optional.empty();
        this.repositories = Optional.empty();
        this.defaultCustomizer = NoOpCustomizer.INSTANCE;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = Optional.of(applicationContext.getAutowireCapableBeanFactory());
        this.repositories = Optional.of(new Repositories((ListableBeanFactory)applicationContext));
        this.defaultCustomizer = this.findDefaultCustomizer();
    }

    public EntityPathResolver getEntityPathResolver() {
        return this.entityPathResolver;
    }

    public QuerydslBindings createBindingsFor(TypeInformation<?> domainType) {
        return this.createBindingsFor(domainType, Optional.empty());
    }

    public QuerydslBindings createBindingsFor(TypeInformation<?> domainType, Class<? extends QuerydslBinderCustomizer<?>> customizer) {
        return this.createBindingsFor(domainType, Optional.of(customizer));
    }

    private QuerydslBindings createBindingsFor(TypeInformation<?> domainType, Optional<Class<? extends QuerydslBinderCustomizer<?>>> customizer) {
        Assert.notNull(customizer, (String)"Customizer must not be null!");
        Assert.notNull(domainType, (String)"Domain type must not be null!");
        EntityPath<?> path = this.verifyEntityPathPresent(domainType);
        QuerydslBindings bindings = new QuerydslBindings();
        this.defaultCustomizer.customize(bindings, path);
        this.findCustomizerForDomainType(customizer, domainType.getType()).customize(bindings, path);
        return bindings;
    }

    private EntityPath<?> verifyEntityPathPresent(TypeInformation<?> candidate) {
        return this.entityPaths.computeIfAbsent(candidate, key -> {
            try {
                return this.entityPathResolver.createPath(key.getType());
            }
            catch (IllegalArgumentException o_O) {
                throw new IllegalStateException(String.format(INVALID_DOMAIN_TYPE, key.getType(), QuerydslPredicate.class.getSimpleName()), o_O);
            }
        });
    }

    private QuerydslBinderCustomizer<EntityPath<?>> findDefaultCustomizer() {
        return this.beanFactory.map(this::getDefaultQuerydslBinderCustomizer).orElse(NoOpCustomizer.INSTANCE);
    }

    private QuerydslBinderCustomizer<EntityPath<?>> getDefaultQuerydslBinderCustomizer(AutowireCapableBeanFactory beanFactory) {
        List customizers = beanFactory.getBeanProvider(QuerydslBinderCustomizerDefaults.class).stream().collect(Collectors.toList());
        return (bindings, root) -> {
            for (QuerydslBinderCustomizerDefaults querydslBinderCustomizerDefaults : customizers) {
                querydslBinderCustomizerDefaults.customize(bindings, root);
            }
        };
    }

    private QuerydslBinderCustomizer<EntityPath<?>> findCustomizerForDomainType(Optional<? extends Class<? extends QuerydslBinderCustomizer>> customizer, Class<?> domainType) {
        return customizer.filter(it -> !QuerydslBinderCustomizer.class.equals(it)).map(this::createQuerydslBinderCustomizer).orElseGet(() -> this.repositories.flatMap(it -> it.getRepositoryFor(domainType)).map(it -> it instanceof QuerydslBinderCustomizer ? (QuerydslBinderCustomizer)it : null).orElse(NoOpCustomizer.INSTANCE));
    }

    private QuerydslBinderCustomizer<EntityPath<?>> createQuerydslBinderCustomizer(Class<? extends QuerydslBinderCustomizer> type) {
        return this.beanFactory.map(it -> {
            try {
                return (QuerydslBinderCustomizer)it.getBean(type);
            }
            catch (NoSuchBeanDefinitionException e) {
                return (QuerydslBinderCustomizer)it.createBean(type);
            }
        }).orElseGet(() -> (QuerydslBinderCustomizer)BeanUtils.instantiateClass((Class)type));
    }

    private static enum NoOpCustomizer implements QuerydslBinderCustomizer<EntityPath<?>>
    {
        INSTANCE;


        @Override
        public void customize(QuerydslBindings bindings, EntityPath<?> root) {
        }
    }
}

