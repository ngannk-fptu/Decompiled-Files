/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class PropertiesConverter
implements Converter {
    private final boolean sort;

    public PropertiesConverter() {
        this(false);
    }

    public PropertiesConverter(boolean sort) {
        this.sort = sort;
    }

    public boolean canConvert(Class type) {
        return Properties.class == type;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Properties defaults;
        Properties properties = (Properties)source;
        Map<Object, Object> map = this.sort ? new TreeMap<Object, Object>(properties) : properties;
        Iterator<Map.Entry<Object, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            writer.startNode("property");
            writer.addAttribute("name", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.endNode();
        }
        if (Reflections.defaultsField != null && (defaults = (Properties)Fields.read(Reflections.defaultsField, properties)) != null) {
            writer.startNode("defaults");
            this.marshal(defaults, writer, context);
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Properties properties = new Properties();
        Properties defaults = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("defaults")) {
                defaults = (Properties)this.unmarshal(reader, context);
            } else {
                String name = reader.getAttribute("name");
                String value = reader.getAttribute("value");
                properties.setProperty(name, value);
            }
            reader.moveUp();
        }
        if (defaults == null) {
            return properties;
        }
        Properties propertiesWithDefaults = new Properties(defaults);
        propertiesWithDefaults.putAll((Map<?, ?>)properties);
        return propertiesWithDefaults;
    }

    private static class Reflections {
        private static final Field defaultsField = Fields.locate(class$java$util$Properties == null ? (class$java$util$Properties = PropertiesConverter.class$("java.util.Properties")) : class$java$util$Properties, class$java$util$Properties == null ? (class$java$util$Properties = PropertiesConverter.class$("java.util.Properties")) : class$java$util$Properties, false);

        private Reflections() {
        }
    }
}

