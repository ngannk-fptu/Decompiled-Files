/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 *  org.apache.batik.ext.awt.g2d.TransformStackElement
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.svggen.SVGBasicStroke;
import org.apache.batik.svggen.SVGClip;
import org.apache.batik.svggen.SVGComposite;
import org.apache.batik.svggen.SVGConverter;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGFont;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicContext;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGPaint;
import org.apache.batik.svggen.SVGRenderingHints;
import org.apache.batik.svggen.SVGTransform;

public class SVGGraphicContextConverter {
    private static final int GRAPHIC_CONTEXT_CONVERTER_COUNT = 6;
    private SVGTransform transformConverter;
    private SVGPaint paintConverter;
    private SVGBasicStroke strokeConverter;
    private SVGComposite compositeConverter;
    private SVGClip clipConverter;
    private SVGRenderingHints hintsConverter;
    private SVGFont fontConverter;
    private SVGConverter[] converters = new SVGConverter[6];

    public SVGTransform getTransformConverter() {
        return this.transformConverter;
    }

    public SVGPaint getPaintConverter() {
        return this.paintConverter;
    }

    public SVGBasicStroke getStrokeConverter() {
        return this.strokeConverter;
    }

    public SVGComposite getCompositeConverter() {
        return this.compositeConverter;
    }

    public SVGClip getClipConverter() {
        return this.clipConverter;
    }

    public SVGRenderingHints getHintsConverter() {
        return this.hintsConverter;
    }

    public SVGFont getFontConverter() {
        return this.fontConverter;
    }

    public SVGGraphicContextConverter(SVGGeneratorContext generatorContext) {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.transformConverter = new SVGTransform(generatorContext);
        this.paintConverter = new SVGPaint(generatorContext);
        this.strokeConverter = new SVGBasicStroke(generatorContext);
        this.compositeConverter = new SVGComposite(generatorContext);
        this.clipConverter = new SVGClip(generatorContext);
        this.hintsConverter = new SVGRenderingHints(generatorContext);
        this.fontConverter = new SVGFont(generatorContext);
        int i = 0;
        this.converters[i++] = this.paintConverter;
        this.converters[i++] = this.strokeConverter;
        this.converters[i++] = this.compositeConverter;
        this.converters[i++] = this.clipConverter;
        this.converters[i++] = this.hintsConverter;
        this.converters[i++] = this.fontConverter;
    }

    public String toSVG(TransformStackElement[] transformStack) {
        return this.transformConverter.toSVGTransform(transformStack);
    }

    public SVGGraphicContext toSVG(GraphicContext gc) {
        HashMap groupAttrMap = new HashMap();
        for (SVGConverter converter : this.converters) {
            SVGDescriptor desc = converter.toSVG(gc);
            if (desc == null) continue;
            desc.getAttributeMap(groupAttrMap);
        }
        return new SVGGraphicContext(groupAttrMap, gc.getTransformStack());
    }

    public List getDefinitionSet() {
        LinkedList defSet = new LinkedList();
        for (SVGConverter converter : this.converters) {
            defSet.addAll(converter.getDefinitionSet());
        }
        return defSet;
    }
}

