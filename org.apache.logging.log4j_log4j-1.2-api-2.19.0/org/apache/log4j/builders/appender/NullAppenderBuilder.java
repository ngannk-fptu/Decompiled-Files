/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.appender.NullAppender
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package org.apache.log4j.builders.appender;

import java.util.Properties;
import org.apache.log4j.Appender;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.appender.NullAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.varia.NullAppender", category="Log4j Builder")
public class NullAppenderBuilder
implements AppenderBuilder {
    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = appenderElement.getAttribute("name");
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)NullAppender.createAppender((String)name));
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)NullAppender.createAppender((String)name));
    }
}

