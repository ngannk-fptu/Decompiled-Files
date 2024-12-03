/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.hwpf.model.Stshif;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.sprm.CharacterSprmUncompressor;
import org.apache.poi.hwpf.sprm.ParagraphSprmUncompressor;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class StyleSheet {
    public static final int NIL_STYLE = 4095;
    @Deprecated
    private static final ParagraphProperties NIL_PAP = new ParagraphProperties();
    @Deprecated
    private static final CharacterProperties NIL_CHP = new CharacterProperties();
    private static final byte[] NIL_CHPX = new byte[0];
    private static final byte[] NIL_PAPX = new byte[]{0, 0};
    private int _cbStshi;
    private Stshif _stshif;
    StyleDescription[] _styleDescriptions;

    public StyleSheet(byte[] tableStream, int offset) {
        int x;
        int startOffset = offset;
        this._cbStshi = LittleEndian.getShort(tableStream, offset);
        this._stshif = new Stshif(tableStream, offset += 2);
        if (this._stshif.getCstd() < 0) {
            throw new IllegalArgumentException("Cannot create StyleSheet, invalid Cstd: " + this._stshif.getCstd());
        }
        offset = startOffset + 2 + this._cbStshi;
        this._styleDescriptions = new StyleDescription[this._stshif.getCstd()];
        for (x = 0; x < this._stshif.getCstd(); ++x) {
            short stdSize = LittleEndian.getShort(tableStream, offset);
            offset += 2;
            if (stdSize > 0) {
                StyleDescription aStyle;
                this._styleDescriptions[x] = aStyle = new StyleDescription(tableStream, this._stshif.getCbSTDBaseInFile(), offset, true);
            }
            offset += stdSize;
        }
        for (x = 0; x < this._styleDescriptions.length; ++x) {
            if (this._styleDescriptions[x] == null) continue;
            this.createPap(x);
            this.createChp(x);
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        int offset = 0;
        this._cbStshi = 18;
        byte[] buf = new byte[this._cbStshi + 2];
        LittleEndian.putUShort(buf, offset, (short)this._cbStshi);
        this._stshif.setCstd(this._styleDescriptions.length);
        this._stshif.serialize(buf, offset += 2);
        out.write(buf);
        byte[] sizeHolder = new byte[2];
        for (StyleDescription styleDescription : this._styleDescriptions) {
            if (styleDescription != null) {
                byte[] std = styleDescription.toByteArray();
                LittleEndian.putShort(sizeHolder, 0, (short)(std.length + std.length % 2));
                out.write(sizeHolder);
                out.write(std);
                if (std.length % 2 != 1) continue;
                out.write(0);
                continue;
            }
            sizeHolder[0] = 0;
            sizeHolder[1] = 0;
            out.write(sizeHolder);
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof StyleSheet)) {
            return false;
        }
        StyleSheet ss = (StyleSheet)o;
        if (!ss._stshif.equals(this._stshif) || ss._cbStshi != this._cbStshi || ss._styleDescriptions.length != this._styleDescriptions.length) {
            return false;
        }
        for (int i = 0; i < this._styleDescriptions.length; ++i) {
            StyleDescription tsd = this._styleDescriptions[i];
            StyleDescription osd = ss._styleDescriptions[i];
            if (tsd == null && osd == null || osd != null && osd.equals(tsd)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    @Deprecated
    private void createPap(int istd) {
        StyleDescription sd = this._styleDescriptions[istd];
        ParagraphProperties pap = sd.getPAP();
        byte[] papx = sd.getPAPX();
        int baseIndex = sd.getBaseStyle();
        if (pap == null && papx != null) {
            ParagraphProperties parentPAP = new ParagraphProperties();
            if (baseIndex != 4095 && (parentPAP = this._styleDescriptions[baseIndex].getPAP()) == null) {
                if (baseIndex == istd) {
                    throw new IllegalStateException("Pap style " + istd + " claimed to have itself as its parent, which isn't allowed");
                }
                this.createPap(baseIndex);
                parentPAP = this._styleDescriptions[baseIndex].getPAP();
            }
            if (parentPAP == null) {
                parentPAP = new ParagraphProperties();
            }
            pap = ParagraphSprmUncompressor.uncompressPAP(parentPAP, papx, 2);
            sd.setPAP(pap);
        }
    }

    @Deprecated
    private void createChp(int istd) {
        StyleDescription sd = this._styleDescriptions[istd];
        CharacterProperties chp = sd.getCHP();
        byte[] chpx = sd.getCHPX();
        int baseIndex = sd.getBaseStyle();
        if (baseIndex == istd) {
            baseIndex = 4095;
        }
        if (chp == null && chpx != null) {
            CharacterProperties parentCHP = new CharacterProperties();
            if (baseIndex != 4095) {
                parentCHP = this._styleDescriptions[baseIndex].getCHP();
                if (parentCHP == null) {
                    this.createChp(baseIndex);
                    parentCHP = this._styleDescriptions[baseIndex].getCHP();
                }
                if (parentCHP == null) {
                    parentCHP = new CharacterProperties();
                }
            }
            chp = CharacterSprmUncompressor.uncompressCHP(parentCHP, chpx, 0);
            sd.setCHP(chp);
        }
    }

    public int numStyles() {
        return this._styleDescriptions.length;
    }

    public StyleDescription getStyleDescription(int styleIndex) {
        return this._styleDescriptions[styleIndex];
    }

    @Deprecated
    public CharacterProperties getCharacterStyle(int styleIndex) {
        if (styleIndex == 4095) {
            return NIL_CHP;
        }
        if (styleIndex >= this._styleDescriptions.length) {
            return NIL_CHP;
        }
        if (styleIndex == -1) {
            return NIL_CHP;
        }
        return this._styleDescriptions[styleIndex] != null ? this._styleDescriptions[styleIndex].getCHP() : NIL_CHP;
    }

    @Deprecated
    public ParagraphProperties getParagraphStyle(int styleIndex) {
        if (styleIndex == 4095) {
            return NIL_PAP;
        }
        if (styleIndex >= this._styleDescriptions.length) {
            return NIL_PAP;
        }
        if (styleIndex == -1) {
            return NIL_PAP;
        }
        if (this._styleDescriptions[styleIndex] == null) {
            return NIL_PAP;
        }
        if (this._styleDescriptions[styleIndex].getPAP() == null) {
            return NIL_PAP;
        }
        return this._styleDescriptions[styleIndex].getPAP();
    }

    public byte[] getCHPX(int styleIndex) {
        if (styleIndex == 4095) {
            return NIL_CHPX;
        }
        if (styleIndex >= this._styleDescriptions.length) {
            return NIL_CHPX;
        }
        if (styleIndex == -1) {
            return NIL_CHPX;
        }
        if (this._styleDescriptions[styleIndex] == null) {
            return NIL_CHPX;
        }
        if (this._styleDescriptions[styleIndex].getCHPX() == null) {
            return NIL_CHPX;
        }
        return this._styleDescriptions[styleIndex].getCHPX();
    }

    public byte[] getPAPX(int styleIndex) {
        if (styleIndex == 4095) {
            return NIL_PAPX;
        }
        if (styleIndex >= this._styleDescriptions.length) {
            return NIL_PAPX;
        }
        if (styleIndex == -1) {
            return NIL_PAPX;
        }
        if (this._styleDescriptions[styleIndex] == null) {
            return NIL_PAPX;
        }
        if (this._styleDescriptions[styleIndex].getPAPX() == null) {
            return NIL_PAPX;
        }
        return this._styleDescriptions[styleIndex].getPAPX();
    }
}

