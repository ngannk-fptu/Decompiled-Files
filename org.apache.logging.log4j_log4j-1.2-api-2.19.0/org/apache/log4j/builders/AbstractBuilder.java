/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Filter$Result
 *  org.apache.logging.log4j.core.filter.ThresholdFilter
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.log4j.builders;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.bridge.FilterWrapper;
import org.apache.log4j.builders.Builder;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Element;

public abstract class AbstractBuilder<T>
implements Builder<T> {
    private static Logger LOGGER = StatusLogger.getLogger();
    protected static final String FILE_PARAM = "File";
    protected static final String APPEND_PARAM = "Append";
    protected static final String BUFFERED_IO_PARAM = "BufferedIO";
    protected static final String BUFFER_SIZE_PARAM = "BufferSize";
    protected static final String IMMEDIATE_FLUSH_PARAM = "ImmediateFlush";
    protected static final String MAX_SIZE_PARAM = "MaxFileSize";
    protected static final String MAX_BACKUP_INDEX = "MaxBackupIndex";
    protected static final String RELATIVE = "RELATIVE";
    protected static final String NULL = "NULL";
    private final String prefix;
    private final Properties properties;

    public AbstractBuilder() {
        this(null, new Properties());
    }

    public AbstractBuilder(String prefix, Properties props) {
        this.prefix = prefix != null ? prefix + "." : null;
        this.properties = (Properties)props.clone();
        HashMap map = new HashMap();
        System.getProperties().forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(k, v) -> map.put(k.toString(), v.toString())));
        props.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(k, v) -> map.put(k.toString(), v.toString())));
        props.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(k, v) -> map.put(this.toBeanKey(k.toString()), v.toString())));
        props.entrySet().forEach(e -> this.properties.put(this.toBeanKey(e.getKey().toString()), e.getValue()));
    }

    protected static Filter buildFilters(String level, org.apache.log4j.spi.Filter filter) {
        org.apache.log4j.spi.Filter head = null;
        if (level != null) {
            ThresholdFilter thresholdFilter = ThresholdFilter.createFilter((Level)OptionConverter.convertLevel(level, Level.TRACE), (Filter.Result)Filter.Result.NEUTRAL, (Filter.Result)Filter.Result.DENY);
            head = new FilterWrapper((Filter)thresholdFilter);
        }
        if (filter != null) {
            head = FilterAdapter.addFilter(head, filter);
        }
        return FilterAdapter.adapt(head);
    }

    private String capitalize(String value) {
        if (Strings.isEmpty((CharSequence)value) || Character.isUpperCase(value.charAt(0))) {
            return value;
        }
        char[] chars = value.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(this.getProperty(key, Boolean.toString(defaultValue)));
    }

    public boolean getBooleanProperty(String key) {
        return this.getBooleanProperty(key, false);
    }

    protected boolean getBooleanValueAttribute(Element element) {
        return Boolean.parseBoolean(this.getValueAttribute(element));
    }

    public int getIntegerProperty(String key, int defaultValue) {
        String value = null;
        try {
            value = this.getProperty(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
        }
        catch (Exception ex) {
            LOGGER.warn("Error converting value {} of {} to an integer: {}", (Object)value, (Object)key, (Object)ex.getMessage());
        }
        return defaultValue;
    }

    public long getLongProperty(String key, long defaultValue) {
        String value = null;
        try {
            value = this.getProperty(key);
            if (value != null) {
                return Long.parseLong(value);
            }
        }
        catch (Exception ex) {
            LOGGER.warn("Error converting value {} of {} to a long: {}", (Object)value, (Object)key, (Object)ex.getMessage());
        }
        return defaultValue;
    }

    protected String getNameAttribute(Element element) {
        return element.getAttribute("name");
    }

    protected String getNameAttributeKey(Element element) {
        return this.toBeanKey(element.getAttribute("name"));
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getProperty(String key) {
        return this.getProperty(key, null);
    }

    public String getProperty(String key, String defaultValue) {
        String value = this.properties.getProperty(this.prefix + this.toJavaKey(key));
        value = value != null ? value : this.properties.getProperty(this.prefix + this.toBeanKey(key), defaultValue);
        value = value != null ? this.substVars(value) : defaultValue;
        return value != null ? value.trim() : defaultValue;
    }

    protected String getValueAttribute(Element element) {
        return this.getValueAttribute(element, null);
    }

    protected String getValueAttribute(Element element, String defaultValue) {
        String attribute = element.getAttribute("value");
        return this.substVars(attribute != null ? attribute.trim() : defaultValue);
    }

    protected String substVars(String value) {
        return OptionConverter.substVars(value, this.properties);
    }

    String toBeanKey(String value) {
        return this.capitalize(value);
    }

    String toJavaKey(String value) {
        return this.uncapitalize(value);
    }

    private String uncapitalize(String value) {
        if (Strings.isEmpty((CharSequence)value) || Character.isLowerCase(value.charAt(0))) {
            return value;
        }
        char[] chars = value.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    protected void set(String name, Element element, AtomicBoolean ref) {
        String value = this.getValueAttribute(element);
        if (value == null) {
            LOGGER.warn("No value for {} parameter, using default {}", (Object)name, (Object)ref);
        } else {
            ref.set(Boolean.parseBoolean(value));
        }
    }

    protected void set(String name, Element element, AtomicInteger ref) {
        String value = this.getValueAttribute(element);
        if (value == null) {
            LOGGER.warn("No value for {} parameter, using default {}", (Object)name, (Object)ref);
        } else {
            try {
                ref.set(Integer.parseInt(value));
            }
            catch (NumberFormatException e) {
                LOGGER.warn("{} parsing {} parameter, using default {}: {}", (Object)e.getClass().getName(), (Object)name, (Object)ref, (Object)e.getMessage(), (Object)e);
            }
        }
    }

    protected void set(String name, Element element, AtomicLong ref) {
        String value = this.getValueAttribute(element);
        if (value == null) {
            LOGGER.warn("No value for {} parameter, using default {}", (Object)name, (Object)ref);
        } else {
            try {
                ref.set(Long.parseLong(value));
            }
            catch (NumberFormatException e) {
                LOGGER.warn("{} parsing {} parameter, using default {}: {}", (Object)e.getClass().getName(), (Object)name, (Object)ref, (Object)e.getMessage(), (Object)e);
            }
        }
    }

    protected void set(String name, Element element, AtomicReference<String> ref) {
        String value = this.getValueAttribute(element);
        if (value == null) {
            LOGGER.warn("No value for {} parameter, using default {}", (Object)name, ref);
        } else {
            ref.set(value);
        }
    }
}

