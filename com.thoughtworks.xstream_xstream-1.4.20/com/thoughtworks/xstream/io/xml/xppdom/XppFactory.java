/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.xmlpull.v1.XmlPullParser
 *  org.xmlpull.v1.XmlPullParserException
 *  org.xmlpull.v1.XmlPullParserFactory
 */
package com.thoughtworks.xstream.io.xml.xppdom;

import com.thoughtworks.xstream.io.xml.xppdom.XppDom;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XppFactory {
    public static XmlPullParser createDefaultParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        return factory.newPullParser();
    }

    public static XppDom buildDom(String xml) throws XmlPullParserException, IOException {
        return XppFactory.buildDom(new StringReader(xml));
    }

    public static XppDom buildDom(Reader r) throws XmlPullParserException, IOException {
        XmlPullParser parser = XppFactory.createDefaultParser();
        parser.setInput(r);
        return XppDom.build(parser);
    }

    public static XppDom buildDom(InputStream in, String encoding) throws XmlPullParserException, IOException {
        XmlPullParser parser = XppFactory.createDefaultParser();
        parser.setInput(in, encoding);
        return XppDom.build(parser);
    }
}

