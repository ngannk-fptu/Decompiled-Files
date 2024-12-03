/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.svggen.AbstractSVGFilterConverter;
import org.apache.batik.svggen.SVGConvolveOp;
import org.apache.batik.svggen.SVGCustomBufferedImageOp;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGLookupOp;
import org.apache.batik.svggen.SVGRescaleOp;

public class SVGBufferedImageOp
extends AbstractSVGFilterConverter {
    private SVGLookupOp svgLookupOp;
    private SVGRescaleOp svgRescaleOp;
    private SVGConvolveOp svgConvolveOp;
    private SVGCustomBufferedImageOp svgCustomBufferedImageOp;

    public SVGBufferedImageOp(SVGGeneratorContext generatorContext) {
        super(generatorContext);
        this.svgLookupOp = new SVGLookupOp(generatorContext);
        this.svgRescaleOp = new SVGRescaleOp(generatorContext);
        this.svgConvolveOp = new SVGConvolveOp(generatorContext);
        this.svgCustomBufferedImageOp = new SVGCustomBufferedImageOp(generatorContext);
    }

    @Override
    public List getDefinitionSet() {
        LinkedList filterSet = new LinkedList(this.svgLookupOp.getDefinitionSet());
        filterSet.addAll(this.svgRescaleOp.getDefinitionSet());
        filterSet.addAll(this.svgConvolveOp.getDefinitionSet());
        filterSet.addAll(this.svgCustomBufferedImageOp.getDefinitionSet());
        return filterSet;
    }

    public SVGLookupOp getLookupOpConverter() {
        return this.svgLookupOp;
    }

    public SVGRescaleOp getRescaleOpConverter() {
        return this.svgRescaleOp;
    }

    public SVGConvolveOp getConvolveOpConverter() {
        return this.svgConvolveOp;
    }

    public SVGCustomBufferedImageOp getCustomBufferedImageOpConverter() {
        return this.svgCustomBufferedImageOp;
    }

    @Override
    public SVGFilterDescriptor toSVG(BufferedImageOp op, Rectangle filterRect) {
        SVGFilterDescriptor filterDesc = this.svgCustomBufferedImageOp.toSVG(op, filterRect);
        if (filterDesc == null) {
            if (op instanceof LookupOp) {
                filterDesc = this.svgLookupOp.toSVG(op, filterRect);
            } else if (op instanceof RescaleOp) {
                filterDesc = this.svgRescaleOp.toSVG(op, filterRect);
            } else if (op instanceof ConvolveOp) {
                filterDesc = this.svgConvolveOp.toSVG(op, filterRect);
            }
        }
        return filterDesc;
    }
}

