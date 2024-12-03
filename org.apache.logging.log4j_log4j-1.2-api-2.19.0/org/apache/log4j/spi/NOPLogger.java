/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.spi;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.NOPLoggerRepository;

public final class NOPLogger
extends Logger {
    public NOPLogger(NOPLoggerRepository repo, String name) {
        super(name);
        this.repository = repo;
        this.level = Level.OFF;
        this.parent = this;
    }

    @Override
    public void addAppender(Appender newAppender) {
    }

    @Override
    public void assertLog(boolean assertion, String msg) {
    }

    @Override
    public void callAppenders(LoggingEvent event) {
    }

    void closeNestedAppenders() {
    }

    @Override
    public void debug(Object message) {
    }

    @Override
    public void debug(Object message, Throwable t) {
    }

    @Override
    public void error(Object message) {
    }

    @Override
    public void error(Object message, Throwable t) {
    }

    @Override
    public void fatal(Object message) {
    }

    @Override
    public void fatal(Object message, Throwable t) {
    }

    @Override
    public Enumeration getAllAppenders() {
        return new Vector().elements();
    }

    @Override
    public Appender getAppender(String name) {
        return null;
    }

    @Override
    public Priority getChainedPriority() {
        return this.getEffectiveLevel();
    }

    @Override
    public Level getEffectiveLevel() {
        return Level.OFF;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return null;
    }

    @Override
    public void info(Object message) {
    }

    @Override
    public void info(Object message, Throwable t) {
    }

    @Override
    public boolean isAttached(Appender appender) {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isEnabledFor(Priority level) {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
    }

    @Override
    public void l7dlog(Priority priority, String key, Throwable t) {
    }

    @Override
    public void log(Priority priority, Object message) {
    }

    @Override
    public void log(Priority priority, Object message, Throwable t) {
    }

    @Override
    public void log(String callerFQCN, Priority level, Object message, Throwable t) {
    }

    @Override
    public void removeAllAppenders() {
    }

    @Override
    public void removeAppender(Appender appender) {
    }

    @Override
    public void removeAppender(String name) {
    }

    @Override
    public void setLevel(Level level) {
    }

    @Override
    public void setPriority(Priority priority) {
    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
    }

    @Override
    public void trace(Object message) {
    }

    @Override
    public void trace(Object message, Throwable t) {
    }

    @Override
    public void warn(Object message) {
    }

    @Override
    public void warn(Object message, Throwable t) {
    }
}

