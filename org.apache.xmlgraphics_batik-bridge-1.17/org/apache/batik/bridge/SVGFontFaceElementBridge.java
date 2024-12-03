/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import java.util.LinkedList;
import java.util.List;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.SVGFontFace;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGFontFaceElementBridge
extends AbstractSVGBridge
implements ErrorConstants {
    @Override
    public String getLocalName() {
        return "font-face";
    }

    public SVGFontFace createFontFace(BridgeContext ctx, Element fontFaceElement) {
        float overlineThickness;
        float overlinePos;
        float strikethroughThickness;
        float strikethroughPos;
        float underlineThickness;
        float underlinePos;
        float descent;
        float ascent;
        String ascentStr;
        float slope;
        String slopeStr;
        String fontStretch;
        String fontVariant;
        String fontStyle;
        float unitsPerEm;
        String familyNames = fontFaceElement.getAttributeNS(null, "font-family");
        String unitsPerEmStr = fontFaceElement.getAttributeNS(null, "units-per-em");
        if (unitsPerEmStr.length() == 0) {
            unitsPerEmStr = "1000";
        }
        try {
            unitsPerEm = SVGUtilities.convertSVGNumber(unitsPerEmStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"units-per-em", unitsPerEmStr});
        }
        String fontWeight = fontFaceElement.getAttributeNS(null, "font-weight");
        if (fontWeight.length() == 0) {
            fontWeight = "all";
        }
        if ((fontStyle = fontFaceElement.getAttributeNS(null, "font-style")).length() == 0) {
            fontStyle = "all";
        }
        if ((fontVariant = fontFaceElement.getAttributeNS(null, "font-variant")).length() == 0) {
            fontVariant = "normal";
        }
        if ((fontStretch = fontFaceElement.getAttributeNS(null, "font-stretch")).length() == 0) {
            fontStretch = "normal";
        }
        if ((slopeStr = fontFaceElement.getAttributeNS(null, "slope")).length() == 0) {
            slopeStr = "0";
        }
        try {
            slope = SVGUtilities.convertSVGNumber(slopeStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", slopeStr});
        }
        String panose1 = fontFaceElement.getAttributeNS(null, "panose-1");
        if (panose1.length() == 0) {
            panose1 = "0 0 0 0 0 0 0 0 0 0";
        }
        if ((ascentStr = fontFaceElement.getAttributeNS(null, "ascent")).length() == 0) {
            ascentStr = String.valueOf((double)unitsPerEm * 0.8);
        }
        try {
            ascent = SVGUtilities.convertSVGNumber(ascentStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", ascentStr});
        }
        String descentStr = fontFaceElement.getAttributeNS(null, "descent");
        if (descentStr.length() == 0) {
            descentStr = String.valueOf((double)unitsPerEm * 0.2);
        }
        try {
            descent = SVGUtilities.convertSVGNumber(descentStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", descentStr});
        }
        String underlinePosStr = fontFaceElement.getAttributeNS(null, "underline-position");
        if (underlinePosStr.length() == 0) {
            underlinePosStr = String.valueOf(-3.0f * unitsPerEm / 40.0f);
        }
        try {
            underlinePos = SVGUtilities.convertSVGNumber(underlinePosStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", underlinePosStr});
        }
        String underlineThicknessStr = fontFaceElement.getAttributeNS(null, "underline-thickness");
        if (underlineThicknessStr.length() == 0) {
            underlineThicknessStr = String.valueOf(unitsPerEm / 20.0f);
        }
        try {
            underlineThickness = SVGUtilities.convertSVGNumber(underlineThicknessStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", underlineThicknessStr});
        }
        String strikethroughPosStr = fontFaceElement.getAttributeNS(null, "strikethrough-position");
        if (strikethroughPosStr.length() == 0) {
            strikethroughPosStr = String.valueOf(3.0f * ascent / 8.0f);
        }
        try {
            strikethroughPos = SVGUtilities.convertSVGNumber(strikethroughPosStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", strikethroughPosStr});
        }
        String strikethroughThicknessStr = fontFaceElement.getAttributeNS(null, "strikethrough-thickness");
        if (strikethroughThicknessStr.length() == 0) {
            strikethroughThicknessStr = String.valueOf(unitsPerEm / 20.0f);
        }
        try {
            strikethroughThickness = SVGUtilities.convertSVGNumber(strikethroughThicknessStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", strikethroughThicknessStr});
        }
        String overlinePosStr = fontFaceElement.getAttributeNS(null, "overline-position");
        if (overlinePosStr.length() == 0) {
            overlinePosStr = String.valueOf(ascent);
        }
        try {
            overlinePos = SVGUtilities.convertSVGNumber(overlinePosStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", overlinePosStr});
        }
        String overlineThicknessStr = fontFaceElement.getAttributeNS(null, "overline-thickness");
        if (overlineThicknessStr.length() == 0) {
            overlineThicknessStr = String.valueOf(unitsPerEm / 20.0f);
        }
        try {
            overlineThickness = SVGUtilities.convertSVGNumber(overlineThicknessStr);
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, fontFaceElement, nfEx, "attribute.malformed", new Object[]{"0", overlineThicknessStr});
        }
        List srcs = null;
        Element fontElt = SVGUtilities.getParentElement(fontFaceElement);
        if (!fontElt.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !fontElt.getLocalName().equals("font")) {
            srcs = this.getFontFaceSrcs(fontFaceElement);
        }
        return new SVGFontFace(fontFaceElement, srcs, familyNames, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPos, strikethroughThickness, underlinePos, underlineThickness, overlinePos, overlineThickness);
    }

    public List getFontFaceSrcs(Element fontFaceElement) {
        Node ffsrc = null;
        for (Node n = fontFaceElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1 || !n.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !n.getLocalName().equals("font-face-src")) continue;
            ffsrc = (Element)n;
            break;
        }
        if (ffsrc == null) {
            return null;
        }
        LinkedList<Object> ret = new LinkedList<Object>();
        for (Node n = ffsrc.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element ffname;
            String s;
            if (n.getNodeType() != 1 || !n.getNamespaceURI().equals("http://www.w3.org/2000/svg")) continue;
            if (n.getLocalName().equals("font-face-uri")) {
                Element ffuri = (Element)n;
                String uri = XLinkSupport.getXLinkHref((Element)ffuri);
                String base = AbstractNode.getBaseURI((Node)ffuri);
                ParsedURL purl = base != null ? new ParsedURL(base, uri) : new ParsedURL(uri);
                ret.add(purl);
                continue;
            }
            if (!n.getLocalName().equals("font-face-name") || (s = (ffname = (Element)n).getAttribute("name")).length() == 0) continue;
            ret.add(s);
        }
        return ret;
    }
}

