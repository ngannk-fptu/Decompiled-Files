/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.wrapper;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public interface XmlSerializerWrapper
extends XmlSerializer {
    public static final String NO_NAMESPACE = "";
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    public String getCurrentNamespaceForElements();

    public String setCurrentNamespaceForElements(String var1);

    public XmlSerializerWrapper attribute(String var1, String var2) throws IOException, IllegalArgumentException, IllegalStateException;

    public XmlSerializerWrapper startTag(String var1) throws IOException, IllegalArgumentException, IllegalStateException;

    public XmlSerializerWrapper endTag(String var1) throws IOException, IllegalArgumentException, IllegalStateException;

    public XmlSerializerWrapper element(String var1, String var2, String var3) throws IOException, XmlPullParserException;

    public XmlSerializerWrapper element(String var1, String var2) throws IOException, XmlPullParserException;

    public void fragment(String var1) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException;

    public void event(XmlPullParser var1) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException;

    public String escapeText(String var1) throws IllegalArgumentException;

    public String escapeAttributeValue(String var1) throws IllegalArgumentException;
}

