/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import java.io.Reader;

public interface Transformer {
    public String transform(Reader var1, ConversionContext var2) throws XhtmlException;
}

