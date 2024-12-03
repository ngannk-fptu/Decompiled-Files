/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.nio.charset.Charset;
import org.apache.poi.hwpf.model.PieceDescriptor;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.util.DoubleByteUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.StringUtil;

@Internal
public class TextPiece
extends PropertyNode<TextPiece> {
    private final boolean _usesUnicode;
    private final PieceDescriptor _pd;

    public TextPiece(TextPiece other) {
        super(other);
        this._usesUnicode = other._usesUnicode;
        this._pd = other._pd == null ? null : other._pd.copy();
    }

    public TextPiece(int start, int end, byte[] text, PieceDescriptor pd, int cpStart) {
        this(start, end, text, pd);
    }

    public TextPiece(int start, int end, byte[] text, PieceDescriptor pd) {
        super(start, end, TextPiece.buildInitSB(text, pd));
        this._usesUnicode = pd.isUnicode();
        this._pd = pd;
        int textLength = ((CharSequence)this._buf).length();
        if (end - start != textLength) {
            throw new IllegalStateException("Told we're for characters " + start + " -> " + end + ", but actually covers " + textLength + " characters!");
        }
        if (end < start) {
            throw new IllegalStateException("Told we're of negative size! start=" + start + " end=" + end);
        }
    }

    private static StringBuilder buildInitSB(byte[] text, PieceDescriptor pd) {
        if (DoubleByteUtil.BIG5.equals(pd.getCharset())) {
            return new StringBuilder(DoubleByteUtil.cp950ToString(text, 0, text.length));
        }
        String str = new String(text, 0, text.length, pd.isUnicode() ? StringUtil.UTF16LE : pd.getCharset());
        return new StringBuilder(str);
    }

    public boolean isUnicode() {
        return this._usesUnicode;
    }

    public PieceDescriptor getPieceDescriptor() {
        return this._pd;
    }

    @Deprecated
    public StringBuffer getStringBuffer() {
        return new StringBuffer(this.getStringBuilder());
    }

    public StringBuilder getStringBuilder() {
        return (StringBuilder)this._buf;
    }

    public byte[] getRawBytes() {
        return this._buf.toString().getBytes(Charset.forName(this._usesUnicode ? "UTF-16LE" : "Cp1252"));
    }

    @Deprecated
    public String substring(int start, int end) {
        StringBuilder buf = (StringBuilder)this._buf;
        if (start < 0) {
            throw new StringIndexOutOfBoundsException("Can't request a substring before 0 - asked for " + start);
        }
        if (end > buf.length()) {
            throw new StringIndexOutOfBoundsException("Index " + end + " out of range 0 -> " + buf.length());
        }
        if (end < start) {
            throw new StringIndexOutOfBoundsException("Asked for text from " + start + " to " + end + ", which has an end before the start!");
        }
        return buf.substring(start, end);
    }

    @Override
    @Deprecated
    public void adjustForDelete(int start, int length) {
        int myStart = this.getStart();
        int myEnd = this.getEnd();
        int end = start + length;
        if (start <= myEnd && end >= myStart) {
            int overlapStart = Math.max(myStart, start);
            int overlapEnd = Math.min(myEnd, end);
            int bufStart = overlapStart - myStart;
            int bufEnd = overlapEnd - myStart;
            ((StringBuilder)this._buf).delete(bufStart, bufEnd);
        }
        super.adjustForDelete(start, length);
    }

    @Deprecated
    public int characterLength() {
        return this.getEnd() - this.getStart();
    }

    public int bytesLength() {
        return (this.getEnd() - this.getStart()) * (this._usesUnicode ? 2 : 1);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TextPiece)) {
            return false;
        }
        TextPiece tp = (TextPiece)o;
        assert (this._buf != null && tp._buf != null && this._pd != null && tp._pd != null);
        return this.limitsAreEqual(o) && tp._usesUnicode == this._usesUnicode && tp._buf.toString().equals(this._buf.toString()) && tp._pd.equals(this._pd);
    }

    @Override
    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public int getCP() {
        return this.getStart();
    }

    public String toString() {
        return "TextPiece from " + this.getStart() + " to " + this.getEnd() + " (" + this.getPieceDescriptor() + ")";
    }

    @Override
    public TextPiece copy() {
        return new TextPiece(this);
    }
}

