/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XMLString {
    private final StringBuilder builder_ = new StringBuilder();

    public XMLString() {
    }

    public XMLString(char[] ch, int offset, int length) {
        this();
        this.builder_.append(ch, offset, length);
    }

    public void append(char c) {
        this.builder_.append(c);
    }

    public void append(String str) {
        this.builder_.append(str);
    }

    public void append(XMLString xmlStr) {
        this.builder_.append((CharSequence)xmlStr.builder_);
    }

    public void append(char[] str, int offset, int len) {
        this.builder_.append(str, offset, len);
    }

    public char charAt(int index) {
        return this.builder_.charAt(index);
    }

    public int length() {
        return this.builder_.length();
    }

    public boolean endsWith(String string) {
        int l = string.length();
        if (this.builder_.length() < l) {
            return false;
        }
        return string.equals(this.builder_.substring(this.length() - l));
    }

    public void reduceToContent(String startMarker, String endMarker) {
        char c;
        int i;
        int startContent = -1;
        int startMarkerLength = startMarker.length();
        int endMarkerLength = endMarker.length();
        for (i = 0; i < this.builder_.length() - startMarkerLength - endMarkerLength; ++i) {
            c = this.builder_.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == startMarker.charAt(0) && startMarker.equals(this.builder_.substring(i, i + startMarkerLength))) {
                startContent = i + startMarkerLength;
                break;
            }
            return;
        }
        if (startContent == -1) {
            return;
        }
        for (i = this.builder_.length() - 1; i > startContent + endMarkerLength; --i) {
            c = this.builder_.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == endMarker.charAt(endMarkerLength - 1) && endMarker.equals(this.builder_.substring(i - endMarkerLength + 1, i + 1))) {
                this.builder_.delete(i - endMarkerLength + 1, this.builder_.length());
                if (startContent > 0) {
                    this.builder_.delete(0, startContent);
                }
                return;
            }
            return;
        }
    }

    @Deprecated
    public char[] getChars() {
        char[] chars = new char[this.builder_.length()];
        this.builder_.getChars(0, this.builder_.length(), chars, 0);
        return chars;
    }

    public XMLString clone() {
        XMLString clone = new XMLString();
        clone.builder_.append((CharSequence)this.builder_);
        return clone;
    }

    public XMLString clear() {
        this.builder_.setLength(0);
        return this;
    }

    public boolean isWhitespace() {
        for (int i = 0; i < this.builder_.length(); ++i) {
            if (Character.isWhitespace(this.builder_.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public void trimWhitespaceAtEnd() {
        for (int i = this.builder_.length() - 1; i > -1; --i) {
            if (Character.isWhitespace(this.builder_.charAt(i))) continue;
            this.builder_.delete(i + 1, this.builder_.length());
            return;
        }
        this.clear();
    }

    public void appendTo(StringBuilder stringBuilder) {
        stringBuilder.append((CharSequence)this.builder_);
    }

    public String toString() {
        return this.builder_.toString();
    }

    public void characters(ContentHandler contentHandler) throws SAXException {
        contentHandler.characters(this.getChars(), 0, this.length());
    }

    public void ignorableWhitespace(ContentHandler contentHandler) throws SAXException {
        contentHandler.ignorableWhitespace(this.getChars(), 0, this.length());
    }

    public void comment(LexicalHandler lexicalHandler) throws SAXException {
        lexicalHandler.comment(this.getChars(), 0, this.length());
    }
}

