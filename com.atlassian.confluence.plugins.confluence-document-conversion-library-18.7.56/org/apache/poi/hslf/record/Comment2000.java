/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.Comment2000Atom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public final class Comment2000
extends RecordContainer {
    private byte[] _header;
    private static final long _type = RecordTypes.Comment2000.typeID;
    private CString authorRecord;
    private CString authorInitialsRecord;
    private CString commentRecord;
    private Comment2000Atom commentAtom;

    public Comment2000Atom getComment2000Atom() {
        return this.commentAtom;
    }

    public String getAuthor() {
        return this.authorRecord == null ? null : this.authorRecord.getText();
    }

    public void setAuthor(String author) {
        this.authorRecord.setText(author);
    }

    public String getAuthorInitials() {
        return this.authorInitialsRecord == null ? null : this.authorInitialsRecord.getText();
    }

    public void setAuthorInitials(String initials) {
        this.authorInitialsRecord.setText(initials);
    }

    public String getText() {
        return this.commentRecord == null ? null : this.commentRecord.getText();
    }

    public void setText(String text) {
        this.commentRecord.setText(text);
    }

    protected Comment2000(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        for (Record r : this._children) {
            if (r instanceof CString) {
                CString cs = (CString)r;
                int recInstance = cs.getOptions() >> 4;
                switch (recInstance) {
                    case 0: {
                        this.authorRecord = cs;
                        break;
                    }
                    case 1: {
                        this.commentRecord = cs;
                        break;
                    }
                    case 2: {
                        this.authorInitialsRecord = cs;
                        break;
                    }
                }
                continue;
            }
            if (r instanceof Comment2000Atom) {
                this.commentAtom = (Comment2000Atom)r;
                continue;
            }
            LOG.atWarn().log("Unexpected record with type={} in Comment2000: {}", (Object)Unbox.box(r.getRecordType()), (Object)r.getClass().getName());
        }
    }

    public Comment2000() {
        this._header = new byte[8];
        this._children = new Record[4];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)_type);
        CString csa = new CString();
        CString csb = new CString();
        CString csc = new CString();
        csa.setOptions(0);
        csb.setOptions(16);
        csc.setOptions(32);
        this._children[0] = csa;
        this._children[1] = csb;
        this._children[2] = csc;
        this._children[3] = new Comment2000Atom();
        this.findInterestingChildren();
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], _type, this._children, out);
    }
}

