/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.ErrorHandler
 *  org.apache.logging.log4j.core.filter.AbstractFilterable
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.log4j.bridge;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.AppenderAdapter;
import org.apache.log4j.bridge.ErrorHandlerAdapter;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.bridge.LogEventAdapter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.status.StatusLogger;

public class AppenderWrapper
implements Appender {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private final org.apache.logging.log4j.core.Appender appender;

    public static Appender adapt(org.apache.logging.log4j.core.Appender appender) {
        AppenderAdapter.Adapter adapter;
        if (appender instanceof Appender) {
            return (Appender)appender;
        }
        if (appender instanceof AppenderAdapter.Adapter && !(adapter = (AppenderAdapter.Adapter)appender).hasFilter()) {
            return adapter.getAppender();
        }
        if (appender != null) {
            return new AppenderWrapper(appender);
        }
        return null;
    }

    public AppenderWrapper(org.apache.logging.log4j.core.Appender appender) {
        this.appender = appender;
    }

    public org.apache.logging.log4j.core.Appender getAppender() {
        return this.appender;
    }

    @Override
    public void addFilter(Filter newFilter) {
        if (this.appender instanceof AbstractFilterable) {
            ((AbstractFilterable)this.appender).addFilter(FilterAdapter.adapt(newFilter));
        } else {
            LOGGER.warn("Unable to add filter to appender {}, it does not support filters", (Object)this.appender.getName());
        }
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public void clearFilters() {
    }

    @Override
    public void close() {
    }

    @Override
    public void doAppend(LoggingEvent event) {
        if (event instanceof LogEventAdapter) {
            this.appender.append(((LogEventAdapter)event).getEvent());
        }
    }

    @Override
    public String getName() {
        return this.appender.getName();
    }

    @Override
    public void setErrorHandler(org.apache.log4j.spi.ErrorHandler errorHandler) {
        this.appender.setHandler((ErrorHandler)new ErrorHandlerAdapter(errorHandler));
    }

    @Override
    public org.apache.log4j.spi.ErrorHandler getErrorHandler() {
        return ((ErrorHandlerAdapter)this.appender.getHandler()).getHandler();
    }

    @Override
    public void setLayout(Layout layout) {
    }

    @Override
    public Layout getLayout() {
        return new LayoutWrapper(this.appender.getLayout());
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}

