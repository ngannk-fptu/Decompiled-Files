/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Appender
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.Priority
 */
package com.mchange.v2.log.log4j;

import com.mchange.v2.log.FallbackMLog;
import com.mchange.v2.log.LogUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.NullMLogger;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public final class Log4jMLog
extends MLog {
    static final String CHECK_CLASS = "org.apache.log4j.Logger";

    public Log4jMLog() throws ClassNotFoundException {
        Class.forName(CHECK_CLASS);
    }

    @Override
    public MLogger getMLogger(String string) {
        Logger logger = Logger.getLogger((String)string);
        if (logger == null) {
            this.fallbackWarn(" with name '" + string + "'");
            return NullMLogger.instance();
        }
        return new Log4jMLogger(logger);
    }

    @Override
    public MLogger getMLogger(Class clazz) {
        Logger logger = Logger.getLogger((Class)clazz);
        if (logger == null) {
            this.fallbackWarn(" for class '" + clazz.getName() + "'");
            return NullMLogger.instance();
        }
        return new Log4jMLogger(logger);
    }

    @Override
    public MLogger getMLogger() {
        Logger logger = Logger.getRootLogger();
        if (logger == null) {
            this.fallbackWarn(" (root logger)");
            return NullMLogger.instance();
        }
        return new Log4jMLogger(logger);
    }

    private void fallbackWarn(String string) {
        FallbackMLog.getLogger().warning("Could not create or find log4j Logger" + string + ". Using NullMLogger. All messages sent to thislogger will be silently ignored. You might want to fix this.");
    }

    private static final class Log4jMLogger
    implements MLogger {
        static final String FQCN = Log4jMLogger.class.getName();
        MLevel myLevel = null;
        final Logger logger;

        Log4jMLogger(Logger logger) {
            this.logger = logger;
        }

        private static MLevel guessMLevel(Level level) {
            if (level == null) {
                return null;
            }
            if (level == Level.ALL) {
                return MLevel.ALL;
            }
            if (level == Level.TRACE) {
                return MLevel.FINEST;
            }
            if (level == Level.DEBUG) {
                return MLevel.FINER;
            }
            if (level == Level.ERROR) {
                return MLevel.SEVERE;
            }
            if (level == Level.FATAL) {
                return MLevel.SEVERE;
            }
            if (level == Level.INFO) {
                return MLevel.INFO;
            }
            if (level == Level.OFF) {
                return MLevel.OFF;
            }
            if (level == Level.WARN) {
                return MLevel.WARNING;
            }
            throw new IllegalArgumentException("Unknown level: " + level);
        }

        private static Level level(MLevel mLevel) {
            if (mLevel == null) {
                return null;
            }
            if (mLevel == MLevel.ALL) {
                return Level.ALL;
            }
            if (mLevel == MLevel.CONFIG) {
                return Level.DEBUG;
            }
            if (mLevel == MLevel.FINE) {
                return Level.DEBUG;
            }
            if (mLevel == MLevel.FINER) {
                return Level.DEBUG;
            }
            if (mLevel == MLevel.FINEST) {
                return Level.TRACE;
            }
            if (mLevel == MLevel.INFO) {
                return Level.INFO;
            }
            if (mLevel == MLevel.OFF) {
                return Level.OFF;
            }
            if (mLevel == MLevel.SEVERE) {
                return Level.ERROR;
            }
            if (mLevel == MLevel.WARNING) {
                return Level.WARN;
            }
            throw new IllegalArgumentException("Unknown MLevel: " + mLevel);
        }

        @Override
        public ResourceBundle getResourceBundle() {
            return null;
        }

        @Override
        public String getResourceBundleName() {
            return null;
        }

        @Override
        public void setFilter(Object object) throws SecurityException {
            this.warning("setFilter() not supported by MLogger " + this.getClass().getName());
        }

        @Override
        public Object getFilter() {
            return null;
        }

        private void log(Level level, Object object, Throwable throwable) {
            this.logger.log(FQCN, (Priority)level, object, throwable);
        }

        @Override
        public void log(MLevel mLevel, String string) {
            this.log(Log4jMLogger.level(mLevel), (Object)string, null);
        }

        @Override
        public void log(MLevel mLevel, String string, Object object) {
            this.log(Log4jMLogger.level(mLevel), string != null ? MessageFormat.format(string, object) : null, null);
        }

        @Override
        public void log(MLevel mLevel, String string, Object[] objectArray) {
            this.log(Log4jMLogger.level(mLevel), (Object)(string != null ? MessageFormat.format(string, objectArray) : null), null);
        }

        @Override
        public void log(MLevel mLevel, String string, Throwable throwable) {
            this.log(Log4jMLogger.level(mLevel), (Object)string, throwable);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, string3), null);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, string3 != null ? MessageFormat.format(string3, object) : null), null);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, string3 != null ? MessageFormat.format(string3, objectArray) : null), null);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, string3), throwable);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, null)), null);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, new Object[]{object})), null);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, objectArray)), null);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
            this.log(Log4jMLogger.level(mLevel), (Object)LogUtils.createMessage(string, string2, LogUtils.formatMessage(string3, string4, null)), throwable);
        }

        @Override
        public void entering(String string, String string2) {
            this.log(Level.TRACE, (Object)LogUtils.createMessage(string, string2, "entering method."), null);
        }

        @Override
        public void entering(String string, String string2, Object object) {
            this.log(Level.TRACE, (Object)LogUtils.createMessage(string, string2, "entering method... param: " + object.toString()), null);
        }

        @Override
        public void entering(String string, String string2, Object[] objectArray) {
            this.log(Level.TRACE, (Object)LogUtils.createMessage(string, string2, "entering method... " + LogUtils.createParamsList(objectArray)), null);
        }

        @Override
        public void exiting(String string, String string2) {
            this.log(Level.TRACE, (Object)LogUtils.createMessage(string, string2, "exiting method."), null);
        }

        @Override
        public void exiting(String string, String string2, Object object) {
            this.log(Level.TRACE, (Object)LogUtils.createMessage(string, string2, "exiting method... result: " + object.toString()), null);
        }

        @Override
        public void throwing(String string, String string2, Throwable throwable) {
            this.log(Level.TRACE, (Object)LogUtils.createMessage(string, string2, "throwing exception... "), throwable);
        }

        @Override
        public void severe(String string) {
            this.log(Level.ERROR, (Object)string, null);
        }

        @Override
        public void warning(String string) {
            this.log(Level.WARN, (Object)string, null);
        }

        @Override
        public void info(String string) {
            this.log(Level.INFO, (Object)string, null);
        }

        @Override
        public void config(String string) {
            this.log(Level.DEBUG, (Object)string, null);
        }

        @Override
        public void fine(String string) {
            this.log(Level.DEBUG, (Object)string, null);
        }

        @Override
        public void finer(String string) {
            this.log(Level.DEBUG, (Object)string, null);
        }

        @Override
        public void finest(String string) {
            this.log(Level.TRACE, (Object)string, null);
        }

        @Override
        public synchronized void setLevel(MLevel mLevel) throws SecurityException {
            this.logger.setLevel(Log4jMLogger.level(mLevel));
            this.myLevel = mLevel;
        }

        @Override
        public synchronized MLevel getLevel() {
            if (this.myLevel == null) {
                this.myLevel = Log4jMLogger.guessMLevel(this.logger.getLevel());
            }
            return this.myLevel;
        }

        @Override
        public boolean isLoggable(MLevel mLevel) {
            return this.logger.isEnabledFor((Priority)Log4jMLogger.level(mLevel));
        }

        @Override
        public String getName() {
            return this.logger.getName();
        }

        @Override
        public void addHandler(Object object) throws SecurityException {
            if (!(object instanceof Appender)) {
                throw new IllegalArgumentException("The 'handler' " + object + " is not compatible with MLogger " + this);
            }
            this.logger.addAppender((Appender)object);
        }

        @Override
        public void removeHandler(Object object) throws SecurityException {
            if (!(object instanceof Appender)) {
                throw new IllegalArgumentException("The 'handler' " + object + " is not compatible with MLogger " + this);
            }
            this.logger.removeAppender((Appender)object);
        }

        @Override
        public Object[] getHandlers() {
            LinkedList linkedList = new LinkedList();
            Enumeration enumeration = this.logger.getAllAppenders();
            while (enumeration.hasMoreElements()) {
                linkedList.add(enumeration.nextElement());
            }
            return linkedList.toArray();
        }

        @Override
        public void setUseParentHandlers(boolean bl) {
            this.logger.setAdditivity(bl);
        }

        @Override
        public boolean getUseParentHandlers() {
            return this.logger.getAdditivity();
        }
    }
}

