/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.LangAlt;
import com.lowagie.text.xml.xmp.XmpArray;
import java.util.Enumeration;
import java.util.Properties;

public abstract class XmpSchema
extends Properties {
    private static final long serialVersionUID = -176374295948945272L;
    protected String xmlns;

    public XmpSchema(String xmlns) {
        this.xmlns = xmlns;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Enumeration<?> e = this.propertyNames();
        while (e.hasMoreElements()) {
            this.process(buf, e.nextElement());
        }
        return buf.toString();
    }

    protected void process(StringBuffer buf, Object p) {
        buf.append('<');
        buf.append(p);
        buf.append('>');
        buf.append(this.get(p));
        buf.append("</");
        buf.append(p);
        buf.append('>');
    }

    public String getXmlns() {
        return this.xmlns;
    }

    public Object addProperty(String key, String value) {
        return this.setProperty(key, value);
    }

    @Override
    public Object setProperty(String key, String value) {
        return super.setProperty(key, XmpSchema.escape(value));
    }

    public Object setProperty(String key, XmpArray value) {
        return super.setProperty(key, value.toString());
    }

    public Object setProperty(String key, LangAlt value) {
        return super.setProperty(key, value.toString());
    }

    public static String escape(String content) {
        StringBuilder buf = new StringBuilder();
        block7: for (int i = 0; i < content.length(); ++i) {
            switch (content.charAt(i)) {
                case '<': {
                    buf.append("&lt;");
                    continue block7;
                }
                case '>': {
                    buf.append("&gt;");
                    continue block7;
                }
                case '\'': {
                    buf.append("&apos;");
                    continue block7;
                }
                case '\"': {
                    buf.append("&quot;");
                    continue block7;
                }
                case '&': {
                    buf.append("&amp;");
                    continue block7;
                }
                default: {
                    buf.append(content.charAt(i));
                }
            }
        }
        return buf.toString();
    }
}

