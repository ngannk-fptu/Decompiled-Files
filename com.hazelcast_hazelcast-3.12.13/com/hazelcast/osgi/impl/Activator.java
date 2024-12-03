/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package com.hazelcast.osgi.impl;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.osgi.impl.HazelcastInternalOSGiService;
import com.hazelcast.osgi.impl.HazelcastOSGiServiceImpl;
import java.lang.reflect.Method;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator
implements BundleActivator {
    private static final ILogger LOGGER = Logger.getLogger(Activator.class);
    private volatile HazelcastInternalOSGiService hazelcastOSGiService;

    public void start(BundleContext context) throws Exception {
        this.activateJavaxScripting(context);
        assert (this.hazelcastOSGiService == null) : "Hazelcast OSGI service should be null while starting!";
        this.hazelcastOSGiService = new HazelcastOSGiServiceImpl(context.getBundle());
        this.hazelcastOSGiService.activate();
    }

    public void stop(BundleContext context) throws Exception {
        assert (this.hazelcastOSGiService != null) : "Hazelcast OSGI service should not be null while stopping!";
        this.hazelcastOSGiService.deactivate();
        this.hazelcastOSGiService = null;
    }

    private void activateJavaxScripting(BundleContext context) throws Exception {
        if (Activator.isJavaxScriptingAvailable()) {
            Class clazz = context.getBundle().loadClass("com.hazelcast.osgi.impl.ScriptEngineActivator");
            Method register = clazz.getDeclaredMethod("registerOsgiScriptEngineManager", BundleContext.class);
            register.setAccessible(true);
            register.invoke((Object)clazz, context);
        } else {
            LOGGER.warning("javax.scripting is not available, scripts from Management Center cannot be executed!");
        }
    }

    static boolean isJavaxScriptingAvailable() {
        if (Boolean.getBoolean("hazelcast.osgi.jsr223.disabled")) {
            return false;
        }
        try {
            Class.forName("javax.script.ScriptEngineManager");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

