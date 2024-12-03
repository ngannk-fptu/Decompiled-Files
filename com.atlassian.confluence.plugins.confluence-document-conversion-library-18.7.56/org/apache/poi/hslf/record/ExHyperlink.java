/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.ExHyperlinkAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public class ExHyperlink
extends RecordContainer {
    private static final long _type = RecordTypes.ExHyperlink.typeID;
    private byte[] _header;
    private ExHyperlinkAtom linkAtom;
    private CString linkDetailsA;
    private CString linkDetailsB;

    public ExHyperlinkAtom getExHyperlinkAtom() {
        return this.linkAtom;
    }

    public String getLinkURL() {
        return this.linkDetailsB == null ? null : this.linkDetailsB.getText();
    }

    public String getLinkTitle() {
        return this.linkDetailsA == null ? null : this.linkDetailsA.getText();
    }

    public void setLinkURL(String url) {
        if (this.linkDetailsB != null) {
            this.linkDetailsB.setText(url);
        }
    }

    public void setLinkOptions(int options) {
        if (this.linkDetailsB != null) {
            this.linkDetailsB.setOptions(options);
        }
    }

    public void setLinkTitle(String title) {
        if (this.linkDetailsA != null) {
            this.linkDetailsA.setText(title);
        }
    }

    public String _getDetailsA() {
        return this.linkDetailsA == null ? null : this.linkDetailsA.getText();
    }

    public String _getDetailsB() {
        return this.linkDetailsB == null ? null : this.linkDetailsB.getText();
    }

    protected ExHyperlink(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        Record child = this._children[0];
        if (child instanceof ExHyperlinkAtom) {
            this.linkAtom = (ExHyperlinkAtom)child;
        } else {
            LOG.atError().log("First child record wasn't a ExHyperlinkAtom, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
        for (int i = 1; i < this._children.length; ++i) {
            child = this._children[i];
            if (child instanceof CString) {
                if (this.linkDetailsA == null) {
                    this.linkDetailsA = (CString)child;
                    continue;
                }
                this.linkDetailsB = (CString)child;
                continue;
            }
            LOG.atError().log("Record after ExHyperlinkAtom wasn't a CString, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
    }

    public ExHyperlink() {
        this._header = new byte[8];
        this._children = new Record[3];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)_type);
        CString csa = new CString();
        CString csb = new CString();
        csa.setOptions(0);
        csb.setOptions(16);
        this._children[0] = new ExHyperlinkAtom();
        this._children[1] = csa;
        this._children[2] = csb;
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

