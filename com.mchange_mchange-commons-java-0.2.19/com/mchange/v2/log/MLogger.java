/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.v2.log.MLevel;
import java.util.ResourceBundle;

public interface MLogger {
    public String getName();

    public void log(MLevel var1, String var2);

    public void log(MLevel var1, String var2, Object var3);

    public void log(MLevel var1, String var2, Object[] var3);

    public void log(MLevel var1, String var2, Throwable var3);

    public void logp(MLevel var1, String var2, String var3, String var4);

    public void logp(MLevel var1, String var2, String var3, String var4, Object var5);

    public void logp(MLevel var1, String var2, String var3, String var4, Object[] var5);

    public void logp(MLevel var1, String var2, String var3, String var4, Throwable var5);

    public void logrb(MLevel var1, String var2, String var3, String var4, String var5);

    public void logrb(MLevel var1, String var2, String var3, String var4, String var5, Object var6);

    public void logrb(MLevel var1, String var2, String var3, String var4, String var5, Object[] var6);

    public void logrb(MLevel var1, String var2, String var3, String var4, String var5, Throwable var6);

    public void entering(String var1, String var2);

    public void entering(String var1, String var2, Object var3);

    public void entering(String var1, String var2, Object[] var3);

    public void exiting(String var1, String var2);

    public void exiting(String var1, String var2, Object var3);

    public void throwing(String var1, String var2, Throwable var3);

    public void severe(String var1);

    public void warning(String var1);

    public void info(String var1);

    public void config(String var1);

    public void fine(String var1);

    public void finer(String var1);

    public void finest(String var1);

    public boolean isLoggable(MLevel var1);

    public ResourceBundle getResourceBundle();

    public String getResourceBundleName();

    public void setFilter(Object var1) throws SecurityException;

    public Object getFilter();

    public void setLevel(MLevel var1) throws SecurityException;

    public MLevel getLevel();

    public void addHandler(Object var1) throws SecurityException;

    public void removeHandler(Object var1) throws SecurityException;

    public Object[] getHandlers();

    public void setUseParentHandlers(boolean var1);

    public boolean getUseParentHandlers();
}

