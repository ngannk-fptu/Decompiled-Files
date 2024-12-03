/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.appender.SocketAppender
 *  org.apache.logging.log4j.core.appender.SocketAppender$Builder
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
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.net.SocketAppender", category="Log4j Builder")
public class SocketAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final String HOST_PARAM = "RemoteHost";
    private static final String PORT_PARAM = "Port";
    private static final String RECONNECTION_DELAY_PARAM = "ReconnectionDelay";
    private static final int DEFAULT_PORT = 4560;
    private static final int DEFAULT_RECONNECTION_DELAY = 30000;
    public static final Logger LOGGER = StatusLogger.getLogger();

    public SocketAppenderBuilder() {
    }

    public SocketAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    private <T extends Log4j1Configuration> Appender createAppender(String name, String host, int port, Layout layout, org.apache.log4j.spi.Filter filter, String level, boolean immediateFlush, int reconnectDelayMillis, T configuration) {
        org.apache.logging.log4j.core.Layout<?> actualLayout = LayoutAdapter.adapt(layout);
        Filter actualFilter = SocketAppenderBuilder.buildFilters(level, filter);
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)((SocketAppender.Builder)((SocketAppender.Builder)((SocketAppender.Builder)((SocketAppender.Builder)((SocketAppender.Builder)((SocketAppender.Builder)((SocketAppender.Builder)((SocketAppender.Builder)SocketAppender.newBuilder().setHost(host)).setPort(port)).setReconnectDelayMillis(reconnectDelayMillis)).setName(name)).setLayout(actualLayout)).setFilter(actualFilter)).setConfiguration(configuration)).setImmediateFlush(immediateFlush)).build());
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference<String> host = new AtomicReference<String>("localhost");
        AtomicInteger port = new AtomicInteger(4560);
        AtomicInteger reconnectDelay = new AtomicInteger(30000);
        AtomicReference layout = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        AtomicReference level = new AtomicReference();
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
                        case "RemoteHost": {
                            this.set(HOST_PARAM, (Element)currentElement, host);
                            break block5;
                        }
                        case "Port": {
                            this.set(PORT_PARAM, (Element)currentElement, port);
                            break block5;
                        }
                        case "ReconnectionDelay": {
                            this.set(RECONNECTION_DELAY_PARAM, (Element)currentElement, reconnectDelay);
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
        return this.createAppender(name, host.get(), port.get(), (Layout)layout.get(), (org.apache.log4j.spi.Filter)filter.get(), (String)level.get(), immediateFlush.get(), reconnectDelay.get(), config);
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        return this.createAppender(name, this.getProperty(HOST_PARAM), this.getIntegerProperty(PORT_PARAM, 4560), configuration.parseLayout(layoutPrefix, name, props), configuration.parseAppenderFilters(props, filterPrefix, name), this.getProperty("Threshold"), this.getBooleanProperty("ImmediateFlush"), this.getIntegerProperty(RECONNECTION_DELAY_PARAM, 30000), configuration);
    }
}

