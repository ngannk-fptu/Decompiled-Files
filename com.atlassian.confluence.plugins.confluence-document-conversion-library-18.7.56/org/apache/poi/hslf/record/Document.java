/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hslf.record.DocumentAtom;
import org.apache.poi.hslf.record.Environment;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.PPDrawingGroup;
import org.apache.poi.hslf.record.PositionDependentRecordContainer;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SlideListWithText;

public final class Document
extends PositionDependentRecordContainer {
    private byte[] _header;
    private static long _type = 1000L;
    private DocumentAtom documentAtom;
    private Environment environment;
    private PPDrawingGroup ppDrawing;
    private SlideListWithText[] slwts;
    private ExObjList exObjList;

    public DocumentAtom getDocumentAtom() {
        return this.documentAtom;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public PPDrawingGroup getPPDrawingGroup() {
        return this.ppDrawing;
    }

    public ExObjList getExObjList(boolean create) {
        if (this.exObjList == null && create) {
            this.exObjList = new ExObjList();
            this.addChildAfter(this.exObjList, this.getDocumentAtom());
        }
        return this.exObjList;
    }

    public SlideListWithText[] getSlideListWithTexts() {
        return this.slwts;
    }

    public SlideListWithText getMasterSlideListWithText() {
        for (SlideListWithText slwt : this.slwts) {
            if (slwt.getInstance() != 1) continue;
            return slwt;
        }
        return null;
    }

    public SlideListWithText getSlideSlideListWithText() {
        for (SlideListWithText slwt : this.slwts) {
            if (slwt.getInstance() != 0) continue;
            return slwt;
        }
        return null;
    }

    public SlideListWithText getNotesSlideListWithText() {
        for (SlideListWithText slwt : this.slwts) {
            if (slwt.getInstance() != 2) continue;
            return slwt;
        }
        return null;
    }

    Document(byte[] source, int start, int len) {
        int i;
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        if (!(this._children[0] instanceof DocumentAtom)) {
            throw new IllegalStateException("The first child of a Document must be a DocumentAtom");
        }
        this.documentAtom = (DocumentAtom)this._children[0];
        int slwtcount = 0;
        for (i = 1; i < this._children.length; ++i) {
            if (this._children[i] instanceof SlideListWithText) {
                ++slwtcount;
            }
            if (this._children[i] instanceof Environment) {
                this.environment = (Environment)this._children[i];
            }
            if (this._children[i] instanceof PPDrawingGroup) {
                this.ppDrawing = (PPDrawingGroup)this._children[i];
            }
            if (!(this._children[i] instanceof ExObjList)) continue;
            this.exObjList = (ExObjList)this._children[i];
        }
        if (slwtcount == 0) {
            LOG.atWarn().log("No SlideListWithText's found - there should normally be at least one!");
        }
        if (slwtcount > 3) {
            LOG.atWarn().log("Found {} SlideListWithTexts - normally there should only be three!", (Object)Unbox.box(slwtcount));
        }
        this.slwts = new SlideListWithText[slwtcount];
        slwtcount = 0;
        for (i = 1; i < this._children.length; ++i) {
            if (!(this._children[i] instanceof SlideListWithText)) continue;
            this.slwts[slwtcount] = (SlideListWithText)this._children[i];
            ++slwtcount;
        }
    }

    public void addSlideListWithText(SlideListWithText slwt) {
        Record endDoc = this._children[this._children.length - 1];
        if (endDoc.getRecordType() == (long)RecordTypes.RoundTripCustomTableStyles12.typeID) {
            endDoc = this._children[this._children.length - 2];
        }
        if (endDoc.getRecordType() != (long)RecordTypes.EndDocument.typeID) {
            throw new IllegalStateException("The last child record of a Document should be EndDocument, but it was " + endDoc);
        }
        this.addChildBefore(slwt, endDoc);
        int newSize = this.slwts.length + 1;
        SlideListWithText[] nl = new SlideListWithText[newSize];
        System.arraycopy(this.slwts, 0, nl, 0, this.slwts.length);
        nl[nl.length - 1] = slwt;
        this.slwts = nl;
    }

    public void removeSlideListWithText(SlideListWithText slwt) {
        ArrayList<SlideListWithText> lst = new ArrayList<SlideListWithText>();
        for (SlideListWithText s : this.slwts) {
            if (s != slwt) {
                lst.add(s);
                continue;
            }
            this.removeChild(slwt);
        }
        this.slwts = lst.toArray(new SlideListWithText[0]);
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

