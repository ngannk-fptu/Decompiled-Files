/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.JDKXRLogger;
import org.xhtmlrenderer.util.XRLogger;

public class XRLog {
    private static final List<String> LOGGER_NAMES = new ArrayList<String>(20);
    public static final String CONFIG = XRLog.registerLoggerByName("org.xhtmlrenderer.config");
    public static final String EXCEPTION = XRLog.registerLoggerByName("org.xhtmlrenderer.exception");
    public static final String GENERAL = XRLog.registerLoggerByName("org.xhtmlrenderer.general");
    public static final String INIT = XRLog.registerLoggerByName("org.xhtmlrenderer.init");
    public static final String JUNIT = XRLog.registerLoggerByName("org.xhtmlrenderer.junit");
    public static final String LOAD = XRLog.registerLoggerByName("org.xhtmlrenderer.load");
    public static final String MATCH = XRLog.registerLoggerByName("org.xhtmlrenderer.match");
    public static final String CASCADE = XRLog.registerLoggerByName("org.xhtmlrenderer.cascade");
    public static final String XML_ENTITIES = XRLog.registerLoggerByName("org.xhtmlrenderer.load.xml-entities");
    public static final String CSS_PARSE = XRLog.registerLoggerByName("org.xhtmlrenderer.css-parse");
    public static final String LAYOUT = XRLog.registerLoggerByName("org.xhtmlrenderer.layout");
    public static final String RENDER = XRLog.registerLoggerByName("org.xhtmlrenderer.render");
    private static boolean initPending = true;
    private static XRLogger loggerImpl;
    private static boolean loggingEnabled;

    private static String registerLoggerByName(String loggerName) {
        LOGGER_NAMES.add(loggerName);
        return loggerName;
    }

    public static List<String> listRegisteredLoggers() {
        return new ArrayList<String>(LOGGER_NAMES);
    }

    public static void cssParse(String msg) {
        XRLog.cssParse(Level.INFO, msg);
    }

    public static void cssParse(Level level, String msg) {
        XRLog.log(CSS_PARSE, level, msg);
    }

    public static void cssParse(Level level, String msg, Throwable th) {
        XRLog.log(CSS_PARSE, level, msg, th);
    }

    public static void xmlEntities(String msg) {
        XRLog.xmlEntities(Level.INFO, msg);
    }

    public static void xmlEntities(Level level, String msg) {
        XRLog.log(XML_ENTITIES, level, msg);
    }

    public static void xmlEntities(Level level, String msg, Throwable th) {
        XRLog.log(XML_ENTITIES, level, msg, th);
    }

    public static void cascade(String msg) {
        XRLog.cascade(Level.INFO, msg);
    }

    public static void cascade(Level level, String msg) {
        XRLog.log(CASCADE, level, msg);
    }

    public static void cascade(Level level, String msg, Throwable th) {
        XRLog.log(CASCADE, level, msg, th);
    }

    public static void exception(String msg) {
        XRLog.exception(msg, null);
    }

    public static void exception(String msg, Throwable th) {
        XRLog.log(EXCEPTION, Level.WARNING, msg, th);
    }

    public static void general(String msg) {
        XRLog.general(Level.INFO, msg);
    }

    public static void general(Level level, String msg) {
        XRLog.log(GENERAL, level, msg);
    }

    public static void general(Level level, String msg, Throwable th) {
        XRLog.log(GENERAL, level, msg, th);
    }

    public static void init(String msg) {
        XRLog.init(Level.INFO, msg);
    }

    public static void init(Level level, String msg) {
        XRLog.log(INIT, level, msg);
    }

    public static void init(Level level, String msg, Throwable th) {
        XRLog.log(INIT, level, msg, th);
    }

    public static void junit(String msg) {
        XRLog.junit(Level.FINEST, msg);
    }

    public static void junit(Level level, String msg) {
        XRLog.log(JUNIT, level, msg);
    }

    public static void junit(Level level, String msg, Throwable th) {
        XRLog.log(JUNIT, level, msg, th);
    }

    public static void load(String msg) {
        XRLog.load(Level.INFO, msg);
    }

    public static void load(Level level, String msg) {
        XRLog.log(LOAD, level, msg);
    }

    public static void load(Level level, String msg, Throwable th) {
        XRLog.log(LOAD, level, msg, th);
    }

    public static void match(String msg) {
        XRLog.match(Level.INFO, msg);
    }

    public static void match(Level level, String msg) {
        XRLog.log(MATCH, level, msg);
    }

    public static void match(Level level, String msg, Throwable th) {
        XRLog.log(MATCH, level, msg, th);
    }

    public static void layout(String msg) {
        XRLog.layout(Level.INFO, msg);
    }

    public static void layout(Level level, String msg) {
        XRLog.log(LAYOUT, level, msg);
    }

    public static void layout(Level level, String msg, Throwable th) {
        XRLog.log(LAYOUT, level, msg, th);
    }

    public static void render(String msg) {
        XRLog.render(Level.INFO, msg);
    }

    public static void render(Level level, String msg) {
        XRLog.log(RENDER, level, msg);
    }

    public static void render(Level level, String msg, Throwable th) {
        XRLog.log(RENDER, level, msg, th);
    }

    public static synchronized void log(String where, Level level, String msg) {
        if (initPending) {
            XRLog.init();
        }
        if (XRLog.isLoggingEnabled()) {
            loggerImpl.log(where, level, msg);
        }
    }

    public static synchronized void log(String where, Level level, String msg, Throwable th) {
        if (initPending) {
            XRLog.init();
        }
        if (XRLog.isLoggingEnabled()) {
            loggerImpl.log(where, level, msg, th);
        }
    }

    public static void main(String[] args) {
        try {
            XRLog.cascade("Cascade msg");
            XRLog.cascade(Level.WARNING, "Cascade msg");
            XRLog.exception("Exception msg");
            XRLog.exception("Exception msg", new Exception());
            XRLog.general("General msg");
            XRLog.general(Level.WARNING, "General msg");
            XRLog.init("Init msg");
            XRLog.init(Level.WARNING, "Init msg");
            XRLog.load("Load msg");
            XRLog.load(Level.WARNING, "Load msg");
            XRLog.match("Match msg");
            XRLog.match(Level.WARNING, "Match msg");
            XRLog.layout("Layout msg");
            XRLog.layout(Level.WARNING, "Layout msg");
            XRLog.render("Render msg");
            XRLog.render(Level.WARNING, "Render msg");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void init() {
        Class<XRLog> clazz = XRLog.class;
        synchronized (XRLog.class) {
            if (!initPending) {
                // ** MonitorExit[var0] (shouldn't be in output)
                return;
            }
            XRLog.setLoggingEnabled(Configuration.isTrue("xr.util-logging.loggingEnabled", true));
            if (loggerImpl == null) {
                loggerImpl = new JDKXRLogger();
            }
            initPending = false;
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static synchronized void setLevel(String log, Level level) {
        if (initPending) {
            XRLog.init();
        }
        loggerImpl.setLevel(log, level);
    }

    public static synchronized boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static synchronized void setLoggingEnabled(boolean loggingEnabled) {
        XRLog.loggingEnabled = loggingEnabled;
    }

    public static synchronized XRLogger getLoggerImpl() {
        return loggerImpl;
    }

    public static synchronized void setLoggerImpl(XRLogger loggerImpl) {
        XRLog.loggerImpl = loggerImpl;
    }

    static {
        loggingEnabled = true;
    }
}

