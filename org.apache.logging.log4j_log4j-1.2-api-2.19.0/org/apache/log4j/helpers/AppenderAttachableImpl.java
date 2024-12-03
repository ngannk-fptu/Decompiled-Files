/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.helpers;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

public class AppenderAttachableImpl
implements AppenderAttachable {
    private final ConcurrentMap<String, Appender> appenders = new ConcurrentHashMap<String, Appender>();
    protected Vector appenderList;

    @Override
    public void addAppender(Appender appender) {
        if (appender != null) {
            this.appenders.put(Objects.toString(appender.getName()), appender);
        }
    }

    public int appendLoopOnAppenders(LoggingEvent event) {
        for (Appender appender : this.appenders.values()) {
            appender.doAppend(event);
        }
        return this.appenders.size();
    }

    public void close() {
        for (Appender appender : this.appenders.values()) {
            appender.close();
        }
    }

    @Override
    public Enumeration<Appender> getAllAppenders() {
        return Collections.enumeration(this.appenders.values());
    }

    @Override
    public Appender getAppender(String name) {
        return name == null ? null : (Appender)this.appenders.get(name);
    }

    @Override
    public boolean isAttached(Appender appender) {
        return appender != null ? this.appenders.containsValue(appender) : false;
    }

    @Override
    public void removeAllAppenders() {
        this.appenders.clear();
    }

    @Override
    public void removeAppender(Appender appender) {
        String name;
        if (appender != null && (name = appender.getName()) != null) {
            this.appenders.remove(name, appender);
        }
    }

    @Override
    public void removeAppender(String name) {
        if (name != null) {
            this.appenders.remove(name);
        }
    }
}

