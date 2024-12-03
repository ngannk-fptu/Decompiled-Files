/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFOldDocument;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.FFData;
import org.apache.poi.hwpf.model.Ffn;
import org.apache.poi.hwpf.model.NilPICFAndBinData;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.Range;

public final class CharacterRun
extends Range
implements Duplicatable,
org.apache.poi.wp.usermodel.CharacterRun {
    public static final short SPRM_FRMARKDEL = 2048;
    public static final short SPRM_FRMARK = 2049;
    public static final short SPRM_FFLDVANISH = 2050;
    public static final short SPRM_PICLOCATION = 27139;
    public static final short SPRM_IBSTRMARK = 18436;
    public static final short SPRM_DTTMRMARK = 26629;
    public static final short SPRM_FDATA = 2054;
    public static final short SPRM_SYMBOL = 27145;
    public static final short SPRM_FOLE2 = 2058;
    public static final short SPRM_HIGHLIGHT = 10764;
    public static final short SPRM_OBJLOCATION = 26638;
    public static final short SPRM_ISTD = 18992;
    public static final short SPRM_FBOLD = 2101;
    public static final short SPRM_FITALIC = 2102;
    public static final short SPRM_FSTRIKE = 2103;
    public static final short SPRM_FOUTLINE = 2104;
    public static final short SPRM_FSHADOW = 2105;
    public static final short SPRM_FSMALLCAPS = 2106;
    public static final short SPRM_FCAPS = 2107;
    public static final short SPRM_FVANISH = 2108;
    public static final short SPRM_KUL = 10814;
    public static final short SPRM_DXASPACE = -30656;
    public static final short SPRM_LID = 19009;
    public static final short SPRM_ICO = 10818;
    public static final short SPRM_HPS = 19011;
    public static final short SPRM_HPSPOS = 18501;
    public static final short SPRM_ISS = 10824;
    public static final short SPRM_HPSKERN = 18507;
    public static final short SPRM_YSRI = 18510;
    public static final short SPRM_RGFTCASCII = 19023;
    public static final short SPRM_RGFTCFAREAST = 19024;
    public static final short SPRM_RGFTCNOTFAREAST = 19025;
    public static final short SPRM_CHARSCALE = 18514;
    public static final short SPRM_FDSTRIKE = 10835;
    public static final short SPRM_FIMPRINT = 2132;
    public static final short SPRM_FSPEC = 2133;
    public static final short SPRM_FOBJ = 2134;
    public static final short SPRM_PROPRMARK = -13737;
    public static final short SPRM_FEMBOSS = 2136;
    public static final short SPRM_SFXTEXT = 10329;
    public static final short SPRM_DISPFLDRMARK = -13726;
    public static final short SPRM_IBSTRMARKDEL = 18531;
    public static final short SPRM_DTTMRMARKDEL = 26724;
    public static final short SPRM_BRC = 26725;
    public static final short SPRM_SHD = 18534;
    public static final short SPRM_IDSIRMARKDEL = 18535;
    public static final short SPRM_CPG = 18539;
    public static final short SPRM_NONFELID = 18541;
    public static final short SPRM_FELID = 18542;
    public static final short SPRM_IDCTHINT = 10351;
    protected short _istd;
    protected SprmBuffer _chpx;
    protected CharacterProperties _props;

    CharacterRun(CHPX chpx, StyleSheet ss, short istd, Range parent) {
        super(Math.max(parent._start, chpx.getStart()), Math.min(parent._end, chpx.getEnd()), parent);
        this._props = chpx.getCharacterProperties(ss, istd);
        this._chpx = chpx.getSprmBuf();
        this._istd = istd;
    }

    CharacterRun(CharacterRun other) {
        super(other);
        this._istd = other._istd;
        this._chpx = other._chpx == null ? null : other._chpx.copy();
        this._props = other._props == null ? null : other._props.copy();
    }

    public int type() {
        return 1;
    }

    public boolean isMarkedDeleted() {
        return this._props.isFRMarkDel();
    }

    public void markDeleted(boolean mark) {
        this._props.setFRMarkDel(mark);
        byte newVal = (byte)(mark ? 1 : 0);
        this._chpx.updateSprm((short)2048, newVal);
    }

    @Override
    public boolean isBold() {
        return this._props.isFBold();
    }

    @Override
    public void setBold(boolean bold) {
        this._props.setFBold(bold);
        byte newVal = (byte)(bold ? 1 : 0);
        this._chpx.updateSprm((short)2101, newVal);
    }

    @Override
    public boolean isItalic() {
        return this._props.isFItalic();
    }

    @Override
    public void setItalic(boolean italic) {
        this._props.setFItalic(italic);
        byte newVal = (byte)(italic ? 1 : 0);
        this._chpx.updateSprm((short)2102, newVal);
    }

    public boolean isOutlined() {
        return this._props.isFOutline();
    }

    public void setOutline(boolean outlined) {
        this._props.setFOutline(outlined);
        byte newVal = (byte)(outlined ? 1 : 0);
        this._chpx.updateSprm((short)2104, newVal);
    }

    public boolean isFldVanished() {
        return this._props.isFFldVanish();
    }

    public void setFldVanish(boolean fldVanish) {
        this._props.setFFldVanish(fldVanish);
        byte newVal = (byte)(fldVanish ? 1 : 0);
        this._chpx.updateSprm((short)2050, newVal);
    }

    @Override
    public boolean isSmallCaps() {
        return this._props.isFSmallCaps();
    }

    @Override
    public void setSmallCaps(boolean smallCaps) {
        this._props.setFSmallCaps(smallCaps);
        byte newVal = (byte)(smallCaps ? 1 : 0);
        this._chpx.updateSprm((short)2106, newVal);
    }

    @Override
    public boolean isCapitalized() {
        return this._props.isFCaps();
    }

    @Override
    public void setCapitalized(boolean caps) {
        this._props.setFCaps(caps);
        byte newVal = (byte)(caps ? 1 : 0);
        this._chpx.updateSprm((short)2107, newVal);
    }

    public boolean isVanished() {
        return this._props.isFVanish();
    }

    public void setVanished(boolean vanish) {
        this._props.setFVanish(vanish);
        byte newVal = (byte)(vanish ? 1 : 0);
        this._chpx.updateSprm((short)2108, newVal);
    }

    public boolean isMarkedInserted() {
        return this._props.isFRMark();
    }

    public void markInserted(boolean mark) {
        this._props.setFRMark(mark);
        byte newVal = (byte)(mark ? 1 : 0);
        this._chpx.updateSprm((short)2049, newVal);
    }

    @Override
    public boolean isStrikeThrough() {
        return this._props.isFStrike();
    }

    @Override
    public void setStrikeThrough(boolean strike) {
        this.strikeThrough(strike);
    }

    public void strikeThrough(boolean strike) {
        this._props.setFStrike(strike);
        byte newVal = (byte)(strike ? 1 : 0);
        this._chpx.updateSprm((short)2103, newVal);
    }

    @Override
    public boolean isShadowed() {
        return this._props.isFShadow();
    }

    @Override
    public void setShadow(boolean shadow) {
        this._props.setFShadow(shadow);
        byte newVal = (byte)(shadow ? 1 : 0);
        this._chpx.updateSprm((short)2105, newVal);
    }

    @Override
    public boolean isEmbossed() {
        return this._props.isFEmboss();
    }

    @Override
    public void setEmbossed(boolean emboss) {
        this._props.setFEmboss(emboss);
        byte newVal = (byte)(emboss ? 1 : 0);
        this._chpx.updateSprm((short)2136, newVal);
    }

    @Override
    public boolean isImprinted() {
        return this._props.isFImprint();
    }

    @Override
    public void setImprinted(boolean imprint) {
        this._props.setFImprint(imprint);
        byte newVal = (byte)(imprint ? 1 : 0);
        this._chpx.updateSprm((short)2132, newVal);
    }

    @Override
    public boolean isDoubleStrikeThrough() {
        return this._props.isFDStrike();
    }

    @Override
    public void setDoubleStrikethrough(boolean dstrike) {
        this._props.setFDStrike(dstrike);
        byte newVal = (byte)(dstrike ? 1 : 0);
        this._chpx.updateSprm((short)10835, newVal);
    }

    public void setFtcAscii(int ftcAscii) {
        this._props.setFtcAscii(ftcAscii);
        this._chpx.updateSprm((short)19023, (short)ftcAscii);
    }

    public void setFtcFE(int ftcFE) {
        this._props.setFtcFE(ftcFE);
        this._chpx.updateSprm((short)19024, (short)ftcFE);
    }

    public void setFtcOther(int ftcOther) {
        this._props.setFtcOther(ftcOther);
        this._chpx.updateSprm((short)19025, (short)ftcOther);
    }

    @Override
    public int getFontSize() {
        return this._props.getHps();
    }

    @Override
    public Double getFontSizeAsDouble() {
        return this.getFontSize();
    }

    @Override
    public void setFontSize(int halfPoints) {
        this._props.setHps(halfPoints);
        this._chpx.updateSprm((short)19011, (short)halfPoints);
    }

    @Override
    public void setFontSize(double halfPoints) {
        this.setFontSize(BigDecimal.valueOf(halfPoints).setScale(0, RoundingMode.HALF_UP).intValue());
    }

    @Override
    public int getCharacterSpacing() {
        return this._props.getDxaSpace();
    }

    @Override
    public void setCharacterSpacing(int twips) {
        this._props.setDxaSpace(twips);
        this._chpx.updateSprm((short)-30656, twips);
    }

    public short getSubSuperScriptIndex() {
        return this._props.getIss();
    }

    public void setSubSuperScriptIndex(short iss) {
        this._props.setDxaSpace(iss);
        this._chpx.updateSprm((short)-30656, iss);
    }

    public int getUnderlineCode() {
        return this._props.getKul();
    }

    public void setUnderlineCode(int kul) {
        this._props.setKul((byte)kul);
        this._chpx.updateSprm((short)10814, (byte)kul);
    }

    public int getColor() {
        return this._props.getIco();
    }

    public void setColor(int color) {
        this._props.setIco((byte)color);
        this._chpx.updateSprm((short)10818, (byte)color);
    }

    public int getVerticalOffset() {
        return this._props.getHpsPos();
    }

    public void setVerticalOffset(int hpsPos) {
        this._props.setHpsPos((short)hpsPos);
        this._chpx.updateSprm((short)18501, (byte)hpsPos);
    }

    @Override
    public int getKerning() {
        return this._props.getHpsKern();
    }

    @Override
    public void setKerning(int kern) {
        this._props.setHpsKern(kern);
        this._chpx.updateSprm((short)18507, (short)kern);
    }

    @Override
    public boolean isHighlighted() {
        return this._props.isFHighlight();
    }

    public byte getHighlightedColor() {
        return this._props.getIcoHighlight();
    }

    public void setHighlighted(byte color) {
        this._props.setFHighlight(true);
        this._props.setIcoHighlight(color);
        this._chpx.updateSprm((short)10764, color);
    }

    @Override
    public String getFontName() {
        if (this._doc instanceof HWPFOldDocument) {
            return ((HWPFOldDocument)this._doc).getOldFontTable().getMainFont(this._props.getFtcAscii());
        }
        if (this._doc.getFontTable() == null) {
            return null;
        }
        return this._doc.getFontTable().getMainFont(this._props.getFtcAscii());
    }

    public boolean isSpecialCharacter() {
        return this._props.isFSpec();
    }

    public void setSpecialCharacter(boolean spec) {
        this._props.setFSpec(spec);
        byte newVal = (byte)(spec ? 1 : 0);
        this._chpx.updateSprm((short)2133, newVal);
    }

    public boolean isObj() {
        return this._props.isFObj();
    }

    public void setObj(boolean obj) {
        this._props.setFObj(obj);
        byte newVal = (byte)(obj ? 1 : 0);
        this._chpx.updateSprm((short)2134, newVal);
    }

    public int getPicOffset() {
        return this._props.getFcPic();
    }

    public void setPicOffset(int offset) {
        this._props.setFcPic(offset);
        this._chpx.updateSprm((short)27139, offset);
    }

    public boolean isData() {
        return this._props.isFData();
    }

    public void setData(boolean data) {
        this._props.setFData(data);
        byte newVal = (byte)(data ? 1 : 0);
        this._chpx.updateSprm((short)2134, newVal);
    }

    public boolean isOle2() {
        return this._props.isFOle2();
    }

    public void setOle2(boolean ole) {
        this._props.setFOle2(ole);
        byte newVal = (byte)(ole ? 1 : 0);
        this._chpx.updateSprm((short)2134, newVal);
    }

    public int getObjOffset() {
        return this._props.getFcObj();
    }

    public void setObjOffset(int obj) {
        this._props.setFcObj(obj);
        this._chpx.updateSprm((short)26638, obj);
    }

    public int getIco24() {
        return this._props.getIco24();
    }

    public void setIco24(int colour24) {
        this._props.setIco24(colour24);
    }

    @Override
    public CharacterRun copy() {
        return new CharacterRun(this);
    }

    public boolean isSymbol() {
        return this.isSpecialCharacter() && this.text().equals("(");
    }

    public char getSymbolCharacter() {
        if (this.isSymbol()) {
            return (char)this._props.getXchSym();
        }
        throw new IllegalStateException("Not a symbol CharacterRun");
    }

    public Ffn getSymbolFont() {
        if (this.isSymbol()) {
            if (this._doc.getFontTable() == null) {
                return null;
            }
            Ffn[] fontNames = this._doc.getFontTable().getFontNames();
            if (fontNames.length <= this._props.getFtcSym()) {
                return null;
            }
            return fontNames[this._props.getFtcSym()];
        }
        throw new IllegalStateException("Not a symbol CharacterRun");
    }

    public BorderCode getBorder() {
        return this._props.getBrc();
    }

    public int getLanguageCode() {
        return this._props.getLidDefault();
    }

    public short getStyleIndex() {
        return this._istd;
    }

    @Override
    public String toString() {
        String text = this.text();
        return "CharacterRun of " + text.length() + " characters - " + text;
    }

    public String[] getDropDownListValues() {
        char c;
        if (this.getDocument() instanceof HWPFDocument && (c = this._text.charAt(this._start)) == '\u0001') {
            NilPICFAndBinData data = new NilPICFAndBinData(((HWPFDocument)this.getDocument()).getDataStream(), this.getPicOffset());
            FFData ffData = new FFData(data.getBinData(), 0);
            return ffData.getDropList();
        }
        return null;
    }

    public Integer getDropDownListDefaultItemIndex() {
        char c;
        if (this.getDocument() instanceof HWPFDocument && (c = this._text.charAt(this._start)) == '\u0001') {
            NilPICFAndBinData data = new NilPICFAndBinData(((HWPFDocument)this.getDocument()).getDataStream(), this.getPicOffset());
            FFData ffData = new FFData(data.getBinData(), 0);
            return ffData.getDefaultDropDownItemIndex();
        }
        return null;
    }
}

