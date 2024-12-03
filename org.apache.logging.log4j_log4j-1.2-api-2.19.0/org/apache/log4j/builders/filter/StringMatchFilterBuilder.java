/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Filter$Result
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.filter.StringMatchFilter
 *  org.apache.logging.log4j.core.filter.StringMatchFilter$Builder
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.log4j.builders.filter;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.bridge.FilterWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.filter.FilterBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.StringMatchFilter;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.varia.StringMatchFilter", category="Log4j Builder")
public class StringMatchFilterBuilder
extends AbstractBuilder<org.apache.log4j.spi.Filter>
implements FilterBuilder {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String STRING_TO_MATCH = "StringToMatch";
    private static final String ACCEPT_ON_MATCH = "AcceptOnMatch";

    public StringMatchFilterBuilder() {
    }

    public StringMatchFilterBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public org.apache.log4j.spi.Filter parse(Element filterElement, XmlConfiguration config) {
        AtomicBoolean acceptOnMatch = new AtomicBoolean();
        AtomicReference text = new AtomicReference();
        XmlConfiguration.forEachElement(filterElement.getElementsByTagName("param"), currentElement -> {
            if (currentElement.getTagName().equals("param")) {
                switch (this.getNameAttributeKey((Element)currentElement)) {
                    case "StringToMatch": {
                        text.set(this.getValueAttribute((Element)currentElement));
                        break;
                    }
                    case "AcceptOnMatch": {
                        acceptOnMatch.set(this.getBooleanValueAttribute((Element)currentElement));
                    }
                }
            }
        });
        return this.createFilter((String)text.get(), acceptOnMatch.get());
    }

    @Override
    public org.apache.log4j.spi.Filter parse(PropertiesConfiguration config) {
        String text = this.getProperty(STRING_TO_MATCH);
        boolean acceptOnMatch = this.getBooleanProperty(ACCEPT_ON_MATCH);
        return this.createFilter(text, acceptOnMatch);
    }

    private org.apache.log4j.spi.Filter createFilter(String text, boolean acceptOnMatch) {
        if (text == null) {
            LOGGER.error("No text provided for StringMatchFilter");
            return null;
        }
        Filter.Result onMatch = acceptOnMatch ? Filter.Result.ACCEPT : Filter.Result.DENY;
        return FilterWrapper.adapt((Filter)((StringMatchFilter.Builder)((StringMatchFilter.Builder)StringMatchFilter.newBuilder().setMatchString(text).setOnMatch(onMatch)).setOnMismatch(Filter.Result.NEUTRAL)).build());
    }
}

