/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.ResourceBundle;

final class RedirectableMLogger
implements MLogger {
    private static HashSet weakRefSet = new HashSet();
    private String _name;
    private MLogger _inner;

    static synchronized RedirectableMLogger wrap(MLogger mLogger) {
        RedirectableMLogger redirectableMLogger = new RedirectableMLogger(mLogger);
        weakRefSet.add(new WeakReference<RedirectableMLogger>(redirectableMLogger));
        return redirectableMLogger;
    }

    static synchronized void resetAll() {
        HashSet hashSet = (HashSet)weakRefSet.clone();
        for (WeakReference weakReference : hashSet) {
            RedirectableMLogger redirectableMLogger = (RedirectableMLogger)weakReference.get();
            if (redirectableMLogger == null) {
                weakRefSet.remove(weakReference);
                continue;
            }
            redirectableMLogger.reset();
        }
    }

    private synchronized void reset() {
        this._inner = null;
    }

    private synchronized MLogger inner() {
        if (this._inner == null) {
            this._inner = MLog.getLogger(this._name);
        }
        return this._inner;
    }

    private RedirectableMLogger(MLogger mLogger) {
        this._inner = mLogger;
        this._name = mLogger.getName();
    }

    @Override
    public String getName() {
        return this.inner().getName();
    }

    @Override
    public void log(MLevel mLevel, String string) {
        this.inner().log(mLevel, string);
    }

    @Override
    public void log(MLevel mLevel, String string, Object object) {
        this.inner().log(mLevel, string, object);
    }

    @Override
    public void log(MLevel mLevel, String string, Object[] objectArray) {
        this.inner().log(mLevel, string, objectArray);
    }

    @Override
    public void log(MLevel mLevel, String string, Throwable throwable) {
        this.inner().log(mLevel, string, throwable);
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3) {
        this.inner().logp(mLevel, string, string2, string3);
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
        this.inner().logp(mLevel, string, string2, string3, object);
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
        this.inner().logp(mLevel, string, string2, string3, objectArray);
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
        this.inner().logp(mLevel, string, string2, string3, throwable);
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
        this.inner().logrb(mLevel, string, string2, string3, string4);
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
        this.inner().logrb(mLevel, string, string2, string3, string4, object);
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
        this.inner().logrb(mLevel, string, string2, string3, string4, objectArray);
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
        this.inner().logrb(mLevel, string, string2, string3, string4, throwable);
    }

    @Override
    public void entering(String string, String string2) {
        this.inner().entering(string, string2);
    }

    @Override
    public void entering(String string, String string2, Object object) {
        this.inner().entering(string, string2, object);
    }

    @Override
    public void entering(String string, String string2, Object[] objectArray) {
        this.inner().entering(string, string2, objectArray);
    }

    @Override
    public void exiting(String string, String string2) {
        this.inner().exiting(string, string2);
    }

    @Override
    public void exiting(String string, String string2, Object object) {
        this.inner().exiting(string, string2, object);
    }

    @Override
    public void throwing(String string, String string2, Throwable throwable) {
        this.inner().throwing(string, string2, throwable);
    }

    @Override
    public void severe(String string) {
        this.inner().severe(string);
    }

    @Override
    public void warning(String string) {
        this.inner().warning(string);
    }

    @Override
    public void info(String string) {
        this.inner().info(string);
    }

    @Override
    public void config(String string) {
        this.inner().config(string);
    }

    @Override
    public void fine(String string) {
        this.inner().fine(string);
    }

    @Override
    public void finer(String string) {
        this.inner().finer(string);
    }

    @Override
    public void finest(String string) {
        this.inner().finest(string);
    }

    @Override
    public boolean isLoggable(MLevel mLevel) {
        return this.inner().isLoggable(mLevel);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return this.inner().getResourceBundle();
    }

    @Override
    public String getResourceBundleName() {
        return this.inner().getResourceBundleName();
    }

    @Override
    public void setFilter(Object object) throws SecurityException {
        this.inner().setFilter(object);
    }

    @Override
    public Object getFilter() {
        return this.inner().getFilter();
    }

    @Override
    public void setLevel(MLevel mLevel) throws SecurityException {
        this.inner().setLevel(mLevel);
    }

    @Override
    public MLevel getLevel() {
        return this.inner().getLevel();
    }

    @Override
    public void addHandler(Object object) throws SecurityException {
        this.inner().addHandler(object);
    }

    @Override
    public void removeHandler(Object object) throws SecurityException {
        this.inner().removeHandler(object);
    }

    @Override
    public Object[] getHandlers() {
        return this.inner().getHandlers();
    }

    @Override
    public void setUseParentHandlers(boolean bl) {
        this.inner().setUseParentHandlers(bl);
    }

    @Override
    public boolean getUseParentHandlers() {
        return this.inner().getUseParentHandlers();
    }
}

