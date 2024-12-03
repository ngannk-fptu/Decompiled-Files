/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.log4j.Layout
 *  org.apache.log4j.bridge.LayoutWrapper
 *  org.apache.log4j.builders.AbstractBuilder
 *  org.apache.log4j.builders.layout.LayoutBuilder
 *  org.apache.log4j.config.PropertiesConfiguration
 *  org.apache.log4j.xml.XmlConfiguration
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.layout.PatternLayout
 */
package com.atlassian.confluence.impl.logging.log4j.layout;

import java.util.Properties;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.bridge.LayoutWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.layout.LayoutBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.w3c.dom.Element;

abstract class Log4j2AbstractPatternModifyingLayoutBuilder
extends AbstractBuilder<org.apache.log4j.Layout>
implements LayoutBuilder {
    private static final String PATTERN = "ConversionPattern";

    public Log4j2AbstractPatternModifyingLayoutBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    public org.apache.log4j.Layout parse(PropertiesConfiguration config) {
        String pattern = (String)ObjectUtils.firstNonNull((Object[])new String[]{this.getProperty(PATTERN), "%m%n"});
        return this.createLayout(pattern, (Configuration)config);
    }

    private org.apache.log4j.Layout createLayout(String pattern, Configuration config) {
        return new LayoutWrapper((Layout)PatternLayout.newBuilder().withPattern(this.modifyPattern(pattern)).withConfiguration(config).withAlwaysWriteExceptions(false).build());
    }

    protected abstract String modifyPattern(String var1);

    public org.apache.log4j.Layout parse(Element element, XmlConfiguration config) {
        throw new UnsupportedOperationException("XML config style not supported");
    }
}

