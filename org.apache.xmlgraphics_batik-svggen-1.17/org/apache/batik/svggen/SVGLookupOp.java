/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.util.Arrays;
import org.apache.batik.svggen.AbstractSVGFilterConverter;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGLookupOp
extends AbstractSVGFilterConverter {
    private static final double GAMMA = 0.4166666666666667;
    private static final int[] linearToSRGBLut = new int[256];
    private static final int[] sRGBToLinear = new int[256];

    public SVGLookupOp(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGFilterDescriptor toSVG(BufferedImageOp filter, Rectangle filterRect) {
        if (filter instanceof LookupOp) {
            return this.toSVG((LookupOp)filter);
        }
        return null;
    }

    public SVGFilterDescriptor toSVG(LookupOp lookupOp) {
        SVGFilterDescriptor filterDesc = (SVGFilterDescriptor)this.descMap.get(lookupOp);
        Document domFactory = this.generatorContext.domFactory;
        if (filterDesc == null) {
            Element filterDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
            Element feComponentTransferDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "feComponentTransfer");
            String[] lookupTables = this.convertLookupTables(lookupOp);
            Element feFuncR = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncR");
            Element feFuncG = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncG");
            Element feFuncB = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncB");
            Element feFuncA = null;
            String type = "table";
            if (lookupTables.length == 1) {
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "tableValues", lookupTables[0]);
                feFuncG.setAttributeNS(null, "tableValues", lookupTables[0]);
                feFuncB.setAttributeNS(null, "tableValues", lookupTables[0]);
            } else if (lookupTables.length >= 3) {
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "tableValues", lookupTables[0]);
                feFuncG.setAttributeNS(null, "tableValues", lookupTables[1]);
                feFuncB.setAttributeNS(null, "tableValues", lookupTables[2]);
                if (lookupTables.length == 4) {
                    feFuncA = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncA");
                    feFuncA.setAttributeNS(null, "type", type);
                    feFuncA.setAttributeNS(null, "tableValues", lookupTables[3]);
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
            this.descMap.put(lookupOp, filterDesc);
        }
        return filterDesc;
    }

    private String[] convertLookupTables(LookupOp lookupOp) {
        int j;
        int i;
        int offset;
        Object[] dest;
        Object[] src;
        LookupTable lookupTable = lookupOp.getTable();
        int nComponents = lookupTable.getNumComponents();
        if (nComponents != 1 && nComponents != 3 && nComponents != 4) {
            throw new SVGGraphics2DRuntimeException("BufferedImage LookupOp should have 1, 3 or 4 lookup arrays");
        }
        StringBuffer[] lookupTableBuf = new StringBuffer[nComponents];
        for (int i2 = 0; i2 < nComponents; ++i2) {
            lookupTableBuf[i2] = new StringBuffer();
        }
        if (!(lookupTable instanceof ByteLookupTable)) {
            src = new int[nComponents];
            dest = new int[nComponents];
            offset = lookupTable.getOffset();
            for (i = 0; i < offset; ++i) {
                for (j = 0; j < nComponents; ++j) {
                    lookupTableBuf[j].append(this.doubleString((double)i / 255.0)).append(" ");
                }
            }
            for (i = offset; i <= 255; ++i) {
                Arrays.fill(src, i);
                lookupTable.lookupPixel((int[])src, (int[])dest);
                for (j = 0; j < nComponents; ++j) {
                    lookupTableBuf[j].append(this.doubleString((double)dest[j] / 255.0)).append(" ");
                }
            }
        } else {
            src = new byte[nComponents];
            dest = new byte[nComponents];
            offset = lookupTable.getOffset();
            for (i = 0; i < offset; ++i) {
                for (j = 0; j < nComponents; ++j) {
                    lookupTableBuf[j].append(this.doubleString((double)i / 255.0)).append(" ");
                }
            }
            for (i = 0; i <= 255; ++i) {
                Arrays.fill((byte[])src, (byte)(0xFF & i));
                ((ByteLookupTable)lookupTable).lookupPixel((byte[])src, (byte[])dest);
                for (j = 0; j < nComponents; ++j) {
                    lookupTableBuf[j].append(this.doubleString((double)(0xFF & dest[j]) / 255.0)).append(" ");
                }
            }
        }
        String[] lookupTables = new String[nComponents];
        for (int i3 = 0; i3 < nComponents; ++i3) {
            lookupTables[i3] = lookupTableBuf[i3].toString().trim();
        }
        return lookupTables;
    }

    static {
        for (int i = 0; i < 256; ++i) {
            float value = (float)i / 255.0f;
            value = (double)value <= 0.0031308 ? (value *= 12.92f) : 1.055f * (float)Math.pow(value, 0.4166666666666667) - 0.055f;
            SVGLookupOp.linearToSRGBLut[i] = Math.round(value * 255.0f);
            value = (float)i / 255.0f;
            value = (double)value <= 0.04045 ? (value /= 12.92f) : (float)Math.pow((value + 0.055f) / 1.055f, 2.4);
            SVGLookupOp.sRGBToLinear[i] = Math.round(value * 255.0f);
        }
    }
}

