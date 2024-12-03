/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;

@Component
public class DefaultSubCalendarColorRegistry
implements SubCalendarColorRegistry {
    private static final String COLOUR_SCHEME_VERY_LIGHT_SUFFIX = "-very-light";
    private final Map<String, String> colorClassToHexMap = new LinkedHashMap<String, String>();
    private final Map<String, String> lightenedColorClassToHexMap = new HashMap<String, String>();
    private final Map<String, String> evenMoreLightenedColorClassToHexMap = new HashMap<String, String>();
    private final int hashCodeCache;
    private static final Map<String, String> COLOUR_NAME_VALUE_MAP = Collections.unmodifiableMap(new HashMap<String, String>(){
        {
            this.put("subcalendar-blue", "0052CC");
            this.put("subcalendar-blue2", "0049B0");
            this.put("subcalendar-turquoise", "00A3BF");
            this.put("subcalendar-gray", "091E42");
            this.put("subcalendar-gray2", "0065FF");
            this.put("subcalendar-red", "BF2600");
            this.put("subcalendar-pink", "DE350B");
            this.put("subcalendar-purple", "5243AA");
            this.put("subcalendar-purple2", "403294");
            this.put("subcalendar-purple3", "253858");
            this.put("subcalendar-purple4", "8777D9");
            this.put("subcalendar-green", "006644");
            this.put("subcalendar-green2", "00875A");
            this.put("subcalendar-green3", "36B37E");
            this.put("subcalendar-green4", "008DA6");
            this.put("subcalendar-green5", "00B8D9");
            this.put("subcalendar-green6", "42526E");
            this.put("subcalendar-yellow", "FFC400");
            this.put("subcalendar-orange", "FF8B00");
            this.put("subcalendar-orange2", "FF5630");
            this.put("subcalendar-bronze", "FFAB00");
        }
    });

    public DefaultSubCalendarColorRegistry() {
        this.populateColorRegistries();
        this.hashCodeCache = this.computeHashCode();
    }

    private void populateColorRegistries() {
        for (Map.Entry<String, String> colourPair : COLOUR_NAME_VALUE_MAP.entrySet()) {
            String colourName = colourPair.getKey();
            String colourHex = colourPair.getValue();
            this.colorClassToHexMap.put(colourName, colourHex);
            this.lightenedColorClassToHexMap.put(colourName, this.getLightenedColorHex(colourName, 0.4f));
            this.evenMoreLightenedColorClassToHexMap.put(colourName, this.getLightenedColorHex(colourName, 0.68f));
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass() && obj.hashCode() != this.hashCode()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        DefaultSubCalendarColorRegistry rhs = (DefaultSubCalendarColorRegistry)obj;
        return new EqualsBuilder().append(this.colorClassToHexMap, rhs.colorClassToHexMap).append(this.lightenedColorClassToHexMap, rhs.lightenedColorClassToHexMap).append(this.evenMoreLightenedColorClassToHexMap, rhs.evenMoreLightenedColorClassToHexMap).isEquals();
    }

    public int hashCode() {
        return this.getHashCodeCache();
    }

    @VisibleForTesting
    int getHashCodeCache() {
        return this.hashCodeCache;
    }

    @VisibleForTesting
    int computeHashCode() {
        return new HashCodeBuilder().append(this.colorClassToHexMap).append(this.lightenedColorClassToHexMap).append(this.evenMoreLightenedColorClassToHexMap).build();
    }

    private String getLightenedColorHex(String colorClass, float alpha) {
        int rgb = Integer.parseInt(this.getColorHex(colorClass), 16);
        return StringUtils.leftPad(String.format("%x", this.getAlphaBlend(rgb >> 16 & 0xFF, alpha) << 16 | this.getAlphaBlend(rgb >> 8 & 0xFF, alpha) << 8 | this.getAlphaBlend(rgb & 0xFF, alpha)), 6, '0');
    }

    private int getAlphaBlend(int colorComponent, float alpha) {
        return (int)(alpha * 255.0f + (1.0f - alpha) * (float)colorComponent);
    }

    private String getColorNameKey(StringBuilder keyBuilder, int number) {
        return this.getColorKey(keyBuilder, number, "name");
    }

    private String getColorKey(StringBuilder keyBuilder, int number, String property) {
        keyBuilder.setLength(0);
        return keyBuilder.append("color").append(number).append('.').append(property).toString();
    }

    private String getColorValueKey(StringBuilder keyBuilder, int number) {
        return this.getColorKey(keyBuilder, number, "value");
    }

    @Override
    public Set<String> getColorClasses() {
        return this.colorClassToHexMap.keySet();
    }

    @Override
    public String getColorHex(String colorClass) {
        return this.colorClassToHexMap.get(colorClass);
    }

    @Override
    public String getLightenedColorHex(String colorClass) {
        return this.lightenedColorClassToHexMap.get(colorClass);
    }

    @Override
    public String getEvenMoreLightenedColorHex(String colorClass) {
        return this.evenMoreLightenedColorClassToHexMap.get(colorClass);
    }

    @Override
    public String getEventMoreLightenedColourScheme(String colourClass) {
        return colourClass + COLOUR_SCHEME_VERY_LIGHT_SUFFIX;
    }

    @Override
    public boolean isEventMoreLightenedColourScheme(String evenMoreLightenedColourScheme) {
        return StringUtils.endsWith(evenMoreLightenedColourScheme, COLOUR_SCHEME_VERY_LIGHT_SUFFIX);
    }

    @Override
    public String getRandomColourClass(String ... excludeColourClasses) {
        String randomColourClass;
        ArrayList colourClasses = Lists.newArrayList(this.getColorClasses());
        while (ArrayUtils.contains(excludeColourClasses, randomColourClass = (String)colourClasses.get(RandomUtils.nextInt(colourClasses.size())))) {
        }
        return randomColourClass;
    }
}

