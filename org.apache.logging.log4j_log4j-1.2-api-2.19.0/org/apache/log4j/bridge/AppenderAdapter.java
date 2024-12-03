/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.appender.AbstractAppender
 *  org.apache.logging.log4j.core.config.Property
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.log4j.bridge;

import java.io.Serializable;
import org.apache.log4j.Appender;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.bridge.LogEventAdapter;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.util.Strings;

public class AppenderAdapter {
    private final Appender appender;
    private final Adapter adapter;

    public static org.apache.logging.log4j.core.Appender adapt(Appender appender) {
        if (appender instanceof org.apache.logging.log4j.core.Appender) {
            return (org.apache.logging.log4j.core.Appender)appender;
        }
        if (appender instanceof AppenderWrapper) {
            return ((AppenderWrapper)appender).getAppender();
        }
        if (appender != null) {
            return new AppenderAdapter(appender).getAdapter();
        }
        return null;
    }

    private AppenderAdapter(Appender appender) {
        this.appender = appender;
        Filter appenderFilter = FilterAdapter.adapt(appender.getFilter());
        String name = appender.getName();
        if (Strings.isEmpty((CharSequence)name)) {
            name = String.format("0x%08x", appender.hashCode());
        }
        this.adapter = new Adapter(name, appenderFilter, null, true, null);
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    public class Adapter
    extends AbstractAppender {
        protected Adapter(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
            super(name, filter, layout, ignoreExceptions, properties);
        }

        public void append(LogEvent event) {
            AppenderAdapter.this.appender.doAppend(new LogEventAdapter(event));
        }

        public void stop() {
            AppenderAdapter.this.appender.close();
        }

        public Appender getAppender() {
            return AppenderAdapter.this.appender;
        }
    }
}

