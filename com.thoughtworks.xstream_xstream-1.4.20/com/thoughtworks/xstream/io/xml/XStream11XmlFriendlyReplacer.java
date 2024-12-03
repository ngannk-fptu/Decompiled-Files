/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

public class XStream11XmlFriendlyReplacer
extends XmlFriendlyReplacer {
    public String decodeAttribute(String attributeName) {
        return attributeName;
    }

    public String decodeNode(String elementName) {
        return elementName;
    }

    public String unescapeName(String name) {
        return name;
    }
}

