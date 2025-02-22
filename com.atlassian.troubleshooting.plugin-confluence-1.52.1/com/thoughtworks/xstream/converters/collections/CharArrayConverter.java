/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CharArrayConverter
implements Converter {
    public boolean canConvert(Class type) {
        return type != null && type.isArray() && type.getComponentType().equals(Character.TYPE);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        char[] chars = (char[])source;
        writer.setValue(new String(chars));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return reader.getValue().toCharArray();
    }
}

