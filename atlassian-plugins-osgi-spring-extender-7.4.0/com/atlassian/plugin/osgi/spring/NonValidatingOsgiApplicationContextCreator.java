/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator
 *  org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration
 *  org.eclipse.gemini.blueprint.extender.support.DefaultOsgiApplicationContextCreator
 *  org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner
 *  org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.util.ObjectUtils
 */
package com.atlassian.plugin.osgi.spring;

import com.atlassian.plugin.osgi.spring.external.ApplicationContextPreProcessor;
import java.util.List;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.extender.support.DefaultOsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.osgi.atlassian.NonValidatingOsgiBundleXmlApplicationContext;
import org.springframework.util.ObjectUtils;

public class NonValidatingOsgiApplicationContextCreator
implements OsgiApplicationContextCreator {
    private static final Logger log = LoggerFactory.getLogger(DefaultOsgiApplicationContextCreator.class);
    private final List<ApplicationContextPreProcessor> applicationContextPreProcessors;
    private ConfigurationScanner configurationScanner = new DefaultConfigurationScanner();

    public NonValidatingOsgiApplicationContextCreator(List<ApplicationContextPreProcessor> applicationContextPreProcessors) {
        this.applicationContextPreProcessors = applicationContextPreProcessors;
    }

    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext bundleContext) {
        Bundle bundle = bundleContext.getBundle();
        ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle, this.configurationScanner);
        if (log.isTraceEnabled()) {
            log.trace("Created configuration " + config + " for bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle));
        }
        if (!this.isSpringPoweredBundle(bundle, config)) {
            return null;
        }
        log.info("Discovered configurations " + ObjectUtils.nullSafeToString((Object[])config.getConfigurationLocations()) + " in bundle [" + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "]");
        NonValidatingOsgiBundleXmlApplicationContext sdoac = new NonValidatingOsgiBundleXmlApplicationContext(config.getConfigurationLocations());
        sdoac.setBundleContext(bundleContext);
        sdoac.setPublishContextAsService(config.isPublishContextAsService());
        for (ApplicationContextPreProcessor processor : this.applicationContextPreProcessors) {
            processor.process(bundle, (ConfigurableApplicationContext)sdoac);
        }
        return sdoac;
    }

    boolean isSpringPoweredBundle(Bundle bundle, ApplicationContextConfiguration config) {
        if (config.isSpringPoweredBundle()) {
            return true;
        }
        for (ApplicationContextPreProcessor processor : this.applicationContextPreProcessors) {
            if (!processor.isSpringPoweredBundle(bundle)) continue;
            return true;
        }
        return false;
    }
}

