/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.hwpf.model.PieceDescriptor;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;

@Internal
public class OldTextPiece
extends TextPiece {
    private final byte[] rawBytes;

    public OldTextPiece(OldTextPiece other) {
        super(other);
        this.rawBytes = other.rawBytes == null ? null : (byte[])other.rawBytes.clone();
    }

    public OldTextPiece(int start, int end, byte[] text, PieceDescriptor pd) {
        super(start, end, text, pd);
        this.rawBytes = text;
    }

    @Override
    @NotImplemented
    public boolean isUnicode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringBuilder getStringBuilder() {
        return (StringBuilder)this._buf;
    }

    @Override
    public byte[] getRawBytes() {
        return (byte[])this.rawBytes.clone();
    }

    @Override
    @Deprecated
    @NotImplemented
    public String substring(int start, int end) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    @NotImplemented
    public void adjustForDelete(int start, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int bytesLength() {
        return this.rawBytes.length;
    }

    @Override
    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof OldTextPiece && Arrays.equals(this.rawBytes, ((OldTextPiece)other).rawBytes);
    }

    @Override
    public String toString() {
        return "OldTextPiece from " + this.getStart() + " to " + this.getEnd() + " (" + this.getPieceDescriptor() + ")";
    }

    @Override
    public OldTextPiece copy() {
        return new OldTextPiece(this);
    }
}

