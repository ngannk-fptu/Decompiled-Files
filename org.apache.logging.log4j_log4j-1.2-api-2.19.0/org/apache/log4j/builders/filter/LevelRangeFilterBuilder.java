/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Filter$Result
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.filter.LevelRangeFilter
 */
package org.apache.log4j.builders.filter;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Level;
import org.apache.log4j.bridge.FilterWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.filter.FilterBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.varia.LevelRangeFilter", category="Log4j Builder")
public class LevelRangeFilterBuilder
extends AbstractBuilder<org.apache.log4j.spi.Filter>
implements FilterBuilder {
    private static final String LEVEL_MAX = "LevelMax";
    private static final String LEVEL_MIN = "LevelMin";
    private static final String ACCEPT_ON_MATCH = "AcceptOnMatch";

    public LevelRangeFilterBuilder() {
    }

    public LevelRangeFilterBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public org.apache.log4j.spi.Filter parse(Element filterElement, XmlConfiguration config) {
        AtomicReference levelMax = new AtomicReference();
        AtomicReference levelMin = new AtomicReference();
        AtomicBoolean acceptOnMatch = new AtomicBoolean();
        XmlConfiguration.forEachElement(filterElement.getElementsByTagName("param"), currentElement -> {
            if (currentElement.getTagName().equals("param")) {
                switch (this.getNameAttributeKey((Element)currentElement)) {
                    case "LevelMax": {
                        levelMax.set(this.getValueAttribute((Element)currentElement));
                        break;
                    }
                    case "LevelMin": {
                        levelMin.set(this.getValueAttribute((Element)currentElement));
                        break;
                    }
                    case "AcceptOnMatch": {
                        acceptOnMatch.set(this.getBooleanValueAttribute((Element)currentElement));
                    }
                }
            }
        });
        return this.createFilter((String)levelMax.get(), (String)levelMin.get(), acceptOnMatch.get());
    }

    @Override
    public org.apache.log4j.spi.Filter parse(PropertiesConfiguration config) {
        String levelMax = this.getProperty(LEVEL_MAX);
        String levelMin = this.getProperty(LEVEL_MIN);
        boolean acceptOnMatch = this.getBooleanProperty(ACCEPT_ON_MATCH);
        return this.createFilter(levelMax, levelMin, acceptOnMatch);
    }

    private org.apache.log4j.spi.Filter createFilter(String levelMax, String levelMin, boolean acceptOnMatch) {
        org.apache.logging.log4j.Level max = org.apache.logging.log4j.Level.OFF;
        org.apache.logging.log4j.Level min = org.apache.logging.log4j.Level.ALL;
        if (levelMax != null) {
            max = OptionConverter.toLevel(levelMax, Level.OFF).getVersion2Level();
        }
        if (levelMin != null) {
            min = OptionConverter.toLevel(levelMin, Level.ALL).getVersion2Level();
        }
        Filter.Result onMatch = acceptOnMatch ? Filter.Result.ACCEPT : Filter.Result.NEUTRAL;
        return FilterWrapper.adapt((Filter)LevelRangeFilter.createFilter((org.apache.logging.log4j.Level)max, (org.apache.logging.log4j.Level)min, (Filter.Result)onMatch, (Filter.Result)Filter.Result.DENY));
    }
}

