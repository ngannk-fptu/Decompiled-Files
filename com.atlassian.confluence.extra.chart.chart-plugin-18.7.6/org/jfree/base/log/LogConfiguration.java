/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.log;

import org.jfree.base.BaseBoot;

public class LogConfiguration {
    public static final String DISABLE_LOGGING_DEFAULT = "false";
    public static final String LOGLEVEL = "org.jfree.base.LogLevel";
    public static final String LOGLEVEL_DEFAULT = "Info";
    public static final String LOGTARGET = "org.jfree.base.LogTarget";
    public static final String LOGTARGET_DEFAULT = (class$org$jfree$util$PrintStreamLogTarget == null ? (class$org$jfree$util$PrintStreamLogTarget = LogConfiguration.class$("org.jfree.util.PrintStreamLogTarget")) : class$org$jfree$util$PrintStreamLogTarget).getName();
    public static final String DISABLE_LOGGING = "org.jfree.base.NoDefaultDebug";
    static /* synthetic */ Class class$org$jfree$util$PrintStreamLogTarget;

    private LogConfiguration() {
    }

    public static String getLogTarget() {
        return BaseBoot.getInstance().getGlobalConfig().getConfigProperty(LOGTARGET, LOGTARGET_DEFAULT);
    }

    public static void setLogTarget(String logTarget) {
        BaseBoot.getConfiguration().setConfigProperty(LOGTARGET, logTarget);
    }

    public static String getLogLevel() {
        return BaseBoot.getInstance().getGlobalConfig().getConfigProperty(LOGLEVEL, LOGLEVEL_DEFAULT);
    }

    public static void setLogLevel(String level) {
        BaseBoot.getConfiguration().setConfigProperty(LOGLEVEL, level);
    }

    public static boolean isDisableLogging() {
        return BaseBoot.getInstance().getGlobalConfig().getConfigProperty(DISABLE_LOGGING, DISABLE_LOGGING_DEFAULT).equalsIgnoreCase("true");
    }

    public static void setDisableLogging(boolean disableLogging) {
        BaseBoot.getConfiguration().setConfigProperty(DISABLE_LOGGING, String.valueOf(disableLogging));
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

