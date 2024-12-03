/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.layout.PatternLayout
 */
package org.apache.log4j.builders.layout;

import org.apache.log4j.Layout;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.builders.layout.LayoutBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.SimpleLayout", category="Log4j Builder")
public class SimpleLayoutBuilder
implements LayoutBuilder {
    @Override
    public Layout parse(Element layoutElement, XmlConfiguration config) {
        return new LayoutWrapper((org.apache.logging.log4j.core.Layout<?>)PatternLayout.newBuilder().withPattern("%v1Level - %m%n").withConfiguration((Configuration)config).build());
    }

    @Override
    public Layout parse(PropertiesConfiguration config) {
        return new LayoutWrapper((org.apache.logging.log4j.core.Layout<?>)PatternLayout.newBuilder().withPattern("%v1Level - %m%n").withConfiguration((Configuration)config).build());
    }
}

