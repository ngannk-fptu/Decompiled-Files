/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public interface FragmentTransformer {
    public boolean handles(StartElement var1, ConversionContext var2);

    public Streamable transform(XMLEventReader var1, FragmentTransformer var2, ConversionContext var3) throws XhtmlException;
}

