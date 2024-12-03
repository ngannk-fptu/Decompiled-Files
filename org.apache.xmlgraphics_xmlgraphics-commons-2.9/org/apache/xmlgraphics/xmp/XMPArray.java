/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlgraphics.xmp.XMPArrayType;
import org.apache.xmlgraphics.xmp.XMPComplexValue;
import org.apache.xmlgraphics.xmp.XMPStructure;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMPArray
extends XMPComplexValue {
    private XMPArrayType type;
    private List values = new ArrayList();
    private List xmllang = new ArrayList();
    private List<String> parseTypes = new ArrayList<String>();

    public XMPArray(XMPArrayType type) {
        this.type = type;
    }

    public XMPArrayType getType() {
        return this.type;
    }

    public Object getValue(int idx) {
        return this.values.get(idx);
    }

    public XMPStructure getStructure(int idx) {
        return (XMPStructure)this.values.get(idx);
    }

    @Override
    public Object getSimpleValue() {
        if (this.values.size() == 1) {
            return this.getValue(0);
        }
        if (this.values.size() > 1) {
            return this.getLangValue("x-default");
        }
        return null;
    }

    private String getParentLanguage(String lang) {
        if (lang == null) {
            return null;
        }
        int pos = lang.indexOf(45);
        if (pos > 0) {
            String parent = lang.substring(0, pos);
            return parent;
        }
        return null;
    }

    public String getLangValue(String lang) {
        String v = null;
        String valueForParentLanguage = null;
        int c = this.values.size();
        for (int i = 0; i < c; ++i) {
            String parent;
            String l = (String)this.xmllang.get(i);
            if (l == null && lang == null || l != null && l.equals(lang)) {
                v = this.values.get(i).toString();
                break;
            }
            if (l == null || lang == null || (parent = this.getParentLanguage(l)) == null || !parent.equals(lang)) continue;
            valueForParentLanguage = this.values.get(i).toString();
        }
        if (lang != null && v == null && valueForParentLanguage != null) {
            v = valueForParentLanguage;
        }
        if (lang == null && v == null && (v = this.getLangValue("x-default")) == null && this.values.size() > 0) {
            v = this.getValue(0).toString();
        }
        return v;
    }

    public String removeLangValue(String lang) {
        if (lang == null || "".equals(lang)) {
            lang = "x-default";
        }
        int c = this.values.size();
        for (int i = 0; i < c; ++i) {
            String l = (String)this.xmllang.get(i);
            if ((!"x-default".equals(lang) || l != null) && !lang.equals(l)) continue;
            String value = (String)this.values.remove(i);
            this.xmllang.remove(i);
            return value;
        }
        return null;
    }

    public void add(Object value, String lang, String parseType) {
        this.values.add(value);
        this.xmllang.add(lang);
        this.parseTypes.add(parseType);
    }

    public void add(Object value, String lang) {
        this.add(value, lang, null);
    }

    public void add(Object value) {
        this.add(value, null, null);
    }

    public boolean remove(String value) {
        int idx = this.values.indexOf(value);
        if (idx >= 0) {
            this.values.remove(idx);
            this.xmllang.remove(idx);
            this.parseTypes.remove(idx);
            return true;
        }
        return false;
    }

    public int getSize() {
        return this.values.size();
    }

    public boolean isEmpty() {
        return this.getSize() == 0;
    }

    public Object[] toObjectArray() {
        Object[] res = new Object[this.getSize()];
        int c = res.length;
        for (int i = 0; i < c; ++i) {
            res[i] = this.getValue(i);
        }
        return res;
    }

    @Override
    public void toSAX(ContentHandler handler) throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        handler.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", this.type.getName(), "rdf:" + this.type.getName(), atts);
        int c = this.values.size();
        for (int i = 0; i < c; ++i) {
            String parseType;
            String lang = (String)this.xmllang.get(i);
            atts.clear();
            Object v = this.values.get(i);
            if (lang != null) {
                atts.addAttribute("http://www.w3.org/XML/1998/namespace", "lang", "xml:lang", "CDATA", lang);
            }
            if (v instanceof URI) {
                atts.addAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource", "rdf:resource", "CDATA", ((URI)v).toString());
            }
            if ((parseType = this.parseTypes.get(i)) != null) {
                atts.addAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType", "rdf:parseType", "CDATA", parseType);
            }
            handler.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "li", "rdf:li", atts);
            if (v instanceof XMPComplexValue) {
                ((XMPComplexValue)v).toSAX(handler);
            } else if (!(v instanceof URI)) {
                String value = (String)this.values.get(i);
                char[] chars = value.toCharArray();
                handler.characters(chars, 0, chars.length);
            }
            handler.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "li", "rdf:li");
        }
        handler.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", this.type.getName(), "rdf:" + this.type.getName());
    }

    public String toString() {
        return "XMP array: " + this.type + ", " + this.getSize();
    }
}

