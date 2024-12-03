/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;

public interface CssContext {
    public float getMmPerDot();

    public int getDotsPerPixel();

    public float getFontSize2D(FontSpecification var1);

    public float getXHeight(FontSpecification var1);

    public FSFont getFont(FontSpecification var1);

    public StyleReference getCss();

    public FSFontMetrics getFSFontMetrics(FSFont var1);
}

