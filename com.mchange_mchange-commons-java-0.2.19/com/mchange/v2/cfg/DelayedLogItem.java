/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.lang.ObjectUtils;

public final class DelayedLogItem {
    private Level level;
    private String text;
    private Throwable exception;

    public Level getLevel() {
        return this.level;
    }

    public String getText() {
        return this.text;
    }

    public Throwable getException() {
        return this.exception;
    }

    public DelayedLogItem(Level level, String string, Throwable throwable) {
        this.level = level;
        this.text = string;
        this.exception = throwable;
    }

    public DelayedLogItem(Level level, String string) {
        this(level, string, null);
    }

    public boolean equals(Object object) {
        if (object instanceof DelayedLogItem) {
            DelayedLogItem delayedLogItem = (DelayedLogItem)object;
            return this.level.equals((Object)delayedLogItem.level) && this.text.equals(delayedLogItem.text) && ObjectUtils.eqOrBothNull(this.exception, delayedLogItem.exception);
        }
        return false;
    }

    public int hashCode() {
        return this.level.hashCode() ^ this.text.hashCode() ^ ObjectUtils.hashOrZero(this.exception);
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.getClass().getName());
        stringBuffer.append(String.format(" [ level -> %s, text -> \"%s\", exception -> %s]", new Object[]{this.level, this.text, this.exception}));
        return stringBuffer.toString();
    }

    public static enum Level {
        ALL,
        CONFIG,
        FINE,
        FINER,
        FINEST,
        INFO,
        OFF,
        SEVERE,
        WARNING;

    }
}

