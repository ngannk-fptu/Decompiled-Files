/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.net.URI;
import java.net.URISyntaxException;

public class URIConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == URI.class;
    }

    public Object fromString(String str) {
        try {
            return new URI(str);
        }
        catch (URISyntaxException e) {
            throw new ConversionException(e);
        }
    }
}

