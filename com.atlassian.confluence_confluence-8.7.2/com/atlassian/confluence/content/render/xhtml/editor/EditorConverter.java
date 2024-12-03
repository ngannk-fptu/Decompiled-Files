/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;

public interface EditorConverter {
    public String convert(String var1, ConversionContext var2) throws XhtmlParsingException, XhtmlException;
}

