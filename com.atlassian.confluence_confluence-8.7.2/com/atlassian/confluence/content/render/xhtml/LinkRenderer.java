/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;

public interface LinkRenderer {
    public String render(ContentEntityObject var1, ConversionContext var2) throws XhtmlException;

    public String render(ContentEntityObject var1, String var2, ConversionContext var3) throws XhtmlException;
}

