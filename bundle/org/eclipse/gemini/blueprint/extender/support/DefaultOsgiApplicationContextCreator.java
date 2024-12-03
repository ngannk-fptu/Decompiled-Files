/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class DefaultOsgiApplicationContextCreator
implements OsgiApplicationContextCreator {
    private static final Log log = LogFactory.getLog(DefaultOsgiApplicationContextCreator.class);
    private ConfigurationScanner configurationScanner = new DefaultConfigurationScanner();

    @Override
    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext bundleContext) throws Exception {
        Bundle bundle = bundleContext.getBundle();
        ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle, this.configurationScanner);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Created configuration " + config + " for bundle " + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle)));
        }
        if (!config.isSpringPoweredBundle()) {
            return null;
        }
        log.info((Object)("Discovered configurations " + ObjectUtils.nullSafeToString((Object[])config.getConfigurationLocations()) + " in bundle [" + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "]"));
        OsgiBundleXmlApplicationContext sdoac = new OsgiBundleXmlApplicationContext(config.getConfigurationLocations());
        sdoac.setBundleContext(bundleContext);
        sdoac.setPublishContextAsService(config.isPublishContextAsService());
        return sdoac;
    }

    public void setConfigurationScanner(ConfigurationScanner configurationScanner) {
        Assert.notNull((Object)configurationScanner);
        this.configurationScanner = configurationScanner;
    }
}

