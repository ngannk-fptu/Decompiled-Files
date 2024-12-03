/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongConverter
extends LongConverter
implements Converter {
    public boolean canConvert(Class type) {
        return type != null && type == AtomicLong.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(this.toString(source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String data = reader.getValue();
        if (!reader.hasMoreChildren()) {
            return this.fromString(data);
        }
        reader.moveDown();
        Long integer = (Long)super.fromString(reader.getValue());
        reader.moveUp();
        return new AtomicLong(integer);
    }

    public String toString(Object obj) {
        return super.toString(new Long(((AtomicLong)obj).get()));
    }

    public Object fromString(String str) {
        return new AtomicLong((Long)super.fromString(str));
    }
}

