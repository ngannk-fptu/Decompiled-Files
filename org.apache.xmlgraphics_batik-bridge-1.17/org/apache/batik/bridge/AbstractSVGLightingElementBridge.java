/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.DistantLight
 *  org.apache.batik.ext.awt.image.Light
 *  org.apache.batik.ext.awt.image.PointLight
 *  org.apache.batik.ext.awt.image.SpotLight
 */
package org.apache.batik.bridge;

import java.awt.Color;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.DistantLight;
import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.PointLight;
import org.apache.batik.ext.awt.image.SpotLight;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractSVGLightingElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    protected AbstractSVGLightingElementBridge() {
    }

    protected static Light extractLight(Element filterElement, BridgeContext ctx) {
        Color color = CSSUtilities.convertLightingColor(filterElement, ctx);
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element e;
            Bridge bridge;
            if (n.getNodeType() != 1 || (bridge = ctx.getBridge(e = (Element)n)) == null || !(bridge instanceof AbstractSVGLightElementBridge)) continue;
            return ((AbstractSVGLightElementBridge)bridge).createLight(ctx, filterElement, e, color);
        }
        return null;
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

    public static class SVGFePointLightElementBridge
    extends AbstractSVGLightElementBridge {
        @Override
        public String getLocalName() {
            return "fePointLight";
        }

        @Override
        public Light createLight(BridgeContext ctx, Element filterElement, Element lightElement, Color color) {
            double x = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "x", 0.0f, ctx);
            double y = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "y", 0.0f, ctx);
            double z = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "z", 0.0f, ctx);
            return new PointLight(x, y, z, color);
        }
    }

    public static class SVGFeDistantLightElementBridge
    extends AbstractSVGLightElementBridge {
        @Override
        public String getLocalName() {
            return "feDistantLight";
        }

        @Override
        public Light createLight(BridgeContext ctx, Element filterElement, Element lightElement, Color color) {
            double azimuth = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "azimuth", 0.0f, ctx);
            double elevation = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "elevation", 0.0f, ctx);
            return new DistantLight(azimuth, elevation, color);
        }
    }

    public static class SVGFeSpotLightElementBridge
    extends AbstractSVGLightElementBridge {
        @Override
        public String getLocalName() {
            return "feSpotLight";
        }

        @Override
        public Light createLight(BridgeContext ctx, Element filterElement, Element lightElement, Color color) {
            double x = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "x", 0.0f, ctx);
            double y = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "y", 0.0f, ctx);
            double z = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "z", 0.0f, ctx);
            double px = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "pointsAtX", 0.0f, ctx);
            double py = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "pointsAtY", 0.0f, ctx);
            double pz = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "pointsAtZ", 0.0f, ctx);
            double specularExponent = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "specularExponent", 1.0f, ctx);
            double limitingConeAngle = AbstractSVGFilterPrimitiveElementBridge.convertNumber(lightElement, "limitingConeAngle", 90.0f, ctx);
            return new SpotLight(x, y, z, px, py, pz, specularExponent, limitingConeAngle, color);
        }
    }

    protected static abstract class AbstractSVGLightElementBridge
    extends AnimatableGenericSVGBridge {
        protected AbstractSVGLightElementBridge() {
        }

        public abstract Light createLight(BridgeContext var1, Element var2, Element var3, Color var4);
    }
}

