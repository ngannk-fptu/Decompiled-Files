/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.DataHolder;

public interface UnmarshallingContext
extends DataHolder {
    public Object convertAnother(Object var1, Class var2);

    public Object convertAnother(Object var1, Class var2, Converter var3);

    public Object currentObject();

    public Class getRequiredType();

    public void addCompletionCallback(Runnable var1, int var2);
}

