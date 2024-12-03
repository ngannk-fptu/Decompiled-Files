/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.SVGAlphaComposite;
import org.apache.batik.svggen.SVGCompositeDescriptor;
import org.apache.batik.svggen.SVGConverter;
import org.apache.batik.svggen.SVGCustomComposite;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;

public class SVGComposite
implements SVGConverter {
    private SVGAlphaComposite svgAlphaComposite;
    private SVGCustomComposite svgCustomComposite;

    public SVGComposite(SVGGeneratorContext generatorContext) {
        this.svgAlphaComposite = new SVGAlphaComposite(generatorContext);
        this.svgCustomComposite = new SVGCustomComposite(generatorContext);
    }

    @Override
    public List getDefinitionSet() {
        LinkedList compositeDefs = new LinkedList(this.svgAlphaComposite.getDefinitionSet());
        compositeDefs.addAll(this.svgCustomComposite.getDefinitionSet());
        return compositeDefs;
    }

    public SVGAlphaComposite getAlphaCompositeConverter() {
        return this.svgAlphaComposite;
    }

    public SVGCustomComposite getCustomCompositeConverter() {
        return this.svgCustomComposite;
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        return this.toSVG(gc.getComposite());
    }

    public SVGCompositeDescriptor toSVG(Composite composite) {
        if (composite instanceof AlphaComposite) {
            return this.svgAlphaComposite.toSVG((AlphaComposite)composite);
        }
        return this.svgCustomComposite.toSVG(composite);
    }
}

