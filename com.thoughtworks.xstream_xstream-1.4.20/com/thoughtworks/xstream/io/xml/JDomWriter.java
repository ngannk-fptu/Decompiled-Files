/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
 *  org.jdom.DefaultJDOMFactory
 *  org.jdom.Element
 *  org.jdom.JDOMFactory
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractDocumentWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.jdom.Content;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.JDOMFactory;

public class JDomWriter
extends AbstractDocumentWriter {
    private final JDOMFactory documentFactory;

    public JDomWriter(Element container, JDOMFactory factory, NameCoder nameCoder) {
        super((Object)container, nameCoder);
        this.documentFactory = factory;
    }

    public JDomWriter(Element container, JDOMFactory factory, XmlFriendlyReplacer replacer) {
        this(container, factory, (NameCoder)replacer);
    }

    public JDomWriter(Element container, JDOMFactory factory) {
        this(container, factory, new XmlFriendlyNameCoder());
    }

    public JDomWriter(JDOMFactory factory, NameCoder nameCoder) {
        this(null, factory, nameCoder);
    }

    public JDomWriter(JDOMFactory factory, XmlFriendlyReplacer replacer) {
        this(null, factory, (NameCoder)replacer);
    }

    public JDomWriter(JDOMFactory factory) {
        this(null, factory);
    }

    public JDomWriter(Element container, NameCoder nameCoder) {
        this(container, (JDOMFactory)new DefaultJDOMFactory(), nameCoder);
    }

    public JDomWriter(Element container, XmlFriendlyReplacer replacer) {
        this(container, (JDOMFactory)new DefaultJDOMFactory(), (NameCoder)replacer);
    }

    public JDomWriter(Element container) {
        this(container, (JDOMFactory)new DefaultJDOMFactory());
    }

    public JDomWriter() {
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

