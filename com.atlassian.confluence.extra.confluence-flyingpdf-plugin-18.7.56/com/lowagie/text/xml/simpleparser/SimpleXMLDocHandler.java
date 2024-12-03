/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.simpleparser;

import java.util.HashMap;
import java.util.Map;

public interface SimpleXMLDocHandler {
    public void startElement(String var1, HashMap var2);

    public void startElement(String var1, Map<String, String> var2);

    public void endElement(String var1);

    public void startDocument();

    public void endDocument();

    public void text(String var1);
}

