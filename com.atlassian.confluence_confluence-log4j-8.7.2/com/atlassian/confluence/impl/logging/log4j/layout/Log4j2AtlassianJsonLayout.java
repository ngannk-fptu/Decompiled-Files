/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.logging.log4j.layout.AtlassianJsonLayout
 *  org.apache.log4j.Layout
 *  org.apache.log4j.bridge.LayoutWrapper
 *  org.apache.log4j.builders.AbstractBuilder
 *  org.apache.log4j.builders.layout.LayoutBuilder
 *  org.apache.log4j.config.PropertiesConfiguration
 *  org.apache.log4j.xml.XmlConfiguration
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import com.atlassian.logging.log4j.layout.AtlassianJsonLayout;
import java.util.Properties;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.layout.LayoutBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="com.atlassian.logging.log4j.layout.JsonLayout", category="Log4j Builder")
public final class Log4j2AtlassianJsonLayout
extends AbstractBuilder<org.apache.log4j.Layout>
implements LayoutBuilder {
    public Log4j2AtlassianJsonLayout(String prefix, Properties props) {
        super(prefix, props);
    }

    public org.apache.log4j.Layout parse(PropertiesConfiguration config) {
        AtlassianJsonLayout layout = AtlassianJsonLayout.newBuilder().build();
        return LayoutWrapper.adapt((Layout)layout);
    }

    public org.apache.log4j.Layout parse(Element element, XmlConfiguration config) {
        throw new UnsupportedOperationException("XML config style not supported");
    }
}

