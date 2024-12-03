/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XStream11NameCoder
extends XmlFriendlyNameCoder {
    public String decodeAttribute(String attributeName) {
        return attributeName;
    }

    public String decodeNode(String elementName) {
        return elementName;
    }
}

