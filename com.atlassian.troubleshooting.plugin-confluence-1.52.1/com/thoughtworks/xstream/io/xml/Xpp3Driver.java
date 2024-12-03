/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class Xpp3Driver
extends AbstractXppDriver {
    public Xpp3Driver() {
        super(new XmlFriendlyNameCoder());
    }

    public Xpp3Driver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XmlPullParser createParser() {
        return new MXParser();
    }
}

