/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor
 *  org.springframework.transaction.interceptor.TransactionInterceptor
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.DirectoryCacheFactoryImpl;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class TransactionalDirectoryCacheFactory
extends DirectoryCacheFactoryImpl {
    private final TransactionInterceptor transactionInterceptor;

    public TransactionalDirectoryCacheFactory(DirectoryDao directoryDao, SynchronisationStatusManager synchronisationStatusManager, MultiEventPublisher eventPublisher, UserDao userDao, GroupDao groupDao, TransactionInterceptor transactionInterceptor, CrowdDarkFeatureManager crowdDarkFeatureManager) {
        super(directoryDao, synchronisationStatusManager, eventPublisher, userDao, groupDao, crowdDarkFeatureManager);
        this.transactionInterceptor = transactionInterceptor;
    }

    @Override
    public DirectoryCacheChangeOperations createDirectoryCacheChangeOperations(RemoteDirectory remoteDirectory, InternalRemoteDirectory internalDirectory) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvisor((Advisor)new TransactionAttributeSourceAdvisor(this.transactionInterceptor));
        proxyFactory.setInterfaces(new Class[]{DirectoryCacheChangeOperations.class});
        proxyFactory.setTarget((Object)super.createDirectoryCacheChangeOperations(remoteDirectory, internalDirectory));
        return (DirectoryCacheChangeOperations)proxyFactory.getProxy();
    }
}

