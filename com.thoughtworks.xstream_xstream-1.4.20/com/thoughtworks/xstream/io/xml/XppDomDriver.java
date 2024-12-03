/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.xmlpull.v1.XmlPullParser
 *  org.xmlpull.v1.XmlPullParserException
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XppDomDriver
extends AbstractXppDomDriver {
    public XppDomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    public XppDomDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    public XppDomDriver(XmlFriendlyReplacer replacer) {
        super((NameCoder)replacer);
    }

    protected synchronized XmlPullParser createParser() throws XmlPullParserException {
        return XppDriver.createDefaultParser();
    }
}

