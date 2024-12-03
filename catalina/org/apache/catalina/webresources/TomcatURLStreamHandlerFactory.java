/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.webresources.ClasspathURLStreamHandler;
import org.apache.catalina.webresources.war.Handler;

public class TomcatURLStreamHandlerFactory
implements URLStreamHandlerFactory {
    private static final String WAR_PROTOCOL = "war";
    private static final String CLASSPATH_PROTOCOL = "classpath";
    private static volatile TomcatURLStreamHandlerFactory instance = null;
    private final boolean registered;
    private final List<URLStreamHandlerFactory> userFactories = new CopyOnWriteArrayList<URLStreamHandlerFactory>();

    public static TomcatURLStreamHandlerFactory getInstance() {
        TomcatURLStreamHandlerFactory.getInstanceInternal(true);
        return instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static TomcatURLStreamHandlerFactory getInstanceInternal(boolean register) {
        if (instance != null) return instance;
        Class<TomcatURLStreamHandlerFactory> clazz = TomcatURLStreamHandlerFactory.class;
        synchronized (TomcatURLStreamHandlerFactory.class) {
            if (instance != null) return instance;
            instance = new TomcatURLStreamHandlerFactory(register);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return instance;
        }
    }

    public static boolean register() {
        return TomcatURLStreamHandlerFactory.getInstanceInternal(true).isRegistered();
    }

    public static boolean disable() {
        return !TomcatURLStreamHandlerFactory.getInstanceInternal(false).isRegistered();
    }

    public static void release(ClassLoader classLoader) {
        if (instance == null) {
            return;
        }
        List<URLStreamHandlerFactory> factories = TomcatURLStreamHandlerFactory.instance.userFactories;
        block0: for (URLStreamHandlerFactory factory : factories) {
            for (ClassLoader factoryLoader = factory.getClass().getClassLoader(); factoryLoader != null; factoryLoader = factoryLoader.getParent()) {
                if (!classLoader.equals(factoryLoader)) continue;
                factories.remove(factory);
                continue block0;
            }
        }
    }

    private TomcatURLStreamHandlerFactory(boolean register) {
        this.registered = register;
        if (register) {
            URL.setURLStreamHandlerFactory(this);
        }
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void addUserFactory(URLStreamHandlerFactory factory) {
        this.userFactories.add(factory);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (WAR_PROTOCOL.equals(protocol)) {
            return new Handler();
        }
        if (CLASSPATH_PROTOCOL.equals(protocol)) {
            return new ClasspathURLStreamHandler();
        }
        for (URLStreamHandlerFactory factory : this.userFactories) {
            URLStreamHandler handler = factory.createURLStreamHandler(protocol);
            if (handler == null) continue;
            return handler;
        }
        return null;
    }
}

