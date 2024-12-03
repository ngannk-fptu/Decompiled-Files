/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.appender.RollingFileAppender
 *  org.apache.logging.log4j.core.appender.RollingFileAppender$Builder
 *  org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy
 *  org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy
 *  org.apache.logging.log4j.core.appender.rolling.RolloverStrategy
 *  org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy
 *  org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.log4j.builders.appender;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.LayoutAdapter;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.Log4j1Configuration;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.RollingFileAppender", category="Log4j Builder")
public class RollingFileAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final String DEFAULT_MAX_SIZE = "10 MB";
    private static final String DEFAULT_MAX_BACKUPS = "1";
    private static final Logger LOGGER = StatusLogger.getLogger();

    public RollingFileAppenderBuilder() {
    }

    public RollingFileAppenderBuilder(String prefix, Properties properties) {
        super(prefix, properties);
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference layout = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        AtomicReference fileName = new AtomicReference();
        AtomicBoolean immediateFlush = new AtomicBoolean(true);
        AtomicBoolean append = new AtomicBoolean(true);
        AtomicBoolean bufferedIo = new AtomicBoolean();
        AtomicInteger bufferSize = new AtomicInteger(8192);
        AtomicReference<String> maxSize = new AtomicReference<String>(DEFAULT_MAX_SIZE);
        AtomicReference<String> maxBackups = new AtomicReference<String>(DEFAULT_MAX_BACKUPS);
        AtomicReference level = new AtomicReference();
        XmlConfiguration.forEachElement(appenderElement.getChildNodes(), currentElement -> {
            block5 : switch (currentElement.getTagName()) {
                case "layout": {
                    layout.set(config.parseLayout((Element)currentElement));
                    break;
                }
                case "filter": {
                    config.addFilter(filter, (Element)currentElement);
                    break;
                }
                case "param": {
                    switch (this.getNameAttributeKey((Element)currentElement)) {
                        case "File": {
                            this.set("File", (Element)currentElement, fileName);
                            break block5;
                        }
                        case "Append": {
                            this.set("Append", (Element)currentElement, append);
                            break block5;
                        }
                        case "BufferedIO": {
                            this.set("BufferedIO", (Element)currentElement, bufferedIo);
                            break block5;
                        }
                        case "BufferSize": {
                            this.set("BufferSize", (Element)currentElement, bufferSize);
                            break block5;
                        }
                        case "MaxBackupIndex": {
                            this.set("MaxBackupIndex", (Element)currentElement, maxBackups);
                            break block5;
                        }
                        case "MaxFileSize": {
                            this.set("MaxFileSize", (Element)currentElement, maxSize);
                            break block5;
                        }
                        case "Threshold": {
                            this.set("Threshold", (Element)currentElement, level);
                            break block5;
                        }
                        case "ImmediateFlush": {
                            this.set("ImmediateFlush", (Element)currentElement, immediateFlush);
                        }
                    }
                }
            }
        });
        return this.createAppender(name, config, (Layout)layout.get(), (org.apache.log4j.spi.Filter)filter.get(), append.get(), bufferedIo.get(), bufferSize.get(), immediateFlush.get(), (String)fileName.get(), (String)level.get(), maxSize.get(), maxBackups.get());
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        Layout layout = configuration.parseLayout(layoutPrefix, name, props);
        org.apache.log4j.spi.Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        String fileName = this.getProperty("File");
        String level = this.getProperty("Threshold");
        boolean append = this.getBooleanProperty("Append", true);
        boolean immediateFlush = this.getBooleanProperty("ImmediateFlush", true);
        boolean bufferedIo = this.getBooleanProperty("BufferedIO", false);
        int bufferSize = this.getIntegerProperty("BufferSize", 8192);
        String maxSize = this.getProperty("MaxFileSize", DEFAULT_MAX_SIZE);
        String maxBackups = this.getProperty("MaxBackupIndex", DEFAULT_MAX_BACKUPS);
        return this.createAppender(name, configuration, layout, filter, append, bufferedIo, bufferSize, immediateFlush, fileName, level, maxSize, maxBackups);
    }

    private Appender createAppender(String name, Log4j1Configuration config, Layout layout, org.apache.log4j.spi.Filter filter, boolean append, boolean bufferedIo, int bufferSize, boolean immediateFlush, String fileName, String level, String maxSize, String maxBackups) {
        org.apache.logging.log4j.core.Layout<?> fileLayout = LayoutAdapter.adapt(layout);
        if (!bufferedIo) {
            immediateFlush = false;
        }
        Filter fileFilter = RollingFileAppenderBuilder.buildFilters(level, filter);
        if (fileName == null) {
            LOGGER.error("Unable to create RollingFileAppender, no file name provided");
            return null;
        }
        String filePattern = fileName + ".%i";
        SizeBasedTriggeringPolicy sizePolicy = SizeBasedTriggeringPolicy.createPolicy((String)maxSize);
        CompositeTriggeringPolicy policy = CompositeTriggeringPolicy.createPolicy((TriggeringPolicy[])new TriggeringPolicy[]{sizePolicy});
        DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder().withConfig((Configuration)config).withMax(maxBackups).build();
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)RollingFileAppender.newBuilder().setName(name)).setConfiguration((Configuration)config)).setLayout(fileLayout)).setFilter(fileFilter)).withAppend(append).setBufferedIo(bufferedIo)).setBufferSize(bufferSize)).setImmediateFlush(immediateFlush)).withFileName(fileName).withFilePattern(filePattern).withPolicy((TriggeringPolicy)policy).withStrategy((RolloverStrategy)strategy).build());
    }
}

