/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractServiceImporterProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;
import org.eclipse.gemini.blueprint.service.importer.support.DisposableBeanRunnableAdapter;
import org.eclipse.gemini.blueprint.service.importer.support.MemberType;
import org.eclipse.gemini.blueprint.service.importer.support.StaticServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.CollectionProxy;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceCollection;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceList;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceSet;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceSortedList;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceSortedSet;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterController;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterInternalActions;
import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.springframework.util.Assert;

public final class OsgiServiceCollectionProxyFactoryBean
extends AbstractServiceImporterProxyFactoryBean {
    private static final Log log = LogFactory.getLog(OsgiServiceCollectionProxyFactoryBean.class);
    private CollectionProxy exposedProxy;
    private Runnable proxyDestructionCallback;
    private Runnable initializationCallback;
    private ServiceProxyCreator proxyCreator;
    private Comparator comparator;
    private CollectionType collectionType = CollectionType.LIST;
    private boolean greedyProxying = false;
    private MemberType memberType = MemberType.SERVICE_OBJECT;
    private final List<ImporterStateListener> stateListeners = Collections.synchronizedList(new ArrayList(4));
    private final ImporterInternalActions controller = new ImporterController(new Executor());

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.proxyCreator = new StaticServiceProxyCreator(this.getInterfaces(), this.getAopClassLoader(), this.getBeanClassLoader(), this.getBundleContext(), this.getImportContextClassLoader(), this.greedyProxying, this.isUseBlueprintExceptions());
    }

    @Override
    Object createProxy(boolean lazyProxy) {
        Collection delegate;
        OsgiServiceCollection collection;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Creating a multi-value/collection proxy");
        }
        BundleContext bundleContext = this.getBundleContext();
        ClassLoader classLoader = this.getAopClassLoader();
        Filter filter = this.getUnifiedFilter();
        boolean useServiceReferences = MemberType.SERVICE_REFERENCE.equals((Object)this.memberType);
        if (CollectionType.LIST.equals((Object)this.collectionType)) {
            collection = this.comparator == null ? new OsgiServiceList(filter, bundleContext, classLoader, this.proxyCreator, useServiceReferences) : new OsgiServiceSortedList(filter, bundleContext, classLoader, this.comparator, this.proxyCreator, useServiceReferences);
            delegate = Collections.unmodifiableList((List)((Object)collection));
        } else if (CollectionType.SET.equals((Object)this.collectionType)) {
            collection = this.comparator == null ? new OsgiServiceSet(filter, bundleContext, classLoader, this.proxyCreator, useServiceReferences) : new OsgiServiceSortedSet(filter, bundleContext, classLoader, this.comparator, this.proxyCreator, useServiceReferences);
            delegate = Collections.unmodifiableSet((Set)((Object)collection));
        } else if (CollectionType.SORTED_LIST.equals((Object)this.collectionType)) {
            collection = new OsgiServiceSortedList(filter, bundleContext, classLoader, this.comparator, this.proxyCreator, useServiceReferences);
            delegate = Collections.unmodifiableList((List)((Object)collection));
        } else if (CollectionType.SORTED_SET.equals((Object)this.collectionType)) {
            collection = new OsgiServiceSortedSet(filter, bundleContext, classLoader, this.comparator, this.proxyCreator, useServiceReferences);
            delegate = Collections.unmodifiableSortedSet((SortedSet)((Object)collection));
        } else {
            throw new IllegalArgumentException("Unknown collection type:" + (Object)((Object)this.collectionType));
        }
        this.proxy = delegate;
        collection.setRequiredAtStartup(Availability.MANDATORY.equals((Object)this.getAvailability()));
        collection.setListeners(this.getListeners());
        collection.setStateListeners(this.stateListeners);
        collection.setServiceImporter(this);
        collection.setServiceImporterName(this.getBeanName());
        collection.setUseBlueprintExceptions(this.isUseBlueprintExceptions());
        if (!lazyProxy) {
            collection.afterPropertiesSet();
        } else {
            final OsgiServiceCollection col = collection;
            this.initializationCallback = new Runnable(){

                @Override
                public void run() {
                    col.afterPropertiesSet();
                }
            };
        }
        this.exposedProxy = collection;
        this.proxyDestructionCallback = new DisposableBeanRunnableAdapter(collection);
        return delegate;
    }

    @Override
    Runnable getProxyInitializer() {
        return this.initializationCallback;
    }

    @Override
    Runnable getProxyDestructionCallback() {
        return this.proxyDestructionCallback;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public void setCollectionType(CollectionType collectionType) {
        Assert.notNull((Object)((Object)collectionType));
        this.collectionType = collectionType;
    }

    public void setGreedyProxying(boolean greedyProxying) {
        this.greedyProxying = greedyProxying;
    }

    public MemberType getMemberType() {
        return this.memberType;
    }

    public void setMemberType(MemberType type) {
        Assert.notNull((Object)((Object)type));
        this.memberType = type;
    }

    private class Executor
    implements ImporterInternalActions {
        private Executor() {
        }

        @Override
        public void addStateListener(ImporterStateListener stateListener) {
            OsgiServiceCollectionProxyFactoryBean.this.stateListeners.add(stateListener);
        }

        @Override
        public void removeStateListener(ImporterStateListener stateListener) {
            OsgiServiceCollectionProxyFactoryBean.this.stateListeners.remove(stateListener);
        }

        @Override
        public boolean isSatisfied() {
            return OsgiServiceCollectionProxyFactoryBean.this.exposedProxy == null ? true : OsgiServiceCollectionProxyFactoryBean.this.exposedProxy.isSatisfied();
        }
    }
}

