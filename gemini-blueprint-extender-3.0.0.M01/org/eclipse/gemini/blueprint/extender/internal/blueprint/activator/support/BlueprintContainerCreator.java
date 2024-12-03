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
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintContainerConfig;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.ObjectUtils;

public class BlueprintContainerCreator
implements OsgiApplicationContextCreator {
    private static final Log log = LogFactory.getLog(BlueprintContainerCreator.class);

    @Override
    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(BundleContext bundleContext) throws Exception {
        Bundle bundle = bundleContext.getBundle();
        BlueprintContainerConfig config = new BlueprintContainerConfig(bundle);
        String bundleName = OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Created configuration " + config + " for bundle " + bundleName));
        }
        if (!config.isSpringPoweredBundle()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("No blueprint configuration found in bundle " + bundleName + "; ignoring it..."));
            }
            return null;
        }
        log.info((Object)("Discovered configurations " + ObjectUtils.nullSafeToString((Object[])config.getConfigurationLocations()) + " in bundle [" + bundleName + "]"));
        OsgiBundleXmlApplicationContext sdoac = new OsgiBundleXmlApplicationContext(config.getConfigurationLocations());
        sdoac.setBundleContext(bundleContext);
        sdoac.setPublishContextAsService(((ApplicationContextConfiguration)config).isPublishContextAsService());
        return sdoac;
    }
}

