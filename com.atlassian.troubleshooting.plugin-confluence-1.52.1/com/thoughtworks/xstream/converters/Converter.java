/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public interface Converter
extends ConverterMatcher {
    public void marshal(Object var1, HierarchicalStreamWriter var2, MarshallingContext var3);

    public Object unmarshal(HierarchicalStreamReader var1, UnmarshallingContext var2);
}

