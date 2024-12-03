/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.html.dom.HTMLDocumentImpl
 *  org.cyberneko.html.parsers.DOMFragmentParser
 */
package com.atlassian.renderer.wysiwyg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public abstract class HtmlParserUtil {
    private DocumentFragment document;

    public DocumentFragment getDocumentFragment() {
        return this.document;
    }

    public Document getDocument() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.appendChild(this.document);
            return doc;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HtmlParserUtil(String xhtml) throws UnsupportedEncodingException {
        this(new ByteArrayInputStream(xhtml.getBytes("UTF-8")));
    }

    public HtmlParserUtil(InputStream in) {
        DOMFragmentParser parser = new DOMFragmentParser();
        HTMLDocumentImpl htmlDocument = new HTMLDocumentImpl();
        this.document = htmlDocument.createDocumentFragment();
        InputSource inputSource = new InputSource(in);
        try {
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            this.init(parser);
            parser.parse(inputSource, this.document);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void init(DOMFragmentParser var1);

    public Node findTag(String name) {
        return this.findTag(name, this.document);
    }

    private Node findTag(String name, Node node) {
        if (node.getNodeName().toLowerCase().equals(name.toLowerCase())) {
            return node;
        }
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node n = this.findTag(name, node.getChildNodes().item(i));
            if (n == null) continue;
            return n;
        }
        return null;
    }

    public String getText(Node node) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node n = node.getChildNodes().item(i);
            if (n.getNodeType() != 3) continue;
            sb.append(n.getNodeValue());
        }
        return sb.toString();
    }
}

