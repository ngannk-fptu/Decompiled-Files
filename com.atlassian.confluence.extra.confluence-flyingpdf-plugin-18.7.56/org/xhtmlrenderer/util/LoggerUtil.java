/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    public static Logger getDebugLogger(Class cls) {
        Logger l = Logger.getLogger(cls.getName());
        l.setLevel(Level.ALL);
        return l;
    }

    public static Level parseLogLevel(String val, Level defaultLogLevel) {
        if ("ALL".equals(val)) {
            return Level.ALL;
        }
        if ("CONFIG".equals(val)) {
            return Level.CONFIG;
        }
        if ("FINE".equals(val)) {
            return Level.FINE;
        }
        if ("FINER".equals(val)) {
            return Level.FINER;
        }
        if ("FINEST".equals(val)) {
            return Level.FINEST;
        }
        if ("INFO".equals(val)) {
            return Level.INFO;
        }
        if ("OFF".equals(val)) {
            return Level.OFF;
        }
        if ("SEVERE".equals(val)) {
            return Level.SEVERE;
        }
        if ("WARNING".equals(val)) {
            return Level.WARNING;
        }
        return defaultLogLevel;
    }
}

