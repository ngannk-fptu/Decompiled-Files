/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.objects;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xpath.objects.XString;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class XStringForChars
extends XString {
    static final long serialVersionUID = -2235248887220850467L;
    int m_start;
    int m_length;
    protected String m_strCache = null;

    public XStringForChars(char[] val, int start, int length) {
        super(val);
        this.m_start = start;
        this.m_length = length;
        if (null == val) {
            throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null));
        }
    }

    private XStringForChars(String val) {
        super(val);
        throw new IllegalArgumentException(XSLMessages.createXPATHMessage("ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING", null));
    }

    public FastStringBuffer fsb() {
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS", null));
    }

    @Override
    public void appendToFsb(FastStringBuffer fsb) {
        fsb.append((char[])this.m_obj, this.m_start, this.m_length);
    }

    @Override
    public boolean hasString() {
        return null != this.m_strCache;
    }

    @Override
    public String str() {
        if (null == this.m_strCache) {
            this.m_strCache = new String((char[])this.m_obj, this.m_start, this.m_length);
        }
        return this.m_strCache;
    }

    @Override
    public Object object() {
        return this.str();
    }

    @Override
    public void dispatchCharactersEvents(ContentHandler ch) throws SAXException {
        ch.characters((char[])this.m_obj, this.m_start, this.m_length);
    }

    @Override
    public void dispatchAsComment(LexicalHandler lh) throws SAXException {
        lh.comment((char[])this.m_obj, this.m_start, this.m_length);
    }

    @Override
    public int length() {
        return this.m_length;
    }

    @Override
    public char charAt(int index) {
        return ((char[])this.m_obj)[index + this.m_start];
    }

    @Override
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        System.arraycopy((char[])this.m_obj, this.m_start + srcBegin, dst, dstBegin, srcEnd);
    }
}

