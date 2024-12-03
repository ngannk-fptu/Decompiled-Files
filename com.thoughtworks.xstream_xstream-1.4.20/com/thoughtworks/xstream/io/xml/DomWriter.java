/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractDocumentWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DomWriter
extends AbstractDocumentWriter {
    private final Document document;
    private boolean hasRootElement;

    public DomWriter(Document document) {
        this(document, (NameCoder)new XmlFriendlyNameCoder());
    }

    public DomWriter(Element rootElement) {
        this(rootElement, (NameCoder)new XmlFriendlyNameCoder());
    }

    public DomWriter(Document document, NameCoder nameCoder) {
        this(document.getDocumentElement(), document, nameCoder);
    }

    public DomWriter(Element element, Document document, NameCoder nameCoder) {
        super((Object)element, nameCoder);
        this.document = document;
        this.hasRootElement = document.getDocumentElement() != null;
    }

    public DomWriter(Element rootElement, NameCoder nameCoder) {
        this(rootElement, rootElement.getOwnerDocument(), nameCoder);
    }

    public DomWriter(Document document, XmlFriendlyReplacer replacer) {
        this(document.getDocumentElement(), document, (NameCoder)replacer);
    }

    public DomWriter(Element element, Document document, XmlFriendlyReplacer replacer) {
        this(element, document, (NameCoder)replacer);
    }

    public DomWriter(Element rootElement, XmlFriendlyReplacer replacer) {
        this(rootElement, rootElement.getOwnerDocument(), (NameCoder)replacer);
    }

    protected Object createNode(String name) {
        Element child = this.document.createElement(this.encodeNode(name));
        Element top = this.top();
        if (top != null) {
            this.top().appendChild(child);
        } else if (!this.hasRootElement) {
            this.document.appendChild(child);
            this.hasRootElement = true;
        }
        return child;
    }

    public void addAttribute(String name, String value) {
        this.top().setAttribute(this.encodeAttribute(name), value);
    }

    public void setValue(String text) {
        this.top().appendChild(this.document.createTextNode(text));
    }

    private Element top() {
        return (Element)this.getCurrent();
    }
}

