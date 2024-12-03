/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

public interface Unmarshaller<T> {
    public T unmarshal(XMLEventReader var1, FragmentTransformer var2, ConversionContext var3) throws XhtmlException;

    public boolean handles(StartElement var1, ConversionContext var2);
}

