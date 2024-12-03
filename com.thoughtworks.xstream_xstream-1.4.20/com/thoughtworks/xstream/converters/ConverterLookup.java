/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.Converter;

public interface ConverterLookup {
    public Converter lookupConverterForType(Class var1);
}

