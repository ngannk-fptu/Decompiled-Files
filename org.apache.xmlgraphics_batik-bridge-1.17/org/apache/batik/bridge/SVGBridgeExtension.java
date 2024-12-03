/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.util.Collections;
import java.util.Iterator;
import org.apache.batik.bridge.AbstractSVGGradientElementBridge;
import org.apache.batik.bridge.AbstractSVGLightingElementBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeExtension;
import org.apache.batik.bridge.SVGAElementBridge;
import org.apache.batik.bridge.SVGAltGlyphElementBridge;
import org.apache.batik.bridge.SVGAnimateColorElementBridge;
import org.apache.batik.bridge.SVGAnimateElementBridge;
import org.apache.batik.bridge.SVGAnimateMotionElementBridge;
import org.apache.batik.bridge.SVGAnimateTransformElementBridge;
import org.apache.batik.bridge.SVGCircleElementBridge;
import org.apache.batik.bridge.SVGClipPathElementBridge;
import org.apache.batik.bridge.SVGColorProfileElementBridge;
import org.apache.batik.bridge.SVGDescElementBridge;
import org.apache.batik.bridge.SVGEllipseElementBridge;
import org.apache.batik.bridge.SVGFeBlendElementBridge;
import org.apache.batik.bridge.SVGFeColorMatrixElementBridge;
import org.apache.batik.bridge.SVGFeComponentTransferElementBridge;
import org.apache.batik.bridge.SVGFeCompositeElementBridge;
import org.apache.batik.bridge.SVGFeConvolveMatrixElementBridge;
import org.apache.batik.bridge.SVGFeDiffuseLightingElementBridge;
import org.apache.batik.bridge.SVGFeDisplacementMapElementBridge;
import org.apache.batik.bridge.SVGFeFloodElementBridge;
import org.apache.batik.bridge.SVGFeGaussianBlurElementBridge;
import org.apache.batik.bridge.SVGFeImageElementBridge;
import org.apache.batik.bridge.SVGFeMergeElementBridge;
import org.apache.batik.bridge.SVGFeMorphologyElementBridge;
import org.apache.batik.bridge.SVGFeOffsetElementBridge;
import org.apache.batik.bridge.SVGFeSpecularLightingElementBridge;
import org.apache.batik.bridge.SVGFeTileElementBridge;
import org.apache.batik.bridge.SVGFeTurbulenceElementBridge;
import org.apache.batik.bridge.SVGFilterElementBridge;
import org.apache.batik.bridge.SVGFontElementBridge;
import org.apache.batik.bridge.SVGFontFaceElementBridge;
import org.apache.batik.bridge.SVGGElementBridge;
import org.apache.batik.bridge.SVGGlyphElementBridge;
import org.apache.batik.bridge.SVGHKernElementBridge;
import org.apache.batik.bridge.SVGImageElementBridge;
import org.apache.batik.bridge.SVGLineElementBridge;
import org.apache.batik.bridge.SVGLinearGradientElementBridge;
import org.apache.batik.bridge.SVGMarkerElementBridge;
import org.apache.batik.bridge.SVGMaskElementBridge;
import org.apache.batik.bridge.SVGMissingGlyphElementBridge;
import org.apache.batik.bridge.SVGPathElementBridge;
import org.apache.batik.bridge.SVGPatternElementBridge;
import org.apache.batik.bridge.SVGPolygonElementBridge;
import org.apache.batik.bridge.SVGPolylineElementBridge;
import org.apache.batik.bridge.SVGRadialGradientElementBridge;
import org.apache.batik.bridge.SVGRectElementBridge;
import org.apache.batik.bridge.SVGSVGElementBridge;
import org.apache.batik.bridge.SVGSetElementBridge;
import org.apache.batik.bridge.SVGSwitchElementBridge;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.SVGTextPathElementBridge;
import org.apache.batik.bridge.SVGTitleElementBridge;
import org.apache.batik.bridge.SVGUseElementBridge;
import org.apache.batik.bridge.SVGVKernElementBridge;
import org.w3c.dom.Element;

public class SVGBridgeExtension
implements BridgeExtension {
    @Override
    public float getPriority() {
        return 0.0f;
    }

    @Override
    public Iterator getImplementedExtensions() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    public String getAuthor() {
        return "The Apache Batik Team.";
    }

    @Override
    public String getContactAddress() {
        return "batik-dev@xmlgraphics.apache.org";
    }

    @Override
    public String getURL() {
        return "http://xml.apache.org/batik";
    }

    @Override
    public String getDescription() {
        return "The required SVG 1.0 tags";
    }

    @Override
    public void registerTags(BridgeContext ctx) {
        ctx.putBridge(new SVGAElementBridge());
        ctx.putBridge(new SVGAltGlyphElementBridge());
        ctx.putBridge(new SVGCircleElementBridge());
        ctx.putBridge(new SVGClipPathElementBridge());
        ctx.putBridge(new SVGColorProfileElementBridge());
        ctx.putBridge(new SVGDescElementBridge());
        ctx.putBridge(new SVGEllipseElementBridge());
        ctx.putBridge(new SVGFeBlendElementBridge());
        ctx.putBridge(new SVGFeColorMatrixElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge());
        ctx.putBridge(new SVGFeCompositeElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncAElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncRElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncGElementBridge());
        ctx.putBridge(new SVGFeComponentTransferElementBridge.SVGFeFuncBElementBridge());
        ctx.putBridge(new SVGFeConvolveMatrixElementBridge());
        ctx.putBridge(new SVGFeDiffuseLightingElementBridge());
        ctx.putBridge(new SVGFeDisplacementMapElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFeDistantLightElementBridge());
        ctx.putBridge(new SVGFeFloodElementBridge());
        ctx.putBridge(new SVGFeGaussianBlurElementBridge());
        ctx.putBridge(new SVGFeImageElementBridge());
        ctx.putBridge(new SVGFeMergeElementBridge());
        ctx.putBridge(new SVGFeMergeElementBridge.SVGFeMergeNodeElementBridge());
        ctx.putBridge(new SVGFeMorphologyElementBridge());
        ctx.putBridge(new SVGFeOffsetElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFePointLightElementBridge());
        ctx.putBridge(new SVGFeSpecularLightingElementBridge());
        ctx.putBridge(new AbstractSVGLightingElementBridge.SVGFeSpotLightElementBridge());
        ctx.putBridge(new SVGFeTileElementBridge());
        ctx.putBridge(new SVGFeTurbulenceElementBridge());
        ctx.putBridge(new SVGFontElementBridge());
        ctx.putBridge(new SVGFontFaceElementBridge());
        ctx.putBridge(new SVGFilterElementBridge());
        ctx.putBridge(new SVGGElementBridge());
        ctx.putBridge(new SVGGlyphElementBridge());
        ctx.putBridge(new SVGHKernElementBridge());
        ctx.putBridge(new SVGImageElementBridge());
        ctx.putBridge(new SVGLineElementBridge());
        ctx.putBridge(new SVGLinearGradientElementBridge());
        ctx.putBridge(new SVGMarkerElementBridge());
        ctx.putBridge(new SVGMaskElementBridge());
        ctx.putBridge(new SVGMissingGlyphElementBridge());
        ctx.putBridge(new SVGPathElementBridge());
        ctx.putBridge(new SVGPatternElementBridge());
        ctx.putBridge(new SVGPolylineElementBridge());
        ctx.putBridge(new SVGPolygonElementBridge());
        ctx.putBridge(new SVGRadialGradientElementBridge());
        ctx.putBridge(new SVGRectElementBridge());
        ctx.putBridge(new AbstractSVGGradientElementBridge.SVGStopElementBridge());
        ctx.putBridge(new SVGSVGElementBridge());
        ctx.putBridge(new SVGSwitchElementBridge());
        ctx.putBridge(new SVGTextElementBridge());
        ctx.putBridge(new SVGTextPathElementBridge());
        ctx.putBridge(new SVGTitleElementBridge());
        ctx.putBridge(new SVGUseElementBridge());
        ctx.putBridge(new SVGVKernElementBridge());
        ctx.putBridge(new SVGSetElementBridge());
        ctx.putBridge(new SVGAnimateElementBridge());
        ctx.putBridge(new SVGAnimateColorElementBridge());
        ctx.putBridge(new SVGAnimateTransformElementBridge());
        ctx.putBridge(new SVGAnimateMotionElementBridge());
    }

    @Override
    public boolean isDynamicElement(Element e) {
        String ns = e.getNamespaceURI();
        if (!"http://www.w3.org/2000/svg".equals(ns)) {
            return false;
        }
        String ln = e.getLocalName();
        return ln.equals("script") || ln.startsWith("animate") || ln.equals("set");
    }
}

