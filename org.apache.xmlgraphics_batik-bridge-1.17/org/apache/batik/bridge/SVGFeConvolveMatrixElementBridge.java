/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.ConvolveMatrixRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.Kernel;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.ConvolveMatrixRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;

public class SVGFeConvolveMatrixElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feConvolveMatrix";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        int[] orderXY = SVGFeConvolveMatrixElementBridge.convertOrder(filterElement, ctx);
        float[] kernelMatrix = SVGFeConvolveMatrixElementBridge.convertKernelMatrix(filterElement, orderXY, ctx);
        float divisor = SVGFeConvolveMatrixElementBridge.convertDivisor(filterElement, kernelMatrix, ctx);
        float bias = SVGFeConvolveMatrixElementBridge.convertNumber(filterElement, "bias", 0.0f, ctx);
        int[] targetXY = SVGFeConvolveMatrixElementBridge.convertTarget(filterElement, orderXY, ctx);
        PadMode padMode = SVGFeConvolveMatrixElementBridge.convertEdgeMode(filterElement, ctx);
        double[] kernelUnitLength = SVGFeConvolveMatrixElementBridge.convertKernelUnitLength(filterElement, ctx);
        boolean preserveAlpha = SVGFeConvolveMatrixElementBridge.convertPreserveAlpha(filterElement, ctx);
        Filter in = SVGFeConvolveMatrixElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        PadRable8Bit pad = new PadRable8Bit(in, primitiveRegion, PadMode.ZERO_PAD);
        ConvolveMatrixRable8Bit convolve = new ConvolveMatrixRable8Bit((Filter)pad);
        int i = 0;
        while (i < kernelMatrix.length) {
            int n = i++;
            kernelMatrix[n] = kernelMatrix[n] / divisor;
        }
        convolve.setKernel(new Kernel(orderXY[0], orderXY[1], kernelMatrix));
        convolve.setTarget(new Point(targetXY[0], targetXY[1]));
        convolve.setBias((double)bias);
        convolve.setEdgeMode(padMode);
        convolve.setKernelUnitLength(kernelUnitLength);
        convolve.setPreserveAlpha(preserveAlpha);
        SVGFeConvolveMatrixElementBridge.handleColorInterpolationFilters((Filter)convolve, filterElement);
        PadRable8Bit filter = new PadRable8Bit((Filter)convolve, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeConvolveMatrixElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static int[] convertOrder(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "order");
        if (s.length() == 0) {
            return new int[]{3, 3};
        }
        int[] orderXY = new int[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            orderXY[0] = SVGUtilities.convertSVGInteger(tokens.nextToken());
            orderXY[1] = tokens.hasMoreTokens() ? SVGUtilities.convertSVGInteger(tokens.nextToken()) : orderXY[0];
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"order", s, nfEx});
        }
        if (tokens.hasMoreTokens() || orderXY[0] <= 0 || orderXY[1] <= 0) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"order", s});
        }
        return orderXY;
    }

    protected static float[] convertKernelMatrix(Element filterElement, int[] orderXY, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "kernelMatrix");
        if (s.length() == 0) {
            throw new BridgeException(ctx, filterElement, "attribute.missing", new Object[]{"kernelMatrix"});
        }
        int size = orderXY[0] * orderXY[1];
        float[] kernelMatrix = new float[size];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        int i = 0;
        try {
            while (tokens.hasMoreTokens() && i < size) {
                kernelMatrix[i++] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"kernelMatrix", s, nfEx});
        }
        if (i != size) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"kernelMatrix", s});
        }
        return kernelMatrix;
    }

    protected static float convertDivisor(Element filterElement, float[] kernelMatrix, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "divisor");
        if (s.length() == 0) {
            float sum = 0.0f;
            for (float aKernelMatrix : kernelMatrix) {
                sum += aKernelMatrix;
            }
            return sum == 0.0f ? 1.0f : sum;
        }
        try {
            return SVGUtilities.convertSVGNumber(s);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"divisor", s, nfEx});
        }
    }

    protected static int[] convertTarget(Element filterElement, int[] orderXY, BridgeContext ctx) {
        int v;
        int[] targetXY = new int[2];
        String s = filterElement.getAttributeNS(null, "targetX");
        if (s.length() == 0) {
            targetXY[0] = orderXY[0] / 2;
        } else {
            try {
                v = SVGUtilities.convertSVGInteger(s);
                if (v < 0 || v >= orderXY[0]) {
                    throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"targetX", s});
                }
                targetXY[0] = v;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"targetX", s, nfEx});
            }
        }
        s = filterElement.getAttributeNS(null, "targetY");
        if (s.length() == 0) {
            targetXY[1] = orderXY[1] / 2;
        } else {
            try {
                v = SVGUtilities.convertSVGInteger(s);
                if (v < 0 || v >= orderXY[1]) {
                    throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"targetY", s});
                }
                targetXY[1] = v;
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"targetY", s, nfEx});
            }
        }
        return targetXY;
    }

    protected static double[] convertKernelUnitLength(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "kernelUnitLength");
        if (s.length() == 0) {
            return null;
        }
        double[] units = new double[2];
        StringTokenizer tokens = new StringTokenizer(s, " ,");
        try {
            units[0] = SVGUtilities.convertSVGNumber(tokens.nextToken());
            units[1] = tokens.hasMoreTokens() ? (double)SVGUtilities.convertSVGNumber(tokens.nextToken()) : units[0];
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, filterElement, nfEx, "attribute.malformed", new Object[]{"kernelUnitLength", s});
        }
        if (tokens.hasMoreTokens() || units[0] <= 0.0 || units[1] <= 0.0) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"kernelUnitLength", s});
        }
        return units;
    }

    protected static PadMode convertEdgeMode(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "edgeMode");
        if (s.length() == 0) {
            return PadMode.REPLICATE;
        }
        if ("duplicate".equals(s)) {
            return PadMode.REPLICATE;
        }
        if ("wrap".equals(s)) {
            return PadMode.WRAP;
        }
        if ("none".equals(s)) {
            return PadMode.ZERO_PAD;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"edgeMode", s});
    }

    protected static boolean convertPreserveAlpha(Element filterElement, BridgeContext ctx) {
        String s = filterElement.getAttributeNS(null, "preserveAlpha");
        if (s.length() == 0) {
            return false;
        }
        if ("true".equals(s)) {
            return true;
        }
        if ("false".equals(s)) {
            return false;
        }
        throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"preserveAlpha", s});
    }
}

