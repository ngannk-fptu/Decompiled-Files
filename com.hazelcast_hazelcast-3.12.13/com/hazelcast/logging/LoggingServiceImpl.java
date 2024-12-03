/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.JetBuildInfo;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.logging.AbstractLogger;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LogListener;
import com.hazelcast.logging.Logger;
import com.hazelcast.logging.LoggerFactory;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggingServiceImpl
implements LoggingService {
    private final CopyOnWriteArrayList<LogListenerRegistration> listeners = new CopyOnWriteArrayList();
    private final ConcurrentMap<String, ILogger> mapLoggers = new ConcurrentHashMap<String, ILogger>(100);
    private final ConstructorFunction<String, ILogger> loggerConstructor = new ConstructorFunction<String, ILogger>(){

        @Override
        public ILogger createNew(String key) {
            return new DefaultLogger(key);
        }
    };
    private final LoggerFactory loggerFactory;
    private final String versionMessage;
    private volatile MemberImpl thisMember = new MemberImpl();
    private volatile String thisAddressString = "[LOCAL] ";
    private volatile Level minLevel = Level.OFF;

    public LoggingServiceImpl(String groupName, String loggingType, BuildInfo buildInfo) {
        this.loggerFactory = Logger.newLoggerFactory(loggingType);
        JetBuildInfo jetBuildInfo = buildInfo.getJetBuildInfo();
        this.versionMessage = "[" + groupName + "] [" + (jetBuildInfo != null ? jetBuildInfo.getVersion() : buildInfo.getVersion()) + "] ";
    }

    public void setThisMember(MemberImpl thisMember) {
        this.thisMember = thisMember;
        this.thisAddressString = "[" + thisMember.getAddress().getHost() + "]:" + thisMember.getAddress().getPort() + " ";
    }

    @Override
    public ILogger getLogger(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.mapLoggers, name, this.loggerConstructor);
    }

    @Override
    public ILogger getLogger(Class clazz) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.mapLoggers, clazz.getName(), this.loggerConstructor);
    }

    @Override
    public void addLogListener(Level level, LogListener logListener) {
        this.listeners.add(new LogListenerRegistration(level, logListener));
        if (level.intValue() < this.minLevel.intValue()) {
            this.minLevel = level;
        }
    }

    @Override
    public void removeLogListener(LogListener logListener) {
        this.listeners.remove(new LogListenerRegistration(Level.ALL, logListener));
    }

    void handleLogEvent(LogEvent logEvent) {
        for (LogListenerRegistration logListenerRegistration : this.listeners) {
            if (logEvent.getLogRecord().getLevel().intValue() < logListenerRegistration.getLevel().intValue()) continue;
            logListenerRegistration.getLogListener().log(logEvent);
        }
    }

    private class DefaultLogger
    extends AbstractLogger {
        final String name;
        final ILogger logger;

        DefaultLogger(String name) {
            this.name = name;
            this.logger = LoggingServiceImpl.this.loggerFactory.getLogger(name);
        }

        @Override
        public void log(Level level, String message) {
            this.log(level, message, null);
        }

        @Override
        public void log(Level level, String message, Throwable thrown) {
            boolean loggable = this.logger.isLoggable(level);
            if (loggable || level.intValue() >= LoggingServiceImpl.this.minLevel.intValue()) {
                String address = LoggingServiceImpl.this.thisAddressString;
                String logMessage = (address != null ? address : "") + LoggingServiceImpl.this.versionMessage + message;
                if (loggable) {
                    this.logger.log(level, logMessage, thrown);
                }
                if (LoggingServiceImpl.this.listeners.size() > 0) {
                    LogRecord logRecord = new LogRecord(level, logMessage);
                    logRecord.setThrown(thrown);
                    logRecord.setLoggerName(this.name);
                    logRecord.setSourceClassName(this.name);
                    LogEvent logEvent = new LogEvent(logRecord, LoggingServiceImpl.this.thisMember);
                    LoggingServiceImpl.this.handleLogEvent(logEvent);
                }
            }
        }

        @Override
        public void log(LogEvent logEvent) {
            LoggingServiceImpl.this.handleLogEvent(logEvent);
        }

        @Override
        public Level getLevel() {
            return this.logger.getLevel();
        }

        @Override
        public boolean isLoggable(Level level) {
            return this.logger.isLoggable(level);
        }
    }

    private static class LogListenerRegistration {
        final Level level;
        final LogListener logListener;

        LogListenerRegistration(Level level, LogListener logListener) {
            this.level = level;
            this.logListener = logListener;
        }

        public Level getLevel() {
            return this.level;
        }

        public LogListener getLogListener() {
            return this.logListener;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            LogListenerRegistration other = (LogListenerRegistration)obj;
            return !(this.logListener == null ? other.logListener != null : !this.logListener.equals(other.logListener));
        }

        public int hashCode() {
            return this.logListener != null ? this.logListener.hashCode() : 0;
        }
    }
}

