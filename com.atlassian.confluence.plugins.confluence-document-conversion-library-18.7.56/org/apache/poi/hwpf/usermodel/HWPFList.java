/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.model.LFO;
import org.apache.poi.hwpf.model.LFOData;
import org.apache.poi.hwpf.model.ListData;
import org.apache.poi.hwpf.model.ListFormatOverrideLevel;
import org.apache.poi.hwpf.model.ListLevel;
import org.apache.poi.hwpf.model.ListTables;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.sprm.CharacterSprmCompressor;
import org.apache.poi.hwpf.sprm.ParagraphSprmCompressor;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.util.Internal;

public final class HWPFList {
    private boolean _ignoreLogicalLeftIdentation;
    private LFO _lfo;
    private LFOData _lfoData;
    private ListData _listData;
    private ListTables _listTables;
    private StyleSheet _styleSheet;

    public HWPFList(boolean numbered, StyleSheet styleSheet) {
        this._listData = new ListData((int)(Math.random() * (double)System.currentTimeMillis()), numbered);
        this._lfo = new LFO();
        this._lfo.setLsid(this._listData.getLsid());
        this._lfoData = new LFOData();
        this._styleSheet = styleSheet;
    }

    public HWPFList(StyleSheet styleSheet, ListTables listTables, int ilfo) {
        this._listTables = listTables;
        this._styleSheet = styleSheet;
        if (ilfo == 0 || ilfo == 63489) {
            throw new IllegalArgumentException("Paragraph not in list");
        }
        if (1 <= ilfo && ilfo <= 2046) {
            this._lfo = listTables.getLfo(ilfo);
            this._lfoData = listTables.getLfoData(ilfo);
        } else if (63490 <= ilfo && ilfo <= 65535) {
            int nilfo = ilfo ^ 0xFFFF;
            this._lfo = listTables.getLfo(nilfo);
            this._lfoData = listTables.getLfoData(nilfo);
            this._ignoreLogicalLeftIdentation = true;
        } else {
            throw new IllegalArgumentException("Incorrect ilfo: " + ilfo);
        }
        this._listData = listTables.getListData(this._lfo.getLsid());
    }

    @Internal
    public LFO getLFO() {
        return this._lfo;
    }

    @Internal
    public LFOData getLFOData() {
        return this._lfoData;
    }

    @Internal
    public ListData getListData() {
        return this._listData;
    }

    public int getLsid() {
        return this._lfo.getLsid();
    }

    @Internal
    ListLevel getLVL(char level) {
        if (level >= this._listData.numLevels()) {
            throw new IllegalArgumentException("Required level " + level + " is more than number of level for list (" + this._listData.numLevels() + ")");
        }
        return this._listData.getLevels()[level];
    }

    public int getNumberFormat(char level) {
        return this.getLVL(level).getNumberFormat();
    }

    public String getNumberText(char level) {
        return this.getLVL(level).getNumberText();
    }

    public int getStartAt(char level) {
        if (this.isStartAtOverriden(level)) {
            return this._lfoData.getRgLfoLvl()[level].getIStartAt();
        }
        return this.getLVL(level).getStartAt();
    }

    public byte getTypeOfCharFollowingTheNumber(char level) {
        return this.getLVL(level).getTypeOfCharFollowingTheNumber();
    }

    public boolean isIgnoreLogicalLeftIdentation() {
        return this._ignoreLogicalLeftIdentation;
    }

    public boolean isStartAtOverriden(char level) {
        ListFormatOverrideLevel lfolvl = this._lfoData.getRgLfoLvl().length > level ? this._lfoData.getRgLfoLvl()[level] : null;
        return lfolvl != null && lfolvl.getIStartAt() != 0 && !lfolvl.isFormatting();
    }

    public void setIgnoreLogicalLeftIdentation(boolean ignoreLogicalLeftIdentation) {
        this._ignoreLogicalLeftIdentation = ignoreLogicalLeftIdentation;
    }

    public void setLevelNumberProperties(int level, CharacterProperties chp) {
        ListLevel listLevel = this._listData.getLevel(level);
        int styleIndex = this._listData.getLevelStyle(level);
        CharacterProperties base = this._styleSheet.getCharacterStyle(styleIndex);
        byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(chp, base);
        listLevel.setNumberProperties(grpprl);
    }

    public void setLevelParagraphProperties(int level, ParagraphProperties pap) {
        ListLevel listLevel = this._listData.getLevel(level);
        int styleIndex = this._listData.getLevelStyle(level);
        ParagraphProperties base = this._styleSheet.getParagraphStyle(styleIndex);
        byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(pap, base);
        listLevel.setLevelProperties(grpprl);
    }

    public void setLevelStyle(int level, int styleIndex) {
        this._listData.setLevelStyle(level, styleIndex);
    }
}

