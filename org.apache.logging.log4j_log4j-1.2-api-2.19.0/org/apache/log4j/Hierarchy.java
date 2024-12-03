/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.spi.AbstractLoggerAdapter
 *  org.apache.logging.log4j.spi.LoggerContext
 *  org.apache.logging.log4j.util.StackLocatorUtil
 */
package org.apache.log4j;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.DefaultCategoryFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.legacy.core.ContextUtil;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;
import org.apache.logging.log4j.spi.AbstractLoggerAdapter;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class Hierarchy
implements LoggerRepository,
RendererSupport,
ThrowableRendererSupport {
    private static final PrivateLoggerAdapter LOGGER_ADAPTER = new PrivateLoggerAdapter();
    private static final WeakHashMap<LoggerContext, ConcurrentMap<String, Logger>> CONTEXT_MAP = new WeakHashMap();
    private final LoggerFactory defaultFactory;
    private final Vector listeners;
    Hashtable ht = new Hashtable();
    Logger root;
    RendererMap rendererMap;
    int thresholdInt;
    Level threshold;
    boolean emittedNoAppenderWarning;
    boolean emittedNoResourceBundleWarning;
    private ThrowableRenderer throwableRenderer;

    static LoggerContext getContext() {
        return PrivateLogManager.getContext();
    }

    private Logger getInstance(LoggerContext context, String name) {
        return this.getInstance(context, name, LOGGER_ADAPTER);
    }

    private Logger getInstance(LoggerContext context, String name, LoggerFactory factory) {
        return Hierarchy.getLoggersMap(context).computeIfAbsent(name, k -> {
            Logger logger = factory.makeNewLoggerInstance(name);
            logger.setHierarchy(this);
            return logger;
        });
    }

    private Logger getInstance(LoggerContext context, String name, PrivateLoggerAdapter factory) {
        return Hierarchy.getLoggersMap(context).computeIfAbsent(name, k -> {
            Logger logger = factory.newLogger(name, context);
            logger.setHierarchy(this);
            return logger;
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ConcurrentMap<String, Logger> getLoggersMap(LoggerContext context) {
        WeakHashMap<LoggerContext, ConcurrentMap<String, Logger>> weakHashMap = CONTEXT_MAP;
        synchronized (weakHashMap) {
            return CONTEXT_MAP.computeIfAbsent(context, k -> new ConcurrentHashMap());
        }
    }

    public Hierarchy(Logger root) {
        this.listeners = new Vector(1);
        this.root = root;
        this.setThreshold(Level.ALL);
        this.root.setHierarchy(this);
        this.rendererMap = new RendererMap();
        this.defaultFactory = new DefaultCategoryFactory();
    }

    @Override
    public void addHierarchyEventListener(HierarchyEventListener listener) {
        if (this.listeners.contains(listener)) {
            LogLog.warn("Ignoring attempt to add an existent listener.");
        } else {
            this.listeners.addElement(listener);
        }
    }

    public void addRenderer(Class classToRender, ObjectRenderer or) {
        this.rendererMap.put(classToRender, or);
    }

    public void clear() {
        this.ht.clear();
        Hierarchy.getLoggersMap(Hierarchy.getContext()).clear();
    }

    @Override
    public void emitNoAppenderWarning(Category cat) {
        if (!this.emittedNoAppenderWarning) {
            LogLog.warn("No appenders could be found for logger (" + cat.getName() + ").");
            LogLog.warn("Please initialize the log4j system properly.");
            LogLog.warn("See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.");
            this.emittedNoAppenderWarning = true;
        }
    }

    @Override
    public Logger exists(String name) {
        return this.exists(name, Hierarchy.getContext());
    }

    Logger exists(String name, ClassLoader classLoader) {
        return this.exists(name, this.getContext(classLoader));
    }

    Logger exists(String name, LoggerContext loggerContext) {
        if (!loggerContext.hasLogger(name)) {
            return null;
        }
        return Logger.getLogger(name);
    }

    @Override
    public void fireAddAppenderEvent(Category logger, Appender appender) {
        if (this.listeners != null) {
            int size = this.listeners.size();
            for (int i = 0; i < size; ++i) {
                HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
                listener.addAppenderEvent(logger, appender);
            }
        }
    }

    void fireRemoveAppenderEvent(Category logger, Appender appender) {
        if (this.listeners != null) {
            int size = this.listeners.size();
            for (int i = 0; i < size; ++i) {
                HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
                listener.removeAppenderEvent(logger, appender);
            }
        }
    }

    LoggerContext getContext(ClassLoader classLoader) {
        return LogManager.getContext(classLoader);
    }

    @Override
    @Deprecated
    public Enumeration getCurrentCategories() {
        return this.getCurrentLoggers();
    }

    @Override
    public Enumeration getCurrentLoggers() {
        return LogManager.getCurrentLoggers(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    @Override
    public Logger getLogger(String name) {
        return this.getInstance(Hierarchy.getContext(), name);
    }

    Logger getLogger(String name, ClassLoader classLoader) {
        return this.getInstance(this.getContext(classLoader), name);
    }

    @Override
    public Logger getLogger(String name, LoggerFactory factory) {
        return this.getInstance(Hierarchy.getContext(), name, factory);
    }

    Logger getLogger(String name, LoggerFactory factory, ClassLoader classLoader) {
        return this.getInstance(this.getContext(classLoader), name, factory);
    }

    @Override
    public RendererMap getRendererMap() {
        return this.rendererMap;
    }

    @Override
    public Logger getRootLogger() {
        return this.getInstance(Hierarchy.getContext(), "");
    }

    Logger getRootLogger(ClassLoader classLoader) {
        return this.getInstance(this.getContext(classLoader), "");
    }

    @Override
    public Level getThreshold() {
        return this.threshold;
    }

    @Override
    public ThrowableRenderer getThrowableRenderer() {
        return this.throwableRenderer;
    }

    @Override
    public boolean isDisabled(int level) {
        return this.thresholdInt > level;
    }

    @Deprecated
    public void overrideAsNeeded(String override) {
        LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
    }

    @Override
    public void resetConfiguration() {
        this.resetConfiguration(Hierarchy.getContext());
    }

    void resetConfiguration(ClassLoader classLoader) {
        this.resetConfiguration(this.getContext(classLoader));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void resetConfiguration(LoggerContext loggerContext) {
        Hierarchy.getLoggersMap(loggerContext).clear();
        this.getRootLogger().setLevel(Level.DEBUG);
        this.root.setResourceBundle(null);
        this.setThreshold(Level.ALL);
        Hashtable hashtable = this.ht;
        synchronized (hashtable) {
            this.shutdown();
            Enumeration cats = this.getCurrentLoggers();
            while (cats.hasMoreElements()) {
                Logger c = (Logger)cats.nextElement();
                c.setLevel(null);
                c.setAdditivity(true);
                c.setResourceBundle(null);
            }
        }
        this.rendererMap.clear();
        this.throwableRenderer = null;
    }

    @Deprecated
    public void setDisableOverride(String override) {
        LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
    }

    @Override
    public void setRenderer(Class renderedClass, ObjectRenderer renderer) {
        this.rendererMap.put(renderedClass, renderer);
    }

    @Override
    public void setThreshold(Level level) {
        if (level != null) {
            this.thresholdInt = level.level;
            this.threshold = level;
        }
    }

    @Override
    public void setThreshold(String levelStr) {
        Level level = OptionConverter.toLevel(levelStr, null);
        if (level != null) {
            this.setThreshold(level);
        } else {
            LogLog.warn("Could not convert [" + levelStr + "] to Level.");
        }
    }

    @Override
    public void setThrowableRenderer(ThrowableRenderer throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    @Override
    public void shutdown() {
        this.shutdown(Hierarchy.getContext());
    }

    public void shutdown(ClassLoader classLoader) {
        this.shutdown(org.apache.logging.log4j.LogManager.getContext((ClassLoader)classLoader, (boolean)false));
    }

    void shutdown(LoggerContext context) {
        Hierarchy.getLoggersMap(context).clear();
        if (LogManager.isLog4jCorePresent()) {
            ContextUtil.shutdown(context);
        }
    }

    private static class PrivateLogManager
    extends org.apache.logging.log4j.LogManager {
        private static final String FQCN = Hierarchy.class.getName();

        private PrivateLogManager() {
        }

        public static LoggerContext getContext() {
            return PrivateLogManager.getContext((String)FQCN, (boolean)false);
        }

        public static org.apache.logging.log4j.Logger getLogger(String name) {
            return PrivateLogManager.getLogger((String)FQCN, (String)name);
        }
    }

    private static class PrivateLoggerAdapter
    extends AbstractLoggerAdapter<Logger> {
        private PrivateLoggerAdapter() {
        }

        protected LoggerContext getContext() {
            return PrivateLogManager.getContext();
        }

        protected Logger newLogger(String name, LoggerContext context) {
            return new Logger(context, name);
        }
    }
}

