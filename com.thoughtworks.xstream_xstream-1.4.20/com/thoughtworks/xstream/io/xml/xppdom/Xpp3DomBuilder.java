/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.xmlpull.mxp1.MXParser
 *  org.xmlpull.v1.XmlPullParser
 */
package com.thoughtworks.xstream.io.xml.xppdom;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;
import java.io.Reader;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

public class Xpp3DomBuilder {
    public static Xpp3Dom build(Reader reader) throws Exception {
        MXParser parser = new MXParser();
        parser.setInput(reader);
        try {
            Xpp3Dom xpp3Dom = (Xpp3Dom)XppDom.build((XmlPullParser)parser);
            return xpp3Dom;
        }
        finally {
            reader.close();
        }
    }
}

