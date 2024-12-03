/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.pluginsettings;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.core.pluginsettings.EscapeUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStringPluginSettings
implements PluginSettings {
    private static final Logger log = LoggerFactory.getLogger(AbstractStringPluginSettings.class);
    private static final String PROPERTIES_ENCODING = "ISO8859_1";
    private static final String PROPERTIES_IDENTIFIER = "java.util.Properties";
    private static final String LIST_IDENTIFIER = "#java.util.List";
    private static final String MAP_IDENTIFIER = "#java.util.Map";
    private final boolean isDeveloperMode = Boolean.getBoolean("atlassian.dev.mode");

    public Object put(String key, Object value) {
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
        Validate.isTrue((key.length() <= 255 ? 1 : 0) != 0, (String)"The plugin settings key cannot be more than 255 characters", (Object[])new Object[0]);
        if (this.isDeveloperMode) {
            Validate.isTrue((key.length() <= 100 ? 1 : 0) != 0, (String)"The plugin settings key cannot be more than 100 characters in developer mode", (Object[])new Object[0]);
        }
        if (value == null) {
            return this.remove(key);
        }
        Object oldValue = this.get(key);
        if (value instanceof Properties) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try {
                Properties properties = (Properties)value;
                properties.store(bout, PROPERTIES_IDENTIFIER);
                this.putActual(key, new String(bout.toByteArray(), PROPERTIES_ENCODING));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Unable to serialize properties", e);
            }
        } else if (value instanceof String) {
            this.putActual(key, (String)value);
        } else if (value instanceof List) {
            StringBuilder sb = new StringBuilder();
            sb.append(LIST_IDENTIFIER).append('\n');
            Iterator i = ((List)value).iterator();
            while (i.hasNext()) {
                sb.append(EscapeUtils.escape(i.next().toString()));
                if (!i.hasNext()) continue;
                sb.append('\n');
            }
            this.putActual(key, sb.toString());
        } else if (value instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append(MAP_IDENTIFIER).append('\n');
            Iterator i = ((Map)value).entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = i.next();
                sb.append(EscapeUtils.escape(entry.getKey().toString()));
                sb.append('\f');
                sb.append(EscapeUtils.escape(entry.getValue().toString()));
                if (!i.hasNext()) continue;
                sb.append('\n');
            }
            this.putActual(key, sb.toString());
        } else {
            throw new IllegalArgumentException("Property type: " + value.getClass() + " not supported");
        }
        return oldValue;
    }

    public Object get(String key) {
        String val;
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
        if (this.isDeveloperMode && key.length() > 100) {
            log.warn("PluginSettings.get with excessive key length: {}", (Object)key);
        }
        if ((val = this.getActual(key)) != null && val.startsWith("#java.util.Properties")) {
            Properties p = new Properties();
            try {
                p.load(new ByteArrayInputStream(val.getBytes(PROPERTIES_ENCODING)));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Unable to deserialize properties", e);
            }
            return p;
        }
        if (val != null && val.startsWith(LIST_IDENTIFIER)) {
            String[] lines = val.split("\n");
            List<String> rawItems = Arrays.asList(lines).subList(1, lines.length);
            return rawItems.stream().map(EscapeUtils::unescape).collect(Collectors.toList());
        }
        if (val != null && val.startsWith(MAP_IDENTIFIER)) {
            String[] items;
            String nval = val.substring(MAP_IDENTIFIER.length() + 1);
            HashMap<String, String> map = new HashMap<String, String>();
            for (String item : items = nval.split("\n")) {
                if (item.length() <= 0) continue;
                int tabLocation = item.indexOf(12);
                if (tabLocation == -1) {
                    log.error("Could not parse map element: '{}'\nFull list: '{}'\n", (Object)item, (Object)nval);
                    continue;
                }
                String keyPart = item.substring(0, tabLocation);
                String valuePart = item.substring(tabLocation + 1, item.length());
                map.put(EscapeUtils.unescape(keyPart), EscapeUtils.unescape(valuePart));
            }
            return map;
        }
        return val;
    }

    public Object remove(String key) {
        Object oldValue;
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
        if (this.isDeveloperMode && key.length() > 100) {
            log.warn("PluginSettings.get with excessive key length: " + key);
        }
        if ((oldValue = this.get(key)) != null) {
            this.removeActual(key);
        }
        return oldValue;
    }

    protected abstract void putActual(String var1, String var2);

    protected abstract String getActual(String var1);

    protected abstract void removeActual(String var1);
}

