/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.LoggerUtil;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRLogger;
import org.xhtmlrenderer.util.XRRuntimeException;

public class JDKXRLogger
implements XRLogger {
    private static boolean initPending = true;

    @Override
    public void log(String where, Level level, String msg) {
        if (initPending) {
            JDKXRLogger.init();
        }
        JDKXRLogger.getLogger(where).log(level, msg);
    }

    @Override
    public void log(String where, Level level, String msg, Throwable th) {
        if (initPending) {
            JDKXRLogger.init();
        }
        JDKXRLogger.getLogger(where).log(level, msg, th);
    }

    @Override
    public void setLevel(String logger, Level level) {
        JDKXRLogger.getLogger(logger).setLevel(level);
    }

    private static Logger getLogger(String log) {
        return Logger.getLogger(log);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void init() {
        Class<JDKXRLogger> clazz = JDKXRLogger.class;
        synchronized (JDKXRLogger.class) {
            if (!initPending) {
                // ** MonitorExit[var0] (shouldn't be in output)
                return;
            }
            initPending = false;
            try {
                Properties props = JDKXRLogger.retrieveLoggingProperties();
                if (!XRLog.isLoggingEnabled()) {
                    Configuration.setConfigLogger(Logger.getLogger(XRLog.CONFIG));
                    // ** MonitorExit[var0] (shouldn't be in output)
                    return;
                }
                JDKXRLogger.initializeJDKLogManager(props);
                Configuration.setConfigLogger(Logger.getLogger(XRLog.CONFIG));
            }
            catch (SecurityException props) {
            }
            catch (IOException e) {
                throw new XRRuntimeException("Could not initialize logs. " + e.getLocalizedMessage(), e);
            }
            return;
        }
    }

    private static Properties retrieveLoggingProperties() {
        String prefix = "xr.util-logging.";
        Iterator iter = Configuration.keysByPrefix(prefix);
        Properties props = new Properties();
        while (iter.hasNext()) {
            String fullkey = (String)iter.next();
            String lmkey = fullkey.substring(prefix.length());
            String value = Configuration.valueFor(fullkey);
            props.setProperty(lmkey, value);
        }
        return props;
    }

    private static void initializeJDKLogManager(Properties fsLoggingProperties) throws IOException {
        List<Logger> loggers = JDKXRLogger.retrieveLoggers();
        JDKXRLogger.configureLoggerHandlerForwarding(fsLoggingProperties, loggers);
        Enumeration<Object> keys = fsLoggingProperties.keys();
        Map<String, Handler> handlers = new HashMap<String, Handler>();
        HashMap<String, String> handlerFormatterMap = new HashMap<String, String>();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String prop = fsLoggingProperties.getProperty(key);
            if (key.endsWith("level")) {
                JDKXRLogger.configureLogLevel(key.substring(0, key.lastIndexOf(46)), prop);
                continue;
            }
            if (key.endsWith("handlers")) {
                handlers = JDKXRLogger.configureLogHandlers(loggers, prop);
                continue;
            }
            if (!key.endsWith("formatter")) continue;
            String k2 = key.substring(0, key.length() - ".formatter".length());
            handlerFormatterMap.put(k2, prop);
        }
        for (String handlerClassName : handlerFormatterMap.keySet()) {
            String formatterClassName = (String)handlerFormatterMap.get(handlerClassName);
            JDKXRLogger.assignFormatter(handlers, handlerClassName, formatterClassName);
        }
    }

    private static void configureLoggerHandlerForwarding(Properties fsLoggingProperties, List<Logger> loggers) {
        String val = fsLoggingProperties.getProperty("use-parent-handler");
        boolean flag = val != null && Boolean.valueOf(val) != false;
        for (Logger logger : loggers) {
            logger.setUseParentHandlers(flag);
        }
    }

    private static void assignFormatter(Map<String, Handler> handlers, String handlerClassName, String formatterClassName) {
        Handler handler = handlers.get(handlerClassName);
        if (handler != null) {
            try {
                Class<?> fclass = Class.forName(formatterClassName);
                Formatter formatter = (Formatter)fclass.newInstance();
                handler.setFormatter(formatter);
            }
            catch (ClassNotFoundException e) {
                throw new XRRuntimeException("Could not initialize logging properties; Formatter class not found: " + formatterClassName);
            }
            catch (IllegalAccessException e) {
                throw new XRRuntimeException("Could not initialize logging properties; Can't instantiate Formatter class (IllegalAccessException): " + formatterClassName);
            }
            catch (InstantiationException e) {
                throw new XRRuntimeException("Could not initialize logging properties; Can't instantiate Formatter class (InstantiationException): " + formatterClassName);
            }
        }
    }

    private static List<Logger> retrieveLoggers() {
        List<String> loggerNames = XRLog.listRegisteredLoggers();
        ArrayList<Logger> loggers = new ArrayList<Logger>(loggerNames.size());
        for (String loggerName : loggerNames) {
            loggers.add(Logger.getLogger(loggerName));
        }
        return loggers;
    }

    private static Map<String, Handler> configureLogHandlers(List<Logger> loggers, String handlerClassList) {
        String[] names = handlerClassList.split(" ");
        HashMap<String, Handler> handlers = new HashMap<String, Handler>(names.length);
        for (String name : names) {
            try {
                Class<?> handlerClass = Class.forName(name);
                Handler handler = (Handler)handlerClass.newInstance();
                handlers.put(name, handler);
                String hl = Configuration.valueFor("xr.util-logging." + name + ".level", "INFO");
                handler.setLevel(LoggerUtil.parseLogLevel(hl, Level.INFO));
            }
            catch (ClassNotFoundException e) {
                throw new XRRuntimeException("Could not initialize logging properties; Handler class not found: " + name);
            }
            catch (IllegalAccessException e) {
                throw new XRRuntimeException("Could not initialize logging properties; Can't instantiate Handler class (IllegalAccessException): " + name);
            }
            catch (InstantiationException e) {
                throw new XRRuntimeException("Could not initialize logging properties; Can't instantiate Handler class (InstantiationException): " + name);
            }
        }
        for (Logger logger : loggers) {
            for (Handler handler : handlers.values()) {
                logger.addHandler(handler);
            }
        }
        return handlers;
    }

    private static void configureLogLevel(String loggerName, String levelValue) {
        Level level = LoggerUtil.parseLogLevel(levelValue, Level.OFF);
        Logger logger = Logger.getLogger(loggerName);
        logger.setLevel(level);
    }
}

