/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.ColorMatrixRable
 *  org.apache.batik.ext.awt.image.renderable.ColorMatrixRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable;
import org.apache.batik.ext.awt.image.renderable.ColorMatrixRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeColorMatrixElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feColorMatrix";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        ColorMatrixRable colorMatrix;
        Filter in = SVGFeColorMatrixElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        int type = SVGFeColorMatrixElementBridge.convertType(filterElement, ctx);
        switch (type) {
            case 2: {
                float a = SVGFeColorMatrixElementBridge.convertValuesToHueRotate(filterElement, ctx);
                colorMatrix = ColorMatrixRable8Bit.buildHueRotate((float)a);
                break;
            }
            case 3: {
                colorMatrix = ColorMatrixRable8Bit.buildLuminanceToAlpha();
                break;
            }
            case 0: {
                float[][] matrix = SVGFeColorMatrixElementBridge.convertValuesToMatrix(filterElement, ctx);
                colorMatrix = ColorMatrixRable8Bit.buildMatrix((float[][])matrix);
                break;
            }
            case 1: {
                float s = SVGFeColorMatrixElementBridge.convertValuesToSaturate(filterElement, ctx);
                colorMatrix = ColorMatrixRable8Bit.buildSaturate((float)s);
                break;
            }
            default: {
                throw new RuntimeException("invalid convertType:" + type);
            }
        }
        colorMatrix.setSource(in);
        SVGFeColorMatrixElementBridge.handleColorInterpolationFilters((Filter)colorMatrix, filterElement);
        PadRable8Bit filter = new PadRable8Bit((Filter)colorMatrix, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeColorMatrixElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static float[][] convertValuesToMatrix(Element filterElement, BridgeContext ctx) {
        int n;
        String s = filterElement.getAttributeNS(null, "values");
        float[][] matrix = new float[4][5];
        if (s.length() == 0) {
            matrix[0][0] = 1.0f;
            matrix[1][1] = 1.0f;
            matrix[2][2] = 1.0f;
            matrix[3][3] = 1.0f;
            return matrix;
        }
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            for (n = 0; n < 20 && tokens.hasMoreTokens(); ++n) {
                matrix[n / 5][n % 5] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"values", s, nfEx});
        }
        if (n != 20 || tokens.hasMoreTokens()) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"values", s});
        }
        for (int i = 0; i < 4; ++i) {
            float[] fArray = matrix[i];
            fArray[4] = fArray[4] * 255.0f;
        }
        return matrix;
    }

    protected static float convertValuesToSaturate(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "values");
        if (s.length() == 0) {
            return 1.0f;
        }
        try {
            return SVGUtilities.convertSVGNumber(s);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"values", s});
        }
    }

    protected static float convertValuesToHueRotate(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "values");
        if (s.length() == 0) {
            return 0.0f;
        }
        try {
            return (float)Math.toRadians(SVGUtilities.convertSVGNumber(s));
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"values", s});
        }
    }

    protected static int convertType(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "type");
        if (s.length() == 0) {
            return 0;
        }
        if ("hueRotate".equals(s)) {
            return 2;
        }
        if ("luminanceToAlpha".equals(s)) {
            return 3;
        }
        if ("matrix".equals(s)) {
            return 0;
        }
        if ("saturate".equals(s)) {
            return 1;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"type", s});
    }
}

