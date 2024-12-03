/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.OldTextPiece;
import org.apache.poi.hwpf.model.PieceDescriptor;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.util.DoubleByteUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;

@Internal
public class OldTextPieceTable
extends TextPieceTable {
    public OldTextPieceTable() {
    }

    public OldTextPieceTable(byte[] documentStream, byte[] tableStream, int offset, int size, int fcMin, Charset charset) {
        PlexOfCps pieceTable = new PlexOfCps(tableStream, offset, size, PieceDescriptor.getSizeInBytes());
        int length = pieceTable.length();
        PieceDescriptor[] pieces = new PieceDescriptor[length];
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = pieceTable.getProperty(x);
            pieces[x] = new PieceDescriptor(node.getBytes(), 0, charset);
        }
        this._cpMin = pieces[0].getFilePosition() - fcMin;
        for (PieceDescriptor piece : pieces) {
            int start = piece.getFilePosition() - fcMin;
            if (start >= this._cpMin) continue;
            this._cpMin = start;
        }
        for (int x = 0; x < pieces.length; ++x) {
            int start = pieces[x].getFilePosition();
            GenericPropertyNode node = pieceTable.getProperty(x);
            int nodeStartChars = node.getStart();
            int nodeEndChars = node.getEnd();
            boolean unicode = pieces[x].isUnicode();
            int multiple = 1;
            if (unicode || charset != null && DoubleByteUtil.DOUBLE_BYTE_CHARSETS.contains(charset)) {
                multiple = 2;
            }
            int textSizeChars = nodeEndChars - nodeStartChars;
            int textSizeBytes = textSizeChars * multiple;
            byte[] buf = IOUtils.safelyClone(documentStream, start, textSizeBytes, OldTextPieceTable.getMaxRecordLength());
            TextPiece newTextPiece = this.newTextPiece(nodeStartChars, nodeEndChars, buf, pieces[x]);
            this._textPieces.add(newTextPiece);
        }
        Collections.sort(this._textPieces);
        this._textPiecesFCOrder = new ArrayList(this._textPieces);
        this._textPiecesFCOrder.sort(OldTextPieceTable.byFilePosition());
    }

    @Override
    protected TextPiece newTextPiece(int nodeStartChars, int nodeEndChars, byte[] buf, PieceDescriptor pd) {
        return new OldTextPiece(nodeStartChars, nodeEndChars, buf, pd);
    }

    @Override
    protected int getEncodingMultiplier(TextPiece textPiece) {
        Charset charset = textPiece.getPieceDescriptor().getCharset();
        if (charset != null && DoubleByteUtil.DOUBLE_BYTE_CHARSETS.contains(charset)) {
            return 2;
        }
        return 1;
    }
}

