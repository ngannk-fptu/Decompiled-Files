/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class ClusterAwareBeanSelector
implements BeanFactoryAware {
    private final ClusterManager clusterManager;
    private BeanFactory beanFactory;

    public ClusterAwareBeanSelector(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public <T> T selectBean(Class<T> beanType, String beanNameForCluster, String beanNameForNonCluster) {
        String beanName = this.clusterManager.isClustered() ? beanNameForCluster : beanNameForNonCluster;
        return (T)this.beanFactory.getBean(beanName, beanType);
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

