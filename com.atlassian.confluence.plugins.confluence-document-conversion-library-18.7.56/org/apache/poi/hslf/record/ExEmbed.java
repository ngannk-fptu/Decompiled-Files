/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.ExEmbedAtom;
import org.apache.poi.hslf.record.ExOleObjAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public class ExEmbed
extends RecordContainer {
    private final byte[] _header;
    private RecordAtom embedAtom;
    private ExOleObjAtom oleObjAtom;
    private CString menuName;
    private CString progId;
    private CString clipboardName;

    protected ExEmbed(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    protected ExEmbed(RecordAtom embedAtom) {
        this();
        this.embedAtom = embedAtom;
        this._children[0] = this.embedAtom;
    }

    public ExEmbed() {
        this._header = new byte[8];
        this._children = new Record[5];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        CString cs1 = new CString();
        cs1.setOptions(16);
        CString cs2 = new CString();
        cs2.setOptions(32);
        CString cs3 = new CString();
        cs3.setOptions(48);
        this._children[0] = new ExEmbedAtom();
        this._children[1] = new ExOleObjAtom();
        this._children[2] = cs1;
        this._children[3] = cs2;
        this._children[4] = cs3;
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
        Record child = this._children[0];
        if (child instanceof ExEmbedAtom) {
            this.embedAtom = (ExEmbedAtom)child;
        } else {
            LOG.atError().log("First child record wasn't a ExEmbedAtom, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
        child = this._children[1];
        if (child instanceof ExOleObjAtom) {
            this.oleObjAtom = (ExOleObjAtom)child;
        } else {
            LOG.atError().log("Second child record wasn't a ExOleObjAtom, was of type {}", (Object)Unbox.box(child.getRecordType()));
        }
        block5: for (int i = 2; i < this._children.length; ++i) {
            if (!(this._children[i] instanceof CString)) continue;
            CString cs = (CString)this._children[i];
            int opts = cs.getOptions() >> 4;
            switch (opts) {
                case 1: {
                    this.menuName = cs;
                    continue block5;
                }
                case 2: {
                    this.progId = cs;
                    continue block5;
                }
                case 3: {
                    this.clipboardName = cs;
                    continue block5;
                }
            }
        }
    }

    public ExEmbedAtom getExEmbedAtom() {
        return (ExEmbedAtom)this.embedAtom;
    }

    public ExOleObjAtom getExOleObjAtom() {
        return this.oleObjAtom;
    }

    public String getMenuName() {
        return this.menuName == null ? null : this.menuName.getText();
    }

    public void setMenuName(String menuName) {
        this.menuName = this.safeCString(this.menuName, 1);
        this.menuName.setText(menuName);
    }

    public String getProgId() {
        return this.progId == null ? null : this.progId.getText();
    }

    public void setProgId(String progId) {
        this.progId = this.safeCString(this.progId, 2);
        this.progId.setText(progId);
    }

    public String getClipboardName() {
        return this.clipboardName == null ? null : this.clipboardName.getText();
    }

    public void setClipboardName(String clipboardName) {
        this.clipboardName = this.safeCString(this.clipboardName, 3);
        this.clipboardName.setText(clipboardName);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExEmbed.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this.getRecordType(), this._children, out);
    }

    private CString safeCString(CString oldStr, int optionsId) {
        CString newStr = oldStr;
        if (newStr == null) {
            newStr = new CString();
            newStr.setOptions(optionsId << 4);
        }
        boolean found = false;
        for (Record r : this._children) {
            if (r != newStr) continue;
            found = true;
            break;
        }
        if (!found) {
            this.appendChildRecord(newStr);
        }
        return newStr;
    }
}

