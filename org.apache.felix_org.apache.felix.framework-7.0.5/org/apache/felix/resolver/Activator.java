/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import org.apache.felix.resolver.Logger;
import org.apache.felix.resolver.ResolverImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.resolver.Resolver;

public class Activator
implements BundleActivator {
    public static final String LOG_LEVEL = "felix.resolver.log.level";

    @Override
    public void start(BundleContext bc) throws Exception {
        int logLevel = 4;
        if (bc.getProperty(LOG_LEVEL) != null) {
            try {
                logLevel = Integer.parseInt(bc.getProperty(LOG_LEVEL));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        bc.registerService(Resolver.class, new ResolverImpl(new Logger(logLevel)), null);
    }

    @Override
    public void stop(BundleContext bc) throws Exception {
    }
}

