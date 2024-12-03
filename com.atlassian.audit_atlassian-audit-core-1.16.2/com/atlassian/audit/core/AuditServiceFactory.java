/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  javax.annotation.Nonnull
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.audit.core;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.core.BufferingAuditBroker;
import com.atlassian.audit.core.OsgiServiceSupplier;
import com.atlassian.audit.core.ReflectionAuditBroker;
import com.atlassian.audit.core.ecosystem.AllowlistedBundleDetector;
import com.atlassian.audit.core.ecosystem.BundleDetector;
import com.atlassian.audit.core.ecosystem.CallerAwareAuditService;
import com.atlassian.audit.core.ecosystem.InstallationBasedBundleDetector;
import com.atlassian.audit.core.impl.broker.AuditBroker;
import com.atlassian.audit.core.impl.service.ErrorIgnoredAuditClusterNodeProvider;
import com.atlassian.audit.core.impl.service.ErrorIgnoredAuditCurrentUserProvider;
import com.atlassian.audit.core.impl.service.ErrorIgnoredAuditIpAddressProvider;
import com.atlassian.audit.core.impl.service.ErrorIgnoredAuditMethodProvider;
import com.atlassian.audit.core.impl.service.ErrorIgnoredBaseUrlProvider;
import com.atlassian.audit.core.impl.service.SessionBasedAuditService;
import com.atlassian.audit.core.spi.service.AuditMethodProvider;
import com.atlassian.audit.core.spi.service.BaseUrlProvider;
import com.atlassian.audit.core.spi.service.ClusterNodeProvider;
import com.atlassian.audit.core.spi.service.CurrentUserProvider;
import com.atlassian.audit.core.spi.service.IpAddressProvider;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.osgi.framework.BundleContext;

public class AuditServiceFactory {
    private int downtimeBufferSize = Integer.getInteger("audit.broker.downtime.buffer.size", 100000);
    private final EventPublisher eventPublisher;
    private final CallingBundleResolver callingBundleResolver;
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;
    private final CurrentUserProvider userProvider;
    private final IpAddressProvider ipAddressProvider;
    private final AuditMethodProvider auditMethodProvider;
    private final BaseUrlProvider baseUrlProvider;
    private final ClusterNodeProvider clusterNodeProvider;
    private final Supplier<BundleContext> bundleContextSupplier;
    private final BundleDetector bundleDetector;

    public AuditServiceFactory(@Nonnull OsgiContainerManager manager, @Nonnull EventPublisher eventPublisher, @Nonnull CallingBundleResolver callingBundleResolver, @Nonnull PluginAccessor pluginAccessor, @Nonnull PluginMetadataManager pluginMetadataManager, @Nonnull CurrentUserProvider userProvider, @Nonnull IpAddressProvider ipAddressProvider, @Nonnull AuditMethodProvider auditMethodProvider, @Nonnull BaseUrlProvider baseUrlProvider, @Nonnull ClusterNodeProvider clusterNodeProvider) {
        this(() -> manager.getBundles()[0].getBundleContext(), eventPublisher, callingBundleResolver, pluginAccessor, pluginMetadataManager, userProvider, ipAddressProvider, auditMethodProvider, baseUrlProvider, clusterNodeProvider);
    }

    public AuditServiceFactory(@Nonnull Supplier<BundleContext> bundleContextSupplier, @Nonnull EventPublisher eventPublisher, @Nonnull CallingBundleResolver callingBundleResolver, @Nonnull PluginAccessor pluginAccessor, @Nonnull PluginMetadataManager pluginMetadataManager, @Nonnull CurrentUserProvider userProvider, @Nonnull IpAddressProvider ipAddressProvider, @Nonnull AuditMethodProvider auditMethodProvider, @Nonnull BaseUrlProvider baseUrlProvider, @Nonnull ClusterNodeProvider clusterNodeProvider) {
        this(bundleContextSupplier, eventPublisher, callingBundleResolver, pluginAccessor, pluginMetadataManager, userProvider, ipAddressProvider, auditMethodProvider, baseUrlProvider, clusterNodeProvider, Collections.emptyList());
    }

    public AuditServiceFactory(@Nonnull Supplier<BundleContext> bundleContextSupplier, @Nonnull EventPublisher eventPublisher, @Nonnull CallingBundleResolver callingBundleResolver, @Nonnull PluginAccessor pluginAccessor, @Nonnull PluginMetadataManager pluginMetadataManager, @Nonnull CurrentUserProvider userProvider, @Nonnull IpAddressProvider ipAddressProvider, @Nonnull AuditMethodProvider auditMethodProvider, @Nonnull BaseUrlProvider baseUrlProvider, @Nonnull ClusterNodeProvider clusterNodeProvider, @Nonnull Collection<String> allowlistedPluginKeys) {
        this.bundleContextSupplier = bundleContextSupplier;
        this.eventPublisher = eventPublisher;
        this.callingBundleResolver = callingBundleResolver;
        this.pluginAccessor = pluginAccessor;
        this.pluginMetadataManager = pluginMetadataManager;
        this.userProvider = new ErrorIgnoredAuditCurrentUserProvider(userProvider);
        this.ipAddressProvider = new ErrorIgnoredAuditIpAddressProvider(ipAddressProvider);
        this.auditMethodProvider = new ErrorIgnoredAuditMethodProvider(auditMethodProvider);
        this.baseUrlProvider = new ErrorIgnoredBaseUrlProvider(baseUrlProvider);
        this.clusterNodeProvider = new ErrorIgnoredAuditClusterNodeProvider(clusterNodeProvider);
        this.bundleDetector = this.createBundleDetector(allowlistedPluginKeys);
    }

    public void setDowntimeBufferSize(int downtimeBufferSize) {
        this.downtimeBufferSize = downtimeBufferSize;
    }

    public AuditService create() {
        OsgiServiceSupplier<Object, AuditBroker> realBrokerSupplier = new OsgiServiceSupplier<Object, AuditBroker>(this.bundleContextSupplier, this.eventPublisher, "com.atlassian.audit.broker.InternalAuditBroker", ReflectionAuditBroker::new);
        realBrokerSupplier.start();
        BufferingAuditBroker bufferedBroker = new BufferingAuditBroker(this.eventPublisher, realBrokerSupplier, this.downtimeBufferSize);
        bufferedBroker.start();
        SessionBasedAuditService sessionBasedService = new SessionBasedAuditService(bufferedBroker, this.userProvider, this.ipAddressProvider, this.auditMethodProvider, this.baseUrlProvider, this.clusterNodeProvider);
        return new CallerAwareAuditService(this.bundleDetector, this.callingBundleResolver, sessionBasedService);
    }

    private BundleDetector createBundleDetector(Collection<String> allowlistedPluginKeys) {
        return new AllowlistedBundleDetector(new InstallationBasedBundleDetector(this.pluginAccessor, this.pluginMetadataManager), allowlistedPluginKeys);
    }
}

