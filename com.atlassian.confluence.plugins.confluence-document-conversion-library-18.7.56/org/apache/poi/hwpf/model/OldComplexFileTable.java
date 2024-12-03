/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.poi.hwpf.model.ComplexFileTable;
import org.apache.poi.hwpf.model.OldTextPieceTable;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.util.Internal;

@Internal
public final class OldComplexFileTable
extends ComplexFileTable {
    public OldComplexFileTable(byte[] documentStream, byte[] tableStream, int offset, int fcMin, Charset charset) throws IOException {
        super(documentStream, tableStream, offset, fcMin, charset);
    }

    @Override
    protected TextPieceTable newTextPieceTable(byte[] documentStream, byte[] tableStream, int offset, int pieceTableSize, int fcMin, Charset charset) {
        return new OldTextPieceTable(documentStream, tableStream, offset, pieceTableSize, fcMin, charset);
    }
}

