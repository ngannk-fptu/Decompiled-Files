/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.MultipleGradientPaint
 *  org.apache.batik.ext.awt.MultipleGradientPaint$ColorSpaceEnum
 *  org.apache.batik.ext.awt.MultipleGradientPaint$CycleMethodEnum
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractSVGGradientElementBridge
extends AnimatableGenericSVGBridge
implements PaintBridge,
ErrorConstants {
    protected AbstractSVGGradientElementBridge() {
    }

    @Override
    public Paint createPaint(BridgeContext ctx, Element paintElement, Element paintedElement, GraphicsNode paintedNode, float opacity) {
        List stops = AbstractSVGGradientElementBridge.extractStop(paintElement, opacity, ctx);
        if (stops == null) {
            return null;
        }
        int stopLength = stops.size();
        if (stopLength == 1) {
            return ((Stop)stops.get((int)0)).color;
        }
        float[] offsets = new float[stopLength];
        Color[] colors = new Color[stopLength];
        Iterator iter = stops.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Stop stop = (Stop)iter.next();
            offsets[i] = stop.offset;
            colors[i] = stop.color;
            ++i;
        }
        MultipleGradientPaint.CycleMethodEnum spreadMethod = MultipleGradientPaint.NO_CYCLE;
        String s = SVGUtilities.getChainableAttributeNS(paintElement, null, "spreadMethod", ctx);
        if (s.length() != 0) {
            spreadMethod = AbstractSVGGradientElementBridge.convertSpreadMethod(paintElement, s, ctx);
        }
        MultipleGradientPaint.ColorSpaceEnum colorSpace = CSSUtilities.convertColorInterpolation(paintElement);
        s = SVGUtilities.getChainableAttributeNS(paintElement, null, "gradientTransform", ctx);
        AffineTransform transform = s.length() != 0 ? SVGUtilities.convertTransform(paintElement, "gradientTransform", s, ctx) : new AffineTransform();
        Paint paint = this.buildGradient(paintElement, paintedElement, paintedNode, spreadMethod, colorSpace, transform, colors, offsets, ctx);
        return paint;
    }

    protected abstract Paint buildGradient(Element var1, Element var2, GraphicsNode var3, MultipleGradientPaint.CycleMethodEnum var4, MultipleGradientPaint.ColorSpaceEnum var5, AffineTransform var6, Color[] var7, float[] var8, BridgeContext var9);

    protected static MultipleGradientPaint.CycleMethodEnum convertSpreadMethod(Element paintElement, String s, BridgeContext ctx) {
        if ("repeat".equals(s)) {
            return MultipleGradientPaint.REPEAT;
        }
        if ("reflect".equals(s)) {
            return MultipleGradientPaint.REFLECT;
        }
        if ("pad".equals(s)) {
            return MultipleGradientPaint.NO_CYCLE;
        }
        throw new BridgeException(ctx, paintElement, "attribute.malformed", new Object[]{"spreadMethod", s});
    }

    protected static List extractStop(Element paintElement, float opacity, BridgeContext ctx) {
        LinkedList<ParsedURL> refs = new LinkedList<ParsedURL>();
        List stops;
        while ((stops = AbstractSVGGradientElementBridge.extractLocalStop(paintElement, opacity, ctx)) == null) {
            String uri = XLinkSupport.getXLinkHref((Element)paintElement);
            if (uri.length() == 0) {
                return null;
            }
            String baseURI = paintElement.getBaseURI();
            ParsedURL purl = new ParsedURL(baseURI, uri);
            if (AbstractSVGGradientElementBridge.contains(refs, purl)) {
                throw new BridgeException(ctx, paintElement, "xlink.href.circularDependencies", new Object[]{uri});
            }
            refs.add(purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
        return stops;
    }

    protected static List extractLocalStop(Element gradientElement, float opacity, BridgeContext ctx) {
        LinkedList<Stop> stops = null;
        Stop previous = null;
        for (Node n = gradientElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element e;
            Bridge bridge;
            if (n.getNodeType() != 1 || (bridge = ctx.getBridge(e = (Element)n)) == null || !(bridge instanceof SVGStopElementBridge)) continue;
            Stop stop = ((SVGStopElementBridge)bridge).createStop(ctx, gradientElement, e, opacity);
            if (stops == null) {
                stops = new LinkedList<Stop>();
            }
            if (previous != null && stop.offset < previous.offset) {
                stop.offset = previous.offset;
            }
            stops.add(stop);
            previous = stop;
        }
        return stops;
    }

    private static boolean contains(List urls, ParsedURL key) {
        for (Object url : urls) {
            if (!key.equals(url)) continue;
            return true;
        }
        return false;
    }

    public static class SVGStopElementBridge
    extends AnimatableGenericSVGBridge
    implements Bridge {
        @Override
        public String getLocalName() {
            return "stop";
        }

        public Stop createStop(BridgeContext ctx, Element gradientElement, Element stopElement, float opacity) {
            float offset;
            String s = stopElement.getAttributeNS(null, "offset");
            if (s.length() == 0) {
                throw new BridgeException(ctx, stopElement, "attribute.missing", new Object[]{"offset"});
            }
            try {
                offset = SVGUtilities.convertRatio(s);
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, stopElement, nfEx, "attribute.malformed", new Object[]{"offset", s, nfEx});
            }
            Color color = CSSUtilities.convertStopColor(stopElement, opacity, ctx);
            return new Stop(color, offset);
        }
    }

    public static class Stop {
        public Color color;
        public float offset;

        public Stop(Color color, float offset) {
            this.color = color;
            this.offset = offset;
        }
    }
}

