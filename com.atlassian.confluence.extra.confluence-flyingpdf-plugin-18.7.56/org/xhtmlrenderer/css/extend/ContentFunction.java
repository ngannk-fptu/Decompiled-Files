/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.extend;

import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.RenderingContext;

public interface ContentFunction {
    public boolean isStatic();

    public String calculate(LayoutContext var1, FSFunction var2);

    public String calculate(RenderingContext var1, FSFunction var2, InlineText var3);

    public String getLayoutReplacementText();

    public boolean canHandle(LayoutContext var1, FSFunction var2);
}

