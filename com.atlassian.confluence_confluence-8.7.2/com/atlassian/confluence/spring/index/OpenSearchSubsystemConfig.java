/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.ImportResource
 */
package com.atlassian.confluence.spring.index;

import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.impl.osgi.OsgiProxyFactory;
import com.atlassian.confluence.internal.index.config.ConditionalOnSearchPlatform;
import com.atlassian.confluence.internal.index.opensearch.NoopSplitIndexUpgradeTask;
import com.atlassian.confluence.journal.ExportedJournalStateStore;
import com.atlassian.confluence.search.SearchPlatform;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ConditionalOnSearchPlatform(value=SearchPlatform.OPENSEARCH)
@ImportResource(value={"classpath:/index/openSearchSubsystemContext.xml"})
public class OpenSearchSubsystemConfig
implements DisposableBean {
    private final OsgiContainerManager osgiContainerManager;
    private final List<ServiceTracker> openedTrackers = new ArrayList<ServiceTracker>();

    public OpenSearchSubsystemConfig(OsgiContainerManager osgiContainerManager) {
        this.osgiContainerManager = osgiContainerManager;
    }

    @Bean
    public SearchManager searchManagerImpl() {
        return this.importBeanName(SearchManager.class, "openSearchSearchManager");
    }

    @Bean
    public SearchIndexAccessor contentSearchIndexAccessor() {
        return this.importBeanName(SearchIndexAccessor.class, "contentSearchIndexAccessor");
    }

    @Bean
    public SearchIndexAccessor changeSearchIndexAccessor() {
        return this.importBeanName(SearchIndexAccessor.class, "changeSearchIndexAccessor");
    }

    @Bean
    public CustomSearchIndexRegistry customSearchIndexRegistry() {
        return this.importBeanName(CustomSearchIndexRegistry.class, "customSearchIndexRegistry");
    }

    @Bean
    public JournalStateStore journalStateStore() {
        return this.importBeanName(ExportedJournalStateStore.class, "openSearchJournalStateStore");
    }

    @Bean
    public UpgradeTask splitIndexUpgradeTask() {
        return new NoopSplitIndexUpgradeTask();
    }

    @Bean
    public SearchPlatformConfig searchPlatformConfig() {
        return this.importBeanName(SearchPlatformConfig.class, "openSearchConfig");
    }

    private ServiceTracker<?, ?> getServiceTrackerForBeanName(String beanName) {
        Filter filter;
        try {
            filter = FrameworkUtil.createFilter((String)String.format("(%s=%s)", "org.eclipse.gemini.blueprint.bean.name", beanName));
        }
        catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid beanName", e);
        }
        BundleContext context = this.osgiContainerManager.getBundles()[0].getBundleContext();
        ServiceTracker tracker = new ServiceTracker(context, filter, null);
        tracker.open();
        this.openedTrackers.add(tracker);
        return tracker;
    }

    private <T> T importBeanName(Class<T> proxyType, String beanName) {
        return OsgiProxyFactory.createProxy(proxyType, () -> this.getServiceTrackerForBeanName(beanName));
    }

    public void destroy() throws Exception {
        for (ServiceTracker tracker : this.openedTrackers) {
            tracker.close();
        }
    }
}

