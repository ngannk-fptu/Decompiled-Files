/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.CMap;
import java.nio.ByteBuffer;

public class CMapFormat0
extends CMap {
    private byte[] glyphIndex;

    protected CMapFormat0(short language) {
        super((short)0, language);
        byte[] initialIndex = new byte[256];
        for (int i = 0; i < initialIndex.length; ++i) {
            initialIndex[i] = (byte)i;
        }
        this.setMap(initialIndex);
    }

    @Override
    public short getLength() {
        return 262;
    }

    @Override
    public byte map(byte src) {
        int i = 0xFF & src;
        return this.glyphIndex[i];
    }

    @Override
    public char map(char src) {
        if (src < '\u0000' || src > '\u00ff') {
            return '\u0000';
        }
        return (char)(this.map((byte)src) & 0xFF);
    }

    @Override
    public char reverseMap(short glyphID) {
        for (int i = 0; i < this.glyphIndex.length; ++i) {
            if ((this.glyphIndex[i] & 0xFF) != glyphID) continue;
            return (char)i;
        }
        return '\u0000';
    }

    public void setMap(byte[] glyphIndex) {
        if (glyphIndex.length != 256) {
            throw new IllegalArgumentException("Glyph map must be size 256!");
        }
        this.glyphIndex = glyphIndex;
    }

    public void setMap(byte src, byte dest) {
        int i = 0xFF & src;
        this.glyphIndex[i] = dest;
    }

    protected byte[] getMap() {
        return this.glyphIndex;
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(262);
        buf.putShort(this.getFormat());
        buf.putShort(this.getLength());
        buf.putShort(this.getLanguage());
        buf.put(this.getMap());
        buf.flip();
        return buf;
    }

    @Override
    public void setData(int length, ByteBuffer data) {
        if (length != 262) {
            throw new IllegalArgumentException("Bad length for CMap format 0");
        }
        if (data.remaining() != 256) {
            throw new IllegalArgumentException("Wrong amount of data for CMap format 0");
        }
        byte[] map = new byte[256];
        data.get(map);
        this.setMap(map);
    }
}

