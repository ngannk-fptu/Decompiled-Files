/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.model.SEPX;
import org.apache.poi.hwpf.model.SectionDescriptor;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.hwpf.model.io.HWPFFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class SectionTable {
    private static final Logger LOG = LogManager.getLogger(SectionTable.class);
    private static final int SED_SIZE = 12;
    protected List<SEPX> _sections = new ArrayList<SEPX>();
    protected List<TextPiece> _text;

    public SectionTable() {
    }

    public SectionTable(byte[] documentStream, byte[] tableStream, int offset, int size, int fcMin, TextPieceTable tpt, int mainLength) {
        PlexOfCps sedPlex = new PlexOfCps(tableStream, offset, size, 12);
        this._text = tpt.getTextPieces();
        int length = sedPlex.length();
        for (int x = 0; x < length; ++x) {
            GenericPropertyNode node = sedPlex.getProperty(x);
            SectionDescriptor sed = new SectionDescriptor(node.getBytes(), 0);
            int fileOffset = sed.getFc();
            int startAt = node.getStart();
            int endAt = node.getEnd();
            if (fileOffset == -1) {
                this._sections.add(new SEPX(sed, startAt, endAt, new byte[0]));
                continue;
            }
            short sepxSize = LittleEndian.getShort(documentStream, fileOffset);
            byte[] buf = IOUtils.safelyClone(documentStream, fileOffset += 2, sepxSize, HWPFDocument.getMaxRecordLength());
            this._sections.add(new SEPX(sed, startAt, endAt, buf));
        }
        boolean matchAt = false;
        boolean matchHalf = false;
        for (SEPX s : this._sections) {
            if (s.getEnd() == mainLength) {
                matchAt = true;
                continue;
            }
            if (s.getEnd() != mainLength && s.getEnd() != mainLength - 1) continue;
            matchHalf = true;
        }
        if (!matchAt && matchHalf) {
            LOG.atWarn().log("Your document seemed to be mostly unicode, but the section definition was in bytes! Trying anyway, but things may well go wrong!");
            for (int i = 0; i < this._sections.size(); ++i) {
                SEPX s = this._sections.get(i);
                GenericPropertyNode node = sedPlex.getProperty(i);
                int startAt = node.getStart();
                int endAt = node.getEnd();
                s.setStart(startAt);
                s.setEnd(endAt);
            }
        }
        this._sections.sort(PropertyNode.StartComparator);
    }

    public void adjustForInsert(int listIndex, int length) {
        int size = this._sections.size();
        SEPX sepx = this._sections.get(listIndex);
        sepx.setEnd(sepx.getEnd() + length);
        for (int x = listIndex + 1; x < size; ++x) {
            sepx = this._sections.get(x);
            sepx.setStart(sepx.getStart() + length);
            sepx.setEnd(sepx.getEnd() + length);
        }
    }

    public List<SEPX> getSections() {
        return this._sections;
    }

    @Deprecated
    public void writeTo(HWPFFileSystem sys, int fcMin) throws IOException {
        ByteArrayOutputStream docStream = sys.getStream("WordDocument");
        ByteArrayOutputStream tableStream = sys.getStream("1Table");
        this.writeTo(docStream, tableStream);
    }

    public void writeTo(ByteArrayOutputStream wordDocumentStream, ByteArrayOutputStream tableStream) throws IOException {
        int offset = wordDocumentStream.size();
        PlexOfCps plex = new PlexOfCps(12);
        for (SEPX sepx : this._sections) {
            byte[] grpprl = sepx.getGrpprl();
            byte[] shortBuf = new byte[2];
            LittleEndian.putShort(shortBuf, 0, (short)grpprl.length);
            wordDocumentStream.write(shortBuf);
            wordDocumentStream.write(grpprl);
            SectionDescriptor sed = sepx.getSectionDescriptor();
            sed.setFc(offset);
            GenericPropertyNode property = new GenericPropertyNode(sepx.getStart(), sepx.getEnd(), sed.toByteArray());
            plex.addProperty(property);
            offset = wordDocumentStream.size();
        }
        tableStream.write(plex.toByteArray());
    }
}

