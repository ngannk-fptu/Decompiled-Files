/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log.jdk14logging;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogConfig;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.util.DoubleWeakHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Jdk14MLog
extends MLog {
    static final String SUPPRESS_STACK_WALK_KEY = "com.mchange.v2.log.jdk14logging.suppressStackWalk";
    private static String[] UNKNOWN_ARRAY = new String[]{"UNKNOWN_CLASS", "UNKNOWN_METHOD"};
    private static final String CHECK_CLASS = "java.util.logging.Logger";
    private final Map namedLoggerMap = new DoubleWeakHashMap();
    private static final boolean suppress_stack_walk;
    MLogger global = null;

    public Jdk14MLog() throws ClassNotFoundException {
        Class.forName(CHECK_CLASS);
    }

    @Override
    public synchronized MLogger getMLogger(String string) {
        MLogger mLogger = (MLogger)this.namedLoggerMap.get(string = string.intern());
        if (mLogger == null) {
            Logger logger = Logger.getLogger(string);
            mLogger = new Jdk14MLogger(logger);
            this.namedLoggerMap.put(string, mLogger);
        }
        return mLogger;
    }

    @Override
    public synchronized MLogger getMLogger() {
        if (this.global == null) {
            this.global = new Jdk14MLogger(LogManager.getLogManager().getLogger("global"));
        }
        return this.global;
    }

    private static String[] findCallingClassAndMethod() {
        for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
            String string = stackTraceElement.getClassName();
            if (string == null || string.startsWith("com.mchange.v2.log.jdk14logging") || string.startsWith("com.mchange.sc.v1.log")) continue;
            return new String[]{stackTraceElement.getClassName(), stackTraceElement.getMethodName()};
        }
        return UNKNOWN_ARRAY;
    }

    static {
        String string = MLogConfig.getProperty(SUPPRESS_STACK_WALK_KEY);
        if (string == null || (string = string.trim()).length() == 0) {
            suppress_stack_walk = false;
        } else if (string.equalsIgnoreCase("true")) {
            suppress_stack_walk = true;
        } else if (string.equalsIgnoreCase("false")) {
            suppress_stack_walk = false;
        } else {
            System.err.println("Bad value for com.mchange.v2.log.jdk14logging.suppressStackWalk: '" + string + "'; defaulting to 'false'.");
            suppress_stack_walk = false;
        }
    }

    private static final class Jdk14MLogger
    implements MLogger {
        final Logger logger;
        final String name;
        final ClassAndMethodFinder cmFinder;

        Jdk14MLogger(Logger logger) {
            this.logger = logger;
            this.name = logger.getName();
            this.cmFinder = suppress_stack_walk ? new ClassAndMethodFinder(){
                String[] fakedClassAndMethod;
                {
                    this.fakedClassAndMethod = new String[]{Jdk14MLogger.this.name, ""};
                }

                @Override
                public String[] find() {
                    return this.fakedClassAndMethod;
                }
            } : new ClassAndMethodFinder(){

                @Override
                public String[] find() {
                    return Jdk14MLog.findCallingClassAndMethod();
                }
            };
        }

        private static Level level(MLevel mLevel) {
            return (Level)mLevel.asJdk14Level();
        }

        @Override
        public ResourceBundle getResourceBundle() {
            return this.logger.getResourceBundle();
        }

        @Override
        public String getResourceBundleName() {
            return this.logger.getResourceBundleName();
        }

        @Override
        public void setFilter(Object object) throws SecurityException {
            if (!(object instanceof Filter)) {
                throw new IllegalArgumentException("MLogger.setFilter( ... ) requires a java.util.logging.Filter. This is not enforced by the compiler only to permit building under jdk 1.3");
            }
            this.logger.setFilter((Filter)object);
        }

        @Override
        public Object getFilter() {
            return this.logger.getFilter();
        }

        @Override
        public void log(MLevel mLevel, String string) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Jdk14MLogger.level(mLevel), stringArray[0], stringArray[1], string);
        }

        @Override
        public void log(MLevel mLevel, String string, Object object) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Jdk14MLogger.level(mLevel), stringArray[0], stringArray[1], string, object);
        }

        @Override
        public void log(MLevel mLevel, String string, Object[] objectArray) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Jdk14MLogger.level(mLevel), stringArray[0], stringArray[1], string, objectArray);
        }

        @Override
        public void log(MLevel mLevel, String string, Throwable throwable) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Jdk14MLogger.level(mLevel), stringArray[0], stringArray[1], string, throwable);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logp(Jdk14MLogger.level(mLevel), string, string2, string3);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logp(Jdk14MLogger.level(mLevel), string, string2, string3, object);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logp(Jdk14MLogger.level(mLevel), string, string2, string3, objectArray);
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logp(Jdk14MLogger.level(mLevel), string, string2, string3, throwable);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logrb(Jdk14MLogger.level(mLevel), string, string2, string3, string4);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logrb(Jdk14MLogger.level(mLevel), string, string2, string3, string4, object);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logrb(Jdk14MLogger.level(mLevel), string, string2, string3, string4, objectArray);
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
            if (!this.logger.isLoggable(Jdk14MLogger.level(mLevel))) {
                return;
            }
            if (string == null && string2 == null) {
                String[] stringArray = this.cmFinder.find();
                string = stringArray[0];
                string2 = stringArray[1];
            }
            this.logger.logrb(Jdk14MLogger.level(mLevel), string, string2, string3, string4, throwable);
        }

        @Override
        public void entering(String string, String string2) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            this.logger.entering(string, string2);
        }

        @Override
        public void entering(String string, String string2, Object object) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            this.logger.entering(string, string2, object);
        }

        @Override
        public void entering(String string, String string2, Object[] objectArray) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            this.logger.entering(string, string2, objectArray);
        }

        @Override
        public void exiting(String string, String string2) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            this.logger.exiting(string, string2);
        }

        @Override
        public void exiting(String string, String string2, Object object) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            this.logger.exiting(string, string2, object);
        }

        @Override
        public void throwing(String string, String string2, Throwable throwable) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            this.logger.throwing(string, string2, throwable);
        }

        @Override
        public void severe(String string) {
            if (!this.logger.isLoggable(Level.SEVERE)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.SEVERE, stringArray[0], stringArray[1], string);
        }

        @Override
        public void warning(String string) {
            if (!this.logger.isLoggable(Level.WARNING)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.WARNING, stringArray[0], stringArray[1], string);
        }

        @Override
        public void info(String string) {
            if (!this.logger.isLoggable(Level.INFO)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.INFO, stringArray[0], stringArray[1], string);
        }

        @Override
        public void config(String string) {
            if (!this.logger.isLoggable(Level.CONFIG)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.CONFIG, stringArray[0], stringArray[1], string);
        }

        @Override
        public void fine(String string) {
            if (!this.logger.isLoggable(Level.FINE)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.FINE, stringArray[0], stringArray[1], string);
        }

        @Override
        public void finer(String string) {
            if (!this.logger.isLoggable(Level.FINER)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.FINER, stringArray[0], stringArray[1], string);
        }

        @Override
        public void finest(String string) {
            if (!this.logger.isLoggable(Level.FINEST)) {
                return;
            }
            String[] stringArray = this.cmFinder.find();
            this.logger.logp(Level.FINEST, stringArray[0], stringArray[1], string);
        }

        @Override
        public void setLevel(MLevel mLevel) throws SecurityException {
            this.logger.setLevel(Jdk14MLogger.level(mLevel));
        }

        @Override
        public MLevel getLevel() {
            return MLevel.fromIntValue(this.logger.getLevel().intValue());
        }

        @Override
        public boolean isLoggable(MLevel mLevel) {
            return this.logger.isLoggable(Jdk14MLogger.level(mLevel));
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public void addHandler(Object object) throws SecurityException {
            if (!(object instanceof Handler)) {
                throw new IllegalArgumentException("MLogger.addHandler( ... ) requires a java.util.logging.Handler. This is not enforced by the compiler only to permit building under jdk 1.3");
            }
            this.logger.addHandler((Handler)object);
        }

        @Override
        public void removeHandler(Object object) throws SecurityException {
            if (!(object instanceof Handler)) {
                throw new IllegalArgumentException("MLogger.removeHandler( ... ) requires a java.util.logging.Handler. This is not enforced by the compiler only to permit building under jdk 1.3");
            }
            this.logger.removeHandler((Handler)object);
        }

        @Override
        public Object[] getHandlers() {
            return this.logger.getHandlers();
        }

        @Override
        public void setUseParentHandlers(boolean bl) {
            this.logger.setUseParentHandlers(bl);
        }

        @Override
        public boolean getUseParentHandlers() {
            return this.logger.getUseParentHandlers();
        }

        static interface ClassAndMethodFinder {
            public String[] find();
        }
    }
}

