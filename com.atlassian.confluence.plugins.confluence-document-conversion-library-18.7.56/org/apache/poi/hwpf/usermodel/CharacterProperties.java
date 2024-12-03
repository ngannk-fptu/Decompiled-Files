/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.Colorref;
import org.apache.poi.hwpf.model.types.CHPAbstractType;

public final class CharacterProperties
extends CHPAbstractType
implements Duplicatable {
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
    public static final short SPRM_CCV = 26736;

    public CharacterProperties() {
        this.setFUsePgsuSettings(true);
        this.setXstDispFldRMark(new byte[36]);
    }

    public CharacterProperties(CharacterProperties other) {
        super(other);
    }

    public boolean isMarkedDeleted() {
        return this.isFRMarkDel();
    }

    public void markDeleted(boolean mark) {
        super.setFRMarkDel(mark);
    }

    public boolean isBold() {
        return this.isFBold();
    }

    public void setBold(boolean bold) {
        super.setFBold(bold);
    }

    public boolean isItalic() {
        return this.isFItalic();
    }

    public void setItalic(boolean italic) {
        super.setFItalic(italic);
    }

    public boolean isOutlined() {
        return this.isFOutline();
    }

    public void setOutline(boolean outlined) {
        super.setFOutline(outlined);
    }

    public boolean isFldVanished() {
        return this.isFFldVanish();
    }

    public void setFldVanish(boolean fldVanish) {
        super.setFFldVanish(fldVanish);
    }

    public boolean isSmallCaps() {
        return this.isFSmallCaps();
    }

    public void setSmallCaps(boolean smallCaps) {
        super.setFSmallCaps(smallCaps);
    }

    public boolean isCapitalized() {
        return this.isFCaps();
    }

    public void setCapitalized(boolean caps) {
        super.setFCaps(caps);
    }

    public boolean isVanished() {
        return this.isFVanish();
    }

    public void setVanished(boolean vanish) {
        super.setFVanish(vanish);
    }

    public boolean isMarkedInserted() {
        return this.isFRMark();
    }

    public void markInserted(boolean mark) {
        super.setFRMark(mark);
    }

    public boolean isStrikeThrough() {
        return this.isFStrike();
    }

    public void strikeThrough(boolean strike) {
        super.setFStrike(strike);
    }

    public boolean isShadowed() {
        return this.isFShadow();
    }

    public void setShadow(boolean shadow) {
        super.setFShadow(shadow);
    }

    public boolean isEmbossed() {
        return this.isFEmboss();
    }

    public void setEmbossed(boolean emboss) {
        super.setFEmboss(emboss);
    }

    public boolean isImprinted() {
        return this.isFImprint();
    }

    public void setImprinted(boolean imprint) {
        super.setFImprint(imprint);
    }

    public boolean isDoubleStrikeThrough() {
        return this.isFDStrike();
    }

    public void setDoubleStrikeThrough(boolean dstrike) {
        super.setFDStrike(dstrike);
    }

    public int getFontSize() {
        return this.getHps();
    }

    public void setFontSize(int halfPoints) {
        super.setHps(halfPoints);
    }

    public int getCharacterSpacing() {
        return this.getDxaSpace();
    }

    public void setCharacterSpacing(int twips) {
        super.setDxaSpace(twips);
    }

    public short getSubSuperScriptIndex() {
        return this.getIss();
    }

    public void setSubSuperScriptIndex(short iss) {
        super.setDxaSpace(iss);
    }

    public int getUnderlineCode() {
        return super.getKul();
    }

    public void setUnderlineCode(int kul) {
        super.setKul((byte)kul);
    }

    public int getColor() {
        return super.getIco();
    }

    public void setColor(int color) {
        super.setIco((byte)color);
    }

    public int getVerticalOffset() {
        return super.getHpsPos();
    }

    public void setVerticalOffset(int hpsPos) {
        super.setHpsPos((short)hpsPos);
    }

    public int getKerning() {
        return super.getHpsKern();
    }

    public void setKerning(int kern) {
        super.setHpsKern(kern);
    }

    public boolean isHighlighted() {
        return super.isFHighlight();
    }

    public void setHighlighted(byte color) {
        super.setIcoHighlight(color);
    }

    public int getIco24() {
        if (!this.getCv().isEmpty()) {
            return this.getCv().getValue();
        }
        switch (this.getIco()) {
            case 0: {
                return -1;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 0xFF0000;
            }
            case 3: {
                return 0xFFFF00;
            }
            case 4: {
                return 65280;
            }
            case 5: {
                return 0xFF00FF;
            }
            case 6: {
                return 255;
            }
            case 7: {
                return 65535;
            }
            case 8: {
                return 0xFFFFFF;
            }
            case 9: {
                return 0x800000;
            }
            case 10: {
                return 0x808000;
            }
            case 11: {
                return 32768;
            }
            case 12: {
                return 0x800080;
            }
            case 13: {
                return 128;
            }
            case 14: {
                return 32896;
            }
            case 15: {
                return 0x808080;
            }
            case 16: {
                return 0xC0C0C0;
            }
        }
        return -1;
    }

    public void setIco24(int colour24) {
        this.setCv(new Colorref(colour24 & 0xFFFFFF));
    }

    @Override
    public CharacterProperties copy() {
        return new CharacterProperties(this);
    }
}

