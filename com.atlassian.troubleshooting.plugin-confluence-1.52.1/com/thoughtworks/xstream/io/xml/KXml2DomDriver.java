/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.kxml2.io.KXmlParser
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.AbstractXppDomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

public class KXml2DomDriver
extends AbstractXppDomDriver {
    public KXml2DomDriver() {
        super(new XmlFriendlyNameCoder());
    }

    public KXml2DomDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XmlPullParser createParser() {
        return new KXmlParser();
    }
}

