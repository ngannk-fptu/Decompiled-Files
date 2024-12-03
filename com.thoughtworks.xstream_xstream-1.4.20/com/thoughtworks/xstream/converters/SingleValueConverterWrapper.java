/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SingleValueConverterWrapper
implements Converter,
SingleValueConverter,
ErrorReporter {
    private final SingleValueConverter wrapped;

    public SingleValueConverterWrapper(SingleValueConverter wrapped) {
        this.wrapped = wrapped;
    }

    public boolean canConvert(Class type) {
        return this.wrapped.canConvert(type);
    }

    public String toString(Object obj) {
        return this.wrapped.toString(obj);
    }

    public Object fromString(String str) {
        return this.wrapped.fromString(str);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        writer.setValue(this.toString(source));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return this.fromString(reader.getValue());
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("wrapped-converter", this.wrapped == null ? "(null)" : this.wrapped.getClass().getName());
        if (this.wrapped instanceof ErrorReporter) {
            ((ErrorReporter)((Object)this.wrapped)).appendErrors(errorWriter);
        }
    }
}

