/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.TextDecoration;

public interface OutputDevice {
    public void drawText(RenderingContext var1, InlineText var2);

    public void drawSelection(RenderingContext var1, InlineText var2);

    public void drawTextDecoration(RenderingContext var1, LineBox var2);

    public void drawTextDecoration(RenderingContext var1, InlineLayoutBox var2, TextDecoration var3);

    public void paintBorder(RenderingContext var1, Box var2);

    public void paintBorder(RenderingContext var1, CalculatedStyle var2, Rectangle var3, int var4);

    public void paintCollapsedBorder(RenderingContext var1, BorderPropertySet var2, Rectangle var3, int var4);

    public void paintBackground(RenderingContext var1, Box var2);

    public void paintBackground(RenderingContext var1, CalculatedStyle var2, Rectangle var3, Rectangle var4, BorderPropertySet var5);

    public void paintReplacedElement(RenderingContext var1, BlockBox var2);

    public void drawDebugOutline(RenderingContext var1, Box var2, FSColor var3);

    public void setFont(FSFont var1);

    public void setColor(FSColor var1);

    public void drawRect(int var1, int var2, int var3, int var4);

    public void drawOval(int var1, int var2, int var3, int var4);

    public void drawBorderLine(Shape var1, int var2, int var3, boolean var4);

    public void drawImage(FSImage var1, int var2, int var3);

    public void draw(Shape var1);

    public void fill(Shape var1);

    public void fillRect(int var1, int var2, int var3, int var4);

    public void fillOval(int var1, int var2, int var3, int var4);

    public void clip(Shape var1);

    public Shape getClip();

    public void setClip(Shape var1);

    public void translate(double var1, double var3);

    public void setStroke(Stroke var1);

    public Stroke getStroke();

    public Object getRenderingHint(RenderingHints.Key var1);

    public void setRenderingHint(RenderingHints.Key var1, Object var2);

    public boolean isSupportsSelection();

    public boolean isSupportsCMYKColors();
}

