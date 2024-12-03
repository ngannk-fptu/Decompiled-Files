/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.ILogger;
import java.util.logging.Level;

public abstract class AbstractLogger
implements ILogger {
    @Override
    public void finest(String message) {
        this.log(Level.FINEST, message);
    }

    @Override
    public void finest(String message, Throwable thrown) {
        this.log(Level.FINEST, message, thrown);
    }

    @Override
    public void finest(Throwable thrown) {
        this.log(Level.FINEST, thrown.getMessage(), thrown);
    }

    @Override
    public boolean isFinestEnabled() {
        return this.isLoggable(Level.FINEST);
    }

    @Override
    public void fine(String message) {
        this.log(Level.FINE, message);
    }

    @Override
    public void fine(String message, Throwable thrown) {
        this.log(Level.FINE, message, thrown);
    }

    @Override
    public void fine(Throwable thrown) {
        this.log(Level.FINE, thrown.getMessage(), thrown);
    }

    @Override
    public boolean isFineEnabled() {
        return this.isLoggable(Level.FINE);
    }

    @Override
    public void info(String message) {
        this.log(Level.INFO, message);
    }

    @Override
    public void info(String message, Throwable thrown) {
        this.log(Level.INFO, message, thrown);
    }

    @Override
    public void info(Throwable thrown) {
        this.log(Level.INFO, thrown.getMessage());
    }

    @Override
    public boolean isInfoEnabled() {
        return this.isLoggable(Level.INFO);
    }

    @Override
    public void warning(String message) {
        this.log(Level.WARNING, message);
    }

    @Override
    public void warning(Throwable thrown) {
        this.log(Level.WARNING, thrown.getMessage(), thrown);
    }

    @Override
    public void warning(String message, Throwable thrown) {
        this.log(Level.WARNING, message, thrown);
    }

    @Override
    public boolean isWarningEnabled() {
        return this.isLoggable(Level.WARNING);
    }

    @Override
    public void severe(String message) {
        this.log(Level.SEVERE, message);
    }

    @Override
    public void severe(Throwable thrown) {
        this.log(Level.SEVERE, thrown.getMessage(), thrown);
    }

    @Override
    public void severe(String message, Throwable thrown) {
        this.log(Level.SEVERE, message, thrown);
    }

    @Override
    public boolean isSevereEnabled() {
        return this.isLoggable(Level.SEVERE);
    }
}

