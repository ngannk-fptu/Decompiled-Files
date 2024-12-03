/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.util.Memoizer
 *  org.apache.log4j.Appender
 *  org.apache.log4j.builders.AbstractBuilder
 *  org.apache.log4j.builders.appender.AppenderBuilder
 *  org.apache.log4j.builders.appender.ConsoleAppenderBuilder
 *  org.apache.log4j.builders.appender.RollingFileAppenderBuilder
 *  org.apache.log4j.config.PropertiesConfiguration
 *  org.apache.log4j.xml.XmlConfiguration
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.appender;

import com.atlassian.confluence.impl.logging.log4j.appender.DeferredFileAppender;
import com.atlassian.confluence.impl.logging.log4j.appender.RollingFileManagerRegistrar;
import com.atlassian.confluence.impl.util.Memoizer;
import java.io.File;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.log4j.Appender;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.builders.appender.ConsoleAppenderBuilder;
import org.apache.log4j.builders.appender.RollingFileAppenderBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="com.atlassian.confluence.logging.ConfluenceHomeLogAppender", category="Log4j Builder")
public class Log4j2ConfluenceHomeLogAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final String DEFAULT_LOG_NAME = "atlassian-confluence.log";
    private final String prefix;
    private final AppenderBuilder fileAppenderBuilder;
    private final AppenderBuilder consoleAppenderBuilder;
    private final Consumer<Appender> rolloverRegistrar;
    private final Properties fileAppenderProperties;

    public Log4j2ConfluenceHomeLogAppenderBuilder(String prefix, Properties props) {
        this(prefix, props, new RollingFileAppenderBuilder(prefix, props), new ConsoleAppenderBuilder(prefix, props));
    }

    Log4j2ConfluenceHomeLogAppenderBuilder(String prefix, Properties props, RollingFileAppenderBuilder rollingFileAppenderBuilder, ConsoleAppenderBuilder consoleAppenderBuilder) {
        this(prefix, props, (AppenderBuilder)rollingFileAppenderBuilder, (AppenderBuilder)consoleAppenderBuilder, rollingFileAppenderBuilder.getProperties(), RollingFileManagerRegistrar::register);
    }

    Log4j2ConfluenceHomeLogAppenderBuilder(String prefix, Properties props, AppenderBuilder fileAppenderBuilder, AppenderBuilder consoleAppenderBuilder, Properties fileAppenderProperties, Consumer<Appender> rolloverRegistrar) {
        super(prefix, props);
        this.prefix = prefix;
        this.fileAppenderBuilder = fileAppenderBuilder;
        this.consoleAppenderBuilder = consoleAppenderBuilder;
        this.fileAppenderProperties = fileAppenderProperties;
        this.rolloverRegistrar = rolloverRegistrar;
    }

    public DeferredFileAppender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        Function<AppenderBuilder, Appender> appenderFactory = builder -> builder.parseAppender(name, appenderPrefix, layoutPrefix, filterPrefix, props, configuration);
        return this.createDeferredFileAppender(name, appenderFactory);
    }

    private DeferredFileAppender createDeferredFileAppender(String name, Function<AppenderBuilder, Appender> appenderFactory) {
        Appender consoleAppender = appenderFactory.apply(this.consoleAppenderBuilder);
        DeferredFileAppender appender = new DeferredFileAppender(consoleAppender, Memoizer.memoize(logDirectory -> this.createFileAppender(appenderFactory, (File)logDirectory)));
        appender.setName(name);
        appender.registerForLogDirectoryConfiguration();
        return appender;
    }

    private Appender createFileAppender(Function<AppenderBuilder, Appender> appenderFactory, File logDirectory) {
        Log4j2ConfluenceHomeLogAppenderBuilder.setFileProperty(logDirectory, this.prefix, this.fileAppenderProperties);
        Appender fileAppender = appenderFactory.apply(this.fileAppenderBuilder);
        this.rolloverRegistrar.accept(fileAppender);
        return fileAppender;
    }

    private static void setFileProperty(File logDirectory, String prefix, Properties fileAppenderProperties) {
        String filenamePropertyKey = prefix + ".LogFileName";
        String fileName = Optional.ofNullable(fileAppenderProperties.getProperty(filenamePropertyKey)).orElse(DEFAULT_LOG_NAME);
        File logFile = new File(logDirectory, fileName);
        fileAppenderProperties.setProperty(prefix + ".File", logFile.getPath());
    }

    public Appender parseAppender(Element element, XmlConfiguration configuration) {
        throw new UnsupportedOperationException("XML config style not supported");
    }
}

