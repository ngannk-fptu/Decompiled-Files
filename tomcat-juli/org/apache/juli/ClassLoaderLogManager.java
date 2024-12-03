/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.juli.WebappProperties;

public class ClassLoaderLogManager
extends LogManager {
    private static final boolean isJava9;
    private static ThreadLocal<Boolean> addingLocalRootLogger;
    public static final String DEBUG_PROPERTY;
    protected final Map<ClassLoader, ClassLoaderLogInfo> classLoaderLoggers = new WeakHashMap<ClassLoader, ClassLoaderLogInfo>();
    protected final ThreadLocal<String> prefix = new ThreadLocal();
    protected volatile boolean useShutdownHook = true;

    public ClassLoaderLogManager() {
        try {
            Runtime.getRuntime().addShutdownHook(new Cleaner());
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    public boolean isUseShutdownHook() {
        return this.useShutdownHook;
    }

    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    @Override
    public synchronized boolean addLogger(Logger logger) {
        String useParentHandlersString;
        int dotIndex;
        String loggerName = logger.getName();
        ClassLoader classLoader = ClassLoaderLogManager.getClassLoader();
        ClassLoaderLogInfo info = this.getClassLoaderInfo(classLoader);
        if (info.loggers.containsKey(loggerName)) {
            return false;
        }
        info.loggers.put(loggerName, logger);
        String levelString = this.getProperty(loggerName + ".level");
        if (levelString != null) {
            try {
                AccessController.doPrivileged(() -> {
                    logger.setLevel(Level.parse(levelString.trim()));
                    return null;
                });
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if ((dotIndex = loggerName.lastIndexOf(46)) >= 0) {
            String parentName = loggerName.substring(0, dotIndex);
            Logger.getLogger(parentName);
        }
        LogNode node = info.rootNode.findNode(loggerName);
        node.logger = logger;
        Logger parentLogger = node.findParentLogger();
        if (parentLogger != null) {
            ClassLoaderLogManager.doSetParentLogger(logger, parentLogger);
        }
        node.setParentLogger(logger);
        String handlers = this.getProperty(loggerName + ".handlers");
        if (handlers != null) {
            logger.setUseParentHandlers(false);
            StringTokenizer tok = new StringTokenizer(handlers, ",");
            while (tok.hasMoreTokens()) {
                String handlerName = tok.nextToken().trim();
                Handler handler = null;
                for (ClassLoader current = classLoader; current != null && ((info = this.classLoaderLoggers.get(current)) == null || (handler = info.handlers.get(handlerName)) == null); current = current.getParent()) {
                }
                if (handler == null) continue;
                logger.addHandler(handler);
            }
        }
        if (Boolean.parseBoolean(useParentHandlersString = this.getProperty(loggerName + ".useParentHandlers"))) {
            logger.setUseParentHandlers(true);
        }
        return true;
    }

    @Override
    public synchronized Logger getLogger(String name) {
        ClassLoader classLoader = ClassLoaderLogManager.getClassLoader();
        return this.getClassLoaderInfo((ClassLoader)classLoader).loggers.get(name);
    }

    @Override
    public synchronized Enumeration<String> getLoggerNames() {
        ClassLoader classLoader = ClassLoaderLogManager.getClassLoader();
        return Collections.enumeration(this.getClassLoaderInfo((ClassLoader)classLoader).loggers.keySet());
    }

    @Override
    public String getProperty(String name) {
        if (".handlers".equals(name) && !addingLocalRootLogger.get().booleanValue()) {
            return null;
        }
        String prefix = this.prefix.get();
        String result = null;
        if (prefix != null) {
            result = this.findProperty(prefix + name);
        }
        if (result == null) {
            result = this.findProperty(name);
        }
        if (result != null) {
            result = this.replace(result);
        }
        return result;
    }

    private synchronized String findProperty(String name) {
        ClassLoader classLoader = ClassLoaderLogManager.getClassLoader();
        ClassLoaderLogInfo info = this.getClassLoaderInfo(classLoader);
        String result = info.props.getProperty(name);
        if (result == null && info.props.isEmpty()) {
            if (classLoader != null) {
                for (ClassLoader current = classLoader.getParent(); current != null && ((info = this.classLoaderLoggers.get(current)) == null || (result = info.props.getProperty(name)) == null && info.props.isEmpty()); current = current.getParent()) {
                }
            }
            if (result == null) {
                result = super.getProperty(name);
            }
        }
        return result;
    }

    @Override
    public void readConfiguration() throws IOException, SecurityException {
        this.checkAccess();
        this.readConfiguration(ClassLoaderLogManager.getClassLoader());
    }

    @Override
    public void readConfiguration(InputStream is) throws IOException, SecurityException {
        this.checkAccess();
        this.reset();
        this.readConfiguration(is, ClassLoaderLogManager.getClassLoader());
    }

    @Override
    public synchronized void reset() throws SecurityException {
        Thread thread = Thread.currentThread();
        if (thread.getClass().getName().startsWith("java.util.logging.LogManager$")) {
            return;
        }
        ClassLoader classLoader = ClassLoaderLogManager.getClassLoader();
        ClassLoaderLogInfo clLogInfo = this.getClassLoaderInfo(classLoader);
        this.resetLoggers(clLogInfo);
    }

    public synchronized void shutdown() {
        for (ClassLoaderLogInfo clLogInfo : this.classLoaderLoggers.values()) {
            this.resetLoggers(clLogInfo);
        }
    }

    private void resetLoggers(ClassLoaderLogInfo clLogInfo) {
        for (Logger logger : clLogInfo.loggers.values()) {
            Handler[] handlers;
            for (Handler handler : handlers = logger.getHandlers()) {
                logger.removeHandler(handler);
            }
        }
        for (Handler handler : clLogInfo.handlers.values()) {
            try {
                handler.close();
            }
            catch (Exception exception) {}
        }
        clLogInfo.handlers.clear();
    }

    protected synchronized ClassLoaderLogInfo getClassLoaderInfo(ClassLoader classLoader) {
        ClassLoaderLogInfo info;
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        if ((info = this.classLoaderLoggers.get(classLoader)) == null) {
            ClassLoader classLoaderParam = classLoader;
            AccessController.doPrivileged(() -> {
                try {
                    this.readConfiguration(classLoaderParam);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                return null;
            });
            info = this.classLoaderLoggers.get(classLoader);
        }
        return info;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void readConfiguration(ClassLoader classLoader) throws IOException {
        ClassLoaderLogInfo info;
        InputStream is;
        block26: {
            is = null;
            try {
                if (classLoader instanceof WebappProperties) {
                    if (((WebappProperties)((Object)classLoader)).hasLoggingConfig()) {
                        is = classLoader.getResourceAsStream("logging.properties");
                    }
                } else if (classLoader instanceof URLClassLoader) {
                    URL logConfig = ((URLClassLoader)classLoader).findResource("logging.properties");
                    if (null != logConfig) {
                        if (Boolean.getBoolean(DEBUG_PROPERTY)) {
                            System.err.println(this.getClass().getName() + ".readConfiguration(): Found logging.properties at " + logConfig);
                        }
                        is = classLoader.getResourceAsStream("logging.properties");
                    } else if (Boolean.getBoolean(DEBUG_PROPERTY)) {
                        System.err.println(this.getClass().getName() + ".readConfiguration(): Found no logging.properties");
                    }
                }
            }
            catch (AccessControlException ace) {
                Logger log;
                info = this.classLoaderLoggers.get(ClassLoader.getSystemClassLoader());
                if (info == null || (log = info.loggers.get("")) == null) break block26;
                Permission perm = ace.getPermission();
                if (perm instanceof FilePermission && perm.getActions().equals("read")) {
                    log.warning("Reading " + perm.getName() + " is not permitted. See \"per context logging\" in the default catalina.policy file.");
                }
                log.warning("Reading logging.properties is not permitted in some context. See \"per context logging\" in the default catalina.policy file.");
                log.warning("Original error was: " + ace.getMessage());
            }
        }
        if (is == null && classLoader == ClassLoader.getSystemClassLoader()) {
            String configFileStr = System.getProperty("java.util.logging.config.file");
            if (configFileStr != null) {
                try {
                    is = new FileInputStream(this.replace(configFileStr));
                }
                catch (IOException e) {
                    System.err.println("Configuration error");
                    e.printStackTrace();
                }
            }
            if (is == null) {
                File defaultFile = new File(new File(System.getProperty("java.home"), isJava9 ? "conf" : "lib"), "logging.properties");
                try {
                    is = new FileInputStream(defaultFile);
                }
                catch (IOException e) {
                    System.err.println("Configuration error");
                    e.printStackTrace();
                }
            }
        }
        RootLogger localRootLogger = new RootLogger();
        if (is == null) {
            ClassLoaderLogInfo info2 = null;
            for (ClassLoader current = classLoader.getParent(); current != null && info2 == null; current = current.getParent()) {
                info2 = this.getClassLoaderInfo(current);
            }
            if (info2 != null) {
                localRootLogger.setParent(info2.rootNode.logger);
            }
        }
        info = new ClassLoaderLogInfo(new LogNode(null, localRootLogger));
        this.classLoaderLoggers.put(classLoader, info);
        if (is != null) {
            this.readConfiguration(is, classLoader);
        }
        if (localRootLogger.getParent() == null && localRootLogger.getLevel() == null) {
            localRootLogger.setLevel(Level.INFO);
        }
        try {
            addingLocalRootLogger.set(Boolean.TRUE);
            this.addLogger(localRootLogger);
        }
        finally {
            addingLocalRootLogger.set(Boolean.FALSE);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized void readConfiguration(InputStream is, ClassLoader classLoader) throws IOException {
        ClassLoaderLogInfo info = this.classLoaderLoggers.get(classLoader);
        try {
            info.props.load(is);
        }
        catch (IOException e) {
            System.err.println("Configuration error");
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {}
        }
        String rootHandlers = info.props.getProperty(".handlers");
        String handlers = info.props.getProperty("handlers");
        Logger localRootLogger = info.rootNode.logger;
        if (handlers != null) {
            StringTokenizer tok = new StringTokenizer(handlers, ",");
            while (tok.hasMoreTokens()) {
                int pos;
                String handlerName;
                String handlerClassName = handlerName = tok.nextToken().trim();
                String prefix = "";
                if (handlerClassName.length() <= 0) continue;
                if (Character.isDigit(handlerClassName.charAt(0)) && (pos = handlerClassName.indexOf(46)) >= 0) {
                    prefix = handlerClassName.substring(0, pos + 1);
                    handlerClassName = handlerClassName.substring(pos + 1);
                }
                try {
                    this.prefix.set(prefix);
                    Handler handler = (Handler)classLoader.loadClass(handlerClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
                    this.prefix.set(null);
                    info.handlers.put(handlerName, handler);
                    if (rootHandlers != null) continue;
                    localRootLogger.addHandler(handler);
                }
                catch (Exception e) {
                    System.err.println("Handler error");
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void doSetParentLogger(Logger logger, Logger parent) {
        AccessController.doPrivileged(() -> {
            logger.setParent(parent);
            return null;
        });
    }

    protected String replace(String str) {
        String result = str;
        int pos_start = str.indexOf("${");
        if (pos_start >= 0) {
            StringBuilder builder = new StringBuilder();
            int pos_end = -1;
            while (pos_start >= 0) {
                builder.append(str, pos_end + 1, pos_start);
                pos_end = str.indexOf(125, pos_start + 2);
                if (pos_end < 0) {
                    pos_end = pos_start - 1;
                    break;
                }
                String propName = str.substring(pos_start + 2, pos_end);
                String replacement = this.replaceWebApplicationProperties(propName);
                if (replacement == null) {
                    String string = replacement = propName.length() > 0 ? System.getProperty(propName) : null;
                }
                if (replacement != null) {
                    builder.append(replacement);
                } else {
                    builder.append(str, pos_start, pos_end + 1);
                }
                pos_start = str.indexOf("${", pos_end + 1);
            }
            builder.append(str, pos_end + 1, str.length());
            result = builder.toString();
        }
        return result;
    }

    private String replaceWebApplicationProperties(String propName) {
        ClassLoader cl = ClassLoaderLogManager.getClassLoader();
        if (cl instanceof WebappProperties) {
            WebappProperties wProps = (WebappProperties)((Object)cl);
            if ("classloader.webappName".equals(propName)) {
                return wProps.getWebappName();
            }
            if ("classloader.hostName".equals(propName)) {
                return wProps.getHostName();
            }
            if ("classloader.serviceName".equals(propName)) {
                return wProps.getServiceName();
            }
            return null;
        }
        return null;
    }

    static ClassLoader getClassLoader() {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            result = ClassLoaderLogManager.class.getClassLoader();
        }
        return result;
    }

    static {
        addingLocalRootLogger = ThreadLocal.withInitial(() -> Boolean.FALSE);
        DEBUG_PROPERTY = ClassLoaderLogManager.class.getName() + ".debug";
        Class<?> c = null;
        try {
            c = Class.forName("java.lang.Runtime$Version");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        isJava9 = c != null;
    }

    private final class Cleaner
    extends Thread {
        private Cleaner() {
        }

        @Override
        public void run() {
            if (ClassLoaderLogManager.this.useShutdownHook) {
                ClassLoaderLogManager.this.shutdown();
            }
        }
    }

    protected static final class ClassLoaderLogInfo {
        final LogNode rootNode;
        final Map<String, Logger> loggers = new ConcurrentHashMap<String, Logger>();
        final Map<String, Handler> handlers = new HashMap<String, Handler>();
        final Properties props = new Properties();

        ClassLoaderLogInfo(LogNode rootNode) {
            this.rootNode = rootNode;
        }
    }

    protected static final class LogNode {
        Logger logger;
        final Map<String, LogNode> children = new HashMap<String, LogNode>();
        final LogNode parent;

        LogNode(LogNode parent, Logger logger) {
            this.parent = parent;
            this.logger = logger;
        }

        LogNode(LogNode parent) {
            this(parent, null);
        }

        LogNode findNode(String name) {
            LogNode currentNode = this;
            if (this.logger.getName().equals(name)) {
                return this;
            }
            while (name != null) {
                String nextName;
                int dotIndex = name.indexOf(46);
                if (dotIndex < 0) {
                    nextName = name;
                    name = null;
                } else {
                    nextName = name.substring(0, dotIndex);
                    name = name.substring(dotIndex + 1);
                }
                LogNode childNode = currentNode.children.get(nextName);
                if (childNode == null) {
                    childNode = new LogNode(currentNode);
                    currentNode.children.put(nextName, childNode);
                }
                currentNode = childNode;
            }
            return currentNode;
        }

        Logger findParentLogger() {
            Logger logger = null;
            LogNode node = this.parent;
            while (node != null && logger == null) {
                logger = node.logger;
                node = node.parent;
            }
            return logger;
        }

        void setParentLogger(Logger parent) {
            for (LogNode childNode : this.children.values()) {
                if (childNode.logger == null) {
                    childNode.setParentLogger(parent);
                    continue;
                }
                ClassLoaderLogManager.doSetParentLogger(childNode.logger, parent);
            }
        }
    }

    protected static class RootLogger
    extends Logger {
        public RootLogger() {
            super("", null);
        }
    }
}

