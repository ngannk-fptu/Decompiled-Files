/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.HeadersFootersAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public final class HeadersFootersContainer
extends RecordContainer {
    public static final short SlideHeadersFootersContainer = 63;
    public static final short NotesHeadersFootersContainer = 79;
    public static final int USERDATEATOM = 0;
    public static final int HEADERATOM = 1;
    public static final int FOOTERATOM = 2;
    private byte[] _header;
    private HeadersFootersAtom hdAtom;
    private CString csDate;
    private CString csHeader;
    private CString csFooter;

    protected HeadersFootersContainer(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        for (Record child : this._children) {
            if (child instanceof HeadersFootersAtom) {
                this.hdAtom = (HeadersFootersAtom)child;
                continue;
            }
            if (child instanceof CString) {
                CString cs = (CString)child;
                int opts = cs.getOptions() >> 4;
                switch (opts) {
                    case 0: {
                        this.csDate = cs;
                        break;
                    }
                    case 1: {
                        this.csHeader = cs;
                        break;
                    }
                    case 2: {
                        this.csFooter = cs;
                        break;
                    }
                    default: {
                        LOG.atWarn().log("Unexpected CString.Options in HeadersFootersContainer: {}", (Object)Unbox.box(opts));
                        break;
                    }
                }
                continue;
            }
            LOG.atWarn().log("Unexpected record in HeadersFootersContainer: {}", (Object)child);
        }
    }

    public HeadersFootersContainer(short options) {
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 0, options);
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        this.hdAtom = new HeadersFootersAtom();
        this._children = new Record[]{this.hdAtom};
        this.csFooter = null;
        this.csHeader = null;
        this.csDate = null;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.HeadersFooters.typeID;
    }

    public int getOptions() {
        return LittleEndian.getShort(this._header, 0);
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this.getRecordType(), this._children, out);
    }

    public HeadersFootersAtom getHeadersFootersAtom() {
        return this.hdAtom;
    }

    public CString getUserDateAtom() {
        return this.csDate;
    }

    public CString getHeaderAtom() {
        return this.csHeader;
    }

    public CString getFooterAtom() {
        return this.csFooter;
    }

    public CString addUserDateAtom() {
        if (this.csDate != null) {
            return this.csDate;
        }
        this.csDate = new CString();
        this.csDate.setOptions(0);
        this.addChildAfter(this.csDate, this.hdAtom);
        return this.csDate;
    }

    public CString addHeaderAtom() {
        if (this.csHeader != null) {
            return this.csHeader;
        }
        this.csHeader = new CString();
        this.csHeader.setOptions(16);
        HeadersFootersAtom r = this.hdAtom;
        if (this.csDate != null) {
            r = this.hdAtom;
        }
        this.addChildAfter(this.csHeader, r);
        return this.csHeader;
    }

    public CString addFooterAtom() {
        if (this.csFooter != null) {
            return this.csFooter;
        }
        this.csFooter = new CString();
        this.csFooter.setOptions(32);
        RecordAtom r = this.hdAtom;
        if (this.csHeader != null) {
            r = this.csHeader;
        } else if (this.csDate != null) {
            r = this.csDate;
        }
        this.addChildAfter(this.csFooter, r);
        return this.csFooter;
    }
}

