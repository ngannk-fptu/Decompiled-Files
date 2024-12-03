/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.lang.ThrowableUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogConfig;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.jdk14logging.Jdk14LoggingUtils;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;

public final class FallbackMLog
extends MLog {
    static final MLevel DEFAULT_CUTOFF_LEVEL;
    static final String SEP;
    private static MLevel overrideCutoffLevel;
    private static Filter globalFilter;
    private final MLogger nameless = new FallbackMLogger();

    public synchronized MLevel cutoffLevel() {
        if (overrideCutoffLevel != null) {
            return overrideCutoffLevel;
        }
        return DEFAULT_CUTOFF_LEVEL;
    }

    public void overrideCutoffLevel(MLevel mLevel) {
        this.setOverrideCutoffLevel(mLevel);
    }

    public synchronized void setOverrideCutoffLevel(MLevel mLevel) {
        overrideCutoffLevel = mLevel;
    }

    public synchronized MLevel getOverrideCutoffLevel() {
        return overrideCutoffLevel;
    }

    private static Filter filterFromObject(Object object) {
        if (object instanceof Filter) {
            return (Filter)object;
        }
        if (object instanceof java.util.logging.Filter) {
            return new Jdk14FilterAdapter((java.util.logging.Filter)object);
        }
        throw new IllegalArgumentException("Provided filter " + object + " must be either a FallbackMLog.Filter or an instance of java.util.logging.Filter.");
    }

    public synchronized void setGlobalFilter(Object object) {
        globalFilter = FallbackMLog.filterFromObject(object);
    }

    public synchronized Object getGlobalFilter() {
        if (globalFilter instanceof Jdk14FilterAdapter) {
            return ((Jdk14FilterAdapter)globalFilter).getInner();
        }
        return globalFilter;
    }

    private synchronized Filter _getGlobalFilter() {
        return globalFilter;
    }

    @Override
    public MLogger getMLogger(String string) {
        return new FallbackMLogger(string);
    }

    @Override
    public MLogger getMLogger() {
        return this.nameless;
    }

    static {
        overrideCutoffLevel = null;
        globalFilter = null;
        MLevel mLevel = null;
        String string = MLogConfig.getProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL");
        if (string != null) {
            mLevel = MLevel.fromSeverity(string);
        }
        if (mLevel == null) {
            mLevel = MLevel.INFO;
        }
        DEFAULT_CUTOFF_LEVEL = mLevel;
        SEP = System.getProperty("line.separator");
    }

    private final class FallbackMLogger
    implements MLogger {
        String name;
        Filter filter = null;

        public FallbackMLogger(String string) {
            this.name = string;
        }

        public FallbackMLogger() {
            this.name = null;
        }

        private void formatrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray, Throwable throwable) {
            String string5;
            ResourceBundle resourceBundle = ResourceBundle.getBundle(string3);
            if (string4 != null && resourceBundle != null && (string5 = resourceBundle.getString(string4)) != null) {
                string4 = string5;
            }
            this.format(mLevel, string, string2, string4, objectArray, throwable);
        }

        private boolean isLoggableMessage(MLevel mLevel, String string, String string2, String string3, Object[] objectArray, Throwable throwable) {
            boolean bl;
            Filter filter = FallbackMLog.this._getGlobalFilter();
            boolean bl2 = bl = filter == null || filter.isLoggable(mLevel, this.name, string, string2, string3, objectArray, throwable);
            if (bl) {
                Filter filter2 = this._getFilter();
                boolean bl3 = filter2 == null || filter2.isLoggable(mLevel, this.name, string, string2, string3, objectArray, throwable);
                return bl3;
            }
            return false;
        }

        private void format(MLevel mLevel, String string, String string2, String string3, Object[] objectArray, Throwable throwable) {
            if (this.isLoggableMessage(mLevel, string, string2, string3, objectArray, throwable)) {
                System.err.println(this.formatString(mLevel, string, string2, string3, objectArray, throwable));
            }
        }

        private String formatString(MLevel mLevel, String string, String string2, String string3, Object[] objectArray, Throwable throwable) {
            boolean bl = string2 != null && !string2.endsWith(")");
            StringBuffer stringBuffer = new StringBuffer(256);
            stringBuffer.append(mLevel.getLineHeader());
            stringBuffer.append(' ');
            if (this.name != null) {
                stringBuffer.append(this.name);
                stringBuffer.append(' ');
            }
            if (string != null && string2 != null) {
                stringBuffer.append('[');
                stringBuffer.append(string);
                stringBuffer.append('.');
                stringBuffer.append(string2);
                if (bl) {
                    stringBuffer.append("()");
                }
                stringBuffer.append(']');
            } else if (string != null) {
                stringBuffer.append('[');
                stringBuffer.append(string);
                stringBuffer.append(']');
            } else if (string2 != null) {
                stringBuffer.append('[');
                stringBuffer.append(string2);
                if (bl) {
                    stringBuffer.append("()");
                }
                stringBuffer.append(']');
            }
            if (string3 == null) {
                if (objectArray != null) {
                    stringBuffer.append("params: ");
                    int n = objectArray.length;
                    for (int i = 0; i < n; ++i) {
                        if (i != 0) {
                            stringBuffer.append(", ");
                        }
                        stringBuffer.append(objectArray[i]);
                    }
                }
            } else if (objectArray == null) {
                stringBuffer.append(string3);
            } else {
                MessageFormat messageFormat = new MessageFormat(string3);
                stringBuffer.append(messageFormat.format(objectArray));
            }
            if (throwable != null) {
                stringBuffer.append(SEP);
                stringBuffer.append(ThrowableUtils.extractStackTrace(throwable));
            }
            return stringBuffer.toString();
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
        public synchronized void setFilter(Object object) {
            this.filter = FallbackMLog.filterFromObject(object);
        }

        @Override
        public synchronized Object getFilter() {
            if (this.filter instanceof Jdk14FilterAdapter) {
                return ((Jdk14FilterAdapter)this.filter).getInner();
            }
            return this.filter;
        }

        private synchronized Filter _getFilter() {
            return this.filter;
        }

        @Override
        public void log(MLevel mLevel, String string) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, null, null, string, null, null);
            }
        }

        @Override
        public void log(MLevel mLevel, String string, Object object) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, null, null, string, new Object[]{object}, null);
            }
        }

        @Override
        public void log(MLevel mLevel, String string, Object[] objectArray) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, null, null, string, objectArray, null);
            }
        }

        @Override
        public void log(MLevel mLevel, String string, Throwable throwable) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, null, null, string, null, throwable);
            }
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, string, string2, string3, null, null);
            }
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, string, string2, string3, new Object[]{object}, null);
            }
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, string, string2, string3, objectArray, null);
            }
        }

        @Override
        public void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
            if (this.isLoggable(mLevel)) {
                this.format(mLevel, string, string2, string3, null, throwable);
            }
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
            if (this.isLoggable(mLevel)) {
                this.formatrb(mLevel, string, string2, string3, string4, null, null);
            }
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
            if (this.isLoggable(mLevel)) {
                this.formatrb(mLevel, string, string2, string3, string4, new Object[]{object}, null);
            }
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
            if (this.isLoggable(mLevel)) {
                this.formatrb(mLevel, string, string2, string3, string4, objectArray, null);
            }
        }

        @Override
        public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
            if (this.isLoggable(mLevel)) {
                this.formatrb(mLevel, string, string2, string3, string4, null, throwable);
            }
        }

        @Override
        public void entering(String string, String string2) {
            if (this.isLoggable(MLevel.FINER)) {
                this.format(MLevel.FINER, string, string2, "Entering method.", null, null);
            }
        }

        @Override
        public void entering(String string, String string2, Object object) {
            if (this.isLoggable(MLevel.FINER)) {
                this.format(MLevel.FINER, string, string2, "Entering method with argument " + object, null, null);
            }
        }

        @Override
        public void entering(String string, String string2, Object[] objectArray) {
            if (this.isLoggable(MLevel.FINER)) {
                if (objectArray == null) {
                    this.entering(string, string2);
                } else {
                    StringBuffer stringBuffer = new StringBuffer(128);
                    stringBuffer.append("( ");
                    int n = objectArray.length;
                    for (int i = 0; i < n; ++i) {
                        if (i != 0) {
                            stringBuffer.append(", ");
                        }
                        stringBuffer.append(objectArray[i]);
                    }
                    stringBuffer.append(" )");
                    this.format(MLevel.FINER, string, string2, "Entering method with arguments " + stringBuffer.toString(), null, null);
                }
            }
        }

        @Override
        public void exiting(String string, String string2) {
            if (this.isLoggable(MLevel.FINER)) {
                this.format(MLevel.FINER, string, string2, "Exiting method.", null, null);
            }
        }

        @Override
        public void exiting(String string, String string2, Object object) {
            if (this.isLoggable(MLevel.FINER)) {
                this.format(MLevel.FINER, string, string2, "Exiting method with result " + object, null, null);
            }
        }

        @Override
        public void throwing(String string, String string2, Throwable throwable) {
            if (this.isLoggable(MLevel.FINE)) {
                this.format(MLevel.FINE, string, string2, "Throwing exception.", null, throwable);
            }
        }

        @Override
        public void severe(String string) {
            if (this.isLoggable(MLevel.SEVERE)) {
                this.format(MLevel.SEVERE, null, null, string, null, null);
            }
        }

        @Override
        public void warning(String string) {
            if (this.isLoggable(MLevel.WARNING)) {
                this.format(MLevel.WARNING, null, null, string, null, null);
            }
        }

        @Override
        public void info(String string) {
            if (this.isLoggable(MLevel.INFO)) {
                this.format(MLevel.INFO, null, null, string, null, null);
            }
        }

        @Override
        public void config(String string) {
            if (this.isLoggable(MLevel.CONFIG)) {
                this.format(MLevel.CONFIG, null, null, string, null, null);
            }
        }

        @Override
        public void fine(String string) {
            if (this.isLoggable(MLevel.FINE)) {
                this.format(MLevel.FINE, null, null, string, null, null);
            }
        }

        @Override
        public void finer(String string) {
            if (this.isLoggable(MLevel.FINER)) {
                this.format(MLevel.FINER, null, null, string, null, null);
            }
        }

        @Override
        public void finest(String string) {
            if (this.isLoggable(MLevel.FINEST)) {
                this.format(MLevel.FINEST, null, null, string, null, null);
            }
        }

        @Override
        public void setLevel(MLevel mLevel) throws SecurityException {
            FallbackMLog.this.overrideCutoffLevel(mLevel);
        }

        @Override
        public synchronized MLevel getLevel() {
            return FallbackMLog.this.cutoffLevel();
        }

        @Override
        public synchronized boolean isLoggable(MLevel mLevel) {
            return mLevel.intValue() >= FallbackMLog.this.cutoffLevel().intValue();
        }

        @Override
        public String getName() {
            return "global";
        }

        @Override
        public void addHandler(Object object) throws SecurityException {
            this.warning("Using FallbackMLog -- Handlers not supported.");
        }

        @Override
        public void removeHandler(Object object) throws SecurityException {
            this.warning("Using FallbackMLog -- Handlers not supported.");
        }

        @Override
        public Object[] getHandlers() {
            this.warning("Using FallbackMLog -- Handlers not supported.");
            return new Object[0];
        }

        @Override
        public void setUseParentHandlers(boolean bl) {
            this.warning("Using FallbackMLog -- Handlers not supported.");
        }

        @Override
        public boolean getUseParentHandlers() {
            return false;
        }
    }

    private static class Jdk14FilterAdapter
    implements Filter {
        private java.util.logging.Filter julFilter;

        Jdk14FilterAdapter(java.util.logging.Filter filter) {
            this.julFilter = filter;
        }

        java.util.logging.Filter getInner() {
            return this.julFilter;
        }

        @Override
        public boolean isLoggable(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray, Throwable throwable) {
            LogRecord logRecord = new LogRecord(Jdk14LoggingUtils.levelFromMLevel(mLevel), string4);
            logRecord.setLoggerName(string);
            logRecord.setSourceClassName(string2);
            logRecord.setSourceMethodName(string3);
            logRecord.setParameters(objectArray);
            logRecord.setThrown(throwable);
            return this.julFilter.isLoggable(logRecord);
        }
    }

    public static interface Filter {
        public boolean isLoggable(MLevel var1, String var2, String var3, String var4, String var5, Object[] var6, Throwable var7);
    }
}

