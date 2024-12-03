/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.Slide;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSlideMaster;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;

public final class HSLFTitleMaster
extends HSLFMasterSheet {
    private final List<List<HSLFTextParagraph>> _paragraphs = new ArrayList<List<HSLFTextParagraph>>();

    public HSLFTitleMaster(Slide record, int sheetNo) {
        super(record, sheetNo);
        for (List<HSLFTextParagraph> l : HSLFTextParagraph.findTextParagraphs(this.getPPDrawing(), (HSLFSheet)this)) {
            if (this._paragraphs.contains(l)) continue;
            this._paragraphs.add(l);
        }
    }

    @Override
    public List<List<HSLFTextParagraph>> getTextParagraphs() {
        return this._paragraphs;
    }

    @Override
    public TextPropCollection getPropCollection(int txtype, int level, String name, boolean isCharacter) {
        HSLFMasterSheet master = this.getMasterSheet();
        return master == null ? null : master.getPropCollection(txtype, level, name, isCharacter);
    }

    @Override
    public HSLFMasterSheet getMasterSheet() {
        SlideAtom sa = ((Slide)this.getSheetContainer()).getSlideAtom();
        int masterId = sa.getMasterID();
        for (HSLFSlideMaster sm : this.getSlideShow().getSlideMasters()) {
            if (masterId != sm._getSheetNumber()) continue;
            return sm;
        }
        return null;
    }
}

