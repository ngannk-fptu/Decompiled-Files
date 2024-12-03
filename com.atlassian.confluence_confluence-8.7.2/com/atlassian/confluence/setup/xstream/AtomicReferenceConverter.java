/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.xstream.converters.Converter
 *  com.thoughtworks.xstream.converters.MarshallingContext
 *  com.thoughtworks.xstream.converters.UnmarshallingContext
 *  com.thoughtworks.xstream.core.util.HierarchicalStreams
 *  com.thoughtworks.xstream.io.HierarchicalStreamReader
 *  com.thoughtworks.xstream.io.HierarchicalStreamWriter
 *  com.thoughtworks.xstream.mapper.Mapper
 */
package com.atlassian.confluence.setup.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceConverter
implements Converter {
    private final Mapper mapper;

    public AtomicReferenceConverter(Mapper mapper) {
        this.mapper = mapper;
    }

    public boolean canConvert(Class type) {
        return type == AtomicReference.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        AtomicReference ref = (AtomicReference)source;
        if (ref.get() != null) {
            writer.startNode(this.mapper.serializedMember(AtomicReference.class, "value"));
            Object object = ref.get();
            String name = this.mapper.serializedClass(object != null ? object.getClass() : null);
            writer.addAttribute(this.mapper.aliasForSystemAttribute("class"), name);
            context.convertAnother(ref.get());
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        if (reader.hasMoreChildren()) {
            reader.moveDown();
            Class type = HierarchicalStreams.readClassType((HierarchicalStreamReader)reader, (Mapper)this.mapper);
            Object value = context.convertAnother((Object)context, type);
            reader.moveUp();
            return new AtomicReference<Object>(value);
        }
        return new AtomicReference();
    }
}

