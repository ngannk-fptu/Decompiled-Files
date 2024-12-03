/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.compat.setup.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import java.io.Reader;
import java.io.Writer;

interface XStreamCompat {
    public String toXML(Object var1);

    public void toXML(Object var1, Writer var2);

    public Object fromXML(String var1);

    public Object fromXML(Reader var1);

    public XStream getXStream();

    public void registerConverter(Converter var1, Integer var2);

    public void alias(String var1, Class<?> var2);
}

