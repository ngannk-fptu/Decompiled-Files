/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.SVGCompositeDescriptor;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGPaintDescriptor;

public class DefaultExtensionHandler
implements ExtensionHandler {
    @Override
    public SVGPaintDescriptor handlePaint(Paint paint, SVGGeneratorContext generatorContext) {
        return null;
    }

    @Override
    public SVGCompositeDescriptor handleComposite(Composite composite, SVGGeneratorContext generatorContext) {
        return null;
    }

    @Override
    public SVGFilterDescriptor handleFilter(BufferedImageOp filter, Rectangle filterRect, SVGGeneratorContext generatorContext) {
        return null;
    }
}

