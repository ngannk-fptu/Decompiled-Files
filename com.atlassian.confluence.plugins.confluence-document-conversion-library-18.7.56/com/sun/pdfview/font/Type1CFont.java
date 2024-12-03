/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.FlPoint;
import com.sun.pdfview.font.FontSupport;
import com.sun.pdfview.font.OutlineFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

public class Type1CFont
extends OutlineFont {
    String[] chr2name = new String[256];
    byte[] data;
    int pos;
    byte[] subrs;
    float[] stack = new float[100];
    int stackptr = 0;
    String[] names;
    int[] glyphnames;
    int[] encoding = new int[256];
    String fontname;
    AffineTransform at = new AffineTransform(0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f);
    int num;
    float fnum;
    int type;
    static int CMD = 0;
    static int NUM = 1;
    static int FLT = 2;
    int charstringtype = 2;
    float[] temps = new float[32];
    int charsetbase = 0;
    int encodingbase = 0;
    int charstringbase = 0;
    int privatebase = 0;
    int privatesize = 0;
    int gsubrbase = 0;
    int lsubrbase = 0;
    int gsubrsoffset = 0;
    int lsubrsoffset = 0;
    int nglyphs = 1;

    public Type1CFont(String baseFont, PDFObject src, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, src, descriptor);
        PDFObject dataObj = descriptor.getFontFile3();
        this.data = dataObj.getStream();
        this.pos = 0;
        this.parse();
    }

    private void printData() {
        char[] parts = new char[17];
        int partsloc = 0;
        for (int i = 0; i < this.data.length; ++i) {
            int d = this.data[i] & 0xFF;
            parts[partsloc++] = d == 0 ? 46 : (d < 32 || d >= 127 ? 63 : (char)d);
            if (d < 16) {
                System.out.print("0" + Integer.toHexString(d));
            } else {
                System.out.print(Integer.toHexString(d));
            }
            if ((i & 0xF) == 15) {
                System.out.println("      " + new String(parts));
                partsloc = 0;
                continue;
            }
            if ((i & 7) == 7) {
                System.out.print("  ");
                parts[partsloc++] = 32;
                continue;
            }
            if ((i & 1) != 1) continue;
            System.out.print(" ");
        }
        System.out.println();
    }

    private int readNext(boolean charstring) {
        this.num = this.data[this.pos++] & 0xFF;
        if (this.num == 30 && !charstring) {
            this.readFNum();
            this.type = FLT;
            return this.type;
        }
        if (this.num == 28) {
            this.num = (this.data[this.pos] << 8) + (this.data[this.pos + 1] & 0xFF);
            this.pos += 2;
            this.type = NUM;
            return this.type;
        }
        if (this.num == 29 && !charstring) {
            this.num = (this.data[this.pos] & 0xFF) << 24 | (this.data[this.pos + 1] & 0xFF) << 16 | (this.data[this.pos + 2] & 0xFF) << 8 | this.data[this.pos + 3] & 0xFF;
            this.pos += 4;
            this.type = NUM;
            return this.type;
        }
        if (this.num == 12) {
            this.num = 1000 + (this.data[this.pos++] & 0xFF);
            this.type = CMD;
            return this.type;
        }
        if (this.num < 32) {
            this.type = CMD;
            return this.type;
        }
        if (this.num < 247) {
            this.num -= 139;
            this.type = NUM;
            return this.type;
        }
        if (this.num < 251) {
            this.num = (this.num - 247) * 256 + (this.data[this.pos++] & 0xFF) + 108;
            this.type = NUM;
            return this.type;
        }
        if (this.num < 255) {
            this.num = -(this.num - 251) * 256 - (this.data[this.pos++] & 0xFF) - 108;
            this.type = NUM;
            return this.type;
        }
        if (!charstring) {
            this.printData();
            throw new RuntimeException("Got a 255 code while reading dict");
        }
        this.fnum = (float)((this.data[this.pos] & 0xFF) << 24 | (this.data[this.pos + 1] & 0xFF) << 16 | (this.data[this.pos + 2] & 0xFF) << 8 | this.data[this.pos + 3] & 0xFF) / 65536.0f;
        this.pos += 4;
        this.type = FLT;
        return this.type;
    }

    public void readFNum() {
        float f = 0.0f;
        boolean neg = false;
        int exp = 0;
        int eval = 0;
        float mul = 1.0f;
        byte work = this.data[this.pos++];
        while (true) {
            if (work == -35) {
                work = this.data[this.pos++];
            }
            int nyb = work >> 4 & 0xF;
            work = (byte)(work << 4 | 0xD);
            if (nyb < 10) {
                if (exp != 0) {
                    eval = eval * 10 + nyb;
                    continue;
                }
                if (mul == 1.0f) {
                    f = f * 10.0f + (float)nyb;
                    continue;
                }
                f += (float)nyb * mul;
                mul /= 10.0f;
                continue;
            }
            if (nyb == 10) {
                mul = 0.1f;
                continue;
            }
            if (nyb == 11) {
                exp = 1;
                continue;
            }
            if (nyb == 12) {
                exp = -1;
                continue;
            }
            if (nyb != 14) break;
            neg = true;
        }
        this.fnum = (float)(neg ? -1 : 1) * f * (float)Math.pow(10.0, eval * exp);
    }

    private int readInt(int len) {
        int n = 0;
        for (int i = 0; i < len; ++i) {
            n = n << 8 | this.data[this.pos++] & 0xFF;
        }
        return n;
    }

    private int readByte() {
        return this.data[this.pos++] & 0xFF;
    }

    public int getIndexSize(int loc) {
        int hold = this.pos;
        this.pos = loc;
        int count = this.readInt(2);
        if (count <= 0) {
            this.pos = hold;
            return 2;
        }
        int encsz = this.readByte();
        if (encsz < 1 || encsz > 4) {
            throw new RuntimeException("Offsize: " + encsz + ", must be in range 1-4.");
        }
        this.pos += count * encsz;
        int end = this.readInt(encsz);
        this.pos = hold;
        return 2 + (count + 1) * encsz + end;
    }

    public int getTableLength(int loc) {
        int hold = this.pos;
        this.pos = loc;
        int count = this.readInt(2);
        if (count <= 0) {
            return 2;
        }
        this.pos = hold;
        return count;
    }

    Range getIndexEntry(int index, int id) {
        int hold = this.pos;
        this.pos = index;
        int count = this.readInt(2);
        int encsz = this.readByte();
        if (encsz < 1 || encsz > 4) {
            throw new RuntimeException("Offsize: " + encsz + ", must be in range 1-4.");
        }
        this.pos += encsz * id;
        int from = this.readInt(encsz);
        Range r = new Range(from + 2 + index + encsz * (count + 1), this.readInt(encsz) - from);
        this.pos = hold;
        return r;
    }

    private void readDict(Range r) {
        this.pos = r.getStart();
        while (this.pos < r.getEnd()) {
            int cmd = this.readCommand(false);
            if (cmd == 1006) {
                this.charstringtype = (int)this.stack[0];
            } else if (cmd == 1007) {
                this.at = this.stackptr == 4 ? new AffineTransform(this.stack[0], this.stack[1], this.stack[2], this.stack[3], 0.0f, 0.0f) : new AffineTransform(this.stack[0], this.stack[1], this.stack[2], this.stack[3], this.stack[4], this.stack[5]);
            } else if (cmd == 15) {
                this.charsetbase = (int)this.stack[0];
            } else if (cmd == 16) {
                this.encodingbase = (int)this.stack[0];
            } else if (cmd == 17) {
                this.charstringbase = (int)this.stack[0];
            } else if (cmd == 18) {
                this.privatesize = (int)this.stack[0];
                this.privatebase = (int)this.stack[1];
            } else if (cmd == 19) {
                this.lsubrbase = this.privatebase + (int)this.stack[0];
                this.lsubrsoffset = this.calcoffset(this.lsubrbase);
            }
            this.stackptr = 0;
        }
    }

    private int readCommand(boolean charstring) {
        int t;
        while ((t = this.readNext(charstring)) != CMD) {
            this.stack[this.stackptr++] = t == NUM ? (float)this.num : this.fnum;
        }
        return this.num;
    }

    private void readEncodingData(int base) {
        if (base == 0) {
            System.arraycopy(FontSupport.standardEncoding, 0, this.encoding, 0, FontSupport.standardEncoding.length);
        } else if (base == 1) {
            System.out.println("**** EXPERT ENCODING!");
        } else {
            this.pos = base;
            int encodingtype = this.readByte();
            if ((encodingtype & 0x7F) == 0) {
                int ncodes = this.readByte();
                int i = 1;
                while (i < ncodes + 1) {
                    int idx = this.readByte() & 0xFF;
                    this.encoding[idx] = i++;
                }
            } else if ((encodingtype & 0x7F) == 1) {
                int nranges = this.readByte();
                int p = 1;
                for (int i = 0; i < nranges; ++i) {
                    int start = this.readByte();
                    int more = this.readByte();
                    for (int j = start; j < start + more + 1; ++j) {
                        this.encoding[j] = p++;
                    }
                }
            } else {
                System.out.println("Bad encoding type: " + encodingtype);
            }
        }
    }

    private void readGlyphNames(int base) {
        block10: {
            int t;
            block11: {
                block9: {
                    if (base == 0) {
                        this.glyphnames = new int[229];
                        for (int i = 0; i < this.glyphnames.length; ++i) {
                            this.glyphnames[i] = i;
                        }
                        return;
                    }
                    if (base == 1) {
                        this.glyphnames = FontSupport.type1CExpertCharset;
                        return;
                    }
                    if (base == 2) {
                        this.glyphnames = FontSupport.type1CExpertSubCharset;
                        return;
                    }
                    this.glyphnames = new int[this.nglyphs];
                    this.glyphnames[0] = 0;
                    this.pos = base;
                    t = this.readByte();
                    if (t != 0) break block9;
                    for (int i = 1; i < this.nglyphs; ++i) {
                        this.glyphnames[i] = this.readInt(2);
                    }
                    break block10;
                }
                if (t != 1) break block11;
                int n = 1;
                while (n < this.nglyphs) {
                    int sid = this.readInt(2);
                    int range = this.readByte() + 1;
                    for (int i = 0; i < range; ++i) {
                        this.glyphnames[n++] = sid++;
                    }
                }
                break block10;
            }
            if (t != 2) break block10;
            int n = 1;
            while (n < this.nglyphs) {
                int sid = this.readInt(2);
                int range = this.readInt(2) + 1;
                for (int i = 0; i < range; ++i) {
                    this.glyphnames[n++] = sid++;
                }
            }
        }
    }

    private void readNames(int base) {
        this.pos = base;
        int nextra = this.readInt(2);
        this.names = new String[nextra];
        for (int i = 0; i < nextra; ++i) {
            Range r = this.getIndexEntry(base, i);
            this.names[i] = new String(this.data, r.getStart(), r.getLen());
        }
    }

    private void parse() throws IOException {
        int majorVersion = this.readByte();
        int minorVersion = this.readByte();
        int hdrsz = this.readByte();
        int offsize = this.readByte();
        int fnames = hdrsz;
        int topdicts = fnames + this.getIndexSize(fnames);
        int theNames = topdicts + this.getIndexSize(topdicts);
        this.gsubrbase = theNames + this.getIndexSize(theNames);
        this.gsubrsoffset = this.calcoffset(this.gsubrbase);
        this.readNames(theNames);
        this.pos = topdicts;
        if (this.readInt(2) != 1) {
            this.printData();
            throw new RuntimeException("More than one font in this file!");
        }
        Range r = this.getIndexEntry(fnames, 0);
        this.fontname = new String(this.data, r.getStart(), r.getLen());
        this.readDict(this.getIndexEntry(topdicts, 0));
        this.readDict(new Range(this.privatebase, this.privatesize));
        this.pos = this.charstringbase;
        this.nglyphs = this.readInt(2);
        this.readGlyphNames(this.charsetbase);
        this.readEncodingData(this.encodingbase);
    }

    private int getNameIndex(String name) {
        int val = FontSupport.findName(name, FontSupport.stdNames);
        if (val == -1) {
            val = FontSupport.findName(name, this.names) + FontSupport.stdNames.length;
        }
        if (val == -1) {
            val = 0;
        }
        return val;
    }

    private String safe(String src) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < src.length(); ++i) {
            char c = src.charAt(i);
            if (c >= ' ' && c < '\u0080') {
                sb.append(c);
                continue;
            }
            sb.append("<" + c + ">");
        }
        return sb.toString();
    }

    private synchronized GeneralPath readGlyph(int base, int offset) {
        FlPoint pt = new FlPoint();
        Range r = this.getIndexEntry(base, offset);
        GeneralPath gp = new GeneralPath();
        int hold = this.pos;
        this.stackptr = 0;
        this.parseGlyph(r, gp, pt);
        this.pos = hold;
        gp.transform(this.at);
        return gp;
    }

    public int calcoffset(int base) {
        int len = this.getTableLength(base);
        if (len < 1240) {
            return 107;
        }
        if (len < 33900) {
            return 1131;
        }
        return 32768;
    }

    public String getSID(int id) {
        if (id < FontSupport.stdNames.length) {
            return FontSupport.stdNames[id];
        }
        return this.names[id -= FontSupport.stdNames.length];
    }

    private void buildAccentChar(float x, float y, char b, char a, GeneralPath gp) {
        GeneralPath pathA = this.getOutline(a, this.getWidth(a, null));
        AffineTransform xformA = AffineTransform.getTranslateInstance(x, y);
        try {
            xformA.concatenate(this.at.createInverse());
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        pathA.transform(xformA);
        GeneralPath pathB = this.getOutline(b, this.getWidth(b, null));
        try {
            AffineTransform xformB = this.at.createInverse();
            pathB.transform(xformB);
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        gp.append(pathB, false);
        gp.append(pathA, false);
    }

    void parseGlyph(Range r, GeneralPath gp, FlPoint pt) {
        this.pos = r.getStart();
        int stemhints = 0;
        block48: while (this.pos < r.getEnd()) {
            int cmd = this.readCommand(true);
            int hold = 0;
            switch (cmd) {
                case 1: 
                case 3: {
                    this.stackptr = 0;
                    continue block48;
                }
                case 4: {
                    if (this.stackptr > 1) {
                        this.stack[0] = this.stack[1];
                    }
                    pt.y += this.stack[0];
                    if (pt.open) {
                        gp.closePath();
                    }
                    pt.open = false;
                    gp.moveTo(pt.x, pt.y);
                    this.stackptr = 0;
                    continue block48;
                }
                case 5: {
                    int i = 0;
                    while (i < this.stackptr) {
                        pt.x += this.stack[i++];
                        pt.y += this.stack[i++];
                        gp.lineTo(pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 6: {
                    int i = 0;
                    while (i < this.stackptr) {
                        if ((i & 1) == 0) {
                            pt.x += this.stack[i++];
                        } else {
                            pt.y += this.stack[i++];
                        }
                        gp.lineTo(pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 7: {
                    int i = 0;
                    while (i < this.stackptr) {
                        if ((i & 1) == 0) {
                            pt.y += this.stack[i++];
                        } else {
                            pt.x += this.stack[i++];
                        }
                        gp.lineTo(pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 8: {
                    float y2;
                    float x2;
                    float y1;
                    float x1;
                    int i = 0;
                    while (i < this.stackptr) {
                        x1 = pt.x + this.stack[i++];
                        y1 = pt.y + this.stack[i++];
                        x2 = x1 + this.stack[i++];
                        y2 = y1 + this.stack[i++];
                        pt.x = x2 + this.stack[i++];
                        pt.y = y2 + this.stack[i++];
                        gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 10: {
                    hold = this.pos;
                    int i = (int)this.stack[--this.stackptr] + this.lsubrsoffset;
                    Range lsubr = this.getIndexEntry(this.lsubrbase, i);
                    this.parseGlyph(lsubr, gp, pt);
                    this.pos = hold;
                    continue block48;
                }
                case 11: {
                    return;
                }
                case 14: {
                    if (this.stackptr == 5) {
                        this.buildAccentChar(this.stack[1], this.stack[2], (char)this.stack[3], (char)this.stack[4], gp);
                    }
                    if (pt.open) {
                        gp.closePath();
                    }
                    pt.open = false;
                    this.stackptr = 0;
                    continue block48;
                }
                case 18: {
                    stemhints += this.stackptr / 2;
                    this.stackptr = 0;
                    continue block48;
                }
                case 19: 
                case 20: {
                    this.pos += ((stemhints += this.stackptr / 2) - 1) / 8 + 1;
                    this.stackptr = 0;
                    continue block48;
                }
                case 21: {
                    if (this.stackptr > 2) {
                        this.stack[0] = this.stack[1];
                        this.stack[1] = this.stack[2];
                    }
                    pt.x += this.stack[0];
                    pt.y += this.stack[1];
                    if (pt.open) {
                        gp.closePath();
                    }
                    gp.moveTo(pt.x, pt.y);
                    pt.open = false;
                    this.stackptr = 0;
                    continue block48;
                }
                case 22: {
                    if (this.stackptr > 1) {
                        this.stack[0] = this.stack[1];
                    }
                    pt.x += this.stack[0];
                    if (pt.open) {
                        gp.closePath();
                    }
                    gp.moveTo(pt.x, pt.y);
                    pt.open = false;
                    this.stackptr = 0;
                    continue block48;
                }
                case 23: {
                    stemhints += this.stackptr / 2;
                    this.stackptr = 0;
                    continue block48;
                }
                case 24: {
                    float y2;
                    float x2;
                    float y1;
                    float x1;
                    int i = 0;
                    while (i < this.stackptr - 2) {
                        x1 = pt.x + this.stack[i++];
                        y1 = pt.y + this.stack[i++];
                        x2 = x1 + this.stack[i++];
                        y2 = y1 + this.stack[i++];
                        pt.x = x2 + this.stack[i++];
                        pt.y = y2 + this.stack[i++];
                        gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    }
                    pt.x += this.stack[i++];
                    pt.y += this.stack[i++];
                    gp.lineTo(pt.x, pt.y);
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 25: {
                    int i = 0;
                    while (i < this.stackptr - 6) {
                        pt.x += this.stack[i++];
                        pt.y += this.stack[i++];
                        gp.lineTo(pt.x, pt.y);
                    }
                    float x1 = pt.x + this.stack[i++];
                    float y1 = pt.y + this.stack[i++];
                    float x2 = x1 + this.stack[i++];
                    float y2 = y1 + this.stack[i++];
                    pt.x = x2 + this.stack[i++];
                    pt.y = y2 + this.stack[i++];
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 26: {
                    float y2;
                    float x2;
                    float y1;
                    float x1;
                    int i = 0;
                    if ((this.stackptr & 1) == 1) {
                        pt.x += this.stack[i++];
                    }
                    while (i < this.stackptr) {
                        x1 = pt.x;
                        y1 = pt.y + this.stack[i++];
                        x2 = x1 + this.stack[i++];
                        y2 = y1 + this.stack[i++];
                        pt.x = x2;
                        pt.y = y2 + this.stack[i++];
                        gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 27: {
                    float y2;
                    float x2;
                    float y1;
                    float x1;
                    int i = 0;
                    if ((this.stackptr & 1) == 1) {
                        pt.y += this.stack[i++];
                    }
                    while (i < this.stackptr) {
                        x1 = pt.x + this.stack[i++];
                        y1 = pt.y;
                        x2 = x1 + this.stack[i++];
                        y2 = y1 + this.stack[i++];
                        pt.x = x2 + this.stack[i++];
                        pt.y = y2;
                        gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 29: {
                    hold = this.pos;
                    int i = (int)this.stack[--this.stackptr] + this.gsubrsoffset;
                    Range gsubr = this.getIndexEntry(this.gsubrbase, i);
                    this.parseGlyph(gsubr, gp, pt);
                    this.pos = hold;
                    continue block48;
                }
                case 30: {
                    hold = 4;
                }
                case 31: {
                    float y2;
                    float x2;
                    float y1;
                    float x1;
                    int i = 0;
                    while (i < this.stackptr) {
                        boolean hv = (i + hold & 4) == 0;
                        x1 = pt.x + (hv ? this.stack[i++] : 0.0f);
                        y1 = pt.y + (hv ? 0.0f : this.stack[i++]);
                        x2 = x1 + this.stack[i++];
                        y2 = y1 + this.stack[i++];
                        pt.x = x2 + (hv ? 0.0f : this.stack[i++]);
                        pt.y = y2 + (hv ? this.stack[i++] : 0.0f);
                        if (i == this.stackptr - 1) {
                            if (hv) {
                                pt.x += this.stack[i++];
                            } else {
                                pt.y += this.stack[i++];
                            }
                        }
                        gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    }
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 1000: {
                    this.stackptr = 0;
                    continue block48;
                }
                case 1003: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = x1 != 0.0f && y1 != 0.0f ? 1.0f : 0.0f;
                    continue block48;
                }
                case 1004: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = x1 != 0.0f || y1 != 0.0f ? 1.0f : 0.0f;
                    continue block48;
                }
                case 1005: {
                    float x1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = x1 == 0.0f ? 1.0f : 0.0f;
                    continue block48;
                }
                case 1009: {
                    this.stack[this.stackptr - 1] = Math.abs(this.stack[this.stackptr - 1]);
                    continue block48;
                }
                case 1010: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = x1 + y1;
                    continue block48;
                }
                case 1011: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = y1 - x1;
                    continue block48;
                }
                case 1012: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = y1 / x1;
                    continue block48;
                }
                case 1014: {
                    this.stack[this.stackptr - 1] = -this.stack[this.stackptr - 1];
                    continue block48;
                }
                case 1015: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = x1 == y1 ? 1.0f : 0.0f;
                    continue block48;
                }
                case 1018: {
                    --this.stackptr;
                    continue block48;
                }
                case 1020: {
                    float x1;
                    int i = (int)this.stack[--this.stackptr];
                    this.temps[i] = x1 = this.stack[--this.stackptr];
                    continue block48;
                }
                case 1021: {
                    int i = (int)this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = this.temps[i];
                    continue block48;
                }
                case 1022: {
                    if (this.stack[this.stackptr - 2] > this.stack[this.stackptr - 1]) {
                        this.stack[this.stackptr - 4] = this.stack[this.stackptr - 3];
                    }
                    this.stackptr -= 3;
                    continue block48;
                }
                case 1023: {
                    this.stack[this.stackptr++] = (float)Math.random();
                    continue block48;
                }
                case 1024: {
                    float x1 = this.stack[--this.stackptr];
                    float y1 = this.stack[--this.stackptr];
                    this.stack[this.stackptr++] = y1 * x1;
                    continue block48;
                }
                case 1026: {
                    this.stack[this.stackptr - 1] = (float)Math.sqrt(this.stack[this.stackptr - 1]);
                    continue block48;
                }
                case 1027: {
                    float x1 = this.stack[this.stackptr - 1];
                    this.stack[this.stackptr++] = x1;
                    continue block48;
                }
                case 1028: {
                    float x1 = this.stack[this.stackptr - 1];
                    this.stack[this.stackptr - 1] = this.stack[this.stackptr - 2];
                    this.stack[this.stackptr - 2] = x1;
                    continue block48;
                }
                case 1029: {
                    int i = (int)this.stack[this.stackptr - 1];
                    if (i < 0) {
                        i = 0;
                    }
                    this.stack[this.stackptr - 1] = this.stack[this.stackptr - 2 - i];
                    continue block48;
                }
                case 1030: {
                    int i = (int)this.stack[--this.stackptr];
                    int n = (int)this.stack[--this.stackptr];
                    i = i > 0 ? (i %= n) : n - -i % n;
                    if (i <= 0) continue block48;
                    float[] roll = new float[n];
                    System.arraycopy(this.stack, this.stackptr - 1 - i, roll, 0, i);
                    System.arraycopy(this.stack, this.stackptr - 1 - n, roll, i, n - i);
                    System.arraycopy(roll, 0, this.stack, this.stackptr - 1 - n, n);
                    continue block48;
                }
                case 1034: {
                    float ybase;
                    float x1 = pt.x + this.stack[0];
                    float y1 = ybase = pt.y;
                    float x2 = x1 + this.stack[1];
                    float y2 = y1 + this.stack[2];
                    pt.x = x2 + this.stack[3];
                    pt.y = y2;
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    x1 = pt.x + this.stack[4];
                    y1 = pt.y;
                    x2 = x1 + this.stack[5];
                    y2 = ybase;
                    pt.x = x2 + this.stack[6];
                    pt.y = y2;
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 1035: {
                    float x1 = pt.x + this.stack[0];
                    float y1 = pt.y + this.stack[1];
                    float x2 = x1 + this.stack[2];
                    float y2 = y1 + this.stack[3];
                    pt.x = x2 + this.stack[4];
                    pt.y = y2 + this.stack[5];
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    x1 = pt.x + this.stack[6];
                    y1 = pt.y + this.stack[7];
                    x2 = x1 + this.stack[8];
                    y2 = y1 + this.stack[9];
                    pt.x = x2 + this.stack[10];
                    pt.y = y2 + this.stack[11];
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 1036: {
                    float ybase = pt.y;
                    float x1 = pt.x + this.stack[0];
                    float y1 = pt.y + this.stack[1];
                    float x2 = x1 + this.stack[2];
                    float y2 = y1 + this.stack[3];
                    pt.x = x2 + this.stack[4];
                    pt.y = y2;
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    x1 = pt.x + this.stack[5];
                    y1 = pt.y;
                    x2 = x1 + this.stack[6];
                    y2 = y1 + this.stack[7];
                    pt.x = x2 + this.stack[8];
                    pt.y = ybase;
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
                case 1037: {
                    float ybase = pt.y;
                    float xbase = pt.x;
                    float x1 = pt.x + this.stack[0];
                    float y1 = pt.y + this.stack[1];
                    float x2 = x1 + this.stack[2];
                    float y2 = y1 + this.stack[3];
                    pt.x = x2 + this.stack[4];
                    pt.y = y2 + this.stack[5];
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    x1 = pt.x + this.stack[6];
                    y1 = pt.y + this.stack[7];
                    x2 = x1 + this.stack[8];
                    y2 = y1 + this.stack[9];
                    if (Math.abs(x2 - xbase) > Math.abs(y2 - ybase)) {
                        pt.x = x2 + this.stack[10];
                        pt.y = ybase;
                    } else {
                        pt.x = xbase;
                        pt.y = y2 + this.stack[10];
                    }
                    gp.curveTo(x1, y1, x2, y2, pt.x, pt.y);
                    pt.open = true;
                    this.stackptr = 0;
                    continue block48;
                }
            }
            System.out.println("ERROR! TYPE1C CHARSTRING CMD IS " + cmd);
        }
    }

    @Override
    protected GeneralPath getOutline(String name, float width) {
        int index = this.getNameIndex(name);
        for (int i = 0; i < this.glyphnames.length; ++i) {
            if (this.glyphnames[i] != index) continue;
            return this.readGlyph(this.charstringbase, i);
        }
        return this.readGlyph(this.charstringbase, 0);
    }

    @Override
    protected GeneralPath getOutline(char src, float width) {
        int index = src & 0xFF;
        if (this.encodingbase == 0 || this.encodingbase == 1) {
            for (int i = 0; i < this.glyphnames.length; ++i) {
                if (this.glyphnames[i] != this.encoding[index]) continue;
                return this.readGlyph(this.charstringbase, i);
            }
        } else if (index > 0 && index < this.encoding.length) {
            return this.readGlyph(this.charstringbase, this.encoding[index]);
        }
        return this.readGlyph(this.charstringbase, 0);
    }

    class Range {
        private int start;
        private int len;

        public Range(int start, int len) {
            this.start = start;
            this.len = len;
        }

        public final int getStart() {
            return this.start;
        }

        public final int getLen() {
            return this.len;
        }

        public final int getEnd() {
            return this.start + this.len;
        }

        public String toString() {
            return "Range: start: " + this.start + ", len: " + this.len;
        }
    }
}

