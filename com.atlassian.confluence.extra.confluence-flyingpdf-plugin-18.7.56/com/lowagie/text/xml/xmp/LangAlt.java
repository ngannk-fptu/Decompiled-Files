/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpSchema;
import java.util.Enumeration;
import java.util.Properties;

public class LangAlt
extends Properties {
    private static final long serialVersionUID = 4396971487200843099L;
    public static final String DEFAULT = "x-default";

    public LangAlt(String defaultValue) {
        this.addLanguage(DEFAULT, defaultValue);
    }

    public LangAlt() {
    }

    public void addLanguage(String language, String value) {
        this.setProperty(language, XmpSchema.escape(value));
    }

    protected void process(StringBuffer buf, Object lang) {
        buf.append("<rdf:li xml:lang=\"");
        buf.append(lang);
        buf.append("\" >");
        buf.append(this.get(lang));
        buf.append("</rdf:li>");
    }

    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<rdf:Alt>");
        Enumeration<?> e = this.propertyNames();
        while (e.hasMoreElements()) {
            this.process(sb, e.nextElement());
        }
        sb.append("</rdf:Alt>");
        return sb.toString();
    }
}

