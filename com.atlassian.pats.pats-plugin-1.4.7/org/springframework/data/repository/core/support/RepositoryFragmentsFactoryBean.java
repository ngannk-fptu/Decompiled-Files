/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.util.Assert;

public class RepositoryFragmentsFactoryBean<T>
implements FactoryBean<RepositoryComposition.RepositoryFragments>,
BeanFactoryAware,
InitializingBean {
    private final List<String> fragmentBeanNames;
    private BeanFactory beanFactory;
    private RepositoryComposition.RepositoryFragments repositoryFragments = RepositoryComposition.RepositoryFragments.empty();

    public RepositoryFragmentsFactoryBean(List<String> fragmentBeanNames) {
        Assert.notNull(fragmentBeanNames, (String)"Fragment bean names must not be null!");
        this.fragmentBeanNames = fragmentBeanNames;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void afterPropertiesSet() {
        List<RepositoryFragment<?>> fragments = this.fragmentBeanNames.stream().map(it -> (RepositoryFragment)this.beanFactory.getBean(it, RepositoryFragment.class)).collect(Collectors.toList());
        this.repositoryFragments = RepositoryComposition.RepositoryFragments.from(fragments);
    }

    @Nonnull
    public RepositoryComposition.RepositoryFragments getObject() throws Exception {
        return this.repositoryFragments;
    }

    @Nonnull
    public Class<?> getObjectType() {
        return RepositoryComposition.class;
    }
}

