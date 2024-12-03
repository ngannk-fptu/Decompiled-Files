/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.layout.HtmlLayout
 */
package org.apache.log4j.builders.layout;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.layout.LayoutBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.HtmlLayout;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.HTMLLayout", category="Log4j Builder")
public class HtmlLayoutBuilder
extends AbstractBuilder<Layout>
implements LayoutBuilder {
    private static final String DEFAULT_TITLE = "Log4J Log Messages";
    private static final String TITLE_PARAM = "Title";
    private static final String LOCATION_INFO_PARAM = "LocationInfo";

    public HtmlLayoutBuilder() {
    }

    public HtmlLayoutBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Layout parse(Element layoutElement, XmlConfiguration config) {
        AtomicReference<String> title = new AtomicReference<String>(DEFAULT_TITLE);
        AtomicBoolean locationInfo = new AtomicBoolean();
        XmlConfiguration.forEachElement(layoutElement.getElementsByTagName("param"), currentElement -> {
            if (currentElement.getTagName().equals("param")) {
                if (TITLE_PARAM.equalsIgnoreCase(currentElement.getAttribute("name"))) {
                    title.set(currentElement.getAttribute("value"));
                } else if (LOCATION_INFO_PARAM.equalsIgnoreCase(currentElement.getAttribute("name"))) {
                    locationInfo.set(this.getBooleanValueAttribute((Element)currentElement));
                }
            }
        });
        return this.createLayout(title.get(), locationInfo.get());
    }

    @Override
    public Layout parse(PropertiesConfiguration config) {
        String title = this.getProperty(TITLE_PARAM, DEFAULT_TITLE);
        boolean locationInfo = this.getBooleanProperty(LOCATION_INFO_PARAM);
        return this.createLayout(title, locationInfo);
    }

    private Layout createLayout(String title, boolean locationInfo) {
        return LayoutWrapper.adapt(HtmlLayout.newBuilder().withTitle(title).withLocationInfo(locationInfo).build());
    }
}

