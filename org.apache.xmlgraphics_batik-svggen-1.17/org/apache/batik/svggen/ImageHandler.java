/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Element;

public interface ImageHandler
extends SVGSyntax {
    public void handleImage(Image var1, Element var2, SVGGeneratorContext var3);

    public void handleImage(RenderedImage var1, Element var2, SVGGeneratorContext var3);

    public void handleImage(RenderableImage var1, Element var2, SVGGeneratorContext var3);
}

