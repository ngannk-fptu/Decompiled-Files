/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.appender.AsyncAppender
 *  org.apache.logging.log4j.core.appender.AsyncAppender$Builder
 *  org.apache.logging.log4j.core.config.AppenderRef
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.log4j.builders.appender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Appender;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.Log4j1Configuration;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.AsyncAppender", category="Log4j Builder")
public class AsyncAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String BLOCKING_PARAM = "Blocking";
    private static final String INCLUDE_LOCATION_PARAM = "IncludeLocation";

    public AsyncAppenderBuilder() {
    }

    public AsyncAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference appenderRefs = new AtomicReference(new ArrayList());
        AtomicBoolean blocking = new AtomicBoolean();
        AtomicBoolean includeLocation = new AtomicBoolean();
        AtomicReference<String> level = new AtomicReference<String>("trace");
        AtomicInteger bufferSize = new AtomicInteger(1024);
        AtomicReference filter = new AtomicReference();
        XmlConfiguration.forEachElement(appenderElement.getChildNodes(), currentElement -> {
            block5 : switch (currentElement.getTagName()) {
                case "appender-ref": {
                    Appender appender = config.findAppenderByReference((Element)currentElement);
                    if (appender == null) break;
                    ((List)appenderRefs.get()).add(appender.getName());
                    break;
                }
                case "filter": {
                    config.addFilter(filter, (Element)currentElement);
                    break;
                }
                case "param": {
                    switch (this.getNameAttributeKey((Element)currentElement)) {
                        case "BufferSize": {
                            this.set("BufferSize", (Element)currentElement, bufferSize);
                            break block5;
                        }
                        case "Blocking": {
                            this.set(BLOCKING_PARAM, (Element)currentElement, blocking);
                            break block5;
                        }
                        case "IncludeLocation": {
                            this.set(INCLUDE_LOCATION_PARAM, (Element)currentElement, includeLocation);
                            break block5;
                        }
                        case "Threshold": {
                            this.set("Threshold", (Element)currentElement, level);
                        }
                    }
                }
            }
        });
        return this.createAppender(name, level.get(), ((List)appenderRefs.get()).toArray(Strings.EMPTY_ARRAY), blocking.get(), bufferSize.get(), includeLocation.get(), (Filter)filter.get(), config);
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        String appenderRef = this.getProperty("appender-ref");
        Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        boolean blocking = this.getBooleanProperty(BLOCKING_PARAM);
        boolean includeLocation = this.getBooleanProperty(INCLUDE_LOCATION_PARAM);
        String level = this.getProperty("Threshold");
        int bufferSize = this.getIntegerProperty("BufferSize", 1024);
        if (appenderRef == null) {
            LOGGER.error("No appender references configured for AsyncAppender {}", (Object)name);
            return null;
        }
        Appender appender = configuration.parseAppender(props, appenderRef);
        if (appender == null) {
            LOGGER.error("Cannot locate Appender {}", (Object)appenderRef);
            return null;
        }
        return this.createAppender(name, level, new String[]{appenderRef}, blocking, bufferSize, includeLocation, filter, configuration);
    }

    private <T extends Log4j1Configuration> Appender createAppender(String name, String level, String[] appenderRefs, boolean blocking, int bufferSize, boolean includeLocation, Filter filter, T configuration) {
        if (appenderRefs.length == 0) {
            LOGGER.error("No appender references configured for AsyncAppender {}", (Object)name);
            return null;
        }
        Level logLevel = OptionConverter.convertLevel(level, Level.TRACE);
        AppenderRef[] refs = new AppenderRef[appenderRefs.length];
        int index = 0;
        for (String appenderRef : appenderRefs) {
            refs[index++] = AppenderRef.createAppenderRef((String)appenderRef, (Level)logLevel, null);
        }
        AsyncAppender.Builder builder = AsyncAppender.newBuilder();
        builder.setFilter(FilterAdapter.adapt(filter));
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)builder.setName(name).setAppenderRefs(refs).setBlocking(blocking).setBufferSize(bufferSize).setIncludeLocation(includeLocation).setConfiguration(configuration).build());
    }
}

