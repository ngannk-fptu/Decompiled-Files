/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.internal.management.ScriptEngineManagerContext;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.osgi.impl.OSGiScriptEngineManager;
import org.osgi.framework.BundleContext;

final class ScriptEngineActivator {
    private static final ILogger LOGGER = Logger.getLogger(ScriptEngineActivator.class);

    private ScriptEngineActivator() {
    }

    public static void registerOsgiScriptEngineManager(BundleContext context) {
        OSGiScriptEngineManager scriptEngineManager = new OSGiScriptEngineManager(context);
        ScriptEngineManagerContext.setScriptEngineManager(scriptEngineManager);
        if (LOGGER.isFinestEnabled()) {
            LOGGER.finest(scriptEngineManager.printScriptEngines());
        }
    }
}

