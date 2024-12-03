/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.css.engine.value.svg.ICCColor
 *  org.apache.batik.css.engine.value.svg12.CIELabColor
 *  org.apache.batik.css.engine.value.svg12.DeviceColor
 *  org.apache.batik.css.engine.value.svg12.ICCNamedColor
 *  org.apache.batik.gvt.CompositeShapePainter
 *  org.apache.batik.gvt.FillShapePainter
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.Marker
 *  org.apache.batik.gvt.MarkerShapePainter
 *  org.apache.batik.gvt.ShapeNode
 *  org.apache.batik.gvt.ShapePainter
 *  org.apache.batik.gvt.StrokeShapePainter
 *  org.apache.batik.util.CSSConstants
 *  org.apache.batik.util.SVGConstants
 *  org.apache.xmlgraphics.java2d.color.CIELabColorSpace
 *  org.apache.xmlgraphics.java2d.color.ColorSpaces
 *  org.apache.xmlgraphics.java2d.color.ColorWithAlternatives
 *  org.apache.xmlgraphics.java2d.color.DeviceCMYKColorSpace
 *  org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent
 *  org.apache.xmlgraphics.java2d.color.NamedColorSpace
 *  org.apache.xmlgraphics.java2d.color.profile.NamedColorProfile
 *  org.apache.xmlgraphics.java2d.color.profile.NamedColorProfileParser
 */
package org.apache.batik.bridge;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.MarkerBridge;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.SVGColorProfileElementBridge;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ICCColor;
import org.apache.batik.css.engine.value.svg12.CIELabColor;
import org.apache.batik.css.engine.value.svg12.DeviceColor;
import org.apache.batik.css.engine.value.svg12.ICCNamedColor;
import org.apache.batik.gvt.CompositeShapePainter;
import org.apache.batik.gvt.FillShapePainter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Marker;
import org.apache.batik.gvt.MarkerShapePainter;
import org.apache.batik.gvt.ShapeNode;
import org.apache.batik.gvt.ShapePainter;
import org.apache.batik.gvt.StrokeShapePainter;
import org.apache.batik.util.CSSConstants;
import org.apache.batik.util.SVGConstants;
import org.apache.xmlgraphics.java2d.color.CIELabColorSpace;
import org.apache.xmlgraphics.java2d.color.ColorSpaces;
import org.apache.xmlgraphics.java2d.color.ColorWithAlternatives;
import org.apache.xmlgraphics.java2d.color.DeviceCMYKColorSpace;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.xmlgraphics.java2d.color.NamedColorSpace;
import org.apache.xmlgraphics.java2d.color.profile.NamedColorProfile;
import org.apache.xmlgraphics.java2d.color.profile.NamedColorProfileParser;
import org.w3c.dom.Element;

public abstract class PaintServer
implements SVGConstants,
CSSConstants,
ErrorConstants {
    protected PaintServer() {
    }

    public static ShapePainter convertMarkers(Element e, ShapeNode node, BridgeContext ctx) {
        Value v = CSSUtilities.getComputedStyle(e, 36);
        Marker startMarker = PaintServer.convertMarker(e, v, ctx);
        v = CSSUtilities.getComputedStyle(e, 35);
        Marker midMarker = PaintServer.convertMarker(e, v, ctx);
        v = CSSUtilities.getComputedStyle(e, 34);
        Marker endMarker = PaintServer.convertMarker(e, v, ctx);
        if (startMarker != null || midMarker != null || endMarker != null) {
            MarkerShapePainter p = new MarkerShapePainter(node.getShape());
            p.setStartMarker(startMarker);
            p.setMiddleMarker(midMarker);
            p.setEndMarker(endMarker);
            return p;
        }
        return null;
    }

    public static Marker convertMarker(Element e, Value v, BridgeContext ctx) {
        if (v.getPrimitiveType() == 21) {
            return null;
        }
        String uri = v.getStringValue();
        Element markerElement = ctx.getReferencedElement(e, uri);
        Bridge bridge = ctx.getBridge(markerElement);
        if (bridge == null || !(bridge instanceof MarkerBridge)) {
            throw new BridgeException(ctx, e, "css.uri.badTarget", new Object[]{uri});
        }
        return ((MarkerBridge)bridge).createMarker(ctx, markerElement, e);
    }

    public static ShapePainter convertFillAndStroke(Element e, ShapeNode node, BridgeContext ctx) {
        Shape shape = node.getShape();
        if (shape == null) {
            return null;
        }
        Paint fillPaint = PaintServer.convertFillPaint(e, (GraphicsNode)node, ctx);
        FillShapePainter fp = new FillShapePainter(shape);
        fp.setPaint(fillPaint);
        Stroke stroke = PaintServer.convertStroke(e);
        if (stroke == null) {
            return fp;
        }
        Paint strokePaint = PaintServer.convertStrokePaint(e, (GraphicsNode)node, ctx);
        StrokeShapePainter sp = new StrokeShapePainter(shape);
        sp.setStroke(stroke);
        sp.setPaint(strokePaint);
        CompositeShapePainter cp = new CompositeShapePainter(shape);
        cp.addShapePainter((ShapePainter)fp);
        cp.addShapePainter((ShapePainter)sp);
        return cp;
    }

    public static ShapePainter convertStrokePainter(Element e, ShapeNode node, BridgeContext ctx) {
        Shape shape = node.getShape();
        if (shape == null) {
            return null;
        }
        Stroke stroke = PaintServer.convertStroke(e);
        if (stroke == null) {
            return null;
        }
        Paint strokePaint = PaintServer.convertStrokePaint(e, (GraphicsNode)node, ctx);
        StrokeShapePainter sp = new StrokeShapePainter(shape);
        sp.setStroke(stroke);
        sp.setPaint(strokePaint);
        return sp;
    }

    public static Paint convertStrokePaint(Element strokedElement, GraphicsNode strokedNode, BridgeContext ctx) {
        Value v = CSSUtilities.getComputedStyle(strokedElement, 51);
        float opacity = PaintServer.convertOpacity(v);
        v = CSSUtilities.getComputedStyle(strokedElement, 45);
        return PaintServer.convertPaint(strokedElement, strokedNode, v, opacity, ctx);
    }

    public static Paint convertFillPaint(Element filledElement, GraphicsNode filledNode, BridgeContext ctx) {
        Value v = CSSUtilities.getComputedStyle(filledElement, 16);
        float opacity = PaintServer.convertOpacity(v);
        v = CSSUtilities.getComputedStyle(filledElement, 15);
        return PaintServer.convertPaint(filledElement, filledNode, v, opacity, ctx);
    }

    public static Paint convertPaint(Element paintedElement, GraphicsNode paintedNode, Value paintDef, float opacity, BridgeContext ctx) {
        if (paintDef.getCssValueType() == 1) {
            switch (paintDef.getPrimitiveType()) {
                case 21: {
                    return null;
                }
                case 25: {
                    return PaintServer.convertColor(paintDef, opacity);
                }
                case 20: {
                    return PaintServer.convertURIPaint(paintedElement, paintedNode, paintDef, opacity, ctx);
                }
            }
            throw new IllegalArgumentException("Paint argument is not an appropriate CSS value");
        }
        Value v = paintDef.item(0);
        switch (v.getPrimitiveType()) {
            case 25: {
                return PaintServer.convertRGBICCColor(paintedElement, v, paintDef.item(1), opacity, ctx);
            }
            case 20: {
                Paint result = PaintServer.silentConvertURIPaint(paintedElement, paintedNode, v, opacity, ctx);
                if (result != null) {
                    return result;
                }
                v = paintDef.item(1);
                switch (v.getPrimitiveType()) {
                    case 21: {
                        return null;
                    }
                    case 25: {
                        if (paintDef.getLength() == 2) {
                            return PaintServer.convertColor(v, opacity);
                        }
                        return PaintServer.convertRGBICCColor(paintedElement, v, paintDef.item(2), opacity, ctx);
                    }
                }
                throw new IllegalArgumentException("Paint argument is not an appropriate CSS value");
            }
        }
        throw new IllegalArgumentException("Paint argument is not an appropriate CSS value");
    }

    public static Paint silentConvertURIPaint(Element paintedElement, GraphicsNode paintedNode, Value paintDef, float opacity, BridgeContext ctx) {
        Paint paint = null;
        try {
            paint = PaintServer.convertURIPaint(paintedElement, paintedNode, paintDef, opacity, ctx);
        }
        catch (BridgeException bridgeException) {
            // empty catch block
        }
        return paint;
    }

    public static Paint convertURIPaint(Element paintedElement, GraphicsNode paintedNode, Value paintDef, float opacity, BridgeContext ctx) {
        String uri = paintDef.getStringValue();
        Element paintElement = ctx.getReferencedElement(paintedElement, uri);
        Bridge bridge = ctx.getBridge(paintElement);
        if (bridge == null || !(bridge instanceof PaintBridge)) {
            throw new BridgeException(ctx, paintedElement, "css.uri.badTarget", new Object[]{uri});
        }
        return ((PaintBridge)bridge).createPaint(ctx, paintElement, paintedElement, paintedNode, opacity);
    }

    public static Color convertRGBICCColor(Element paintedElement, Value colorDef, Value iccColor, float opacity, BridgeContext ctx) {
        Color color = null;
        if (iccColor != null) {
            if (iccColor instanceof ICCColor) {
                color = PaintServer.convertICCColor(paintedElement, (ICCColor)iccColor, opacity, ctx);
            } else if (iccColor instanceof ICCNamedColor) {
                color = PaintServer.convertICCNamedColor(paintedElement, (ICCNamedColor)iccColor, opacity, ctx);
            } else if (iccColor instanceof CIELabColor) {
                color = PaintServer.convertCIELabColor(paintedElement, (CIELabColor)iccColor, opacity, ctx);
            } else if (iccColor instanceof DeviceColor) {
                color = PaintServer.convertDeviceColor(paintedElement, colorDef, (DeviceColor)iccColor, opacity, ctx);
            }
        }
        if (color == null) {
            color = PaintServer.convertColor(colorDef, opacity);
        }
        return color;
    }

    public static Color convertICCColor(Element e, ICCColor c, float opacity, BridgeContext ctx) {
        String iccProfileName = c.getColorProfile();
        if (iccProfileName == null) {
            return null;
        }
        SVGColorProfileElementBridge profileBridge = (SVGColorProfileElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "color-profile");
        if (profileBridge == null) {
            return null;
        }
        ICCColorSpaceWithIntent profileCS = profileBridge.createICCColorSpaceWithIntent(ctx, e, iccProfileName);
        if (profileCS == null) {
            return null;
        }
        int n = c.getNumberOfColors();
        float[] colorValue = new float[n];
        if (n == 0) {
            return null;
        }
        for (int i = 0; i < n; ++i) {
            colorValue[i] = c.getColor(i);
        }
        float[] rgb = profileCS.intendedToRGB(colorValue);
        return new Color(rgb[0], rgb[1], rgb[2], opacity);
    }

    public static Color convertICCNamedColor(Element e, ICCNamedColor c, float opacity, BridgeContext ctx) {
        String iccProfileName = c.getColorProfile();
        if (iccProfileName == null) {
            return null;
        }
        SVGColorProfileElementBridge profileBridge = (SVGColorProfileElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "color-profile");
        if (profileBridge == null) {
            return null;
        }
        ICCColorSpaceWithIntent profileCS = profileBridge.createICCColorSpaceWithIntent(ctx, e, iccProfileName);
        if (profileCS == null) {
            return null;
        }
        ICC_Profile iccProfile = profileCS.getProfile();
        String iccProfileSrc = null;
        if (NamedColorProfileParser.isNamedColorProfile((ICC_Profile)iccProfile)) {
            NamedColorProfile ncp;
            NamedColorProfileParser parser = new NamedColorProfileParser();
            try {
                ncp = parser.parseProfile(iccProfile, iccProfileName, iccProfileSrc);
            }
            catch (IOException ioe) {
                return null;
            }
            NamedColorSpace ncs = ncp.getNamedColor(c.getColorName());
            if (ncs != null) {
                ColorWithAlternatives specColor = new ColorWithAlternatives((ColorSpace)ncs, new float[]{1.0f}, opacity, null);
                return specColor;
            }
        }
        return null;
    }

    public static Color convertCIELabColor(Element e, CIELabColor c, float opacity, BridgeContext ctx) {
        CIELabColorSpace cs = new CIELabColorSpace(c.getWhitePoint());
        float[] lab = c.getColorValues();
        Color specColor = cs.toColor(lab[0], lab[1], lab[2], opacity);
        return specColor;
    }

    public static Color convertDeviceColor(Element e, Value srgb, DeviceColor c, float opacity, BridgeContext ctx) {
        int r = PaintServer.resolveColorComponent(srgb.getRed());
        int g = PaintServer.resolveColorComponent(srgb.getGreen());
        int b = PaintServer.resolveColorComponent(srgb.getBlue());
        if (c.isNChannel()) {
            return PaintServer.convertColor(srgb, opacity);
        }
        if (c.getNumberOfColors() == 4) {
            DeviceCMYKColorSpace cmykCs = ColorSpaces.getDeviceCMYKColorSpace();
            float[] comps = new float[4];
            for (int i = 0; i < 4; ++i) {
                comps[i] = c.getColor(i);
            }
            ColorWithAlternatives cmyk = new ColorWithAlternatives((ColorSpace)cmykCs, comps, opacity, null);
            ColorWithAlternatives specColor = new ColorWithAlternatives(r, g, b, Math.round(opacity * 255.0f), new Color[]{cmyk});
            return specColor;
        }
        return PaintServer.convertColor(srgb, opacity);
    }

    public static Color convertColor(Value c, float opacity) {
        int r = PaintServer.resolveColorComponent(c.getRed());
        int g = PaintServer.resolveColorComponent(c.getGreen());
        int b = PaintServer.resolveColorComponent(c.getBlue());
        return new Color(r, g, b, Math.round(opacity * 255.0f));
    }

    public static Stroke convertStroke(Element e) {
        Value v = CSSUtilities.getComputedStyle(e, 52);
        float width = v.getFloatValue();
        if (width == 0.0f) {
            return null;
        }
        v = CSSUtilities.getComputedStyle(e, 48);
        int linecap = PaintServer.convertStrokeLinecap(v);
        v = CSSUtilities.getComputedStyle(e, 49);
        int linejoin = PaintServer.convertStrokeLinejoin(v);
        v = CSSUtilities.getComputedStyle(e, 50);
        float miterlimit = PaintServer.convertStrokeMiterlimit(v);
        v = CSSUtilities.getComputedStyle(e, 46);
        float[] dasharray = PaintServer.convertStrokeDasharray(v);
        float dashoffset = 0.0f;
        if (dasharray != null && (dashoffset = (v = CSSUtilities.getComputedStyle(e, 47)).getFloatValue()) < 0.0f) {
            float dashpatternlength = 0.0f;
            for (float aDasharray : dasharray) {
                dashpatternlength += aDasharray;
            }
            if (dasharray.length % 2 != 0) {
                dashpatternlength *= 2.0f;
            }
            if (dashpatternlength == 0.0f) {
                dashoffset = 0.0f;
            } else {
                while (dashoffset < 0.0f) {
                    dashoffset += dashpatternlength;
                }
            }
        }
        return new BasicStroke(width, linecap, linejoin, miterlimit, dasharray, dashoffset);
    }

    public static float[] convertStrokeDasharray(Value v) {
        float[] dasharray = null;
        if (v.getCssValueType() == 2) {
            int length = v.getLength();
            dasharray = new float[length];
            float sum = 0.0f;
            for (int i = 0; i < dasharray.length; ++i) {
                dasharray[i] = v.item(i).getFloatValue();
                sum += dasharray[i];
            }
            if (sum == 0.0f) {
                dasharray = null;
            }
        }
        return dasharray;
    }

    public static float convertStrokeMiterlimit(Value v) {
        float miterlimit = v.getFloatValue();
        return miterlimit < 1.0f ? 1.0f : miterlimit;
    }

    public static int convertStrokeLinecap(Value v) {
        String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'b': {
                return 0;
            }
            case 'r': {
                return 1;
            }
            case 's': {
                return 2;
            }
        }
        throw new IllegalArgumentException("Linecap argument is not an appropriate CSS value");
    }

    public static int convertStrokeLinejoin(Value v) {
        String s = v.getStringValue();
        switch (s.charAt(0)) {
            case 'm': {
                return 0;
            }
            case 'r': {
                return 1;
            }
            case 'b': {
                return 2;
            }
        }
        throw new IllegalArgumentException("Linejoin argument is not an appropriate CSS value");
    }

    public static int resolveColorComponent(Value v) {
        switch (v.getPrimitiveType()) {
            case 2: {
                float f = v.getFloatValue();
                f = f > 100.0f ? 100.0f : (f < 0.0f ? 0.0f : f);
                return Math.round(255.0f * f / 100.0f);
            }
            case 1: {
                float f = v.getFloatValue();
                f = f > 255.0f ? 255.0f : (f < 0.0f ? 0.0f : f);
                return Math.round(f);
            }
        }
        throw new IllegalArgumentException("Color component argument is not an appropriate CSS value");
    }

    public static float convertOpacity(Value v) {
        float r = v.getFloatValue();
        return r < 0.0f ? 0.0f : (r > 1.0f ? 1.0f : r);
    }
}

