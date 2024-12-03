/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.compat.setup.xstream;

import com.atlassian.confluence.compat.setup.xstream.XStreamCompat;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import java.io.Reader;
import java.io.Writer;

class XStream111Compat
implements XStreamCompat {
    private final XStream xStream;

    XStream111Compat(XStream xStream) {
        this.xStream = xStream;
    }

    @Override
    public String toXML(Object obj) {
        return this.xStream.toXML(obj);
    }

    @Override
    public void toXML(Object obj, Writer writer) {
        this.xStream.toXML(obj, writer);
    }

    @Override
    public Object fromXML(String xml) {
        return this.xStream.fromXML(xml);
    }

    @Override
    public Object fromXML(Reader reader) {
        return this.xStream.fromXML(reader);
    }

    @Override
    public XStream getXStream() {
        return this.xStream;
    }

    @Override
    public void registerConverter(Converter converter, Integer priority) {
        this.xStream.registerConverter(converter, (int)priority);
    }

    @Override
    public void alias(String name, Class<?> type) {
        this.xStream.alias(name, type);
    }
}

