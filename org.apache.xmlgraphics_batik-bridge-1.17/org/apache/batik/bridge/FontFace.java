/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.font.GVTFontFamily
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import java.util.LinkedList;
import java.util.List;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSFontFace;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.FontFamilyResolver;
import org.apache.batik.bridge.SVGFontFace;
import org.apache.batik.bridge.SVGFontFaceElementBridge;
import org.apache.batik.bridge.SVGFontFamily;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public abstract class FontFace
extends GVTFontFace
implements ErrorConstants {
    List srcs;

    public FontFace(List srcs, String familyName, float unitsPerEm, String fontWeight, String fontStyle, String fontVariant, String fontStretch, float slope, String panose1, float ascent, float descent, float strikethroughPosition, float strikethroughThickness, float underlinePosition, float underlineThickness, float overlinePosition, float overlineThickness) {
        super(familyName, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPosition, strikethroughThickness, underlinePosition, underlineThickness, overlinePosition, overlineThickness);
        this.srcs = srcs;
    }

    protected FontFace(String familyName) {
        super(familyName);
    }

    public static CSSFontFace createFontFace(String familyName, FontFace src) {
        return new CSSFontFace(new LinkedList(src.srcs), familyName, src.unitsPerEm, src.fontWeight, src.fontStyle, src.fontVariant, src.fontStretch, src.slope, src.panose1, src.ascent, src.descent, src.strikethroughPosition, src.strikethroughThickness, src.underlinePosition, src.underlineThickness, src.overlinePosition, src.overlineThickness);
    }

    public GVTFontFamily getFontFamily(BridgeContext ctx) {
        FontFamilyResolver fontFamilyResolver = ctx.getFontFamilyResolver();
        GVTFontFamily family = fontFamilyResolver.resolve(this.familyName, this);
        if (family != null) {
            return family;
        }
        for (Object o : this.srcs) {
            if (o instanceof String) {
                family = fontFamilyResolver.resolve((String)o, this);
                if (family == null) continue;
                return family;
            }
            if (!(o instanceof ParsedURL)) continue;
            try {
                GVTFontFamily ff = this.getFontFamily(ctx, (ParsedURL)o);
                if (ff == null) continue;
                return ff;
            }
            catch (SecurityException ex) {
                ctx.getUserAgent().displayError(ex);
            }
            catch (BridgeException ex) {
                if (!"uri.unsecure".equals(ex.getCode())) continue;
                ctx.getUserAgent().displayError(ex);
            }
            catch (Exception exception) {
            }
        }
        return null;
    }

    protected GVTFontFamily getFontFamily(BridgeContext ctx, ParsedURL purl) {
        String purlStr = purl.toString();
        Element e = this.getBaseElement(ctx);
        SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
        String docURL = svgDoc.getURL();
        ParsedURL pDocURL = null;
        if (docURL != null) {
            pDocURL = new ParsedURL(docURL);
        }
        String baseURI = AbstractNode.getBaseURI((Node)e);
        purl = new ParsedURL(baseURI, purlStr);
        UserAgent userAgent = ctx.getUserAgent();
        try {
            userAgent.checkLoadExternalResource(purl, pDocURL);
        }
        catch (SecurityException ex) {
            userAgent.displayError(ex);
            return null;
        }
        if (purl.getRef() != null) {
            Element ref = ctx.getReferencedElement(e, purlStr);
            if (!ref.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !ref.getLocalName().equals("font")) {
                return null;
            }
            SVGDocument doc = (SVGDocument)e.getOwnerDocument();
            SVGDocument rdoc = (SVGDocument)ref.getOwnerDocument();
            Element fontElt = ref;
            if (doc != rdoc) {
                fontElt = (Element)doc.importNode((Node)ref, true);
                String base = AbstractNode.getBaseURI((Node)ref);
                Element g = doc.createElementNS("http://www.w3.org/2000/svg", "g");
                g.appendChild(fontElt);
                g.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", base);
                CSSUtilities.computeStyleAndURIs(ref, fontElt, purlStr);
            }
            Element fontFaceElt = null;
            for (Node n = fontElt.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() != 1 || !n.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !n.getLocalName().equals("font-face")) continue;
                fontFaceElt = (Element)n;
                break;
            }
            SVGFontFaceElementBridge fontFaceBridge = (SVGFontFaceElementBridge)ctx.getBridge("http://www.w3.org/2000/svg", "font-face");
            SVGFontFace gff = fontFaceBridge.createFontFace(ctx, fontFaceElt);
            return new SVGFontFamily(gff, fontElt, ctx);
        }
        try {
            return ctx.getFontFamilyResolver().loadFont(purl.openStream(), this);
        }
        catch (Exception exception) {
            return null;
        }
    }

    protected Element getBaseElement(BridgeContext ctx) {
        SVGDocument d = (SVGDocument)ctx.getDocument();
        return d.getRootElement();
    }
}

