/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.github.xstream.mxparser.MXParser
 *  org.xmlpull.v1.XmlPullParser
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import io.github.xstream.mxparser.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class MXParserDriver
extends AbstractXppDriver {
    public MXParserDriver() {
        super(new XmlFriendlyNameCoder());
    }

    public MXParserDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XmlPullParser createParser() {
        return new MXParser();
    }
}

