/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.ImmutableConfigurationInvocationHandler;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.sync.Synchronizer;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ConfigurationUtils {
    private static final String METHOD_CLONE = "clone";
    private static final Class<?>[] IMMUTABLE_CONFIG_IFCS = new Class[]{ImmutableConfiguration.class};
    private static final Class<?>[] IMMUTABLE_HIERARCHICAL_CONFIG_IFCS = new Class[]{ImmutableHierarchicalConfiguration.class};
    private static final EventSource DUMMY_EVENT_SOURCE = new EventSource(){

        @Override
        public <T extends Event> void addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
        }

        @Override
        public <T extends Event> boolean removeEventListener(EventType<T> eventType, EventListener<? super T> listener) {
            return false;
        }
    };
    private static final Log LOG = LogFactory.getLog(ConfigurationUtils.class);

    private ConfigurationUtils() {
    }

    public static void dump(ImmutableConfiguration configuration, PrintStream out) {
        ConfigurationUtils.dump(configuration, new PrintWriter(out));
    }

    public static void dump(Configuration configuration, PrintStream out) {
        ConfigurationUtils.dump((ImmutableConfiguration)configuration, out);
    }

    public static void dump(ImmutableConfiguration configuration, PrintWriter out) {
        Iterator<String> keys = configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = configuration.getProperty(key);
            out.print(key);
            out.print("=");
            out.print(value);
            if (!keys.hasNext()) continue;
            out.println();
        }
        out.flush();
    }

    public static void dump(Configuration configuration, PrintWriter out) {
        ConfigurationUtils.dump((ImmutableConfiguration)configuration, out);
    }

    public static String toString(ImmutableConfiguration configuration) {
        StringWriter writer = new StringWriter();
        ConfigurationUtils.dump(configuration, new PrintWriter(writer));
        return writer.toString();
    }

    public static String toString(Configuration configuration) {
        return ConfigurationUtils.toString((ImmutableConfiguration)configuration);
    }

    public static void copy(ImmutableConfiguration source, Configuration target) {
        source.getKeys().forEachRemaining(key -> target.setProperty((String)key, source.getProperty((String)key)));
    }

    public static void copy(Configuration source, Configuration target) {
        ConfigurationUtils.copy((ImmutableConfiguration)source, target);
    }

    public static void append(ImmutableConfiguration source, Configuration target) {
        source.getKeys().forEachRemaining(key -> target.addProperty((String)key, source.getProperty((String)key)));
    }

    public static void append(Configuration source, Configuration target) {
        ConfigurationUtils.append((ImmutableConfiguration)source, target);
    }

    public static HierarchicalConfiguration<?> convertToHierarchical(Configuration conf) {
        return ConfigurationUtils.convertToHierarchical(conf, null);
    }

    public static HierarchicalConfiguration<?> convertToHierarchical(Configuration conf, ExpressionEngine engine) {
        if (conf == null) {
            return null;
        }
        if (conf instanceof HierarchicalConfiguration) {
            HierarchicalConfiguration hc = (HierarchicalConfiguration)conf;
            if (engine != null) {
                hc.setExpressionEngine(engine);
            }
            return hc;
        }
        BaseHierarchicalConfiguration hc = new BaseHierarchicalConfiguration();
        if (engine != null) {
            hc.setExpressionEngine(engine);
        }
        hc.copy(conf);
        return hc;
    }

    public static Configuration cloneConfiguration(Configuration config) throws ConfigurationRuntimeException {
        if (config == null) {
            return null;
        }
        try {
            return (Configuration)ConfigurationUtils.clone(config);
        }
        catch (CloneNotSupportedException cnex) {
            throw new ConfigurationRuntimeException(cnex);
        }
    }

    public static Object cloneIfPossible(Object obj) {
        try {
            return ConfigurationUtils.clone(obj);
        }
        catch (Exception ex) {
            return obj;
        }
    }

    static Object clone(Object obj) throws CloneNotSupportedException {
        if (obj instanceof Cloneable) {
            try {
                Method m = obj.getClass().getMethod(METHOD_CLONE, new Class[0]);
                return m.invoke(obj, new Object[0]);
            }
            catch (NoSuchMethodException nmex) {
                throw new CloneNotSupportedException("No clone() method found for class" + obj.getClass().getName());
            }
            catch (IllegalAccessException | InvocationTargetException itex) {
                throw new ConfigurationRuntimeException(itex);
            }
        }
        throw new CloneNotSupportedException(obj.getClass().getName() + " does not implement Cloneable");
    }

    public static Synchronizer cloneSynchronizer(Synchronizer sync) {
        if (sync == null) {
            throw new IllegalArgumentException("Synchronizer must not be null!");
        }
        if (NoOpSynchronizer.INSTANCE == sync) {
            return sync;
        }
        try {
            return (Synchronizer)sync.getClass().newInstance();
        }
        catch (Exception ex) {
            LOG.info((Object)("Cannot create new instance of " + sync.getClass()));
            try {
                return (Synchronizer)ConfigurationUtils.clone(sync);
            }
            catch (CloneNotSupportedException cnex) {
                throw new ConfigurationRuntimeException("Cannot clone Synchronizer " + sync);
            }
        }
    }

    public static void enableRuntimeExceptions(Configuration src) {
        if (!(src instanceof EventSource)) {
            throw new IllegalArgumentException("Configuration must implement EventSource!");
        }
        ((EventSource)((Object)src)).addEventListener(ConfigurationErrorEvent.ANY, event -> {
            throw new ConfigurationRuntimeException(event.getCause());
        });
    }

    public static Class<?> loadClass(String clsName) throws ClassNotFoundException {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("Loading class " + clsName));
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            if (cl != null) {
                return cl.loadClass(clsName);
            }
        }
        catch (ClassNotFoundException cnfex) {
            LOG.info((Object)("Could not load class " + clsName + " using CCL. Falling back to default CL."), (Throwable)cnfex);
        }
        return ConfigurationUtils.class.getClassLoader().loadClass(clsName);
    }

    public static Class<?> loadClassNoEx(String clsName) {
        try {
            return ConfigurationUtils.loadClass(clsName);
        }
        catch (ClassNotFoundException cnfex) {
            throw new ConfigurationRuntimeException("Cannot load class " + clsName, cnfex);
        }
    }

    public static ImmutableConfiguration unmodifiableConfiguration(Configuration c) {
        return ConfigurationUtils.createUnmodifiableConfiguration(IMMUTABLE_CONFIG_IFCS, c);
    }

    public static ImmutableHierarchicalConfiguration unmodifiableConfiguration(HierarchicalConfiguration<?> c) {
        return (ImmutableHierarchicalConfiguration)ConfigurationUtils.createUnmodifiableConfiguration(IMMUTABLE_HIERARCHICAL_CONFIG_IFCS, c);
    }

    private static ImmutableConfiguration createUnmodifiableConfiguration(Class<?>[] ifcs, Configuration c) {
        return (ImmutableConfiguration)Proxy.newProxyInstance(ConfigurationUtils.class.getClassLoader(), ifcs, (InvocationHandler)new ImmutableConfigurationInvocationHandler(c));
    }

    public static EventSource asEventSource(Object obj, boolean mockIfUnsupported) {
        if (obj instanceof EventSource) {
            return (EventSource)obj;
        }
        if (!mockIfUnsupported) {
            throw new ConfigurationRuntimeException("Cannot cast to EventSource: " + obj);
        }
        return DUMMY_EVENT_SOURCE;
    }
}

