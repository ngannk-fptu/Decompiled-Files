/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.appender.ConsoleAppender
 *  org.apache.logging.log4j.core.appender.ConsoleAppender$Builder
 *  org.apache.logging.log4j.core.appender.ConsoleAppender$Target
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.log4j.builders.appender;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.ConsoleAppender", category="Log4j Builder")
public class ConsoleAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final String SYSTEM_OUT = "System.out";
    private static final String SYSTEM_ERR = "System.err";
    private static final String TARGET_PARAM = "Target";
    private static final String FOLLOW_PARAM = "Follow";
    private static final Logger LOGGER = StatusLogger.getLogger();

    public ConsoleAppenderBuilder() {
    }

    public ConsoleAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference<String> target = new AtomicReference<String>(SYSTEM_OUT);
        AtomicReference layout = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        AtomicReference level = new AtomicReference();
        AtomicBoolean follow = new AtomicBoolean();
        AtomicBoolean immediateFlush = new AtomicBoolean(true);
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
                        case "Target": {
                            String value = this.getValueAttribute((Element)currentElement);
                            if (value == null) {
                                LOGGER.warn("No value supplied for target parameter. Defaulting to System.out");
                                break block5;
                            }
                            switch (value) {
                                case "System.out": {
                                    target.set(SYSTEM_OUT);
                                    break block5;
                                }
                                case "System.err": {
                                    target.set(SYSTEM_ERR);
                                    break block5;
                                }
                            }
                            LOGGER.warn("Invalid value \"{}\" for target parameter. Using default of {}", (Object)value, (Object)SYSTEM_OUT);
                            break block5;
                        }
                        case "Threshold": {
                            this.set("Threshold", (Element)currentElement, level);
                            break block5;
                        }
                        case "Follow": {
                            this.set(FOLLOW_PARAM, (Element)currentElement, follow);
                            break block5;
                        }
                        case "ImmediateFlush": {
                            this.set("ImmediateFlush", (Element)currentElement, immediateFlush);
                        }
                    }
                }
            }
        });
        return this.createAppender(name, (Layout)layout.get(), (org.apache.log4j.spi.Filter)filter.get(), (String)level.get(), target.get(), immediateFlush.get(), follow.get(), config);
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        Layout layout = configuration.parseLayout(layoutPrefix, name, props);
        org.apache.log4j.spi.Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        String level = this.getProperty("Threshold");
        String target = this.getProperty(TARGET_PARAM);
        boolean follow = this.getBooleanProperty(FOLLOW_PARAM);
        boolean immediateFlush = this.getBooleanProperty("ImmediateFlush");
        return this.createAppender(name, layout, filter, level, target, immediateFlush, follow, configuration);
    }

    private <T extends Log4j1Configuration> Appender createAppender(String name, Layout layout, org.apache.log4j.spi.Filter filter, String level, String target, boolean immediateFlush, boolean follow, T configuration) {
        org.apache.logging.log4j.core.Layout<?> consoleLayout = LayoutAdapter.adapt(layout);
        Filter consoleFilter = ConsoleAppenderBuilder.buildFilters(level, filter);
        ConsoleAppender.Target consoleTarget = SYSTEM_ERR.equals(target) ? ConsoleAppender.Target.SYSTEM_ERR : ConsoleAppender.Target.SYSTEM_OUT;
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)((ConsoleAppender.Builder)ConsoleAppender.newBuilder().setName(name)).setTarget(consoleTarget).setFollow(follow).setLayout(consoleLayout)).setFilter(consoleFilter)).setConfiguration(configuration)).setImmediateFlush(immediateFlush)).build());
    }
}

