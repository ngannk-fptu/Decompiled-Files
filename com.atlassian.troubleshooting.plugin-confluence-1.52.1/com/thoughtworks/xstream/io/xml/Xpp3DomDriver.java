/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class Xpp3DomDriver
extends AbstractXppDomDriver {
    public Xpp3DomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    public Xpp3DomDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XmlPullParser createParser() {
        return new MXParser();
    }
}

