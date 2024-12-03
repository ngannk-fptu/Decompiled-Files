/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.PackageScannerConfiguration
 *  com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.servlet.ServletContext
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PropertiesLoaderUtils
 *  org.springframework.web.context.ServletContextAware
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.setup.PackageScannerConfigurationYamlReader;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.plugin.osgi.container.PackageScannerConfiguration;
import com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.ServletContextAware;

@Configuration
public class PackageScannerConfigurationAppConfig
implements ServletContextAware {
    @Value(value="classpath:package-exports.yaml")
    private Resource packageExportsResource;
    @Value(value="classpath:public-api.yaml")
    private Resource publicApiResource;
    @Value(value="classpath:package-versions.properties")
    private Resource packageVersionsResource;
    @Value(value="${confluence.osgi.treatDeprecatedPackagesAsPublic:true}")
    private boolean treatDeprecatedPackagesAsPublic;
    private ServletContext servletContext;

    @Bean
    @AvailableToPlugins(value=PackageScannerConfiguration.class)
    PackageScannerConfiguration packageScanningConfiguration() throws IOException {
        DefaultPackageScannerConfiguration config = new DefaultPackageScannerConfiguration(BuildInformation.INSTANCE.getBuildNumber());
        config.setServletContext(this.servletContext);
        config.setTreatDeprecatedPackagesAsPublic(this.treatDeprecatedPackagesAsPublic || ConfluenceSystemProperties.isDevMode());
        config.setPackageVersions(this.loadPackageVersions());
        PackageScannerConfigurationYamlReader.populatePackages(config, this.packageExportsResource, this.publicApiResource);
        return config;
    }

    private Map<String, String> loadPackageVersions() throws IOException {
        ImmutableMap versions = Maps.fromProperties((Properties)PropertiesLoaderUtils.loadProperties((Resource)this.packageVersionsResource));
        versions.forEach((key, value) -> Preconditions.checkState((!value.startsWith("$") ? 1 : 0) != 0, (String)"Unresolved version placeholder in %s for key %s", (Object)this.packageVersionsResource, (Object)key));
        return versions;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

