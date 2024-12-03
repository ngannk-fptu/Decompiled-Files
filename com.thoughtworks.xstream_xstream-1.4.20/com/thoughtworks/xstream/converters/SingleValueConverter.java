/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.converters.ConverterMatcher;

public interface SingleValueConverter
extends ConverterMatcher {
    public String toString(Object var1);

    public Object fromString(String var1);
}

