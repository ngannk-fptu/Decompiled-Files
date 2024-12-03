/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.message.LocalizedMessage
 *  org.apache.logging.log4j.message.MapMessage
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ObjectMessage
 *  org.apache.logging.log4j.message.SimpleMessage
 *  org.apache.logging.log4j.spi.ExtendedLogger
 *  org.apache.logging.log4j.spi.LoggerContext
 *  org.apache.logging.log4j.util.StackLocatorUtil
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.log4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.apache.log4j.Appender;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.bridge.AppenderAdapter;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.LogEventWrapper;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.legacy.core.CategoryUtil;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.message.LocalizedMessage;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;

public class Category
implements AppenderAttachable {
    private static final String FQCN = Category.class.getName();
    protected String name;
    protected boolean additive = true;
    protected volatile Level level;
    private RendererMap rendererMap;
    protected volatile Category parent;
    protected ResourceBundle bundle;
    private final org.apache.logging.log4j.Logger logger;
    protected LoggerRepository repository;
    AppenderAttachableImpl aai;

    @Deprecated
    public static Logger exists(String name) {
        return LogManager.exists(name, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    @Deprecated
    public static Enumeration getCurrentCategories() {
        return LogManager.getCurrentLoggers(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    @Deprecated
    public static LoggerRepository getDefaultHierarchy() {
        return LogManager.getLoggerRepository();
    }

    public static Category getInstance(Class clazz) {
        return LogManager.getLogger(clazz.getName(), StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static Category getInstance(String name) {
        return LogManager.getLogger(name, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static Category getRoot() {
        return LogManager.getRootLogger(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    private static String getSubName(String name) {
        if (Strings.isEmpty((CharSequence)name)) {
            return null;
        }
        int i = name.lastIndexOf(46);
        return i > 0 ? name.substring(0, i) : "";
    }

    public static void shutdown() {
        LogManager.shutdown(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    protected Category(LoggerContext context, String name) {
        this.name = name;
        this.logger = context.getLogger(name);
    }

    Category(org.apache.logging.log4j.Logger logger) {
        this.logger = logger;
    }

    protected Category(String name) {
        this(Hierarchy.getContext(), name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAppender(Appender appender) {
        if (appender != null) {
            if (LogManager.isLog4jCorePresent()) {
                CategoryUtil.addAppender(this.logger, AppenderAdapter.adapt(appender));
            } else {
                Category category = this;
                synchronized (category) {
                    if (this.aai == null) {
                        this.aai = new AppenderAttachableImpl();
                    }
                    this.aai.addAppender(appender);
                }
            }
            this.repository.fireAddAppenderEvent(this, appender);
        }
    }

    public void assertLog(boolean assertion, String msg) {
        if (!assertion) {
            this.error(msg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void callAppenders(LoggingEvent event) {
        if (LogManager.isLog4jCorePresent()) {
            CategoryUtil.log(this.logger, new LogEventWrapper(event));
            return;
        }
        int writes = 0;
        Category c = this;
        while (c != null) {
            Category category = c;
            synchronized (category) {
                if (c.aai != null) {
                    writes += c.aai.appendLoopOnAppenders(event);
                }
                if (!c.additive) {
                    break;
                }
            }
            c = c.parent;
        }
        if (writes == 0) {
            this.repository.emitNoAppenderWarning(this);
        }
    }

    synchronized void closeNestedAppenders() {
        Enumeration enumeration = this.getAllAppenders();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Appender a = (Appender)enumeration.nextElement();
                if (!(a instanceof AppenderAttachable)) continue;
                a.close();
            }
        }
    }

    public void debug(Object message) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.DEBUG, message, null);
    }

    public void debug(Object message, Throwable t) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.DEBUG, message, t);
    }

    public void error(Object message) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.ERROR, message, null);
    }

    public void error(Object message, Throwable t) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.ERROR, message, t);
    }

    public void fatal(Object message) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.FATAL, message, null);
    }

    public void fatal(Object message, Throwable t) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.FATAL, message, t);
    }

    private void fireRemoveAppenderEvent(Appender appender) {
        if (appender != null) {
            if (this.repository instanceof Hierarchy) {
                ((Hierarchy)this.repository).fireRemoveAppenderEvent(this, appender);
            } else if (this.repository instanceof HierarchyEventListener) {
                ((HierarchyEventListener)((Object)this.repository)).removeAppenderEvent(this, appender);
            }
        }
    }

    private static Message createMessage(Object message) {
        if (message instanceof String) {
            return new SimpleMessage((String)message);
        }
        if (message instanceof CharSequence) {
            return new SimpleMessage((CharSequence)message);
        }
        if (message instanceof Map) {
            return new MapMessage((Map)message);
        }
        if (message instanceof Message) {
            return (Message)message;
        }
        return new ObjectMessage(message);
    }

    public void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        org.apache.logging.log4j.Level lvl = level.getVersion2Level();
        Message msg = Category.createMessage(message);
        if (this.logger instanceof ExtendedLogger) {
            ((ExtendedLogger)this.logger).logMessage(fqcn, lvl, null, msg, t);
        } else {
            this.logger.log(lvl, msg, t);
        }
    }

    private <T> ObjectRenderer get(Class<T> clazz) {
        ObjectRenderer renderer = null;
        for (Class<T> c = clazz; c != null; c = c.getSuperclass()) {
            renderer = this.rendererMap.get(c);
            if (renderer != null) {
                return renderer;
            }
            renderer = this.searchInterfaces(c);
            if (renderer == null) continue;
            return renderer;
        }
        return null;
    }

    public boolean getAdditivity() {
        return LogManager.isLog4jCorePresent() ? CategoryUtil.isAdditive(this.logger) : false;
    }

    public Enumeration getAllAppenders() {
        if (LogManager.isLog4jCorePresent()) {
            Collection<org.apache.logging.log4j.core.Appender> appenders = CategoryUtil.getAppenders(this.logger).values();
            return Collections.enumeration(appenders.stream().filter(AppenderAdapter.Adapter.class::isInstance).map(AppenderWrapper::adapt).collect(Collectors.toSet()));
        }
        return this.aai == null ? NullEnumeration.getInstance() : this.aai.getAllAppenders();
    }

    @Override
    public Appender getAppender(String name) {
        if (LogManager.isLog4jCorePresent()) {
            return AppenderWrapper.adapt(CategoryUtil.getAppenders(this.logger).get(name));
        }
        return this.aai != null ? this.aai.getAppender(name) : null;
    }

    public Priority getChainedPriority() {
        return this.getEffectiveLevel();
    }

    public Level getEffectiveLevel() {
        switch (this.logger.getLevel().getStandardLevel()) {
            case ALL: {
                return Level.ALL;
            }
            case TRACE: {
                return Level.TRACE;
            }
            case DEBUG: {
                return Level.DEBUG;
            }
            case INFO: {
                return Level.INFO;
            }
            case WARN: {
                return Level.WARN;
            }
            case ERROR: {
                return Level.ERROR;
            }
            case FATAL: {
                return Level.FATAL;
            }
        }
        return Level.OFF;
    }

    @Deprecated
    public LoggerRepository getHierarchy() {
        return this.repository;
    }

    public final Level getLevel() {
        return this.getEffectiveLevel();
    }

    private String getLevelStr(Priority priority) {
        return priority == null ? null : priority.levelStr;
    }

    org.apache.logging.log4j.Logger getLogger() {
        return this.logger;
    }

    public LoggerRepository getLoggerRepository() {
        return this.repository;
    }

    public final String getName() {
        return this.logger.getName();
    }

    public final Category getParent() {
        if (!LogManager.isLog4jCorePresent()) {
            return null;
        }
        org.apache.logging.log4j.Logger parent = CategoryUtil.getParent(this.logger);
        LoggerContext loggerContext = CategoryUtil.getLoggerContext(this.logger);
        if (parent == null || loggerContext == null) {
            return null;
        }
        ConcurrentMap<String, Logger> loggers = Hierarchy.getLoggersMap(loggerContext);
        Category parentLogger = (Category)loggers.get(parent.getName());
        if (parentLogger == null) {
            parentLogger = new Category(parent);
            parentLogger.setHierarchy(this.getLoggerRepository());
        }
        return parentLogger;
    }

    public final Level getPriority() {
        return this.getEffectiveLevel();
    }

    public ResourceBundle getResourceBundle() {
        LoggerContext ctx;
        if (this.bundle != null) {
            return this.bundle;
        }
        String name = this.logger.getName();
        if (LogManager.isLog4jCorePresent() && (ctx = CategoryUtil.getLoggerContext(this.logger)) != null) {
            ConcurrentMap<String, Logger> loggers = Hierarchy.getLoggersMap(ctx);
            while ((name = Category.getSubName(name)) != null) {
                ResourceBundle rb;
                Logger subLogger = (Logger)loggers.get(name);
                if (subLogger == null || (rb = subLogger.bundle) == null) continue;
                return rb;
            }
        }
        return null;
    }

    public void info(Object message) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.INFO, message, null);
    }

    public void info(Object message, Throwable t) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.INFO, message, t);
    }

    @Override
    public boolean isAttached(Appender appender) {
        return this.aai == null ? false : this.aai.isAttached(appender);
    }

    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    private boolean isEnabledFor(org.apache.logging.log4j.Level level) {
        return this.logger.isEnabled(level);
    }

    public boolean isEnabledFor(Priority level) {
        return this.isEnabledFor(level.getVersion2Level());
    }

    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return this.logger.isFatalEnabled();
    }

    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
        if (this.isEnabledFor(priority)) {
            LocalizedMessage msg = new LocalizedMessage(this.bundle, key, params);
            this.forcedLog(FQCN, priority, msg, t);
        }
    }

    public void l7dlog(Priority priority, String key, Throwable t) {
        if (this.isEnabledFor(priority)) {
            LocalizedMessage msg = new LocalizedMessage(this.bundle, key, null);
            this.forcedLog(FQCN, priority, msg, t);
        }
    }

    public void log(Priority priority, Object message) {
        if (this.isEnabledFor(priority)) {
            this.forcedLog(FQCN, priority, message, null);
        }
    }

    public void log(Priority priority, Object message, Throwable t) {
        if (this.isEnabledFor(priority)) {
            this.forcedLog(FQCN, priority, message, t);
        }
    }

    public void log(String fqcn, Priority priority, Object message, Throwable t) {
        if (this.isEnabledFor(priority)) {
            this.forcedLog(fqcn, priority, message, t);
        }
    }

    void maybeLog(String fqcn, org.apache.logging.log4j.Level level, Object message, Throwable throwable) {
        if (this.logger.isEnabled(level)) {
            Message msg = Category.createMessage(message);
            if (this.logger instanceof ExtendedLogger) {
                ((ExtendedLogger)this.logger).logMessage(fqcn, level, null, msg, throwable);
            } else {
                this.logger.log(level, msg, throwable);
            }
        }
    }

    @Override
    public void removeAllAppenders() {
        if (this.aai != null) {
            Vector<Appender> appenders = new Vector<Appender>();
            Enumeration<Appender> iter = this.aai.getAllAppenders();
            while (iter != null && iter.hasMoreElements()) {
                appenders.add(iter.nextElement());
            }
            this.aai.removeAllAppenders();
            for (Object e : appenders) {
                this.fireRemoveAppenderEvent((Appender)e);
            }
            this.aai = null;
        }
    }

    @Override
    public void removeAppender(Appender appender) {
        if (appender == null || this.aai == null) {
            return;
        }
        boolean wasAttached = this.aai.isAttached(appender);
        this.aai.removeAppender(appender);
        if (wasAttached) {
            this.fireRemoveAppenderEvent(appender);
        }
    }

    @Override
    public void removeAppender(String name) {
        if (name == null || this.aai == null) {
            return;
        }
        Appender appender = this.aai.getAppender(name);
        this.aai.removeAppender(name);
        if (appender != null) {
            this.fireRemoveAppenderEvent(appender);
        }
    }

    ObjectRenderer searchInterfaces(Class<?> c) {
        Class<?>[] ia;
        ObjectRenderer renderer = this.rendererMap.get(c);
        if (renderer != null) {
            return renderer;
        }
        for (Class<?> clazz : ia = c.getInterfaces()) {
            renderer = this.searchInterfaces(clazz);
            if (renderer == null) continue;
            return renderer;
        }
        return null;
    }

    public void setAdditivity(boolean additivity) {
        if (LogManager.isLog4jCorePresent()) {
            CategoryUtil.setAdditivity(this.logger, additivity);
        }
    }

    final void setHierarchy(LoggerRepository repository) {
        this.repository = repository;
    }

    public void setLevel(Level level) {
        this.setLevel(level != null ? level.getVersion2Level() : null);
    }

    private void setLevel(org.apache.logging.log4j.Level level) {
        if (LogManager.isLog4jCorePresent()) {
            CategoryUtil.setLevel(this.logger, level);
        }
    }

    public void setPriority(Priority priority) {
        this.setLevel(priority != null ? priority.getVersion2Level() : null);
    }

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void warn(Object message) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.WARN, message, null);
    }

    public void warn(Object message, Throwable t) {
        this.maybeLog(FQCN, org.apache.logging.log4j.Level.WARN, message, t);
    }
}

