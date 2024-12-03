/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

public abstract class AbstractXmlDriver
extends AbstractDriver {
    public AbstractXmlDriver() {
        this(new XmlFriendlyNameCoder());
    }

    public AbstractXmlDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    public AbstractXmlDriver(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    protected XmlFriendlyReplacer xmlFriendlyReplacer() {
        NameCoder nameCoder = this.getNameCoder();
        return nameCoder instanceof XmlFriendlyReplacer ? (XmlFriendlyReplacer)nameCoder : null;
    }
}

