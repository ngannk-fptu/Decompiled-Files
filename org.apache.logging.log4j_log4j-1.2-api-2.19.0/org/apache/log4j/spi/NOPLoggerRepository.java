/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.spi;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.NOPLogger;

public final class NOPLoggerRepository
implements LoggerRepository {
    @Override
    public void addHierarchyEventListener(HierarchyEventListener listener) {
    }

    @Override
    public void emitNoAppenderWarning(Category cat) {
    }

    @Override
    public Logger exists(String name) {
        return null;
    }

    @Override
    public void fireAddAppenderEvent(Category logger, Appender appender) {
    }

    @Override
    public Enumeration getCurrentCategories() {
        return this.getCurrentLoggers();
    }

    @Override
    public Enumeration getCurrentLoggers() {
        return new Vector().elements();
    }

    @Override
    public Logger getLogger(String name) {
        return new NOPLogger(this, name);
    }

    @Override
    public Logger getLogger(String name, LoggerFactory factory) {
        return new NOPLogger(this, name);
    }

    @Override
    public Logger getRootLogger() {
        return new NOPLogger(this, "root");
    }

    @Override
    public Level getThreshold() {
        return Level.OFF;
    }

    @Override
    public boolean isDisabled(int level) {
        return true;
    }

    @Override
    public void resetConfiguration() {
    }

    @Override
    public void setThreshold(Level level) {
    }

    @Override
    public void setThreshold(String val) {
    }

    @Override
    public void shutdown() {
    }
}

