/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml.xppdom;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;
import java.io.Reader;
import org.xmlpull.mxp1.MXParser;

public class Xpp3DomBuilder {
    public static Xpp3Dom build(Reader reader) throws Exception {
        MXParser parser = new MXParser();
        parser.setInput(reader);
        try {
            Xpp3Dom xpp3Dom = (Xpp3Dom)XppDom.build(parser);
            return xpp3Dom;
        }
        finally {
            reader.close();
        }
    }
}

