/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.Composite;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.AbstractSVGConverter;
import org.apache.batik.svggen.SVGCompositeDescriptor;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.w3c.dom.Element;

public class SVGCustomComposite
extends AbstractSVGConverter {
    public SVGCustomComposite(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        return this.toSVG(gc.getComposite());
    }

    public SVGCompositeDescriptor toSVG(Composite composite) {
        SVGCompositeDescriptor desc;
        if (composite == null) {
            throw new NullPointerException();
        }
        SVGCompositeDescriptor compositeDesc = (SVGCompositeDescriptor)this.descMap.get(composite);
        if (compositeDesc == null && (desc = this.generatorContext.extensionHandler.handleComposite(composite, this.generatorContext)) != null) {
            Element def = desc.getDef();
            if (def != null) {
                this.defSet.add(def);
            }
            this.descMap.put(composite, desc);
        }
        return compositeDesc;
    }
}

