/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.CMapFormat0;
import com.sun.pdfview.font.ttf.CMapFormat4;
import com.sun.pdfview.font.ttf.CMapFormat6;
import java.nio.ByteBuffer;

public abstract class CMap {
    private short format;
    private short language;

    protected CMap(short format, short language) {
        this.format = format;
        this.language = language;
    }

    public static CMap createMap(short format, short language) {
        CMap outMap = null;
        switch (format) {
            case 0: {
                outMap = new CMapFormat0(language);
                break;
            }
            case 4: {
                outMap = new CMapFormat4(language);
                break;
            }
            case 6: {
                outMap = new CMapFormat6(language);
                break;
            }
            default: {
                System.out.println("Unsupport CMap format: " + format);
                return null;
            }
        }
        return outMap;
    }

    public static CMap getMap(ByteBuffer data) {
        short format = data.getShort();
        short lengthShort = data.getShort();
        int length = 0xFFFF & lengthShort;
        data.limit(Math.min(length, data.limit()));
        short language = data.getShort();
        CMap outMap = CMap.createMap(format, language);
        if (outMap == null) {
            return null;
        }
        outMap.setData(data.limit(), data);
        return outMap;
    }

    public short getFormat() {
        return this.format;
    }

    public short getLanguage() {
        return this.language;
    }

    public abstract void setData(int var1, ByteBuffer var2);

    public abstract ByteBuffer getData();

    public abstract short getLength();

    public abstract byte map(byte var1);

    public abstract char map(char var1);

    public abstract char reverseMap(short var1);

    public String toString() {
        String indent = "        ";
        return indent + " format: " + this.getFormat() + " length: " + this.getLength() + " language: " + this.getLanguage() + "\n";
    }
}

