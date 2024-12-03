/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.appender.SyslogAppender
 *  org.apache.logging.log4j.core.appender.SyslogAppender$Builder
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.net.Facility
 *  org.apache.logging.log4j.core.net.Protocol
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.Strings
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
import org.apache.log4j.layout.Log4j1SyslogLayout;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.SyslogAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.net.SyslogAppender", category="Log4j Builder")
public class SyslogAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 514;
    private static final String DEFAULT_FACILITY = "LOCAL0";
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String FACILITY_PARAM = "Facility";
    private static final String FACILITY_PRINTING_PARAM = "FacilityPrinting";
    private static final String HEADER_PARAM = "Header";
    private static final String PROTOCOL_PARAM = "Protocol";
    private static final String SYSLOG_HOST_PARAM = "SyslogHost";

    public SyslogAppenderBuilder() {
    }

    public SyslogAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference layout = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        AtomicReference facility = new AtomicReference();
        AtomicReference level = new AtomicReference();
        AtomicReference host = new AtomicReference();
        AtomicReference<Protocol> protocol = new AtomicReference<Protocol>(Protocol.TCP);
        AtomicBoolean header = new AtomicBoolean(false);
        AtomicBoolean facilityPrinting = new AtomicBoolean(false);
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
                        case "Facility": {
                            this.set(FACILITY_PARAM, (Element)currentElement, facility);
                            break block5;
                        }
                        case "FacilityPrinting": {
                            this.set(FACILITY_PRINTING_PARAM, (Element)currentElement, facilityPrinting);
                            break block5;
                        }
                        case "Header": {
                            this.set(HEADER_PARAM, (Element)currentElement, header);
                            break block5;
                        }
                        case "Protocol": {
                            protocol.set(Protocol.valueOf((String)this.getValueAttribute((Element)currentElement, Protocol.TCP.name())));
                            break block5;
                        }
                        case "SyslogHost": {
                            this.set(SYSLOG_HOST_PARAM, (Element)currentElement, host);
                            break block5;
                        }
                        case "Threshold": {
                            this.set("Threshold", (Element)currentElement, level);
                        }
                    }
                }
            }
        });
        return this.createAppender(name, config, (Layout)layout.get(), (String)facility.get(), (org.apache.log4j.spi.Filter)filter.get(), (String)host.get(), (String)level.get(), protocol.get(), header.get(), facilityPrinting.get());
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        org.apache.log4j.spi.Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        Layout layout = configuration.parseLayout(layoutPrefix, name, props);
        String level = this.getProperty("Threshold");
        String facility = this.getProperty(FACILITY_PARAM, DEFAULT_FACILITY);
        boolean facilityPrinting = this.getBooleanProperty(FACILITY_PRINTING_PARAM, false);
        boolean header = this.getBooleanProperty(HEADER_PARAM, false);
        String protocol = this.getProperty(PROTOCOL_PARAM, Protocol.TCP.name());
        String syslogHost = this.getProperty(SYSLOG_HOST_PARAM, "localhost:" + DEFAULT_PORT);
        return this.createAppender(name, configuration, layout, facility, filter, syslogHost, level, Protocol.valueOf((String)protocol), header, facilityPrinting);
    }

    private Appender createAppender(String name, Log4j1Configuration configuration, Layout layout, String facility, org.apache.log4j.spi.Filter filter, String syslogHost, String level, Protocol protocol, boolean header, boolean facilityPrinting) {
        AtomicReference<String> host = new AtomicReference<String>();
        AtomicInteger port = new AtomicInteger();
        this.resolveSyslogHost(syslogHost, host, port);
        org.apache.logging.log4j.core.Layout<?> messageLayout = LayoutAdapter.adapt(layout);
        Log4j1SyslogLayout appenderLayout = ((Log4j1SyslogLayout.Builder)((Object)((Log4j1SyslogLayout.Builder)((Object)((Log4j1SyslogLayout.Builder)((Object)((Log4j1SyslogLayout.Builder)((Object)((Log4j1SyslogLayout.Builder)((Object)Log4j1SyslogLayout.newBuilder())).setHeader(header))).setFacility(Facility.toFacility((String)facility)))).setFacilityPrinting(facilityPrinting))).setMessageLayout(messageLayout))).build();
        Filter fileFilter = SyslogAppenderBuilder.buildFilters(level, filter);
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)((SyslogAppender.Builder)SyslogAppender.newSyslogAppenderBuilder().setName(name)).setConfiguration((Configuration)configuration)).setLayout((org.apache.logging.log4j.core.Layout)appenderLayout)).setFilter(fileFilter)).setPort(port.get())).setProtocol(protocol)).setHost(host.get())).build());
    }

    private void resolveSyslogHost(String syslogHost, AtomicReference<String> host, AtomicInteger port) {
        String[] parts;
        String[] stringArray = parts = syslogHost != null ? syslogHost.split(":") : Strings.EMPTY_ARRAY;
        if (parts.length == 1) {
            host.set(parts[0]);
            port.set(DEFAULT_PORT);
        } else if (parts.length == 2) {
            host.set(parts[0]);
            port.set(Integer.parseInt(parts[1].trim()));
        } else {
            LOGGER.warn("Invalid {} setting: {}. Using default.", (Object)SYSLOG_HOST_PARAM, (Object)syslogHost);
            host.set(DEFAULT_HOST);
            port.set(DEFAULT_PORT);
        }
    }
}

