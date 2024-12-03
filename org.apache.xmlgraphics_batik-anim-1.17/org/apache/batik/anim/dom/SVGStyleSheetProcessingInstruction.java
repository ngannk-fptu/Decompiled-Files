/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.CSSStyleSheetNode
 *  org.apache.batik.css.engine.StyleSheet
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.StyleSheetFactory
 *  org.apache.batik.dom.StyleSheetProcessingInstruction
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.anim.dom;

import java.util.HashMap;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.StyleSheetProcessingInstruction;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class SVGStyleSheetProcessingInstruction
extends StyleSheetProcessingInstruction
implements CSSStyleSheetNode {
    protected StyleSheet styleSheet;

    protected SVGStyleSheetProcessingInstruction() {
    }

    public SVGStyleSheetProcessingInstruction(String data, AbstractDocument owner, StyleSheetFactory f) {
        super(data, owner, f);
    }

    public String getStyleSheetURI() {
        SVGOMDocument svgDoc = (SVGOMDocument)((Object)this.getOwnerDocument());
        ParsedURL url = svgDoc.getParsedURL();
        String href = (String)this.getPseudoAttributes().get("href");
        if (url != null) {
            return new ParsedURL(url, href).toString();
        }
        return href;
    }

    public StyleSheet getCSSStyleSheet() {
        HashMap attrs;
        String type;
        if (this.styleSheet == null && "text/css".equals(type = (String)(attrs = this.getPseudoAttributes()).get("type"))) {
            String title = (String)attrs.get("title");
            String media = (String)attrs.get("media");
            String href = (String)attrs.get("href");
            String alternate = (String)attrs.get("alternate");
            SVGOMDocument doc = (SVGOMDocument)((Object)this.getOwnerDocument());
            ParsedURL durl = doc.getParsedURL();
            ParsedURL burl = new ParsedURL(durl, href);
            CSSEngine e = doc.getCSSEngine();
            this.styleSheet = e.parseStyleSheet(burl, media);
            this.styleSheet.setAlternate("yes".equals(alternate));
            this.styleSheet.setTitle(title);
        }
        return this.styleSheet;
    }

    public void setData(String data) throws DOMException {
        super.setData(data);
        this.styleSheet = null;
    }

    protected Node newNode() {
        return new SVGStyleSheetProcessingInstruction();
    }
}

