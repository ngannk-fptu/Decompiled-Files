/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.AltGlyphHandler
 *  org.apache.batik.gvt.font.GVTGlyphVector
 *  org.apache.batik.gvt.font.Glyph
 *  org.apache.batik.gvt.font.SVGGVTGlyphVector
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGAltGlyphElementBridge;
import org.apache.batik.gvt.font.AltGlyphHandler;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

public class SVGAltGlyphHandler
implements AltGlyphHandler,
SVGConstants {
    private BridgeContext ctx;
    private Element textElement;

    public SVGAltGlyphHandler(BridgeContext ctx, Element textElement) {
        this.ctx = ctx;
        this.textElement = textElement;
    }

    public GVTGlyphVector createGlyphVector(FontRenderContext frc, float fontSize, AttributedCharacterIterator aci) {
        try {
            SVGAltGlyphElementBridge altGlyphBridge;
            Glyph[] glyphArray;
            if ("http://www.w3.org/2000/svg".equals(this.textElement.getNamespaceURI()) && "altGlyph".equals(this.textElement.getLocalName()) && (glyphArray = (altGlyphBridge = (SVGAltGlyphElementBridge)this.ctx.getBridge(this.textElement)).createAltGlyphArray(this.ctx, this.textElement, fontSize, aci)) != null) {
                return new SVGGVTGlyphVector(null, glyphArray, frc);
            }
        }
        catch (SecurityException e) {
            this.ctx.getUserAgent().displayError(e);
            throw e;
        }
        return null;
    }
}

