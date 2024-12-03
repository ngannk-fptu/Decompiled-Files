/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.atlassian.plugin.osgi.container.OsgiPersistentCache
 *  com.atlassian.plugin.osgi.container.PackageScannerConfiguration
 *  com.atlassian.plugin.osgi.container.felix.FelixOsgiContainerManager
 *  com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.plugin.spring;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.container.OsgiPersistentCache;
import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.osgi.container.felix.FelixOsgiContainerManager;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider;
import java.io.File;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

public class FelixOsgiContainerManagerFactory
implements ServletContextAware,
FactoryBean<FelixOsgiContainerManager> {
    private static final String FRAMEWORK_BUNDLES_LOCATION = "/WEB-INF/osgi-framework-bundles";
    private final PluginEventManager pluginEventManager;
    private final PackageScannerConfiguration packageScannerConfiguration;
    private final HostComponentProvider provider;
    private final OsgiPersistentCache osgiPersistentCache;
    private FelixOsgiContainerManager containerManager;
    private ServletContext servletContext;
    private static final Logger log = LoggerFactory.getLogger(FelixOsgiContainerManagerFactory.class);

    public FelixOsgiContainerManagerFactory(PackageScannerConfiguration packageScannerConfiguration, HostComponentProvider provider, PluginEventManager pluginEventManager, OsgiPersistentCache osgiPersistentCache) {
        this.packageScannerConfiguration = packageScannerConfiguration;
        this.provider = provider;
        this.pluginEventManager = pluginEventManager;
        this.osgiPersistentCache = osgiPersistentCache;
    }

    public FelixOsgiContainerManager getObject() throws Exception {
        if (this.containerManager == null) {
            this.containerManager = new FelixOsgiContainerManager(this.populatedFrameworkBundles(), this.osgiPersistentCache, this.packageScannerConfiguration, this.provider, this.pluginEventManager);
        }
        return this.containerManager;
    }

    private File populatedFrameworkBundles() {
        String osgiJarPath = this.servletContext.getRealPath(FRAMEWORK_BUNDLES_LOCATION);
        if (osgiJarPath != null) {
            return new File(osgiJarPath);
        }
        String message = String.format("Could not find framework bundles location. Expected to see it here: %s", FRAMEWORK_BUNDLES_LOCATION);
        log.error(message);
        throw new RuntimeException(message);
    }

    public Class getObjectType() {
        return OsgiContainerManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        this.containerManager.clearExportCache();
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

