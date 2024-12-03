/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.model.io.HWPFFileSystem;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

@Internal
public class ComplexFileTable {
    private static final byte GRPPRL_TYPE = 1;
    private static final byte TEXT_PIECE_TABLE_TYPE = 2;
    protected TextPieceTable _tpt;
    private SprmBuffer[] _grpprls;

    public ComplexFileTable() {
        this._tpt = new TextPieceTable();
    }

    protected ComplexFileTable(byte[] documentStream, byte[] tableStream, int offset, int fcMin, Charset charset) throws IOException {
        LinkedList<SprmBuffer> sprmBuffers = new LinkedList<SprmBuffer>();
        while (tableStream[offset] == 1) {
            short size = LittleEndian.getShort(tableStream, ++offset);
            byte[] bs = IOUtils.safelyClone(tableStream, offset += 2, size, HWPFDocument.getMaxRecordLength());
            offset += size;
            SprmBuffer sprmBuffer = new SprmBuffer(bs, false, 0);
            sprmBuffers.add(sprmBuffer);
        }
        this._grpprls = sprmBuffers.toArray(new SprmBuffer[0]);
        if (tableStream[offset] != 2) {
            throw new IOException("The text piece table is corrupted, expected byte value 2 but had " + tableStream[offset]);
        }
        int pieceTableSize = LittleEndian.getInt(tableStream, ++offset);
        this._tpt = this.newTextPieceTable(documentStream, tableStream, offset += 4, pieceTableSize, fcMin, charset);
    }

    public ComplexFileTable(byte[] documentStream, byte[] tableStream, int offset, int fcMin) throws IOException {
        this(documentStream, tableStream, offset, fcMin, StringUtil.WIN_1252);
    }

    public TextPieceTable getTextPieceTable() {
        return this._tpt;
    }

    public SprmBuffer[] getGrpprls() {
        return this._grpprls;
    }

    @Deprecated
    public void writeTo(HWPFFileSystem sys) throws IOException {
        ByteArrayOutputStream docStream = sys.getStream("WordDocument");
        ByteArrayOutputStream tableStream = sys.getStream("1Table");
        this.writeTo(docStream, tableStream);
    }

    public void writeTo(ByteArrayOutputStream wordDocumentStream, ByteArrayOutputStream tableStream) throws IOException {
        tableStream.write(2);
        byte[] table = this._tpt.writeTo(wordDocumentStream);
        byte[] numHolder = new byte[4];
        LittleEndian.putInt(numHolder, 0, table.length);
        tableStream.write(numHolder);
        tableStream.write(table);
    }

    protected TextPieceTable newTextPieceTable(byte[] documentStream, byte[] tableStream, int offset, int pieceTableSize, int fcMin, Charset charset) {
        return new TextPieceTable(documentStream, tableStream, offset, pieceTableSize, fcMin);
    }
}

