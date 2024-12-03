/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.gvt.font.Glyph
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPaintInfo
 */
package org.apache.batik.bridge;

import java.text.AttributedCharacterIterator;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.SVGFontFace;
import org.apache.batik.bridge.SVGFontFaceElementBridge;
import org.apache.batik.bridge.SVGGlyphElementBridge;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGAltGlyphElementBridge
extends AbstractSVGBridge
implements ErrorConstants {
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;

    @Override
    public String getLocalName() {
        return "altGlyph";
    }

    public Glyph[] createAltGlyphArray(BridgeContext ctx, Element altGlyphElement, float fontSize, AttributedCharacterIterator aci) {
        Element refElement;
        String uri;
        block17: {
            uri = XLinkSupport.getXLinkHref((Element)altGlyphElement);
            refElement = null;
            try {
                refElement = ctx.getReferencedElement(altGlyphElement, uri);
            }
            catch (BridgeException e) {
                if (!"uri.unsecure".equals(e.getCode())) break block17;
                ctx.getUserAgent().displayError(e);
            }
        }
        if (refElement == null) {
            return null;
        }
        if (!"http://www.w3.org/2000/svg".equals(refElement.getNamespaceURI())) {
            return null;
        }
        if (refElement.getLocalName().equals("glyph")) {
            Glyph glyph = this.getGlyph(ctx, uri, altGlyphElement, fontSize, aci);
            if (glyph == null) {
                return null;
            }
            Glyph[] glyphArray = new Glyph[]{glyph};
            return glyphArray;
        }
        if (refElement.getLocalName().equals("altGlyphDef")) {
            Element localRefElement;
            SVGOMDocument document = (SVGOMDocument)altGlyphElement.getOwnerDocument();
            SVGOMDocument refDocument = (SVGOMDocument)refElement.getOwnerDocument();
            boolean isLocal = refDocument == document;
            Element element = localRefElement = isLocal ? refElement : (Element)document.importNode((Node)refElement, true);
            if (!isLocal) {
                String base = AbstractNode.getBaseURI((Node)altGlyphElement);
                Element g = document.createElementNS("http://www.w3.org/2000/svg", "g");
                g.appendChild(localRefElement);
                g.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
                CSSUtilities.computeStyleAndURIs(refElement, localRefElement, uri);
            }
            NodeList altGlyphDefChildren = localRefElement.getChildNodes();
            boolean containsGlyphRefNodes = false;
            int numAltGlyphDefChildren = altGlyphDefChildren.getLength();
            for (int i = 0; i < numAltGlyphDefChildren; ++i) {
                Element agc;
                Node altGlyphChild = altGlyphDefChildren.item(i);
                if (altGlyphChild.getNodeType() != 1 || !"http://www.w3.org/2000/svg".equals((agc = (Element)altGlyphChild).getNamespaceURI()) || !"glyphRef".equals(agc.getLocalName())) continue;
                containsGlyphRefNodes = true;
                break;
            }
            if (containsGlyphRefNodes) {
                NodeList glyphRefNodes = localRefElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyphRef");
                int numGlyphRefNodes = glyphRefNodes.getLength();
                Glyph[] glyphArray = new Glyph[numGlyphRefNodes];
                for (int i = 0; i < numGlyphRefNodes; ++i) {
                    Element glyphRefElement = (Element)glyphRefNodes.item(i);
                    String glyphUri = XLinkSupport.getXLinkHref((Element)glyphRefElement);
                    Glyph glyph = this.getGlyph(ctx, glyphUri, glyphRefElement, fontSize, aci);
                    if (glyph == null) {
                        return null;
                    }
                    glyphArray[i] = glyph;
                }
                return glyphArray;
            }
            NodeList altGlyphItemNodes = localRefElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "altGlyphItem");
            int numAltGlyphItemNodes = altGlyphItemNodes.getLength();
            if (numAltGlyphItemNodes > 0) {
                boolean foundMatchingGlyph = false;
                Glyph[] glyphArray = null;
                block4: for (int i = 0; i < numAltGlyphItemNodes && !foundMatchingGlyph; ++i) {
                    Element altGlyphItemElement = (Element)altGlyphItemNodes.item(i);
                    NodeList altGlyphRefNodes = altGlyphItemElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyphRef");
                    int numAltGlyphRefNodes = altGlyphRefNodes.getLength();
                    glyphArray = new Glyph[numAltGlyphRefNodes];
                    foundMatchingGlyph = true;
                    for (int j = 0; j < numAltGlyphRefNodes; ++j) {
                        Element glyphRefElement = (Element)altGlyphRefNodes.item(j);
                        String glyphUri = XLinkSupport.getXLinkHref((Element)glyphRefElement);
                        Glyph glyph = this.getGlyph(ctx, glyphUri, glyphRefElement, fontSize, aci);
                        if (glyph == null) {
                            foundMatchingGlyph = false;
                            continue block4;
                        }
                        glyphArray[j] = glyph;
                    }
                }
                if (!foundMatchingGlyph) {
                    return null;
                }
                return glyphArray;
            }
        }
        return null;
    }

    private Glyph getGlyph(BridgeContext ctx, String glyphUri, Element altGlyphElement, float fontSize, AttributedCharacterIterator aci) {
        Element refGlyphElement;
        block9: {
            refGlyphElement = null;
            try {
                refGlyphElement = ctx.getReferencedElement(altGlyphElement, glyphUri);
            }
            catch (BridgeException e) {
                if (!"uri.unsecure".equals(e.getCode())) break block9;
                ctx.getUserAgent().displayError(e);
            }
        }
        if (refGlyphElement == null || !"http://www.w3.org/2000/svg".equals(refGlyphElement.getNamespaceURI()) || !"glyph".equals(refGlyphElement.getLocalName())) {
            return null;
        }
        SVGOMDocument document = (SVGOMDocument)altGlyphElement.getOwnerDocument();
        SVGOMDocument refDocument = (SVGOMDocument)refGlyphElement.getOwnerDocument();
        boolean isLocal = refDocument == document;
        Element localGlyphElement = null;
        Element localFontFaceElement = null;
        Element localFontElement = null;
        if (isLocal) {
            localGlyphElement = refGlyphElement;
            localFontElement = (Element)localGlyphElement.getParentNode();
            NodeList fontFaceElements = localFontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "font-face");
            if (fontFaceElements.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }
        } else {
            NodeList fontFaceElements;
            localFontElement = (Element)document.importNode(refGlyphElement.getParentNode(), true);
            String base = AbstractNode.getBaseURI((Node)altGlyphElement);
            Element g = document.createElementNS("http://www.w3.org/2000/svg", "g");
            g.appendChild(localFontElement);
            g.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
            CSSUtilities.computeStyleAndURIs((Element)refGlyphElement.getParentNode(), localFontElement, glyphUri);
            String glyphId = refGlyphElement.getAttributeNS(null, "id");
            NodeList glyphElements = localFontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyph");
            for (int i = 0; i < glyphElements.getLength(); ++i) {
                Element glyphElem = (Element)glyphElements.item(i);
                if (!glyphElem.getAttributeNS(null, "id").equals(glyphId)) continue;
                localGlyphElement = glyphElem;
                break;
            }
            if ((fontFaceElements = localFontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "font-face")).getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }
        }
        if (localGlyphElement == null || localFontFaceElement == null) {
            return null;
        }
        SVGFontFaceElementBridge fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge(localFontFaceElement);
        SVGFontFace fontFace = fontFaceBridge.createFontFace(ctx, localFontFaceElement);
        SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)ctx.getBridge(localGlyphElement);
        aci.first();
        TextPaintInfo tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
        return glyphBridge.createGlyph(ctx, localGlyphElement, altGlyphElement, -1, fontSize, fontFace, tpi);
    }
}

