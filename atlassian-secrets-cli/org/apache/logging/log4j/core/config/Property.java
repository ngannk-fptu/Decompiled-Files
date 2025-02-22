/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.config;

import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.PropertiesPlugin;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(name="property", category="Core", printObject=true)
public final class Property {
    public static final Property[] EMPTY_ARRAY = new Property[0];
    private static final Logger LOGGER = StatusLogger.getLogger();
    private final String name;
    private final String rawValue;
    private final String value;
    private final boolean valueNeedsLookup;

    private Property(String name, String rawValue, String value) {
        this.name = name;
        this.rawValue = rawValue;
        this.value = value;
        this.valueNeedsLookup = value != null && value.contains("${");
    }

    public String getName() {
        return this.name;
    }

    public String getRawValue() {
        return Objects.toString(this.rawValue, "");
    }

    public String getValue() {
        return Objects.toString(this.value, "");
    }

    public boolean isValueNeedsLookup() {
        return this.valueNeedsLookup;
    }

    public String evaluate(StrSubstitutor substitutor) {
        return this.valueNeedsLookup ? substitutor.replace(PropertiesPlugin.unescape(this.getRawValue())) : this.getValue();
    }

    public static Property createProperty(String name, String value) {
        if (name == null) {
            LOGGER.error("Property name cannot be null");
        }
        return new Property(name, value, value);
    }

    @PluginFactory
    public static Property createProperty(@PluginAttribute(value="name") String name, @PluginValue(value="value", substitute=false) String rawValue, @PluginValue(value="value") String value) {
        if (name == null) {
            LOGGER.error("Property name cannot be null");
        }
        return new Property(name, rawValue, value);
    }

    public String toString() {
        return this.name + '=' + this.getValue();
    }
}

