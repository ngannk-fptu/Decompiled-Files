/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.EmptySerializer
 */
package org.apache.xalan.xsltc.runtime;

import org.apache.xml.serializer.EmptySerializer;
import org.xml.sax.SAXException;

public final class StringValueHandler
extends EmptySerializer {
    private StringBuffer _buffer = new StringBuffer();
    private String _str = null;
    private static final String EMPTY_STR = "";
    private boolean m_escaping = false;
    private int _nestedLevel = 0;

    public void characters(char[] ch, int off, int len) throws SAXException {
        if (this._nestedLevel > 0) {
            return;
        }
        if (this._str != null) {
            this._buffer.append(this._str);
            this._str = null;
        }
        this._buffer.append(ch, off, len);
    }

    public String getValue() {
        if (this._buffer.length() != 0) {
            String result = this._buffer.toString();
            this._buffer.setLength(0);
            return result;
        }
        String result = this._str;
        this._str = null;
        return result != null ? result : EMPTY_STR;
    }

    public void characters(String characters) throws SAXException {
        if (this._nestedLevel > 0) {
            return;
        }
        if (this._str == null && this._buffer.length() == 0) {
            this._str = characters;
        } else {
            if (this._str != null) {
                this._buffer.append(this._str);
                this._str = null;
            }
            this._buffer.append(characters);
        }
    }

    public void startElement(String qname) throws SAXException {
        ++this._nestedLevel;
    }

    public void endElement(String qname) throws SAXException {
        --this._nestedLevel;
    }

    public boolean setEscaping(boolean bool) {
        boolean oldEscaping = this.m_escaping;
        this.m_escaping = bool;
        return bool;
    }

    public String getValueOfPI() {
        String value = this.getValue();
        if (value.indexOf("?>") > 0) {
            int n = value.length();
            StringBuffer valueOfPI = new StringBuffer();
            int i = 0;
            while (i < n) {
                char ch;
                if ((ch = value.charAt(i++)) == '?' && value.charAt(i) == '>') {
                    valueOfPI.append("? >");
                    ++i;
                    continue;
                }
                valueOfPI.append(ch);
            }
            return valueOfPI.toString();
        }
        return value;
    }
}

