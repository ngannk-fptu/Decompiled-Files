/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiBundleUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Version
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class LoggingActivator
implements BundleActivator {
    private final Log log = LogFactory.getLog(this.getClass());

    public void start(BundleContext extenderBundleContext) throws Exception {
        Version extenderVersion = OsgiBundleUtils.getBundleVersion((Bundle)extenderBundleContext.getBundle());
        this.log.info((Object)("Starting [" + extenderBundleContext.getBundle().getSymbolicName() + "] bundle v.[" + extenderVersion + "]"));
    }

    public void stop(BundleContext extenderBundleContext) throws Exception {
        Version extenderVersion = OsgiBundleUtils.getBundleVersion((Bundle)extenderBundleContext.getBundle());
        this.log.info((Object)("Stopping [" + extenderBundleContext.getBundle().getSymbolicName() + "] bundle v.[" + extenderVersion + "]"));
    }
}

