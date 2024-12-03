/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.monitoring;

import com.sun.jersey.spi.monitoring.GlassfishMonitoringProvider;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import com.sun.jersey.spi.service.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlassFishMonitoringInitializer {
    private static final Logger LOGGER = Logger.getLogger(GlassFishMonitoringInitializer.class.getName());

    public static void initialize() {
        try {
            for (GlassfishMonitoringProvider monitoring : ServiceFinder.find(GlassfishMonitoringProvider.class)) {
                monitoring.register();
            }
        }
        catch (ServiceConfigurationError ex) {
            LOGGER.log(Level.CONFIG, "GlassFish Jersey monitoring could not be enabled. Processing will continue but montoring is disabled.", ex);
        }
    }
}

