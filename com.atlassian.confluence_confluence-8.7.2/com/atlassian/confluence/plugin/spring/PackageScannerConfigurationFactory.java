/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.PackageScannerConfiguration
 *  com.atlassian.plugin.spring.SpringAwarePackageScannerConfiguration
 *  com.google.common.collect.Maps
 *  javax.servlet.ServletContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.plugin.spring;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.spring.SpringAwarePackageScannerConfiguration;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

@Deprecated(forRemoval=true)
public class PackageScannerConfigurationFactory
implements FactoryBean,
ServletContextAware {
    private static final Logger log = LoggerFactory.getLogger(PackageScannerConfigurationFactory.class);
    private final List<String> packageIncludes;
    private final List<String> packageExcludes;
    private final Map<String, String> packageVersions;
    private final Set<String> publicPackages;
    private final Set<String> publicPackagesExcludes;
    private final Set<String> applicationBundledInternalPlugins;
    private ServletContext servletContext;

    public PackageScannerConfigurationFactory(List<String> packageIncludes, List<String> packageExcludes, Properties packageVersions, Set<String> publicPackages, Set<String> publicPackagesExcludes, Set<String> applicationBundledInternalPlugins) {
        this.packageIncludes = packageIncludes;
        this.packageExcludes = packageExcludes;
        this.packageVersions = null == packageVersions ? Collections.emptyMap() : PackageScannerConfigurationFactory.filterPackageVersions(packageVersions);
        this.publicPackages = publicPackages;
        this.publicPackagesExcludes = publicPackagesExcludes;
        this.applicationBundledInternalPlugins = applicationBundledInternalPlugins;
    }

    public Object getObject() {
        SpringAwarePackageScannerConfiguration config = new SpringAwarePackageScannerConfiguration(GeneralUtil.getVersionNumber());
        config.setPackageIncludes(this.packageIncludes);
        config.setPackageExcludes(this.packageExcludes);
        config.setServletContext(this.servletContext);
        config.setPackageVersions(this.packageVersions);
        config.setOsgiPublicPackages(this.publicPackages);
        config.setOsgiPublicPackagesExcludes(this.publicPackagesExcludes);
        config.setApplicationBundledInternalPlugins(this.applicationBundledInternalPlugins);
        return config;
    }

    public Class<PackageScannerConfiguration> getObjectType() {
        return PackageScannerConfiguration.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private static Map<String, String> filterPackageVersions(Properties packageVersions) {
        return Maps.newHashMap((Map)Maps.filterEntries((Map)Maps.fromProperties((Properties)packageVersions), entry -> {
            String version = (String)entry.getValue();
            if (null == version || version.startsWith("$")) {
                log.warn("Invalid package to version mapping for '{}'. Configured version: '{}'.", entry.getKey(), (Object)version);
                return false;
            }
            return true;
        }));
    }
}

