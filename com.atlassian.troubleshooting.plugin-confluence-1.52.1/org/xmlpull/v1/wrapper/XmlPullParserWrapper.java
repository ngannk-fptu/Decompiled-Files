/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.wrapper;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public interface XmlPullParserWrapper
extends XmlPullParser {
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    public String getAttributeValue(String var1);

    public String getPITarget() throws IllegalStateException;

    public String getPIData() throws IllegalStateException;

    public String getRequiredAttributeValue(String var1) throws IOException, XmlPullParserException;

    public String getRequiredAttributeValue(String var1, String var2) throws IOException, XmlPullParserException;

    public String getRequiredElementText(String var1, String var2) throws IOException, XmlPullParserException;

    public boolean isNil() throws IOException, XmlPullParserException;

    public boolean matches(int var1, String var2, String var3) throws XmlPullParserException;

    public void nextStartTag() throws XmlPullParserException, IOException;

    public void nextStartTag(String var1) throws XmlPullParserException, IOException;

    public void nextStartTag(String var1, String var2) throws XmlPullParserException, IOException;

    public void nextEndTag() throws XmlPullParserException, IOException;

    public void nextEndTag(String var1) throws XmlPullParserException, IOException;

    public void nextEndTag(String var1, String var2) throws XmlPullParserException, IOException;

    public String nextText(String var1, String var2) throws IOException, XmlPullParserException;

    public void skipSubTree() throws XmlPullParserException, IOException;
}

