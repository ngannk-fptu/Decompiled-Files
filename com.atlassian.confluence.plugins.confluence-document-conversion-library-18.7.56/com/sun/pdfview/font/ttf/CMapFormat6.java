/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.CMap;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class CMapFormat6
extends CMap {
    private short firstCode;
    private short entryCount;
    private short[] glyphIndexArray;
    private HashMap<Short, Short> glyphLookup = new HashMap();

    protected CMapFormat6(short language) {
        super((short)6, language);
    }

    @Override
    public short getLength() {
        short size = 10;
        size = (short)(size + this.entryCount * 2);
        return size;
    }

    @Override
    public byte map(byte src) {
        char c = this.map((char)src);
        if (c < '\uffffff80' || c > '\u007f') {
            return 0;
        }
        return (byte)c;
    }

    @Override
    public char map(char src) {
        if (src < this.firstCode || src > this.firstCode + this.entryCount) {
            return '\u0000';
        }
        return (char)this.glyphIndexArray[src - this.firstCode];
    }

    @Override
    public char reverseMap(short glyphID) {
        Short result = this.glyphLookup.get(new Short(glyphID));
        if (result == null) {
            return '\u0000';
        }
        return (char)result.shortValue();
    }

    @Override
    public void setData(int length, ByteBuffer data) {
        this.firstCode = data.getShort();
        this.entryCount = data.getShort();
        this.glyphIndexArray = new short[this.entryCount];
        for (int i = 0; i < this.glyphIndexArray.length; ++i) {
            this.glyphIndexArray[i] = data.getShort();
            this.glyphLookup.put(new Short(this.glyphIndexArray[i]), new Short((short)(i + this.firstCode)));
        }
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putShort(this.getFormat());
        buf.putShort(this.getLength());
        buf.putShort(this.getLanguage());
        buf.putShort(this.firstCode);
        buf.putShort(this.entryCount);
        for (int i = 0; i < this.glyphIndexArray.length; ++i) {
            buf.putShort(this.glyphIndexArray[i]);
        }
        buf.flip();
        return buf;
    }
}

