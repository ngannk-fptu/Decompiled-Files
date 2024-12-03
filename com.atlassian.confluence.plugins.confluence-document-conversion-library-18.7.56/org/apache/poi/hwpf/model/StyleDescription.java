/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.StdfBase;
import org.apache.poi.hwpf.model.StdfPost2000;
import org.apache.poi.hwpf.model.UPX;
import org.apache.poi.hwpf.model.types.StdfBaseAbstractType;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

@Internal
public final class StyleDescription {
    private static final Logger LOG = LogManager.getLogger(StyleDescription.class);
    private static final int PARAGRAPH_STYLE = 1;
    private static final int CHARACTER_STYLE = 2;
    private int _baseLength;
    private StdfBase _stdfBase;
    private StdfPost2000 _stdfPost2000;
    UPX[] _upxs;
    String _name;
    @Deprecated
    ParagraphProperties _pap;
    @Deprecated
    CharacterProperties _chp;

    public StyleDescription() {
    }

    public StyleDescription(byte[] std, int baseLength, int offset, boolean word9) {
        int multiplier;
        short nameLength;
        this._baseLength = baseLength;
        int nameStart = offset + baseLength;
        boolean readStdfPost2000 = false;
        if (baseLength == 18) {
            readStdfPost2000 = true;
        } else if (baseLength == 10) {
            readStdfPost2000 = false;
        } else {
            LOG.atWarn().log("Style definition has non-standard size of {}", (Object)Unbox.box(baseLength));
        }
        this._stdfBase = new StdfBase(std, offset);
        offset += StdfBaseAbstractType.getSize();
        if (readStdfPost2000) {
            this._stdfPost2000 = new StdfPost2000(std, offset);
        }
        if (word9) {
            nameLength = LittleEndian.getShort(std, nameStart);
            multiplier = 2;
            nameStart += 2;
        } else {
            multiplier = 1;
            nameLength = std[nameStart];
        }
        this._name = StringUtil.getFromUnicodeLE(std, nameStart, nameLength * multiplier / 2);
        int varOffset = (nameLength + 1) * multiplier + nameStart;
        int countOfUPX = this._stdfBase.getCupx();
        this._upxs = new UPX[countOfUPX];
        for (int x = 0; x < countOfUPX; ++x) {
            short upxSize = LittleEndian.getShort(std, varOffset);
            byte[] upx = IOUtils.safelyClone(std, varOffset += 2, upxSize, Short.MAX_VALUE);
            this._upxs[x] = new UPX(upx);
            varOffset += upxSize;
            if ((upxSize & 1) != 1) continue;
            ++varOffset;
        }
    }

    public int getBaseStyle() {
        return this._stdfBase.getIstdBase();
    }

    public byte[] getCHPX() {
        switch (this._stdfBase.getStk()) {
            case 1: {
                if (this._upxs.length > 1) {
                    return this._upxs[1].getUPX();
                }
                return null;
            }
            case 2: {
                return this._upxs[0].getUPX();
            }
        }
        return null;
    }

    public byte[] getPAPX() {
        return this._stdfBase.getStk() == 1 ? this._upxs[0].getUPX() : null;
    }

    @Deprecated
    public ParagraphProperties getPAP() {
        return this._pap;
    }

    @Deprecated
    public CharacterProperties getCHP() {
        return this._chp;
    }

    @Deprecated
    void setPAP(ParagraphProperties pap) {
        this._pap = pap;
    }

    @Deprecated
    void setCHP(CharacterProperties chp) {
        this._chp = chp;
    }

    public String getName() {
        return this._name;
    }

    public byte[] toByteArray() {
        int size = this._baseLength + 2 + (this._name.length() + 1) * 2;
        size += this._upxs[0].size() + 2;
        for (int x = 1; x < this._upxs.length; ++x) {
            size += this._upxs[x - 1].size() % 2;
            size += this._upxs[x].size() + 2;
        }
        byte[] buf = new byte[size];
        this._stdfBase.serialize(buf, 0);
        int offset = this._baseLength;
        char[] letters = this._name.toCharArray();
        LittleEndian.putShort(buf, this._baseLength, (short)letters.length);
        offset += 2;
        for (char letter : letters) {
            LittleEndian.putShort(buf, offset, (short)letter);
            offset += 2;
        }
        offset += 2;
        for (UPX upx : this._upxs) {
            short upxSize = (short)upx.size();
            LittleEndian.putShort(buf, offset, upxSize);
            System.arraycopy(upx.getUPX(), 0, buf, offset += 2, upxSize);
            offset += upxSize + upxSize % 2;
        }
        return buf;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this._name, this._stdfBase, this._upxs});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StyleDescription)) {
            return false;
        }
        StyleDescription other = (StyleDescription)obj;
        if (!Objects.equals(this._name, other._name)) {
            return false;
        }
        if (!Objects.equals(this._stdfBase, other._stdfBase)) {
            return false;
        }
        return Arrays.equals(this._upxs, other._upxs);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[STD]: '");
        result.append(this._name);
        result.append("'");
        result.append(("\nStdfBase:\t" + this._stdfBase).replace("\n", "\n    "));
        result.append(("\nStdfPost2000:\t" + this._stdfPost2000).replace("\n", "\n    "));
        for (UPX upx : this._upxs) {
            result.append(("\nUPX:\t" + upx).replace("\n", "\n    "));
        }
        return result.toString();
    }
}

