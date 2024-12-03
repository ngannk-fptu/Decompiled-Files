/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.appender.FileAppender
 *  org.apache.logging.log4j.core.appender.FileAppender$Builder
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
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.FileAppender", category="Log4j Builder")
public class FileAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final Logger LOGGER = StatusLogger.getLogger();

    public FileAppenderBuilder() {
    }

    public FileAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference layout = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        AtomicReference fileName = new AtomicReference();
        AtomicReference level = new AtomicReference();
        AtomicBoolean immediateFlush = new AtomicBoolean(true);
        AtomicBoolean append = new AtomicBoolean(true);
        AtomicBoolean bufferedIo = new AtomicBoolean();
        AtomicInteger bufferSize = new AtomicInteger(8192);
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
        return this.createAppender(name, config, (Layout)layout.get(), (org.apache.log4j.spi.Filter)filter.get(), (String)fileName.get(), (String)level.get(), immediateFlush.get(), append.get(), bufferedIo.get(), bufferSize.get());
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        Layout layout = configuration.parseLayout(layoutPrefix, name, props);
        org.apache.log4j.spi.Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        String level = this.getProperty("Threshold");
        String fileName = this.getProperty("File");
        boolean append = this.getBooleanProperty("Append", true);
        boolean immediateFlush = this.getBooleanProperty("ImmediateFlush", true);
        boolean bufferedIo = this.getBooleanProperty("BufferedIO", false);
        int bufferSize = Integer.parseInt(this.getProperty("BufferSize", "8192"));
        return this.createAppender(name, configuration, layout, filter, fileName, level, immediateFlush, append, bufferedIo, bufferSize);
    }

    private Appender createAppender(String name, Log4j1Configuration configuration, Layout layout, org.apache.log4j.spi.Filter filter, String fileName, String level, boolean immediateFlush, boolean append, boolean bufferedIo, int bufferSize) {
        org.apache.logging.log4j.core.Layout<?> fileLayout = LayoutAdapter.adapt(layout);
        if (bufferedIo) {
            immediateFlush = false;
        }
        Filter fileFilter = FileAppenderBuilder.buildFilters(level, filter);
        if (fileName == null) {
            LOGGER.error("Unable to create FileAppender, no file name provided");
            return null;
        }
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)((FileAppender.Builder)FileAppender.newBuilder().setName(name)).setConfiguration((Configuration)configuration)).setLayout(fileLayout)).setFilter(fileFilter)).withFileName(fileName).setImmediateFlush(immediateFlush)).withAppend(append).setBufferedIo(bufferedIo)).setBufferSize(bufferSize)).build());
    }
}

