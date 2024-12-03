/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xpath.objects.XMLStringFactoryImpl;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XStringForFSB
extends XString {
    static final long serialVersionUID = -1533039186550674548L;
    int m_start;
    int m_length;
    protected String m_strCache = null;
    protected int m_hash = 0;

    public XStringForFSB(FastStringBuffer val, int start, int length) {
        super(val);
        this.m_start = start;
        this.m_length = length;
        if (null == val) {
            throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null));
        }
    }

    private XStringForFSB(String val) {
        super(val);
        throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FSB_CANNOT_TAKE_STRING", null));
    }

    public FastStringBuffer fsb() {
        return (FastStringBuffer)this.m_obj;
    }

    @Override
    public void appendToFsb(FastStringBuffer fsb) {
        fsb.append(this.str());
    }

    @Override
    public boolean hasString() {
        return null != this.m_strCache;
    }

    @Override
    public Object object() {
        return this.str();
    }

    @Override
    public String str() {
        if (null == this.m_strCache) {
            this.m_strCache = this.fsb().getString(this.m_start, this.m_length);
        }
        return this.m_strCache;
    }

    @Override
    public void dispatchCharactersEvents(ContentHandler ch) throws SAXException {
        this.fsb().sendSAXcharacters(ch, this.m_start, this.m_length);
    }

    @Override
    public void dispatchAsComment(LexicalHandler lh) throws SAXException {
        this.fsb().sendSAXComment(lh, this.m_start, this.m_length);
    }

    @Override
    public int length() {
        return this.m_length;
    }

    @Override
    public char charAt(int index) {
        return this.fsb().charAt(this.m_start + index);
    }

    @Override
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        int n = srcEnd - srcBegin;
        if (n > this.m_length) {
            n = this.m_length;
        }
        if (n > dst.length - dstBegin) {
            n = dst.length - dstBegin;
        }
        int end = srcBegin + this.m_start + n;
        int d = dstBegin;
        FastStringBuffer fsb = this.fsb();
        for (int i = srcBegin + this.m_start; i < end; ++i) {
            dst[d++] = fsb.charAt(i);
        }
    }

    @Override
    public boolean equals(XMLString obj2) {
        if (this == obj2) {
            return true;
        }
        int n = this.m_length;
        if (n == obj2.length()) {
            FastStringBuffer fsb = this.fsb();
            int i = this.m_start;
            int j = 0;
            while (n-- != 0) {
                if (fsb.charAt(i) != obj2.charAt(j)) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(XObject obj2) {
        if (this == obj2) {
            return true;
        }
        if (obj2.getType() == 2) {
            return obj2.equals(this);
        }
        int n = this.m_length;
        String str = obj2.str();
        if (n == str.length()) {
            FastStringBuffer fsb = this.fsb();
            int i = this.m_start;
            int j = 0;
            while (n-- != 0) {
                if (fsb.charAt(i) != str.charAt(j)) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(String anotherString) {
        int n = this.m_length;
        if (n == anotherString.length()) {
            FastStringBuffer fsb = this.fsb();
            int i = this.m_start;
            int j = 0;
            while (n-- != 0) {
                if (fsb.charAt(i) != anotherString.charAt(j)) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj2) {
        if (null == obj2) {
            return false;
        }
        if (obj2 instanceof XNumber) {
            return obj2.equals(this);
        }
        if (obj2 instanceof XNodeSet) {
            return obj2.equals(this);
        }
        if (obj2 instanceof XStringForFSB) {
            return this.equals((XMLString)obj2);
        }
        return this.equals(obj2.toString());
    }

    @Override
    public boolean equalsIgnoreCase(String anotherString) {
        return this.m_length == anotherString.length() ? this.str().equalsIgnoreCase(anotherString) : false;
    }

    @Override
    public int compareTo(XMLString xstr) {
        int len1 = this.m_length;
        int len2 = xstr.length();
        int n = Math.min(len1, len2);
        FastStringBuffer fsb = this.fsb();
        int i = this.m_start;
        int j = 0;
        while (n-- != 0) {
            char c2;
            char c1 = fsb.charAt(i);
            if (c1 != (c2 = xstr.charAt(j))) {
                return c1 - c2;
            }
            ++i;
            ++j;
        }
        return len1 - len2;
    }

    @Override
    public int compareToIgnoreCase(XMLString xstr) {
        int len1 = this.m_length;
        int len2 = xstr.length();
        int n = Math.min(len1, len2);
        FastStringBuffer fsb = this.fsb();
        int i = this.m_start;
        int j = 0;
        while (n-- != 0) {
            char c2;
            char c1 = Character.toLowerCase(fsb.charAt(i));
            if (c1 != (c2 = Character.toLowerCase(xstr.charAt(j)))) {
                return c1 - c2;
            }
            ++i;
            ++j;
        }
        return len1 - len2;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean startsWith(XMLString prefix, int toffset) {
        FastStringBuffer fsb = this.fsb();
        int to = this.m_start + toffset;
        int tlim = this.m_start + this.m_length;
        int po = 0;
        int pc = prefix.length();
        if (toffset < 0 || toffset > this.m_length - pc) {
            return false;
        }
        while (--pc >= 0) {
            if (fsb.charAt(to) != prefix.charAt(po)) {
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
    public int indexOf(int ch) {
        return this.indexOf(ch, 0);
    }

    @Override
    public int indexOf(int ch, int fromIndex) {
        int max = this.m_start + this.m_length;
        FastStringBuffer fsb = this.fsb();
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= this.m_length) {
            return -1;
        }
        for (int i = this.m_start + fromIndex; i < max; ++i) {
            if (fsb.charAt(i) != ch) continue;
            return i - this.m_start;
        }
        return -1;
    }

    @Override
    public XMLString substring(int beginIndex) {
        int len = this.m_length - beginIndex;
        if (len <= 0) {
            return XString.EMPTYSTRING;
        }
        int start = this.m_start + beginIndex;
        return new XStringForFSB(this.fsb(), start, len);
    }

    @Override
    public XMLString substring(int beginIndex, int endIndex) {
        int len = endIndex - beginIndex;
        if (len > this.m_length) {
            len = this.m_length;
        }
        if (len <= 0) {
            return XString.EMPTYSTRING;
        }
        int start = this.m_start + beginIndex;
        return new XStringForFSB(this.fsb(), start, len);
    }

    @Override
    public XMLString concat(String str) {
        return new XString(this.str().concat(str));
    }

    @Override
    public XMLString trim() {
        return this.fixWhiteSpace(true, true, false);
    }

    private static boolean isSpace(char ch) {
        return XMLCharacterRecognizer.isWhiteSpace(ch);
    }

    @Override
    public XMLString fixWhiteSpace(boolean trimHead, boolean trimTail, boolean doublePunctuationSpaces) {
        int end = this.m_length + this.m_start;
        char[] buf = new char[this.m_length];
        FastStringBuffer fsb = this.fsb();
        boolean edit = false;
        int d = 0;
        boolean pres = false;
        for (int s = this.m_start; s < end; ++s) {
            char c = fsb.charAt(s);
            if (XStringForFSB.isSpace(c)) {
                if (!pres) {
                    if (' ' != c) {
                        edit = true;
                    }
                    buf[d++] = 32;
                    if (doublePunctuationSpaces && d != 0) {
                        char prevChar = buf[d - 1];
                        if (prevChar == '.' || prevChar == '!' || prevChar == '?') continue;
                        pres = true;
                        continue;
                    }
                    pres = true;
                    continue;
                }
                edit = true;
                pres = true;
                continue;
            }
            buf[d++] = c;
            pres = false;
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
        return edit ? xsf.newstr(buf, start, d - start) : this;
    }

    @Override
    public double toDouble() {
        char c;
        int i;
        if (this.m_length == 0) {
            return Double.NaN;
        }
        String valueString = this.fsb().getString(this.m_start, this.m_length);
        for (i = 0; i < this.m_length && XMLCharacterRecognizer.isWhiteSpace(valueString.charAt(i)); ++i) {
        }
        if (i == this.m_length) {
            return Double.NaN;
        }
        if (valueString.charAt(i) == '-') {
            ++i;
        }
        while (i < this.m_length && ((c = valueString.charAt(i)) == '.' || c >= '0' && c <= '9')) {
            ++i;
        }
        while (i < this.m_length && XMLCharacterRecognizer.isWhiteSpace(valueString.charAt(i))) {
            ++i;
        }
        if (i != this.m_length) {
            return Double.NaN;
        }
        try {
            return new Double(valueString);
        }
        catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }
}

