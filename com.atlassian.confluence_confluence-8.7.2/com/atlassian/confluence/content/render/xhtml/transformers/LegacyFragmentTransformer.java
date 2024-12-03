/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;

public abstract class LegacyFragmentTransformer
implements FragmentTransformer {
    public abstract String transformToString(XMLEventReader var1, FragmentTransformer var2, ConversionContext var3) throws XhtmlException;

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        return Streamables.from(this.transformToString(reader, mainFragmentTransformer, conversionContext));
    }
}

