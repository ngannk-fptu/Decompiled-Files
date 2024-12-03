/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceConsumer
 */
package org.apache.juli.logging;

import aQute.bnd.annotation.spi.ServiceConsumer;
import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.LogManager;
import org.apache.juli.logging.DirectJDKLog;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogConfigurationException;

@ServiceConsumer(value=Log.class)
public class LogFactory {
    private static final LogFactory singleton = new LogFactory();
    private final Constructor<? extends Log> discoveredLogConstructor;

    private LogFactory() {
        FileSystems.getDefault();
        ServiceLoader<Log> logLoader = ServiceLoader.load(Log.class);
        Constructor<?> m = null;
        Iterator<Log> iterator = logLoader.iterator();
        if (iterator.hasNext()) {
            Log log = iterator.next();
            Class<?> c = log.getClass();
            try {
                m = c.getConstructor(String.class);
            }
            catch (NoSuchMethodException | SecurityException e) {
                throw new Error(e);
            }
        }
        this.discoveredLogConstructor = m;
    }

    public Log getInstance(String name) throws LogConfigurationException {
        if (this.discoveredLogConstructor == null) {
            return DirectJDKLog.getInstance(name);
        }
        try {
            return this.discoveredLogConstructor.newInstance(name);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new LogConfigurationException(e);
        }
    }

    public Log getInstance(Class<?> clazz) throws LogConfigurationException {
        return this.getInstance(clazz.getName());
    }

    public static LogFactory getFactory() throws LogConfigurationException {
        return singleton;
    }

    public static Log getLog(Class<?> clazz) throws LogConfigurationException {
        return LogFactory.getFactory().getInstance(clazz);
    }

    public static Log getLog(String name) throws LogConfigurationException {
        return LogFactory.getFactory().getInstance(name);
    }

    public static void release(ClassLoader classLoader) {
        if (!LogManager.getLogManager().getClass().getName().equals("java.util.logging.LogManager")) {
            LogManager.getLogManager().reset();
        }
    }
}

