/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import java.util.Locale;
import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XMLStringFactoryImpl;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XString
extends XObject
implements XMLString {
    static final long serialVersionUID = 2020470518395094525L;
    public static final XString EMPTYSTRING = new XString("");

    protected XString(Object val) {
        super(val);
    }

    public XString(String val) {
        super(val);
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public String getTypeString() {
        return "#STRING";
    }

    @Override
    public boolean hasString() {
        return true;
    }

    @Override
    public double num() {
        return this.toDouble();
    }

    @Override
    public double toDouble() {
        XMLString s = this.trim();
        double result = Double.NaN;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '-' || c == '.' || c >= '0' && c <= '9') continue;
            return result;
        }
        try {
            result = Double.parseDouble(s.toString());
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return result;
    }

    @Override
    public boolean bool() {
        return this.str().length() > 0;
    }

    @Override
    public XMLString xstr() {
        return this;
    }

    @Override
    public String str() {
        return null != this.m_obj ? (String)this.m_obj : "";
    }

    @Override
    public int rtf(XPathContext support) {
        DTM frag = support.createDocumentFragment();
        frag.appendTextChild(this.str());
        return frag.getDocument();
    }

    @Override
    public void dispatchCharactersEvents(ContentHandler ch) throws SAXException {
        String str = this.str();
        ch.characters(str.toCharArray(), 0, str.length());
    }

    @Override
    public void dispatchAsComment(LexicalHandler lh) throws SAXException {
        String str = this.str();
        lh.comment(str.toCharArray(), 0, str.length());
    }

    @Override
    public int length() {
        return this.str().length();
    }

    @Override
    public char charAt(int index) {
        return this.str().charAt(index);
    }

    @Override
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        this.str().getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    @Override
    public boolean equals(XObject obj2) {
        int t = obj2.getType();
        try {
            if (4 == t) {
                return obj2.equals(this);
            }
            if (1 == t) {
                return obj2.bool() == this.bool();
            }
            if (2 == t) {
                return obj2.num() == this.num();
            }
        }
        catch (TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
        return this.xstr().equals(obj2.xstr());
    }

    @Override
    public boolean equals(String obj2) {
        return this.str().equals(obj2);
    }

    @Override
    public boolean equals(XMLString obj2) {
        if (obj2 != null) {
            if (!obj2.hasString()) {
                return obj2.equals(this.str());
            }
            return this.str().equals(obj2.toString());
        }
        return false;
    }

    @Override
    public boolean equals(Object obj2) {
        if (null == obj2) {
            return false;
        }
        if (obj2 instanceof XNodeSet) {
            return obj2.equals(this);
        }
        if (obj2 instanceof XNumber) {
            return obj2.equals(this);
        }
        return this.str().equals(obj2.toString());
    }

    @Override
    public boolean equalsIgnoreCase(String anotherString) {
        return this.str().equalsIgnoreCase(anotherString);
    }

    @Override
    public int compareTo(XMLString xstr) {
        int len1 = this.length();
        int len2 = xstr.length();
        int n = Math.min(len1, len2);
        int i = 0;
        int j = 0;
        while (n-- != 0) {
            char c2;
            char c1 = this.charAt(i);
            if (c1 != (c2 = xstr.charAt(j))) {
                return c1 - c2;
            }
            ++i;
            ++j;
        }
        return len1 - len2;
    }

    @Override
    public int compareToIgnoreCase(XMLString str) {
        throw new WrappedRuntimeException(new NoSuchMethodException("Java 1.2 method, not yet implemented"));
    }

    @Override
    public boolean startsWith(String prefix, int toffset) {
        return this.str().startsWith(prefix, toffset);
    }

    @Override
    public boolean startsWith(String prefix) {
        return this.startsWith(prefix, 0);
    }

    @Override
    public boolean startsWith(XMLString prefix, int toffset) {
        int to = toffset;
        int tlim = this.length();
        int po = 0;
        int pc = prefix.length();
        if (toffset < 0 || toffset > tlim - pc) {
            return false;
        }
        while (--pc >= 0) {
            if (this.charAt(to) != prefix.charAt(po)) {
                return false;
            }
            ++to;
            ++po;
        }
        return true;
    }

    @Override
    public boolean startsWith(XMLString prefix) {
        return this.startsWith(prefix, 0);
    }

    @Override
    public boolean endsWith(String suffix) {
        return this.str().endsWith(suffix);
    }

    @Override
    public int hashCode() {
        return this.str().hashCode();
    }

    @Override
    public int indexOf(int ch) {
        return this.str().indexOf(ch);
    }

    @Override
    public int indexOf(int ch, int fromIndex) {
        return this.str().indexOf(ch, fromIndex);
    }

    @Override
    public int lastIndexOf(int ch) {
        return this.str().lastIndexOf(ch);
    }

    @Override
    public int lastIndexOf(int ch, int fromIndex) {
        return this.str().lastIndexOf(ch, fromIndex);
    }

    @Override
    public int indexOf(String str) {
        return this.str().indexOf(str);
    }

    @Override
    public int indexOf(XMLString str) {
        return this.str().indexOf(str.toString());
    }

    @Override
    public int indexOf(String str, int fromIndex) {
        return this.str().indexOf(str, fromIndex);
    }

    @Override
    public int lastIndexOf(String str) {
        return this.str().lastIndexOf(str);
    }

    @Override
    public int lastIndexOf(String str, int fromIndex) {
        return this.str().lastIndexOf(str, fromIndex);
    }

    @Override
    public XMLString substring(int beginIndex) {
        return new XString(this.str().substring(beginIndex));
    }

    @Override
    public XMLString substring(int beginIndex, int endIndex) {
        return new XString(this.str().substring(beginIndex, endIndex));
    }

    @Override
    public XMLString concat(String str) {
        return new XString(this.str().concat(str));
    }

    @Override
    public XMLString toLowerCase(Locale locale) {
        return new XString(this.str().toLowerCase(locale));
    }

    @Override
    public XMLString toLowerCase() {
        return new XString(this.str().toLowerCase());
    }

    @Override
    public XMLString toUpperCase(Locale locale) {
        return new XString(this.str().toUpperCase(locale));
    }

    @Override
    public XMLString toUpperCase() {
        return new XString(this.str().toUpperCase());
    }

    @Override
    public XMLString trim() {
        return new XString(this.str().trim());
    }

    private static boolean isSpace(char ch) {
        return XMLCharacterRecognizer.isWhiteSpace(ch);
    }

    @Override
    public XMLString fixWhiteSpace(boolean trimHead, boolean trimTail, boolean doublePunctuationSpaces) {
        int s;
        int len = this.length();
        char[] buf = new char[len];
        this.getChars(0, len, buf, 0);
        boolean edit = false;
        for (s = 0; s < len && !XString.isSpace(buf[s]); ++s) {
        }
        int d = s;
        boolean pres = false;
        while (s < len) {
            char c = buf[s];
            if (XString.isSpace(c)) {
                if (!pres) {
                    if (' ' != c) {
                        edit = true;
                    }
                    buf[d++] = 32;
                    if (doublePunctuationSpaces && s != 0) {
                        char prevChar = buf[s - 1];
                        if (prevChar != '.' && prevChar != '!' && prevChar != '?') {
                            pres = true;
                        }
                    } else {
                        pres = true;
                    }
                } else {
                    edit = true;
                    pres = true;
                }
            } else {
                buf[d++] = c;
                pres = false;
            }
            ++s;
        }
        if (trimTail && 1 <= d && ' ' == buf[d - 1]) {
            edit = true;
            --d;
        }
        int start = 0;
        if (trimHead && 0 < d && ' ' == buf[0]) {
            edit = true;
            ++start;
        }
        XMLStringFactory xsf = XMLStringFactoryImpl.getFactory();
        return edit ? xsf.newstr(new String(buf, start, d - start)) : this;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        visitor.visitStringLiteral(owner, this);
    }
}

