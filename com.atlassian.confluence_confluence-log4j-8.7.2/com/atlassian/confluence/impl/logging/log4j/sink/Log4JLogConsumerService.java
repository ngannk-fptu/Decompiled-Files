/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.logging.sink.LogConsumerService
 *  com.atlassian.confluence.logging.sink.LogEvent
 *  javax.annotation.PostConstruct
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.logging.log4j.sink;

import com.atlassian.confluence.impl.logging.log4j.sink.LogConsumerServiceAppender;
import com.atlassian.confluence.logging.sink.LogConsumerService;
import com.atlassian.confluence.logging.sink.LogEvent;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Log4JLogConsumerService
implements LogConsumerService {
    private static final Logger log = LoggerFactory.getLogger(Log4JLogConsumerService.class);
    private final LoggerContext loggerContext;
    private final LogConsumerServiceAppender appender = new LogConsumerServiceAppender(Log4JLogConsumerService.class.getSimpleName());

    public Log4JLogConsumerService() {
        this((LoggerContext)LogManager.getContext((boolean)false));
    }

    Log4JLogConsumerService(LoggerContext loggerContext) {
        this.loggerContext = loggerContext;
    }

    @PostConstruct
    void registerAppender() {
        log.info("Attaching appender to log4j root logger");
        this.appender.start();
        this.loggerContext.getConfiguration().getRootLogger().addAppender((Appender)this.appender, null, null);
        this.loggerContext.updateLoggers();
    }

    public void registerLogConsumer(String key, Consumer<LogEvent> consumer) {
        log.info("Registering log consumer [{}]", (Object)key);
        this.appender.addConsumer(key, consumer);
    }

    public void unregisterLogConsumer(String key) {
        log.info("Unregistering log consumer [{}]", (Object)key);
        this.appender.removeConsumer(key);
    }
}

