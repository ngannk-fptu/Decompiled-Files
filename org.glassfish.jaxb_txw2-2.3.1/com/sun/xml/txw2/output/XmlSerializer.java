/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

public interface XmlSerializer {
    public void startDocument();

    public void beginStartTag(String var1, String var2, String var3);

    public void writeAttribute(String var1, String var2, String var3, StringBuilder var4);

    public void writeXmlns(String var1, String var2);

    public void endStartTag(String var1, String var2, String var3);

    public void endTag();

    public void text(StringBuilder var1);

    public void cdata(StringBuilder var1);

    public void comment(StringBuilder var1);

    public void endDocument();

    public void flush();
}

