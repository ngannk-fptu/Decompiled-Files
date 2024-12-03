/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.v1.util.StringTokenizerUtils;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.mchange.v2.log.FallbackMLog;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLogClasses;
import com.mchange.v2.log.MLogConfig;
import com.mchange.v2.log.MLogInitializationException;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.NameTransformer;
import com.mchange.v2.log.RedirectableMLogger;
import java.util.ArrayList;

public abstract class MLog {
    private static boolean _redirectableLoggers;
    private static NameTransformer _transformer;
    private static MLog _mlog;
    private static MLogger _logger;

    public static synchronized boolean usingRedirectableLoggers() {
        return _redirectableLoggers;
    }

    private static synchronized NameTransformer transformer() {
        return _transformer;
    }

    private static synchronized MLog mlog() {
        return _mlog;
    }

    private static synchronized MLogger logger() {
        return _logger;
    }

    public static void refreshConfig() {
        MLog.refreshConfig(null, null);
    }

    public static MLog forceFallback() {
        return MLog.forceFallback(null);
    }

    public static synchronized MLog forceFallback(MLevel mLevel) {
        MLog mLog = _mlog;
        MLog.info("Forcing replacement of " + mLog.getClass().getName() + " with fallback (with cutoff " + mLevel + ") -- Everything goes to System.err.");
        FallbackMLog fallbackMLog = new FallbackMLog();
        if (mLevel != null) {
            fallbackMLog.overrideCutoffLevel(mLevel);
        }
        _mlog = fallbackMLog;
        _logger = MLog.getLogger(MLog.class);
        MLog.info("Forced replacement of " + mLog.getClass().getName() + " with fallback " + _mlog.getClass().getName() + " (with cutoff " + fallbackMLog.cutoffLevel() + ") -- Everything goes to System.err.");
        RedirectableMLogger.resetAll();
        return mLog;
    }

    public static synchronized MLog forceMLog(MLog mLog) {
        MLog mLog2 = _mlog;
        MLog.info("Forcing replacement of " + mLog2.getClass().getName() + " with " + mLog);
        _mlog = mLog;
        _logger = MLog.getLogger(MLog.class);
        MLog.info("Forced replacement of " + mLog2.getClass().getName() + " with " + _mlog.getClass().getName());
        RedirectableMLogger.resetAll();
        return mLog2;
    }

    public static synchronized void refreshConfig(MultiPropertiesConfig[] multiPropertiesConfigArray, String string) {
        MLogConfig.refresh(multiPropertiesConfigArray, string);
        RedirectableMLogger.resetAll();
        String string2 = MLogConfig.getProperty("com.mchange.v2.log.MLog.useRedirectableLoggers");
        _redirectableLoggers = string2 != null && string2.equalsIgnoreCase("true");
        String string3 = MLogConfig.getProperty("com.mchange.v2.log.MLog");
        String[] stringArray = null;
        if (string3 == null) {
            string3 = MLogConfig.getProperty("com.mchange.v2.log.mlog");
        }
        if (string3 != null) {
            stringArray = StringTokenizerUtils.tokenizeToArray(string3, ", \t\r\n");
        }
        boolean bl = false;
        MLog mLog = null;
        if (stringArray != null) {
            mLog = MLog.findByClassnames(stringArray, true);
        }
        if (mLog == null) {
            mLog = MLog.findByClassnames(MLogClasses.SEARCH_CLASSNAMES, false);
        }
        if (mLog == null) {
            bl = true;
            mLog = new FallbackMLog();
        }
        _mlog = mLog;
        if (bl) {
            MLog.info("Using " + _mlog.getClass().getName() + " -- everything goes to System.err.");
        }
        NameTransformer nameTransformer = null;
        String string4 = MLogConfig.getProperty("com.mchange.v2.log.NameTransformer");
        if (string4 == null) {
            string4 = MLogConfig.getProperty("com.mchange.v2.log.nametransformer");
        }
        try {
            if (string4 != null) {
                nameTransformer = (NameTransformer)Class.forName(string4).newInstance();
            }
        }
        catch (Exception exception) {
            System.err.println("Failed to instantiate com.mchange.v2.log.NameTransformer '" + string4 + "'!");
            exception.printStackTrace();
        }
        _transformer = nameTransformer;
        _logger = MLog.getLogger(MLog.class);
        Thread thread = new Thread("MLog-Init-Reporter"){
            final MLogger logo = MLog.access$000();
            String loggerDesc = MLog.access$100().getClass().getName();

            @Override
            public void run() {
                if ("com.mchange.v2.log.jdk14logging.Jdk14MLog".equals(this.loggerDesc)) {
                    this.loggerDesc = "java 1.4+ standard";
                } else if ("com.mchange.v2.log.log4j2.Log4j2MLog".equals(this.loggerDesc)) {
                    this.loggerDesc = "log4j2";
                } else if ("com.mchange.v2.log.log4j.Log4jMLog".equals(this.loggerDesc)) {
                    this.loggerDesc = "log4j";
                } else if ("com.mchange.v2.log.slf4j.Slf4jMLog".equals(this.loggerDesc)) {
                    this.loggerDesc = "slf4j";
                }
                if (this.logo.isLoggable(MLevel.INFO)) {
                    String string = MLog.usingRedirectableLoggers() ? " with redirectable loggers" : "";
                    this.logo.log(MLevel.INFO, "MLog clients using " + this.loggerDesc + " logging" + string + '.');
                }
                MLogConfig.logDelayedItems(this.logo);
                if (this.logo.isLoggable(MLevel.FINEST)) {
                    this.logo.log(MLevel.FINEST, "Config available to MLog library: " + MLogConfig.dump());
                }
            }
        };
        thread.start();
    }

    public static MLog findByClassnames(String[] stringArray, boolean bl) {
        int n;
        ArrayList<String> arrayList = null;
        int n2 = stringArray.length;
        for (n = 0; n < n2; ++n) {
            try {
                return (MLog)Class.forName(MLogClasses.resolveIfAlias(stringArray[n])).newInstance();
            }
            catch (Exception exception) {
                if (exception instanceof MLogInitializationException) {
                    System.err.println("MLog initialization issue: " + exception.getMessage());
                }
                if (arrayList == null) {
                    arrayList = new ArrayList<String>();
                }
                arrayList.add(stringArray[n]);
                if (!bl) continue;
                System.err.println("com.mchange.v2.log.MLog '" + stringArray[n] + "' could not be loaded!");
                exception.printStackTrace();
                continue;
            }
        }
        System.err.println("Tried without success to load the following MLog classes:");
        n2 = arrayList.size();
        for (n = 0; n < n2; ++n) {
            System.err.println("\t" + arrayList.get(n));
        }
        return null;
    }

    public static MLog instance() {
        return MLog.mlog();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MLogger getLogger(String string) {
        NameTransformer nameTransformer = null;
        MLog mLog = null;
        Object object = MLog.class;
        synchronized (MLog.class) {
            String string2;
            nameTransformer = MLog.transformer();
            mLog = MLog.instance();
            boolean bl = _redirectableLoggers;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            object = nameTransformer == null ? mLog.getMLogger(string) : ((string2 = nameTransformer.transformName(string)) != null ? mLog.getMLogger(string2) : mLog.getMLogger(string));
            return bl ? RedirectableMLogger.wrap((MLogger)object) : object;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MLogger getLogger(Class clazz) {
        NameTransformer nameTransformer = null;
        MLog mLog = null;
        Object object = MLog.class;
        synchronized (MLog.class) {
            String string;
            nameTransformer = MLog.transformer();
            mLog = MLog.instance();
            boolean bl = _redirectableLoggers;
            // ** MonitorExit[var4_3] (shouldn't be in output)
            object = nameTransformer == null ? mLog.getMLogger(clazz) : ((string = nameTransformer.transformName(clazz)) != null ? mLog.getMLogger(string) : mLog.getMLogger(clazz));
            return bl ? RedirectableMLogger.wrap((MLogger)object) : object;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MLogger getLogger() {
        NameTransformer nameTransformer = null;
        MLog mLog = null;
        Object object = MLog.class;
        synchronized (MLog.class) {
            String string;
            nameTransformer = MLog.transformer();
            mLog = MLog.instance();
            boolean bl = _redirectableLoggers;
            // ** MonitorExit[var3_2] (shouldn't be in output)
            object = nameTransformer == null ? mLog.getMLogger() : ((string = nameTransformer.transformName()) != null ? mLog.getMLogger(string) : mLog.getMLogger());
            return bl ? RedirectableMLogger.wrap((MLogger)object) : object;
        }
    }

    public static void log(MLevel mLevel, String string) {
        MLog.instance();
        MLog.getLogger().log(mLevel, string);
    }

    public static void log(MLevel mLevel, String string, Object object) {
        MLog.instance();
        MLog.getLogger().log(mLevel, string, object);
    }

    public static void log(MLevel mLevel, String string, Object[] objectArray) {
        MLog.instance();
        MLog.getLogger().log(mLevel, string, objectArray);
    }

    public static void log(MLevel mLevel, String string, Throwable throwable) {
        MLog.instance();
        MLog.getLogger().log(mLevel, string, throwable);
    }

    public static void logp(MLevel mLevel, String string, String string2, String string3) {
        MLog.instance();
        MLog.getLogger().logp(mLevel, string, string2, string3);
    }

    public static void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
        MLog.instance();
        MLog.getLogger().logp(mLevel, string, string2, string3, object);
    }

    public static void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
        MLog.instance();
        MLog.getLogger().logp(mLevel, string, string2, string3, objectArray);
    }

    public static void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
        MLog.instance();
        MLog.getLogger().logp(mLevel, string, string2, string3, throwable);
    }

    public static void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
        MLog.instance();
        MLog.getLogger().logp(mLevel, string, string2, string3, string4);
    }

    public static void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
        MLog.instance();
        MLog.getLogger().logrb(mLevel, string, string2, string3, string4, object);
    }

    public static void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
        MLog.instance();
        MLog.getLogger().logrb(mLevel, string, string2, string3, string4, objectArray);
    }

    public static void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
        MLog.instance();
        MLog.getLogger().logrb(mLevel, string, string2, string3, string4, throwable);
    }

    public static void entering(String string, String string2) {
        MLog.instance();
        MLog.getLogger().entering(string, string2);
    }

    public static void entering(String string, String string2, Object object) {
        MLog.instance();
        MLog.getLogger().entering(string, string2, object);
    }

    public static void entering(String string, String string2, Object[] objectArray) {
        MLog.instance();
        MLog.getLogger().entering(string, string2, objectArray);
    }

    public static void exiting(String string, String string2) {
        MLog.instance();
        MLog.getLogger().exiting(string, string2);
    }

    public static void exiting(String string, String string2, Object object) {
        MLog.instance();
        MLog.getLogger().exiting(string, string2, object);
    }

    public static void throwing(String string, String string2, Throwable throwable) {
        MLog.instance();
        MLog.getLogger().throwing(string, string2, throwable);
    }

    public static void severe(String string) {
        MLog.instance();
        MLog.getLogger().severe(string);
    }

    public static void warning(String string) {
        MLog.instance();
        MLog.getLogger().warning(string);
    }

    public static void info(String string) {
        MLog.instance();
        MLog.getLogger().info(string);
    }

    public static void config(String string) {
        MLog.instance();
        MLog.getLogger().config(string);
    }

    public static void fine(String string) {
        MLog.instance();
        MLog.getLogger().fine(string);
    }

    public static void finer(String string) {
        MLog.instance();
        MLog.getLogger().finer(string);
    }

    public static void finest(String string) {
        MLog.instance();
        MLog.getLogger().finest(string);
    }

    public MLogger getMLogger(Class clazz) {
        return this.getMLogger(clazz.getName());
    }

    public abstract MLogger getMLogger(String var1);

    public abstract MLogger getMLogger();

    static /* synthetic */ MLogger access$000() {
        return _logger;
    }

    static /* synthetic */ MLog access$100() {
        return _mlog;
    }

    static {
        MLog.refreshConfig(null, null);
    }
}

