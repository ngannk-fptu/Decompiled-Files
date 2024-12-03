/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import org.apache.batik.svggen.SVGCompositeDescriptor;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGPaintDescriptor;

public interface ExtensionHandler {
    public SVGPaintDescriptor handlePaint(Paint var1, SVGGeneratorContext var2);

    public SVGCompositeDescriptor handleComposite(Composite var1, SVGGeneratorContext var2);

    public SVGFilterDescriptor handleFilter(BufferedImageOp var1, Rectangle var2, SVGGeneratorContext var3);
}

