/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.Logger
 *  org.apache.logging.log4j.core.config.LoggerConfig
 *  org.apache.logging.log4j.spi.LoggerContext
 */
package org.apache.log4j.legacy.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.log4j.bridge.AppenderAdapter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.LoggerContext;

public final class CategoryUtil {
    private static org.apache.logging.log4j.core.Logger asCore(Logger logger) {
        return (org.apache.logging.log4j.core.Logger)logger;
    }

    private static <T> T get(Logger logger, Supplier<T> run, T defaultValue) {
        return CategoryUtil.isCore(logger) ? run.get() : defaultValue;
    }

    public static Map<String, Appender> getAppenders(Logger logger) {
        return CategoryUtil.get(logger, () -> CategoryUtil.getDirectAppenders(logger), Collections.emptyMap());
    }

    private static Map<String, Appender> getDirectAppenders(Logger logger) {
        return CategoryUtil.getExactLoggerConfig(logger).map(LoggerConfig::getAppenders).orElse(Collections.emptyMap());
    }

    private static Optional<LoggerConfig> getExactLoggerConfig(Logger logger) {
        return Optional.of(CategoryUtil.asCore(logger).get()).filter(lc -> logger.getName().equals(lc.getName()));
    }

    public static Iterator<Filter> getFilters(Logger logger) {
        return CategoryUtil.get(logger, () -> ((org.apache.logging.log4j.core.Logger)CategoryUtil.asCore(logger)).getFilters(), null);
    }

    public static LoggerContext getLoggerContext(Logger logger) {
        return CategoryUtil.get(logger, () -> ((org.apache.logging.log4j.core.Logger)CategoryUtil.asCore(logger)).getContext(), null);
    }

    public static Logger getParent(Logger logger) {
        return CategoryUtil.get(logger, () -> ((org.apache.logging.log4j.core.Logger)CategoryUtil.asCore(logger)).getParent(), null);
    }

    public static boolean isAdditive(Logger logger) {
        return CategoryUtil.get(logger, () -> ((org.apache.logging.log4j.core.Logger)CategoryUtil.asCore(logger)).isAdditive(), false);
    }

    private static boolean isCore(Logger logger) {
        return logger instanceof org.apache.logging.log4j.core.Logger;
    }

    public static void setAdditivity(Logger logger, boolean additive) {
        if (CategoryUtil.isCore(logger)) {
            CategoryUtil.asCore(logger).setAdditive(additive);
        }
    }

    public static void setLevel(Logger logger, Level level) {
        if (CategoryUtil.isCore(logger)) {
            CategoryUtil.asCore(logger).setLevel(level);
        }
    }

    public static void addAppender(Logger logger, Appender appender) {
        if (appender instanceof AppenderAdapter.Adapter) {
            appender.start();
        }
        CategoryUtil.asCore(logger).addAppender(appender);
    }

    public static void log(Logger logger, LogEvent event) {
        CategoryUtil.getExactLoggerConfig(logger).ifPresent(lc -> lc.log(event));
    }

    private CategoryUtil() {
    }
}

