/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.DefaultJDOMFactory
 *  org.jdom2.Element
 *  org.jdom2.JDOMFactory
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractDocumentWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.jdom2.Content;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Element;
import org.jdom2.JDOMFactory;

public class JDom2Writer
extends AbstractDocumentWriter {
    private final JDOMFactory documentFactory;

    public JDom2Writer(Element container, JDOMFactory factory, NameCoder nameCoder) {
        super((Object)container, nameCoder);
        this.documentFactory = factory;
    }

    public JDom2Writer(Element container, JDOMFactory factory) {
        this(container, factory, new XmlFriendlyNameCoder());
    }

    public JDom2Writer(JDOMFactory factory, NameCoder nameCoder) {
        this(null, factory, nameCoder);
    }

    public JDom2Writer(JDOMFactory factory) {
        this(null, factory);
    }

    public JDom2Writer(Element container, NameCoder nameCoder) {
        this(container, (JDOMFactory)new DefaultJDOMFactory(), nameCoder);
    }

    public JDom2Writer(Element container) {
        this(container, (JDOMFactory)new DefaultJDOMFactory());
    }

    public JDom2Writer() {
        this((JDOMFactory)new DefaultJDOMFactory());
    }

    protected Object createNode(String name) {
        Element element = this.documentFactory.element(this.encodeNode(name));
        Element parent = this.top();
        if (parent != null) {
            parent.addContent((Content)element);
        }
        return element;
    }

    public void setValue(String text) {
        this.top().addContent((Content)this.documentFactory.text(text));
    }

    public void addAttribute(String key, String value) {
        this.top().setAttribute(this.documentFactory.attribute(this.encodeAttribute(key), value));
    }

    private Element top() {
        return (Element)this.getCurrent();
    }
}

