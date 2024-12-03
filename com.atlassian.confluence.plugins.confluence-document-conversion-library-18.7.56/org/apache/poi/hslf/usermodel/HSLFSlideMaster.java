/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.model.textproperties.TextPropCollection;
import org.apache.poi.hslf.record.MainMaster;
import org.apache.poi.hslf.record.TxMasterStyleAtom;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.Internal;

public final class HSLFSlideMaster
extends HSLFMasterSheet {
    private final List<List<HSLFTextParagraph>> _paragraphs = new ArrayList<List<HSLFTextParagraph>>();
    private TxMasterStyleAtom[] _txmaster;

    public HSLFSlideMaster(MainMaster record, int sheetNo) {
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
    public HSLFMasterSheet getMasterSheet() {
        return null;
    }

    @Override
    public TextPropCollection getPropCollection(int txtype, int level, String name, boolean isCharacter) {
        TextPropCollection tpc = this.getPropHelper(txtype, level, name, isCharacter);
        if (tpc != null) {
            return tpc;
        }
        TextShape.TextPlaceholder tp = TextShape.TextPlaceholder.fromNativeId(txtype);
        switch (tp == null ? TextShape.TextPlaceholder.BODY : tp) {
            case BODY: 
            case CENTER_BODY: 
            case HALF_BODY: 
            case QUARTER_BODY: {
                return this.getPropHelper(TextShape.TextPlaceholder.BODY.nativeId, level, name, isCharacter);
            }
            case TITLE: 
            case CENTER_TITLE: {
                return this.getPropHelper(TextShape.TextPlaceholder.TITLE.nativeId, level, name, isCharacter);
            }
        }
        return null;
    }

    private TextPropCollection getPropHelper(int txtype, int level, String name, boolean isCharacter) {
        if (txtype >= this._txmaster.length) {
            return null;
        }
        TxMasterStyleAtom t = this._txmaster[txtype];
        List<TextPropCollection> styles = isCharacter ? t.getCharacterStyles() : t.getParagraphStyles();
        int minLevel = Math.min(level, styles.size() - 1);
        if ("*".equals(name)) {
            return styles.get(minLevel);
        }
        for (int i = minLevel; i >= 0; --i) {
            TextPropCollection col = styles.get(i);
            Object tp = col.findByName(name);
            if (tp == null) continue;
            return col;
        }
        return null;
    }

    @Override
    @Internal
    protected void setSlideShow(HSLFSlideShow ss) {
        int txType;
        super.setSlideShow(ss);
        assert (this._txmaster == null);
        this._txmaster = new TxMasterStyleAtom[9];
        if (this.getSlideShow() == null || this.getSlideShow().getDocumentRecord() == null || this.getSlideShow().getDocumentRecord().getEnvironment() == null) {
            throw new IllegalStateException("Did not find a TxMasterStyleAtom in the current slide show");
        }
        TxMasterStyleAtom txdoc = this.getSlideShow().getDocumentRecord().getEnvironment().getTxMasterStyleAtom();
        if (txdoc == null) {
            throw new IllegalStateException("Did not find a TxMasterStyleAtom in the current slide show");
        }
        this._txmaster[txdoc.getTextType()] = txdoc;
        TxMasterStyleAtom[] txrec = ((MainMaster)this.getSheetContainer()).getTxMasterStyleAtoms();
        for (TxMasterStyleAtom txMasterStyleAtom : txrec) {
            txType = txMasterStyleAtom.getTextType();
            if (txType >= this._txmaster.length || this._txmaster[txType] != null) continue;
            this._txmaster[txType] = txMasterStyleAtom;
        }
        for (List list : this.getTextParagraphs()) {
            for (HSLFTextParagraph htp : list) {
                txType = htp.getRunType();
                if (txType >= this._txmaster.length || this._txmaster[txType] == null) {
                    throw new HSLFException("Master styles not initialized");
                }
                int level = htp.getIndentLevel();
                List<TextPropCollection> charStyles = this._txmaster[txType].getCharacterStyles();
                List<TextPropCollection> paragraphStyles = this._txmaster[txType].getParagraphStyles();
                if (charStyles != null && paragraphStyles != null && charStyles.size() > level && paragraphStyles.size() > level) continue;
                throw new HSLFException("Master styles not initialized");
            }
        }
    }

    @Override
    protected void onAddTextShape(HSLFTextShape shape) {
        List<HSLFTextParagraph> runs = shape.getTextParagraphs();
        this._paragraphs.add(runs);
    }

    public TxMasterStyleAtom[] getTxMasterStyleAtoms() {
        return this._txmaster;
    }
}

