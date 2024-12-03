/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XppDriver
extends AbstractXppDriver {
    private static XmlPullParserFactory factory;

    public XppDriver() {
        super(new XmlFriendlyNameCoder());
    }

    public XppDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    public XppDriver(XmlFriendlyReplacer replacer) {
        this((NameCoder)replacer);
    }

    public static synchronized XmlPullParser createDefaultParser() throws XmlPullParserException {
        if (factory == null) {
            factory = XmlPullParserFactory.newInstance();
        }
        return factory.newPullParser();
    }

    protected XmlPullParser createParser() throws XmlPullParserException {
        return XppDriver.createDefaultParser();
    }
}

