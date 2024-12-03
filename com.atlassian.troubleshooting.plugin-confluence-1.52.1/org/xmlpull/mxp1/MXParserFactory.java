/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.mxp1;

import java.util.Enumeration;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.mxp1.MXParserCachingStrings;
import org.xmlpull.mxp1_serializer.MXSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class MXParserFactory
extends XmlPullParserFactory {
    protected static boolean stringCachedParserAvailable = true;

    public XmlPullParser newPullParser() throws XmlPullParserException {
        MXParser pp = null;
        if (stringCachedParserAvailable) {
            try {
                pp = new MXParserCachingStrings();
            }
            catch (Exception ex) {
                stringCachedParserAvailable = false;
            }
        }
        if (pp == null) {
            pp = new MXParser();
        }
        Enumeration e = this.features.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            Boolean value = (Boolean)this.features.get(key);
            if (value == null || !value.booleanValue()) continue;
            pp.setFeature(key, true);
        }
        return pp;
    }

    public XmlSerializer newSerializer() throws XmlPullParserException {
        return new MXSerializer();
    }
}

