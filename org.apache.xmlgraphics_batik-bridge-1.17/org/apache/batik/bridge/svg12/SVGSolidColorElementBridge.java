/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.CSSStylableElement
 *  org.apache.batik.css.engine.StyleMap
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge.svg12;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

public class SVGSolidColorElementBridge
extends AnimatableGenericSVGBridge
implements PaintBridge {
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }

    @Override
    public String getLocalName() {
        return "solidColor";
    }

    @Override
    public Paint createPaint(BridgeContext ctx, Element paintElement, Element paintedElement, GraphicsNode paintedNode, float opacity) {
        opacity = SVGSolidColorElementBridge.extractOpacity(paintElement, opacity, ctx);
        return SVGSolidColorElementBridge.extractColor(paintElement, opacity, ctx);
    }

    protected static float extractOpacity(Element paintElement, float opacity, BridgeContext ctx) {
        HashMap<ParsedURL, ParsedURL> refs = new HashMap<ParsedURL, ParsedURL>();
        CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        int pidx = eng.getPropertyIndex("solid-opacity");
        while (true) {
            Value opacityVal = CSSUtilities.getComputedStyle(paintElement, pidx);
            StyleMap sm = ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                float attr = PaintServer.convertOpacity(opacityVal);
                return opacity * attr;
            }
            String uri = XLinkSupport.getXLinkHref((Element)paintElement);
            if (uri.length() == 0) {
                return opacity;
            }
            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            ParsedURL purl = new ParsedURL(doc.getURL(), uri);
            if (refs.containsKey(purl)) {
                throw new BridgeException(ctx, paintElement, "xlink.href.circularDependencies", new Object[]{uri});
            }
            refs.put(purl, purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }

    protected static Color extractColor(Element paintElement, float opacity, BridgeContext ctx) {
        HashMap<ParsedURL, ParsedURL> refs = new HashMap<ParsedURL, ParsedURL>();
        CSSEngine eng = CSSUtilities.getCSSEngine(paintElement);
        int pidx = eng.getPropertyIndex("solid-color");
        while (true) {
            Value colorDef = CSSUtilities.getComputedStyle(paintElement, pidx);
            StyleMap sm = ((CSSStylableElement)paintElement).getComputedStyleMap(null);
            if (!sm.isNullCascaded(pidx)) {
                if (colorDef.getCssValueType() == 1) {
                    return PaintServer.convertColor(colorDef, opacity);
                }
                return PaintServer.convertRGBICCColor(paintElement, colorDef.item(0), colorDef.item(1), opacity, ctx);
            }
            String uri = XLinkSupport.getXLinkHref((Element)paintElement);
            if (uri.length() == 0) {
                return new Color(0.0f, 0.0f, 0.0f, opacity);
            }
            SVGOMDocument doc = (SVGOMDocument)paintElement.getOwnerDocument();
            ParsedURL purl = new ParsedURL(doc.getURL(), uri);
            if (refs.containsKey(purl)) {
                throw new BridgeException(ctx, paintElement, "xlink.href.circularDependencies", new Object[]{uri});
            }
            refs.put(purl, purl);
            paintElement = ctx.getReferencedElement(paintElement, uri);
        }
    }
}

