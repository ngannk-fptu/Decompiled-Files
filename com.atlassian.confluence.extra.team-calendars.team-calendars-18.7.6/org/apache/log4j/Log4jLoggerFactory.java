/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

class Log4jLoggerFactory {
    private static ConcurrentMap<String, Logger> log4jLoggers = new ConcurrentHashMap<String, Logger>();

    Log4jLoggerFactory() {
    }

    public static Logger getLogger(String name) {
        Logger instance = (Logger)log4jLoggers.get(name);
        if (instance != null) {
            return instance;
        }
        Logger newInstance = new Logger(name);
        Logger oldInstance = log4jLoggers.putIfAbsent(name, newInstance);
        return oldInstance == null ? newInstance : oldInstance;
    }

    public static Logger getLogger(String name, LoggerFactory loggerFactory) {
        Logger instance = (Logger)log4jLoggers.get(name);
        if (instance != null) {
            return instance;
        }
        Logger newInstance = loggerFactory.makeNewLoggerInstance(name);
        Logger oldInstance = log4jLoggers.putIfAbsent(name, newInstance);
        return oldInstance == null ? newInstance : oldInstance;
    }
}

