/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;

public interface MarshallingContext
extends DataHolder {
    public void convertAnother(Object var1);

    public void convertAnother(Object var1, Converter var2);
}

