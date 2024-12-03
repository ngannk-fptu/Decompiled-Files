/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.BytePropertyNode;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.sprm.CharacterSprmUncompressor;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.util.Internal;

@Internal
public final class CHPX
extends BytePropertyNode<CHPX> {
    public CHPX(CHPX other) {
        super(other);
    }

    CHPX(int charStart, int charEnd, SprmBuffer buf) {
        super(charStart, charEnd, buf);
    }

    public byte[] getGrpprl() {
        return ((SprmBuffer)this._buf).toByteArray();
    }

    public SprmBuffer getSprmBuf() {
        return (SprmBuffer)this._buf;
    }

    public CharacterProperties getCharacterProperties(StyleSheet ss, short istd) {
        if (ss == null) {
            return new CharacterProperties();
        }
        CharacterProperties baseStyle = ss.getCharacterStyle(istd);
        return CharacterSprmUncompressor.uncompressCHP(ss, baseStyle, this.getGrpprl(), 0);
    }

    public String toString() {
        return "CHPX from " + this.getStart() + " to " + this.getEnd() + " (in bytes " + this.getStartBytes() + " to " + this.getEndBytes() + ")";
    }

    @Override
    public CHPX copy() {
        return new CHPX(this);
    }
}

