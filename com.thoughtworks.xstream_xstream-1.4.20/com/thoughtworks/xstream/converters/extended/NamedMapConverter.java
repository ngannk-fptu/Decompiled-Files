/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.converters.extended.UseAttributeForEnumMapper;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.SecurityUtils;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.Iterator;
import java.util.Map;

public class NamedMapConverter
extends MapConverter {
    private final String entryName;
    private final String keyName;
    private final Class keyType;
    private final String valueName;
    private final Class valueType;
    private final boolean keyAsAttribute;
    private final boolean valueAsAttribute;
    private final ConverterLookup lookup;
    private final Mapper enumMapper;

    public NamedMapConverter(Mapper mapper, String entryName, String keyName, Class keyType, String valueName, Class valueType) {
        this(mapper, entryName, keyName, keyType, valueName, valueType, false, false, null);
    }

    public NamedMapConverter(Class type, Mapper mapper, String entryName, String keyName, Class keyType, String valueName, Class valueType) {
        this(type, mapper, entryName, keyName, keyType, valueName, valueType, false, false, null);
    }

    public NamedMapConverter(Mapper mapper, String entryName, String keyName, Class keyType, String valueName, Class valueType, boolean keyAsAttribute, boolean valueAsAttribute, ConverterLookup lookup) {
        this(null, mapper, entryName, keyName, keyType, valueName, valueType, keyAsAttribute, valueAsAttribute, lookup);
    }

    public NamedMapConverter(Class type, Mapper mapper, String entryName, String keyName, Class keyType, String valueName, Class valueType, boolean keyAsAttribute, boolean valueAsAttribute, ConverterLookup lookup) {
        super(mapper, type);
        this.entryName = entryName != null && entryName.length() == 0 ? null : entryName;
        this.keyName = keyName != null && keyName.length() == 0 ? null : keyName;
        this.keyType = keyType;
        this.valueName = valueName != null && valueName.length() == 0 ? null : valueName;
        this.valueType = valueType;
        this.keyAsAttribute = keyAsAttribute;
        this.valueAsAttribute = valueAsAttribute;
        this.lookup = lookup;
        Mapper mapper2 = this.enumMapper = JVM.isVersion(5) ? UseAttributeForEnumMapper.createEnumMapper(mapper) : null;
        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("Class types of key and value are mandatory");
        }
        if (entryName == null) {
            if (keyAsAttribute || valueAsAttribute) {
                throw new IllegalArgumentException("Cannot write attributes to map entry, if map entry must be omitted");
            }
            if (valueName == null) {
                throw new IllegalArgumentException("Cannot write value as text of entry, if entry must be omitted");
            }
        }
        if (keyName == null) {
            throw new IllegalArgumentException("Cannot write key without name");
        }
        if (valueName == null) {
            if (valueAsAttribute) {
                throw new IllegalArgumentException("Cannot write value as attribute without name");
            }
            if (!keyAsAttribute) {
                throw new IllegalArgumentException("Cannot write value as text of entry, if key is also child element");
            }
        }
        if (keyAsAttribute && valueAsAttribute && keyName.equals(valueName)) {
            throw new IllegalArgumentException("Cannot write key and value with same attribute name");
        }
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map)source;
        SingleValueConverter keyConverter = null;
        SingleValueConverter valueConverter = null;
        if (this.keyAsAttribute) {
            keyConverter = this.getSingleValueConverter(this.keyType, "key");
        }
        if (this.valueAsAttribute || this.valueName == null) {
            valueConverter = this.getSingleValueConverter(this.valueType, "value");
        }
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (this.entryName != null) {
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, this.entryName, entry.getClass());
                if (keyConverter != null && key != null) {
                    writer.addAttribute(this.keyName, keyConverter.toString(key));
                }
                if (this.valueName != null && valueConverter != null && value != null) {
                    writer.addAttribute(this.valueName, valueConverter.toString(value));
                }
            }
            if (keyConverter == null) {
                this.writeItem(this.keyName, this.keyType, key, context, writer);
            }
            if (valueConverter == null) {
                this.writeItem(this.valueName, this.valueType, value, context, writer);
            } else if (this.valueName == null) {
                writer.setValue(valueConverter.toString(value));
            }
            if (this.entryName == null) continue;
            writer.endNode();
        }
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map, Map target) {
        SingleValueConverter keyConverter = null;
        SingleValueConverter valueConverter = null;
        if (this.keyAsAttribute) {
            keyConverter = this.getSingleValueConverter(this.keyType, "key");
        }
        if (this.valueAsAttribute || this.valueName == null) {
            valueConverter = this.getSingleValueConverter(this.valueType, "value");
        }
        while (reader.hasMoreChildren()) {
            Object key = null;
            Object value = null;
            if (this.entryName != null) {
                String attribute;
                reader.moveDown();
                if (keyConverter != null && (attribute = reader.getAttribute(this.keyName)) != null) {
                    key = keyConverter.fromString(attribute);
                }
                if (this.valueAsAttribute && valueConverter != null && (attribute = reader.getAttribute(this.valueName)) != null) {
                    value = valueConverter.fromString(attribute);
                }
            }
            if (keyConverter == null) {
                reader.moveDown();
                if (valueConverter == null && !this.keyName.equals(this.valueName) && reader.getNodeName().equals(this.valueName)) {
                    value = this.readItem(this.valueType, reader, context, map);
                } else {
                    key = this.readItem(this.keyType, reader, context, map);
                }
                reader.moveUp();
            }
            if (valueConverter == null) {
                reader.moveDown();
                if (keyConverter == null && key == null && value != null) {
                    key = this.readItem(this.keyType, reader, context, map);
                } else {
                    value = this.readItem(this.valueType, reader, context, map);
                }
                reader.moveUp();
            } else if (!this.valueAsAttribute) {
                value = valueConverter.fromString(reader.getValue());
            }
            long now = System.currentTimeMillis();
            target.put(key, value);
            SecurityUtils.checkForCollectionDoSAttack(context, now);
            if (this.entryName == null) continue;
            reader.moveUp();
        }
    }

    private SingleValueConverter getSingleValueConverter(Class type, String part) {
        SingleValueConverter conv;
        SingleValueConverter singleValueConverter = conv = UseAttributeForEnumMapper.isEnum(type) ? this.enumMapper.getConverterFromItemType(null, type, null) : this.mapper().getConverterFromItemType(null, type, null);
        if (conv == null) {
            Converter converter = this.lookup.lookupConverterForType(type);
            if (converter instanceof SingleValueConverter) {
                conv = (SingleValueConverter)((Object)converter);
            } else {
                throw new ConversionException("No SingleValueConverter for " + part + " available");
            }
        }
        return conv;
    }

    protected void writeItem(String name, Class type, Object item, MarshallingContext context, HierarchicalStreamWriter writer) {
        String attributeName;
        Class clazz = item == null ? Mapper.Null.class : item.getClass();
        Class itemType = clazz;
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, itemType);
        if (!itemType.equals(type) && (attributeName = this.mapper().aliasForSystemAttribute("class")) != null) {
            writer.addAttribute(attributeName, this.mapper().serializedClass(itemType));
        }
        if (item != null) {
            context.convertAnother(item);
        }
        writer.endNode();
    }

    protected Object readItem(Class type, HierarchicalStreamReader reader, UnmarshallingContext context, Object current) {
        String className;
        Class itemType;
        if (Mapper.Null.class.equals(itemType = (className = HierarchicalStreams.readClassAttribute(reader, this.mapper())) == null ? type : this.mapper().realClass(className))) {
            return null;
        }
        return context.convertAnother(current, itemType);
    }
}

