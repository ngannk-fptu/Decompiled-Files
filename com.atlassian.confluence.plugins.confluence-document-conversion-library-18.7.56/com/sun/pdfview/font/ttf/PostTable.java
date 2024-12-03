/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;

public class PostTable
extends TrueTypeTable {
    private int format;
    private int italicAngle;
    private short underlinePosition;
    private short underlineThickness;
    private short isFixedPitch;
    private int minMemType42;
    private int maxMemType42;
    private int minMemType1;
    private int maxMemType1;
    private PostMap nameMap = new PostMap();

    protected PostTable() {
        super(1886352244);
    }

    public short getGlyphNameIndex(String name) {
        return this.nameMap.getCharIndex(name);
    }

    public String getGlyphName(char c) {
        return this.nameMap.getCharName(c);
    }

    @Override
    public ByteBuffer getData() {
        int size = this.getLength();
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.putInt(this.getFormat());
        buf.putInt(this.getItalicAngle());
        buf.putShort(this.getUnderlinePosition());
        buf.putShort(this.getUnderlineThickness());
        buf.putShort(this.getIsFixedPitch());
        buf.putShort((short)0);
        buf.putInt(this.getMinMemType42());
        buf.putInt(this.getMaxMemType42());
        buf.putInt(this.getMinMemType1());
        buf.putInt(this.getMaxMemType1());
        buf.put(this.nameMap.getData());
        buf.flip();
        return buf;
    }

    @Override
    public void setData(ByteBuffer data) {
        this.setFormat(data.getInt());
        this.setItalicAngle(data.getInt());
        this.setUnderlinePosition(data.getShort());
        this.setUnderlineThickness(data.getShort());
        this.setIsFixedPitch(data.getShort());
        data.getShort();
        this.setMinMemType42(data.getInt());
        this.setMaxMemType42(data.getInt());
        this.setMinMemType1(data.getInt());
        this.setMaxMemType1(data.getInt());
        switch (this.format) {
            case 65536: {
                this.nameMap = new PostMapFormat0();
                break;
            }
            case 131072: {
                this.nameMap = new PostMapFormat2();
                break;
            }
            case 196608: {
                this.nameMap = new PostMap();
                break;
            }
            default: {
                this.nameMap = new PostMap();
                System.out.println("Unknown post map type: " + Integer.toHexString(this.format));
            }
        }
        this.nameMap.setData(data);
    }

    @Override
    public int getLength() {
        int size = 32;
        if (this.nameMap != null) {
            size += this.nameMap.getLength();
        }
        return size;
    }

    public int getFormat() {
        return this.format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getItalicAngle() {
        return this.italicAngle;
    }

    public void setItalicAngle(int italicAngle) {
        this.italicAngle = italicAngle;
    }

    public short getUnderlinePosition() {
        return this.underlinePosition;
    }

    public void setUnderlinePosition(short underlinePosition) {
        this.underlinePosition = underlinePosition;
    }

    public short getUnderlineThickness() {
        return this.underlineThickness;
    }

    public void setUnderlineThickness(short underlineThickness) {
        this.underlineThickness = underlineThickness;
    }

    public short getIsFixedPitch() {
        return this.isFixedPitch;
    }

    public void setIsFixedPitch(short isFixedPitch) {
        this.isFixedPitch = isFixedPitch;
    }

    public int getMinMemType42() {
        return this.minMemType42;
    }

    public void setMinMemType42(int minMemType42) {
        this.minMemType42 = minMemType42;
    }

    public int getMaxMemType42() {
        return this.maxMemType42;
    }

    public void setMaxMemType42(int maxMemType42) {
        this.maxMemType42 = maxMemType42;
    }

    public int getMinMemType1() {
        return this.minMemType1;
    }

    public void setMinMemType1(int minMemType1) {
        this.minMemType1 = minMemType1;
    }

    public int getMaxMemType1() {
        return this.maxMemType1;
    }

    public void setMaxMemType1(int maxMemType1) {
        this.maxMemType1 = maxMemType1;
    }

    class PostMapFormat2
    extends PostMapFormat0 {
        short[] glyphNameIndex;
        String[] glyphNames;

        PostMapFormat2() {
        }

        @Override
        short getCharIndex(String charName) {
            short idx = -1;
            for (int i = 0; i < this.glyphNames.length; ++i) {
                if (!charName.equals(this.glyphNames[i])) continue;
                idx = (short)(this.stdNames.length + i);
                break;
            }
            if (idx == -1) {
                idx = super.getCharIndex(charName);
            }
            for (int c = 0; c < this.glyphNameIndex.length; ++c) {
                if (this.glyphNameIndex[c] != idx) continue;
                return (short)c;
            }
            return 0;
        }

        @Override
        String getCharName(char charIndex) {
            if (charIndex >= this.stdNames.length) {
                return this.glyphNames[charIndex - this.stdNames.length];
            }
            return super.getCharName(charIndex);
        }

        @Override
        int getLength() {
            int size = 2 + 2 * this.glyphNameIndex.length;
            for (int i = 0; i < this.glyphNames.length; ++i) {
                size += this.glyphNames[i].length() + 1;
            }
            return size;
        }

        @Override
        ByteBuffer getData() {
            int i;
            ByteBuffer buf = ByteBuffer.allocate(this.getLength());
            buf.putShort((short)this.glyphNameIndex.length);
            for (i = 0; i < this.glyphNameIndex.length; ++i) {
                buf.putShort(this.glyphNameIndex[i]);
            }
            for (i = 0; i < this.glyphNames.length; ++i) {
                buf.put((byte)this.glyphNames[i].length());
                buf.put(this.glyphNames[i].getBytes());
            }
            buf.flip();
            return buf;
        }

        @Override
        void setData(ByteBuffer data) {
            int i;
            int numGlyphs = data.getShort();
            this.glyphNameIndex = new short[numGlyphs];
            int maxGlyph = 257;
            for (i = 0; i < numGlyphs; ++i) {
                this.glyphNameIndex[i] = data.getShort();
                if (this.glyphNameIndex[i] <= maxGlyph) continue;
                maxGlyph = this.glyphNameIndex[i];
            }
            this.glyphNames = new String[maxGlyph -= 257];
            for (i = 0; i < maxGlyph; ++i) {
                byte size = data.get();
                byte[] stringData = new byte[size];
                data.get(stringData);
                this.glyphNames[i] = new String(stringData);
            }
        }
    }

    class PostMapFormat0
    extends PostMap {
        protected final String[] stdNames;

        PostMapFormat0() {
            this.stdNames = new String[]{".notdef", ".null", "nonmarkingreturn", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quotesingle", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "ackslash", "bracketright", "asciicircum", "underscore", "grave", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "Adieresis", "Aring", "Ccedilla", "Eacute", "Ntilde", "Odieresis", "Udieresis", "aacute", "agrave", "acircumflex", "adieresis", "atilde", "aring", "ccedilla", "eacute", "egrave", "ecircumflex", "edieresis", "iacute", "igrave", "icircumflex", "idieresis", "ntilde", "oacute", "ograve", "ocircumflex", "odieresis", "otilde", "uacute", "ugrave", "ucircumflex", "udieresis", "dagger", "degree", "cent", "sterling", "section", "bullet", "paragraph", "germandbls", "registered", "copyright", "trademark", "acute", "dieresis", "notequal", "AE", "Oslash", "infinity", "plusminus", "lessequal", "greaterequal", "yen", "mu", "partialdiff", "summation", "product", "pi", "integral", "ordfeminine", "ordmasculine", "Omega", "ae", "oslash", "questiondown", "exclamdown", "logicalnot", "radical", "florin", "approxequal", "Delta", "guillemotleft", "guillemotright", "ellipsis", "nonbreakingspace", "Agrave", "Atilde", "Otilde", "OE", "oe", "endash", "emdash", "quotedblleft", "quotedblright", "quoteleft", "quoteright", "divide", "lozenge", "ydieresis", "Ydieresis", "fraction", "currency", "guilsinglleft", "guilsinglright", "fi", "fl", "daggerdbl", "periodcentered", "quotesinglbase", "quotedblbase", "perthousand", "Acircumflex", "Ecircumflex", "Aacute", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Oacute", "Ocircumflex", "apple", "Ograve", "Uacute", "Ucircumflex", "Ugrave", "dotlessi", "circumflex", "tilde", "macron", "breve", "dotaccent", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "Lslash", "lslash", "Scaron", "scaron", "Zcaron", "zcaron", "brokenbar", "Eth", "eth", "Yacute", "yacute", "Thorn", "thorn", "minus", "multiply", "onesuperior", "twosuperior", "threesuperior", "onehalf", "onequarter", "threequarters", "franc", "Gbreve", "gbreve", "Idotaccent", "Scedilla", "scedilla", "Cacute", "cacute", "Ccaron", "ccaron", "dcroat"};
        }

        @Override
        short getCharIndex(String charName) {
            for (int i = 0; i < this.stdNames.length; ++i) {
                if (!charName.equals(this.stdNames[i])) continue;
                return (short)i;
            }
            return 0;
        }

        @Override
        String getCharName(char charIndex) {
            return this.stdNames[charIndex];
        }

        @Override
        int getLength() {
            return 0;
        }

        @Override
        ByteBuffer getData() {
            return ByteBuffer.allocate(0);
        }

        @Override
        void setData(ByteBuffer data) {
        }
    }

    class PostMap {
        PostMap() {
        }

        short getCharIndex(String charName) {
            return 0;
        }

        String getCharName(char charIndex) {
            return null;
        }

        int getLength() {
            return 0;
        }

        ByteBuffer getData() {
            return ByteBuffer.allocate(0);
        }

        void setData(ByteBuffer data) {
        }
    }
}

