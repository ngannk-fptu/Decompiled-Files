/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.CSSStyleSheetNode
 *  org.apache.batik.css.engine.StyleSheet
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGStyleElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStyleSheetNode;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.stylesheets.LinkStyle;
import org.w3c.dom.svg.SVGStyleElement;

public class SVGOMStyleElement
extends SVGOMElement
implements CSSStyleSheetNode,
SVGStyleElement,
LinkStyle {
    protected static final AttributeInitializer attributeInitializer = new AttributeInitializer(1);
    protected transient org.w3c.dom.stylesheets.StyleSheet sheet;
    protected transient StyleSheet styleSheet;
    protected transient EventListener domCharacterDataModifiedListener = new DOMCharacterDataModifiedListener();

    protected SVGOMStyleElement() {
    }

    public SVGOMStyleElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "style";
    }

    public StyleSheet getCSSStyleSheet() {
        if (this.styleSheet == null && this.getType().equals("text/css")) {
            SVGOMDocument doc = (SVGOMDocument)((Object)this.getOwnerDocument());
            CSSEngine e = doc.getCSSEngine();
            String text = "";
            Node n = this.getFirstChild();
            if (n != null) {
                StringBuffer sb = new StringBuffer();
                while (n != null) {
                    if (n.getNodeType() == 4 || n.getNodeType() == 3) {
                        sb.append(n.getNodeValue());
                    }
                    n = n.getNextSibling();
                }
                text = sb.toString();
            }
            ParsedURL burl = null;
            String bu = this.getBaseURI();
            if (bu != null) {
                burl = new ParsedURL(bu);
            }
            String media = this.getAttributeNS(null, "media");
            this.styleSheet = e.parseStyleSheet(text, burl, media);
            this.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedListener, false, null);
        }
        return this.styleSheet;
    }

    @Override
    public org.w3c.dom.stylesheets.StyleSheet getSheet() {
        throw new UnsupportedOperationException("LinkStyle.getSheet() is not implemented");
    }

    public String getXMLspace() {
        return XMLSupport.getXMLSpace((Element)((Object)this));
    }

    public void setXMLspace(String space) throws DOMException {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }

    public String getType() {
        if (this.hasAttributeNS(null, "type")) {
            return this.getAttributeNS(null, "type");
        }
        return "text/css";
    }

    public void setType(String type) throws DOMException {
        this.setAttributeNS(null, "type", type);
    }

    public String getMedia() {
        return this.getAttribute("media");
    }

    public void setMedia(String media) throws DOMException {
        this.setAttribute("media", media);
    }

    public String getTitle() {
        return this.getAttribute("title");
    }

    public void setTitle(String title) throws DOMException {
        this.setAttribute("title", title);
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMStyleElement();
    }

    static {
        attributeInitializer.addAttribute("http://www.w3.org/XML/1998/namespace", "xml", "space", "preserve");
    }

    protected class DOMCharacterDataModifiedListener
    implements EventListener {
        protected DOMCharacterDataModifiedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            SVGOMStyleElement.this.styleSheet = null;
        }
    }
}

