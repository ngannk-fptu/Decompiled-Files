/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractDocumentWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;

public class XppDomWriter
extends AbstractDocumentWriter {
    public XppDomWriter() {
        this(null, (NameCoder)new XmlFriendlyNameCoder());
    }

    public XppDomWriter(XppDom parent) {
        this(parent, (NameCoder)new XmlFriendlyNameCoder());
    }

    public XppDomWriter(NameCoder nameCoder) {
        this(null, nameCoder);
    }

    public XppDomWriter(XppDom parent, NameCoder nameCoder) {
        super((Object)parent, nameCoder);
    }

    public XppDomWriter(XmlFriendlyReplacer replacer) {
        this((XppDom)null, replacer);
    }

    public XppDomWriter(XppDom parent, XmlFriendlyReplacer replacer) {
        this(parent, (NameCoder)replacer);
    }

    public XppDom getConfiguration() {
        return (XppDom)this.getTopLevelNodes().get(0);
    }

    protected Object createNode(String name) {
        XppDom newNode = new XppDom(this.encodeNode(name));
        XppDom top = this.top();
        if (top != null) {
            this.top().addChild(newNode);
        }
        return newNode;
    }

    public void setValue(String text) {
        this.top().setValue(text);
    }

    public void addAttribute(String key, String value) {
        this.top().setAttribute(this.encodeAttribute(key), value);
    }

    private XppDom top() {
        return (XppDom)this.getCurrent();
    }
}

