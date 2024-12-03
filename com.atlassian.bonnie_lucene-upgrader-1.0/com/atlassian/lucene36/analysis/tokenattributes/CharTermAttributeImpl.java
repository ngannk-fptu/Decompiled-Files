/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis.tokenattributes;

import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.TermAttribute;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.AttributeImpl;
import com.atlassian.lucene36.util.AttributeReflector;
import java.io.Serializable;
import java.nio.CharBuffer;

public class CharTermAttributeImpl
extends AttributeImpl
implements CharTermAttribute,
TermAttribute,
Cloneable,
Serializable {
    private static int MIN_BUFFER_SIZE = 10;
    private char[] termBuffer = new char[ArrayUtil.oversize(MIN_BUFFER_SIZE, 2)];
    private int termLength = 0;

    @Deprecated
    public String term() {
        return new String(this.termBuffer, 0, this.termLength);
    }

    public final void copyBuffer(char[] buffer, int offset, int length) {
        this.growTermBuffer(length);
        System.arraycopy(buffer, offset, this.termBuffer, 0, length);
        this.termLength = length;
    }

    @Deprecated
    public void setTermBuffer(char[] buffer, int offset, int length) {
        this.copyBuffer(buffer, offset, length);
    }

    @Deprecated
    public void setTermBuffer(String buffer) {
        int length = buffer.length();
        this.growTermBuffer(length);
        buffer.getChars(0, length, this.termBuffer, 0);
        this.termLength = length;
    }

    @Deprecated
    public void setTermBuffer(String buffer, int offset, int length) {
        assert (offset <= buffer.length());
        assert (offset + length <= buffer.length());
        this.growTermBuffer(length);
        buffer.getChars(offset, offset + length, this.termBuffer, 0);
        this.termLength = length;
    }

    public final char[] buffer() {
        return this.termBuffer;
    }

    @Deprecated
    public char[] termBuffer() {
        return this.termBuffer;
    }

    public final char[] resizeBuffer(int newSize) {
        if (this.termBuffer.length < newSize) {
            char[] newCharBuffer = new char[ArrayUtil.oversize(newSize, 2)];
            System.arraycopy(this.termBuffer, 0, newCharBuffer, 0, this.termBuffer.length);
            this.termBuffer = newCharBuffer;
        }
        return this.termBuffer;
    }

    @Deprecated
    public char[] resizeTermBuffer(int newSize) {
        return this.resizeBuffer(newSize);
    }

    private void growTermBuffer(int newSize) {
        if (this.termBuffer.length < newSize) {
            this.termBuffer = new char[ArrayUtil.oversize(newSize, 2)];
        }
    }

    @Deprecated
    public int termLength() {
        return this.termLength;
    }

    public final CharTermAttribute setLength(int length) {
        if (length > this.termBuffer.length) {
            throw new IllegalArgumentException("length " + length + " exceeds the size of the termBuffer (" + this.termBuffer.length + ")");
        }
        this.termLength = length;
        return this;
    }

    public final CharTermAttribute setEmpty() {
        this.termLength = 0;
        return this;
    }

    @Deprecated
    public void setTermLength(int length) {
        this.setLength(length);
    }

    public final int length() {
        return this.termLength;
    }

    public final char charAt(int index) {
        if (index >= this.termLength) {
            throw new IndexOutOfBoundsException();
        }
        return this.termBuffer[index];
    }

    public final CharSequence subSequence(int start, int end) {
        if (start > this.termLength || end > this.termLength) {
            throw new IndexOutOfBoundsException();
        }
        return new String(this.termBuffer, start, end - start);
    }

    public final CharTermAttribute append(CharSequence csq) {
        if (csq == null) {
            return this.appendNull();
        }
        return this.append(csq, 0, csq.length());
    }

    public final CharTermAttribute append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        int len = end - start;
        int csqlen = csq.length();
        if (len < 0 || start > csqlen || end > csqlen) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return this;
        }
        this.resizeBuffer(this.termLength + len);
        if (len > 4) {
            if (csq instanceof String) {
                ((String)csq).getChars(start, end, this.termBuffer, this.termLength);
            } else if (csq instanceof StringBuilder) {
                ((StringBuilder)csq).getChars(start, end, this.termBuffer, this.termLength);
            } else if (csq instanceof CharTermAttribute) {
                System.arraycopy(((CharTermAttribute)csq).buffer(), start, this.termBuffer, this.termLength, len);
            } else if (csq instanceof CharBuffer && ((CharBuffer)csq).hasArray()) {
                CharBuffer cb = (CharBuffer)csq;
                System.arraycopy(cb.array(), cb.arrayOffset() + cb.position() + start, this.termBuffer, this.termLength, len);
            } else if (csq instanceof StringBuffer) {
                ((StringBuffer)csq).getChars(start, end, this.termBuffer, this.termLength);
            } else {
                while (start < end) {
                    this.termBuffer[this.termLength++] = csq.charAt(start++);
                }
                return this;
            }
            this.termLength += len;
            return this;
        }
        while (start < end) {
            this.termBuffer[this.termLength++] = csq.charAt(start++);
        }
        return this;
    }

    public final CharTermAttribute append(char c) {
        this.resizeBuffer((int)(this.termLength + 1))[this.termLength++] = c;
        return this;
    }

    public final CharTermAttribute append(String s) {
        if (s == null) {
            return this.appendNull();
        }
        int len = s.length();
        s.getChars(0, len, this.resizeBuffer(this.termLength + len), this.termLength);
        this.termLength += len;
        return this;
    }

    public final CharTermAttribute append(StringBuilder s) {
        if (s == null) {
            return this.appendNull();
        }
        int len = s.length();
        s.getChars(0, len, this.resizeBuffer(this.termLength + len), this.termLength);
        this.termLength += len;
        return this;
    }

    public final CharTermAttribute append(CharTermAttribute ta) {
        if (ta == null) {
            return this.appendNull();
        }
        int len = ta.length();
        System.arraycopy(ta.buffer(), 0, this.resizeBuffer(this.termLength + len), this.termLength, len);
        this.termLength += len;
        return this;
    }

    private CharTermAttribute appendNull() {
        this.resizeBuffer(this.termLength + 4);
        this.termBuffer[this.termLength++] = 110;
        this.termBuffer[this.termLength++] = 117;
        this.termBuffer[this.termLength++] = 108;
        this.termBuffer[this.termLength++] = 108;
        return this;
    }

    public int hashCode() {
        int code = this.termLength;
        code = code * 31 + ArrayUtil.hashCode(this.termBuffer, 0, this.termLength);
        return code;
    }

    public void clear() {
        this.termLength = 0;
    }

    public Object clone() {
        CharTermAttributeImpl t = (CharTermAttributeImpl)super.clone();
        t.termBuffer = new char[this.termLength];
        System.arraycopy(this.termBuffer, 0, t.termBuffer, 0, this.termLength);
        return t;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof CharTermAttributeImpl) {
            CharTermAttributeImpl o = (CharTermAttributeImpl)other;
            if (this.termLength != o.termLength) {
                return false;
            }
            for (int i = 0; i < this.termLength; ++i) {
                if (this.termBuffer[i] == o.termBuffer[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return new String(this.termBuffer, 0, this.termLength);
    }

    public void reflectWith(AttributeReflector reflector) {
        reflector.reflect(CharTermAttribute.class, "term", this.toString());
    }

    public void copyTo(AttributeImpl target) {
        if (target instanceof CharTermAttribute) {
            CharTermAttribute t = (CharTermAttribute)((Object)target);
            t.copyBuffer(this.termBuffer, 0, this.termLength);
        } else {
            TermAttribute t = (TermAttribute)((Object)target);
            t.setTermBuffer(this.termBuffer, 0, this.termLength);
        }
    }
}

