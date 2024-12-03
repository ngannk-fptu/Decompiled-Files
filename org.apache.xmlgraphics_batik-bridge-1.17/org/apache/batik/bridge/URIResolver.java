/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public class URIResolver {
    protected SVGOMDocument document;
    protected String documentURI;
    protected DocumentLoader documentLoader;

    public URIResolver(SVGDocument doc, DocumentLoader dl) {
        this.document = (SVGOMDocument)doc;
        this.documentLoader = dl;
    }

    public Element getElement(String uri, Element ref) throws MalformedURLException, IOException {
        Node n = this.getNode(uri, ref);
        if (n == null) {
            return null;
        }
        if (n.getNodeType() == 9) {
            throw new IllegalArgumentException();
        }
        return (Element)n;
    }

    public Node getNode(String uri, Element ref) throws MalformedURLException, IOException, SecurityException {
        ParsedURL pDocURL;
        String frag;
        String baseURI = this.getRefererBaseURI(ref);
        if (baseURI == null && uri.charAt(0) == '#') {
            return this.getNodeByFragment(uri.substring(1), ref);
        }
        ParsedURL purl = new ParsedURL(baseURI, uri);
        if (this.documentURI == null) {
            this.documentURI = this.document.getURL();
        }
        if ((frag = purl.getRef()) != null && this.documentURI != null && (pDocURL = new ParsedURL(this.documentURI)).sameFile(purl)) {
            return this.document.getElementById(frag);
        }
        pDocURL = null;
        if (this.documentURI != null) {
            pDocURL = new ParsedURL(this.documentURI);
        }
        UserAgent userAgent = this.documentLoader.getUserAgent();
        userAgent.checkLoadExternalResource(purl, pDocURL);
        String purlStr = purl.toString();
        if (frag != null) {
            purlStr = purlStr.substring(0, purlStr.length() - (frag.length() + 1));
        }
        Document doc = this.documentLoader.loadDocument(purlStr);
        if (frag != null) {
            return doc.getElementById(frag);
        }
        return doc;
    }

    protected String getRefererBaseURI(Element ref) {
        return ref.getBaseURI();
    }

    protected Node getNodeByFragment(String frag, Element ref) {
        return ref.getOwnerDocument().getElementById(frag);
    }
}

