/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

public abstract class ElementReplacer {
    public abstract boolean isElementNameMatch();

    public abstract String getElementNameMatch();

    public abstract boolean accept(LayoutContext var1, Element var2);

    public abstract ReplacedElement replace(LayoutContext var1, BlockBox var2, UserAgentCallback var3, int var4, int var5);

    public abstract void clear(Element var1);

    public abstract void reset();
}

