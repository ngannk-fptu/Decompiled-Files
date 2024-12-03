/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.AbstractWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XmlFriendlyWriter;

public abstract class AbstractXmlWriter
extends AbstractWriter
implements XmlFriendlyWriter {
    protected AbstractXmlWriter() {
        this(new XmlFriendlyNameCoder());
    }

    protected AbstractXmlWriter(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected AbstractXmlWriter(NameCoder nameCoder) {
        super(nameCoder);
    }

    public String escapeXmlName(String name) {
        return super.encodeNode(name);
    }
}

