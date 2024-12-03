/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.convert.ConversionException;

public interface PropertyConverter {
    public Object toObject(String var1, Class var2, String var3) throws ConversionException;

    public String toString(Object var1, String var2) throws ConversionException;
}

