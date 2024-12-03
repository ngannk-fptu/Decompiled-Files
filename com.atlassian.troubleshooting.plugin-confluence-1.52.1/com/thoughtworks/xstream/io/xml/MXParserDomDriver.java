/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import io.github.xstream.mxparser.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class MXParserDomDriver
extends AbstractXppDomDriver {
    public MXParserDomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    public MXParserDomDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XmlPullParser createParser() {
        return new MXParser();
    }
}

