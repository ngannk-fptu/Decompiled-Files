/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLogger;
import java.util.ResourceBundle;

public class NullMLogger
implements MLogger {
    private static final MLogger INSTANCE = new NullMLogger();
    private static final String NAME = "NullMLogger";

    public static MLogger instance() {
        return INSTANCE;
    }

    private NullMLogger() {
    }

    @Override
    public void addHandler(Object object) throws SecurityException {
    }

    @Override
    public void config(String string) {
    }

    @Override
    public void entering(String string, String string2) {
    }

    @Override
    public void entering(String string, String string2, Object object) {
    }

    @Override
    public void entering(String string, String string2, Object[] objectArray) {
    }

    @Override
    public void exiting(String string, String string2) {
    }

    @Override
    public void exiting(String string, String string2, Object object) {
    }

    @Override
    public void fine(String string) {
    }

    @Override
    public void finer(String string) {
    }

    @Override
    public void finest(String string) {
    }

    @Override
    public Object getFilter() {
        return null;
    }

    @Override
    public Object[] getHandlers() {
        return null;
    }

    @Override
    public MLevel getLevel() {
        return MLevel.OFF;
    }

    @Override
    public String getName() {
        return NAME;
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
    public boolean getUseParentHandlers() {
        return false;
    }

    @Override
    public void info(String string) {
    }

    @Override
    public boolean isLoggable(MLevel mLevel) {
        return false;
    }

    @Override
    public void log(MLevel mLevel, String string) {
    }

    @Override
    public void log(MLevel mLevel, String string, Object object) {
    }

    @Override
    public void log(MLevel mLevel, String string, Object[] objectArray) {
    }

    @Override
    public void log(MLevel mLevel, String string, Throwable throwable) {
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3) {
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3, Object object) {
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3, Object[] objectArray) {
    }

    @Override
    public void logp(MLevel mLevel, String string, String string2, String string3, Throwable throwable) {
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4) {
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object object) {
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Object[] objectArray) {
    }

    @Override
    public void logrb(MLevel mLevel, String string, String string2, String string3, String string4, Throwable throwable) {
    }

    @Override
    public void removeHandler(Object object) throws SecurityException {
    }

    @Override
    public void setFilter(Object object) throws SecurityException {
    }

    @Override
    public void setLevel(MLevel mLevel) throws SecurityException {
    }

    @Override
    public void setUseParentHandlers(boolean bl) {
    }

    @Override
    public void severe(String string) {
    }

    @Override
    public void throwing(String string, String string2, Throwable throwable) {
    }

    @Override
    public void warning(String string) {
    }
}

