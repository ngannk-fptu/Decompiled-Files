/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import com.opensymphony.provider.LogProvider;
import com.opensymphony.provider.ProviderFactory;
import com.opensymphony.provider.log.DefaultLogProvider;
import java.io.Serializable;
import java.util.HashMap;

public class Logger
implements Serializable {
    private static final LogProvider logProvider;
    private Object context;

    public Logger(String name) {
        this.init(name);
    }

    public Logger(Class cls) {
        this.init(cls.getName());
    }

    public Logger(Object obj) {
        this.init(obj.getClass().getName());
    }

    public Logger(Class cls, String subCategory) {
        if (subCategory == null) {
            this.init(cls.getName());
        } else if (subCategory.charAt(0) == '.') {
            this.init(cls.getName() + subCategory);
        } else {
            this.init(cls.getName() + "." + subCategory);
        }
    }

    public boolean isDebugEnabled() {
        return this.isEnabledFor(1);
    }

    public boolean isEnabledFor(int priority) {
        return logProvider.isEnabled(this.context, priority);
    }

    public boolean isErrorEnabled() {
        return this.isEnabledFor(4);
    }

    public boolean isFatalEnabled() {
        return this.isEnabledFor(5);
    }

    public boolean isInfoEnabled() {
        return this.isEnabledFor(2);
    }

    public boolean isWarnEnabled() {
        return this.isEnabledFor(3);
    }

    public void debug(Object o) {
        this.log(1, o, null);
    }

    public void debug(Object o, Throwable t) {
        this.log(1, o, t);
    }

    public void error(Object o) {
        this.log(4, o, null);
    }

    public void error(Object o, Throwable t) {
        this.log(4, o, t);
    }

    public void fatal(Object o) {
        this.log(5, o, null);
    }

    public void fatal(Object o, Throwable t) {
        this.log(5, o, t);
    }

    public void info(Object o) {
        this.log(2, o, null);
    }

    public void info(Object o, Throwable t) {
        this.log(2, o, t);
    }

    public void log(int priority, Object o) {
        this.log(priority, o, null);
    }

    public void log(int priority, Object o, Throwable t) {
        logProvider.log(this.context, priority, o, t);
    }

    public void warn(Object o) {
        this.log(3, o, null);
    }

    public void warn(Object o, Throwable t) {
        this.log(3, o, t);
    }

    private void init(String name) {
        this.context = logProvider.getContext(name);
    }

    private static void providerModify() {
        if (System.getProperty("logger.config") != null && System.getProperty("logger.config").trim().length() > 0 && (System.getProperty("logger.provider") == null || System.getProperty("logger.provider").trim().length() == 0)) {
            System.setProperty("logger.provider", "com.opensymphony.provider.log.Log4JProvider");
        }
        HashMap<String, String> providerAliases = new HashMap<String, String>();
        providerAliases.put("default", "com.opensymphony.provider.log.DefaultLogProvider");
        providerAliases.put("null", "com.opensymphony.provider.log.NullLogProvider");
        providerAliases.put("full", "com.opensymphony.provider.log.FullLogProvider");
        providerAliases.put("log4j", "com.opensymphony.provider.log.Log4JProvider");
        if (System.getProperty("logger.provider") != null && providerAliases.containsKey(System.getProperty("logger.provider"))) {
            System.setProperty("logger.provider", (String)providerAliases.get(System.getProperty("logger.provider")));
        }
    }

    static {
        ProviderFactory factory = ProviderFactory.getInstance();
        Logger.providerModify();
        logProvider = (LogProvider)factory.getProvider("logger.provider", DefaultLogProvider.class.getName());
    }
}

