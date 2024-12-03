/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.converters.Converter
 */
package com.atlassian.confluence.setup.xstream;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.setup.xstream.ConfluenceXStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;

@Internal
public interface ConfluenceXStreamInternal
extends ConfluenceXStream {
    @Deprecated
    public XStream getXStream();

    public void registerConverter(Converter var1, Integer var2);

    public void alias(String var1, Class var2);

    public void allowTypes(String[] var1);

    public void allowTypesByWildcard(String[] var1);

    public void allowTypesByRegExp(String[] var1);

    public void setUpDefaultSecurity();
}

