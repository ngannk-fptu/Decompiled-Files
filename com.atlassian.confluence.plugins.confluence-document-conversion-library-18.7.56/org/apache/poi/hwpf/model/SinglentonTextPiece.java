/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.PieceDescriptor;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.util.Internal;
import org.apache.poi.util.StringUtil;

@Internal
public class SinglentonTextPiece
extends TextPiece {
    public SinglentonTextPiece(SinglentonTextPiece other) {
        super(other);
    }

    public SinglentonTextPiece(StringBuilder buffer) {
        super(0, buffer.length(), StringUtil.getToUnicodeLE(buffer.toString()), new PieceDescriptor(new byte[8], 0));
    }

    @Override
    public int bytesLength() {
        return this.getStringBuilder().length() * 2;
    }

    @Override
    public int characterLength() {
        return this.getStringBuilder().length();
    }

    @Override
    public int getCP() {
        return 0;
    }

    @Override
    public int getEnd() {
        return this.characterLength();
    }

    @Override
    public int getStart() {
        return 0;
    }

    @Override
    public String toString() {
        return "SinglentonTextPiece (" + this.characterLength() + " chars)";
    }

    @Override
    public SinglentonTextPiece copy() {
        return new SinglentonTextPiece(this);
    }
}

