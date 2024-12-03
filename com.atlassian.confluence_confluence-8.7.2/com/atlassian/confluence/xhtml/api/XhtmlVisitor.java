/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import javax.xml.stream.events.XMLEvent;

public interface XhtmlVisitor {
    public boolean handle(XMLEvent var1, ConversionContext var2);
}

