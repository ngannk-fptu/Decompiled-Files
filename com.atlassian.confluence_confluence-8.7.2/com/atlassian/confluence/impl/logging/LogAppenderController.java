/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.logging;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogAppenderController {
    private static final Logger log = LoggerFactory.getLogger(LogAppenderController.class);
    private static final boolean SWITCH_DISABLED = Boolean.getBoolean("ConfluenceHomeLogAppender.disabled");
    private static final List<Consumer<Path>> logDirectoryAwares = new CopyOnWriteArrayList<Consumer<Path>>();
    private static final List<Runnable> rolloverRunners = new CopyOnWriteArrayList<Runnable>();

    private LogAppenderController() {
    }

    public static void registerLogDirectoryAware(Consumer<Path> logDirectoryAware) {
        logDirectoryAwares.add(Objects.requireNonNull(logDirectoryAware));
    }

    public static void registerRolloverRunner(Runnable rolloverRunner) {
        rolloverRunners.add(Objects.requireNonNull(rolloverRunner));
    }

    @Deprecated
    public static void reconfigureAppendersWithLogDirectory() {
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        LogAppenderController.reconfigureAppendersWithLogDirectory(bootstrapManager);
    }

    public static void reconfigureAppendersWithLogDirectory(BootstrapManager bootstrapManager) {
        Path logDirectory = bootstrapManager.getLocalHome().toPath().resolve("logs");
        LogAppenderController.reconfigureAppendersWithLogDirectory(logDirectory);
    }

    private static void reconfigureAppendersWithLogDirectory(Path logDirectory) {
        try {
            if (!Files.isDirectory(logDirectory, new LinkOption[0])) {
                Files.createDirectories(logDirectory, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            log.error("Could not create logs directory {}. Logging remains directed to the ConsoleAppender.", (Object)logDirectory, (Object)e);
            return;
        }
        if (!SWITCH_DISABLED) {
            log.debug("Reconfiguring all registered log appenders with log directory {}", (Object)logDirectory);
            logDirectoryAwares.forEach(appender -> {
                try {
                    appender.accept(logDirectory);
                }
                catch (RuntimeException ex) {
                    log.warn("Failed to reconfigure appender with log directory {}", (Object)logDirectory, (Object)ex);
                }
            });
        } else {
            log.debug("Reconfiguring of log appenders is disabled");
        }
    }

    public static void rolloverAppenders() {
        rolloverRunners.forEach(Runnable::run);
    }
}

