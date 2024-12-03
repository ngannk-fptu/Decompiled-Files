/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import org.apache.batik.svggen.AbstractSVGFilterConverter;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGRescaleOp
extends AbstractSVGFilterConverter {
    public SVGRescaleOp(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGFilterDescriptor toSVG(BufferedImageOp filter, Rectangle filterRect) {
        if (filter instanceof RescaleOp) {
            return this.toSVG((RescaleOp)filter);
        }
        return null;
    }

    public SVGFilterDescriptor toSVG(RescaleOp rescaleOp) {
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)this.descMap.get(rescaleOp);
        Document domFactory = this.generatorContext.domFactory;
        if (filterDesc == null) {
            float[] scaleFactors;
            Element filterDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
            Element feComponentTransferDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "feComponentTransfer");
            float[] offsets = rescaleOp.getOffsets(null);
            if (offsets.length != (scaleFactors = rescaleOp.getScaleFactors(null)).length) {
                throw new SVGGraphics2DRuntimeException("RescapeOp offsets and scaleFactor array length do not match");
            }
            if (offsets.length != 1 && offsets.length != 3 && offsets.length != 4) {
                throw new SVGGraphics2DRuntimeException("BufferedImage RescaleOp should have 1, 3 or 4 scale factors");
            }
            Element feFuncR = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncR");
            Element feFuncG = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncG");
            Element feFuncB = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncB");
            Element feFuncA = null;
            String type = "linear";
            if (offsets.length == 1) {
                String slope = this.doubleString(scaleFactors[0]);
                String intercept = this.doubleString(offsets[0]);
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "slope", slope);
                feFuncG.setAttributeNS(null, "slope", slope);
                feFuncB.setAttributeNS(null, "slope", slope);
                feFuncR.setAttributeNS(null, "intercept", intercept);
                feFuncG.setAttributeNS(null, "intercept", intercept);
                feFuncB.setAttributeNS(null, "intercept", intercept);
            } else if (offsets.length >= 3) {
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "slope", this.doubleString(scaleFactors[0]));
                feFuncG.setAttributeNS(null, "slope", this.doubleString(scaleFactors[1]));
                feFuncB.setAttributeNS(null, "slope", this.doubleString(scaleFactors[2]));
                feFuncR.setAttributeNS(null, "intercept", this.doubleString(offsets[0]));
                feFuncG.setAttributeNS(null, "intercept", this.doubleString(offsets[1]));
                feFuncB.setAttributeNS(null, "intercept", this.doubleString(offsets[2]));
                if (offsets.length == 4) {
                    feFuncA = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncA");
                    feFuncA.setAttributeNS(null, "type", type);
                    feFuncA.setAttributeNS(null, "slope", this.doubleString(scaleFactors[3]));
                    feFuncA.setAttributeNS(null, "intercept", this.doubleString(offsets[3]));
                }
            }
            feComponentTransferDef.appendChild(feFuncR);
            feComponentTransferDef.appendChild(feFuncG);
            feComponentTransferDef.appendChild(feFuncB);
            if (feFuncA != null) {
                feComponentTransferDef.appendChild(feFuncA);
            }
            filterDef.appendChild(feComponentTransferDef);
            filterDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("componentTransfer"));
            String filterAttrBuf = "url(#" + filterDef.getAttributeNS(null, "id") + ")";
            filterDesc = new SVGFilterDescriptor(filterAttrBuf, filterDef);
            this.defSet.add(filterDef);
            this.descMap.put(rescaleOp, filterDesc);
        }
        return filterDesc;
    }
}

