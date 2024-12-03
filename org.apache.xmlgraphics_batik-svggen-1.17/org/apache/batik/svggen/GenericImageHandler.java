/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.w3c.dom.Element;

public interface GenericImageHandler {
    public void setDOMTreeManager(DOMTreeManager var1);

    public Element createElement(SVGGeneratorContext var1);

    public AffineTransform handleImage(Image var1, Element var2, int var3, int var4, int var5, int var6, SVGGeneratorContext var7);

    public AffineTransform handleImage(RenderedImage var1, Element var2, int var3, int var4, int var5, int var6, SVGGeneratorContext var7);

    public AffineTransform handleImage(RenderableImage var1, Element var2, double var3, double var5, double var7, double var9, SVGGeneratorContext var11);
}

