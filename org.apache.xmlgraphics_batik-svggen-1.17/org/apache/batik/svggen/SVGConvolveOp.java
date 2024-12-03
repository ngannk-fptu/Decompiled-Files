/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import org.apache.batik.svggen.AbstractSVGFilterConverter;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGConvolveOp
extends AbstractSVGFilterConverter {
    public SVGConvolveOp(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGFilterDescriptor toSVG(BufferedImageOp filter, Rectangle filterRect) {
        if (filter instanceof ConvolveOp) {
            return this.toSVG((ConvolveOp)filter);
        }
        return null;
    }

    public SVGFilterDescriptor toSVG(ConvolveOp convolveOp) {
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)this.descMap.get(convolveOp);
        Document domFactory = this.generatorContext.domFactory;
        if (filterDesc == null) {
            Kernel kernel = convolveOp.getKernel();
            Element filterDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
            Element feConvolveMatrixDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "feConvolveMatrix");
            feConvolveMatrixDef.setAttributeNS(null, "order", kernel.getWidth() + " " + kernel.getHeight());
            float[] data = kernel.getKernelData(null);
            StringBuffer kernelMatrixBuf = new StringBuffer(data.length * 8);
            for (float aData : data) {
                kernelMatrixBuf.append(this.doubleString(aData));
                kernelMatrixBuf.append(" ");
            }
            feConvolveMatrixDef.setAttributeNS(null, "kernelMatrix", kernelMatrixBuf.toString().trim());
            filterDef.appendChild(feConvolveMatrixDef);
            filterDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("convolve"));
            if (convolveOp.getEdgeCondition() == 1) {
                feConvolveMatrixDef.setAttributeNS(null, "edgeMode", "duplicate");
            } else {
                feConvolveMatrixDef.setAttributeNS(null, "edgeMode", "none");
            }
            StringBuffer filterAttrBuf = new StringBuffer("url(");
            filterAttrBuf.append("#");
            filterAttrBuf.append(filterDef.getAttributeNS(null, "id"));
            filterAttrBuf.append(")");
            filterDesc = new SVGFilterDescriptor(filterAttrBuf.toString(), filterDef);
            this.defSet.add(filterDef);
            this.descMap.put(convolveOp, filterDesc);
        }
        return filterDesc;
    }
}

