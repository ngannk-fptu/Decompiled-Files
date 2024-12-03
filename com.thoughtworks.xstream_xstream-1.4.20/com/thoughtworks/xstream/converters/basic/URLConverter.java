/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == URL.class;
    }

    public Object fromString(String str) {
        try {
            return new URL(str);
        }
        catch (MalformedURLException e) {
            throw new ConversionException(e);
        }
    }
}

