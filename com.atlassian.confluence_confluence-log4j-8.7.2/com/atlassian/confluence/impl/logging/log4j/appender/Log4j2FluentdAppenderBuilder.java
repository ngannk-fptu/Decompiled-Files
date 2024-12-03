/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.logging.log4j.appender.FluentdAppender
 *  com.atlassian.logging.log4j.appender.FluentdAppender$Builder
 *  org.apache.log4j.Appender
 *  org.apache.log4j.Layout
 *  org.apache.log4j.bridge.AppenderWrapper
 *  org.apache.log4j.bridge.FilterAdapter
 *  org.apache.log4j.bridge.LayoutAdapter
 *  org.apache.log4j.builders.AbstractBuilder
 *  org.apache.log4j.builders.appender.AppenderBuilder
 *  org.apache.log4j.config.PropertiesConfiguration
 *  org.apache.log4j.spi.Filter
 *  org.apache.log4j.xml.XmlConfiguration
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.appender;

import com.atlassian.logging.log4j.appender.FluentdAppender;
import java.util.Properties;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.bridge.LayoutAdapter;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="com.atlassian.logging.log4j.appender.FluentdAppender", category="Log4j Builder")
public class Log4j2FluentdAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    public Log4j2FluentdAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    public org.apache.log4j.Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        FluentdAppender fluentdAppender = ((FluentdAppender.Builder)((FluentdAppender.Builder)((FluentdAppender.Builder)FluentdAppender.newBuilder().setName(name)).setLayout(LayoutAdapter.adapt((Layout)configuration.parseLayout(layoutPrefix, name, props))).setFilter(FilterAdapter.adapt((Filter)configuration.parseAppenderFilters(props, filterPrefix, name)))).setConfiguration((Configuration)configuration)).build();
        return AppenderWrapper.adapt((Appender)fluentdAppender);
    }

    public org.apache.log4j.Appender parseAppender(Element element, XmlConfiguration configuration) {
        throw new UnsupportedOperationException("XML config style not supported");
    }
}

