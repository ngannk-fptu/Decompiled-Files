/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGSyntax;

public abstract class SVGGraphicObjectConverter
implements SVGSyntax {
    protected SVGGeneratorContext generatorContext;

    public SVGGraphicObjectConverter(SVGGeneratorContext generatorContext) {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.generatorContext = generatorContext;
    }

    public final String doubleString(double value) {
        return this.generatorContext.doubleString(value);
    }
}

