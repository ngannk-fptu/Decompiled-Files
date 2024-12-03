/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package org.apache.log4j.builders.layout;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.layout.LayoutBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.layout.Log4j1XmlLayout;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.xml.XMLLayout", category="Log4j Builder")
public class XmlLayoutBuilder
extends AbstractBuilder<Layout>
implements LayoutBuilder {
    private static final String LOCATION_INFO = "LocationInfo";
    private static final String PROPERTIES = "Properties";

    public XmlLayoutBuilder() {
    }

    public XmlLayoutBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Layout parse(Element layoutElement, XmlConfiguration config) {
        AtomicBoolean properties = new AtomicBoolean();
        AtomicBoolean locationInfo = new AtomicBoolean();
        XmlConfiguration.forEachElement(layoutElement.getElementsByTagName("param"), currentElement -> {
            if (PROPERTIES.equalsIgnoreCase(currentElement.getAttribute("name"))) {
                properties.set(this.getBooleanValueAttribute((Element)currentElement));
            } else if (LOCATION_INFO.equalsIgnoreCase(currentElement.getAttribute("name"))) {
                locationInfo.set(this.getBooleanValueAttribute((Element)currentElement));
            }
        });
        return this.createLayout(properties.get(), locationInfo.get());
    }

    @Override
    public Layout parse(PropertiesConfiguration config) {
        boolean properties = this.getBooleanProperty(PROPERTIES);
        boolean locationInfo = this.getBooleanProperty(LOCATION_INFO);
        return this.createLayout(properties, locationInfo);
    }

    private Layout createLayout(boolean properties, boolean locationInfo) {
        return LayoutWrapper.adapt(Log4j1XmlLayout.createLayout(locationInfo, properties));
    }
}

