/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.appender.AbstractAppender
 *  org.apache.logging.log4j.core.appender.AbstractAppender$Builder
 *  org.apache.logging.log4j.core.config.Property
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute
 *  org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory
 *  org.apache.logging.log4j.core.util.Builder
 */
package com.atlassian.logging.log4j.appender;

import com.atlassian.logging.log4j.appender.fluentd.FluentdAppenderHelper;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name="FluentdAppender", category="Core", elementType="appender", printObject=true)
public class FluentdAppender
extends AbstractAppender {
    private final FluentdAppenderHelper<LogEvent> helper;

    protected FluentdAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, Property[] properties, FluentdAppenderHelper<LogEvent> helper) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.helper = helper;
    }

    public void restart() {
        this.helper.restart();
    }

    public void enable() {
        this.helper.enable();
    }

    public void disable() {
        this.helper.disable();
    }

    public void append(LogEvent loggingEvent) {
        if (this.helper.isEnabled()) {
            loggingEvent.getThreadName();
            loggingEvent.getContextData();
            loggingEvent.getSource();
            this.helper.append(loggingEvent.toImmutable());
        }
    }

    public void start() {
        this.setStarting();
        super.start();
        this.helper.initialise();
        this.setStarted();
    }

    public void stop() {
        this.setStopping();
        this.helper.close();
        super.stop();
        this.setStopped();
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return (B)((Object)((Builder)new Builder().asBuilder()));
    }

    public static class Builder<B extends Builder<B>>
    extends AbstractAppender.Builder<B>
    implements org.apache.logging.log4j.core.util.Builder<FluentdAppender> {
        @PluginBuilderAttribute
        private String fluentdEndpoint;
        @PluginBuilderAttribute
        private Long batchPeriodMs;
        @PluginBuilderAttribute
        private Long maxNumLogEvents;
        @PluginBuilderAttribute
        private Integer maxRetryPeriodMs;
        @PluginBuilderAttribute
        private Integer backoffMultiplier;
        @PluginBuilderAttribute
        private Integer maxBackoffMinutes;
        private final FluentdAppenderHelper<LogEvent> helper = new FluentdAppenderHelper();

        public B setLayout(Layout<? extends Serializable> layout) {
            return (B)((Object)((Builder)super.setLayout(layout)));
        }

        public B setFluentdEndpoint(String fluentdEndpoint) {
            this.fluentdEndpoint = fluentdEndpoint;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setBatchPeriodMs(long batchPeriodMs) {
            this.batchPeriodMs = batchPeriodMs;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setMaxNumLogEvents(long maxNumLogEvents) {
            this.maxNumLogEvents = maxNumLogEvents;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setMaxRetryPeriodMs(int maxRetryPeriodMs) {
            this.maxRetryPeriodMs = maxRetryPeriodMs;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setBackoffMultiplier(int backoffMultiplier) {
            this.backoffMultiplier = backoffMultiplier;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setMaxBackoffMinutes(int maxBackoffMinutes) {
            this.maxBackoffMinutes = maxBackoffMinutes;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public FluentdAppender build() {
            if (this.fluentdEndpoint != null) {
                this.helper.setFluentdEndpoint(this.fluentdEndpoint);
            }
            if (this.batchPeriodMs != null) {
                this.helper.setBatchPeriodMs(this.batchPeriodMs);
            }
            if (this.maxNumLogEvents != null) {
                this.helper.setMaxNumEvents(this.maxNumLogEvents);
            }
            if (this.maxRetryPeriodMs != null) {
                this.helper.setMaxRetryPeriodMs(this.maxRetryPeriodMs);
            }
            if (this.backoffMultiplier != null) {
                this.helper.setBackoffMultiplier(this.backoffMultiplier);
            }
            if (this.maxBackoffMinutes != null) {
                this.helper.setMaxBackoffMinutes(this.maxBackoffMinutes);
            }
            if (this.getLayout() != null) {
                this.helper.setLayout(arg_0 -> ((Layout)this.getLayout()).toSerializable(arg_0));
            }
            return new FluentdAppender(this.getName(), (Layout<? extends Serializable>)this.getLayout(), this.getFilter(), this.isIgnoreExceptions(), this.getPropertyArray(), this.helper);
        }
    }
}

