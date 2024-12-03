/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.FlPoint;
import com.sun.pdfview.font.FontSupport;
import com.sun.pdfview.font.OutlineFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Type1Font
extends OutlineFont {
    String[] chr2name;
    int password;
    byte[][] subrs;
    int lenIV;
    Map<String, Object> name2outline;
    Map<String, FlPoint> name2width;
    AffineTransform at;
    float[] stack = new float[100];
    int sloc = 0;
    float[] psStack = new float[3];
    int psLoc = 0;
    int callcount = 0;

    public Type1Font(String baseName, PDFObject src, PDFFontDescriptor descriptor) throws IOException {
        super(baseName, src, descriptor);
        if (descriptor != null && descriptor.getFontFile() != null) {
            int start = descriptor.getFontFile().getDictRef("Length1").getIntValue();
            int len = descriptor.getFontFile().getDictRef("Length2").getIntValue();
            byte[] font = descriptor.getFontFile().getStream();
            this.parseFont(font, start, len);
        }
    }

    protected void parseFont(byte[] font, int start, int len) {
        this.name2width = new HashMap<String, FlPoint>();
        byte[] data = null;
        if (this.isASCII(font, start)) {
            byte[] bData = this.readASCII(font, start, start + len);
            data = this.decrypt(bData, 0, bData.length, 55665, 4);
        } else {
            data = this.decrypt(font, start, start + len, 55665, 4);
        }
        this.chr2name = this.readEncoding(font);
        int lenIVLoc = this.findSlashName(data, "lenIV");
        PSParser psp = new PSParser(data, 0);
        if (lenIVLoc < 0) {
            this.lenIV = 4;
        } else {
            psp.setLoc(lenIVLoc + 6);
            this.lenIV = Integer.parseInt(psp.readThing());
        }
        this.password = 4330;
        int matrixloc = this.findSlashName(font, "FontMatrix");
        if (matrixloc < 0) {
            System.out.println("No FontMatrix!");
            this.at = new AffineTransform(0.001f, 0.0f, 0.0f, 0.001f, 0.0f, 0.0f);
        } else {
            PSParser psp2 = new PSParser(font, matrixloc + 11);
            float[] xf = psp2.readArray(6);
            this.at = new AffineTransform(xf);
        }
        this.subrs = this.readSubrs(data);
        this.name2outline = new TreeMap<String, byte[]>(this.readChars(data));
    }

    private String[] readEncoding(byte[] d) {
        byte[][] ary = this.readArray(d, "Encoding", "def");
        String[] res = new String[256];
        for (int i = 0; i < ary.length; ++i) {
            if (ary[i] != null) {
                if (ary[i][0] == 47) {
                    res[i] = new String(ary[i]).substring(1);
                    continue;
                }
                res[i] = new String(ary[i]);
                continue;
            }
            res[i] = null;
        }
        return res;
    }

    private byte[][] readSubrs(byte[] d) {
        return this.readArray(d, "Subrs", "index");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private byte[][] readArray(byte[] d, String key, String end) {
        int i = this.findSlashName(d, key);
        if (i < 0) {
            return new byte[0][];
        }
        PSParser psp = new PSParser(d, i);
        String type = psp.readThing();
        type = psp.readThing();
        if (type.equals("StandardEncoding")) {
            byte[][] stdenc = new byte[FontSupport.standardEncoding.length][];
            for (i = 0; i < stdenc.length; ++i) {
                stdenc[i] = FontSupport.getName(FontSupport.standardEncoding[i]).getBytes();
            }
            return stdenc;
        }
        int len = Integer.parseInt(type);
        byte[][] out = new byte[len][];
        while (true) {
            String s;
            if ((s = psp.readThing()).equals("dup")) {
                String thing = psp.readThing();
                int id = 0;
                try {
                    id = Integer.parseInt(thing);
                }
                catch (Exception e) {
                    return out;
                }
                String elt = psp.readThing();
                byte[] line = elt.getBytes();
                if (Character.isDigit(elt.charAt(0))) {
                    int hold = Integer.parseInt(elt);
                    String special = psp.readThing();
                    if (special.equals("-|") || special.equals("RD")) {
                        psp.setLoc(psp.getLoc() + 1);
                        line = psp.getNEncodedBytes(hold, this.password, this.lenIV);
                    }
                }
                out[id] = line;
                continue;
            }
            if (s.equals(end)) return out;
        }
    }

    private byte[] decrypt(byte[] d, int start, int end, int key, int skip) {
        if (end - start - skip < 0) {
            skip = 0;
        }
        byte[] o = new byte[end - start - skip];
        int r = key;
        int c1 = 52845;
        int c2 = 22719;
        for (int ipos = start; ipos < end; ++ipos) {
            int c = d[ipos] & 0xFF;
            int p = (c ^ r >> 8) & 0xFF;
            r = (c + r) * c1 + c2 & 0xFFFF;
            if (ipos - start - skip < 0) continue;
            o[ipos - start - skip] = (byte)p;
        }
        return o;
    }

    private byte[] readASCII(byte[] data, int start, int end) {
        byte[] o = new byte[(end - start) / 2];
        int count = 0;
        int bit = 0;
        for (int loc = start; loc < end; ++loc) {
            char c = (char)(data[loc] & 0xFF);
            byte b = 0;
            if (c >= '0' && c <= '9') {
                b = (byte)(c - 48);
            } else if (c >= 'a' && c <= 'f') {
                b = (byte)(10 + (c - 97));
            } else {
                if (c < 'A' || c > 'F') continue;
                b = (byte)(10 + (c - 65));
            }
            if (bit++ % 2 == 0) {
                o[count] = (byte)(b << 4);
                continue;
            }
            int n = count++;
            o[n] = (byte)(o[n] | b);
        }
        return o;
    }

    private boolean isASCII(byte[] data, int start) {
        for (int i = start; i < start + 4; ++i) {
            char c = (char)(data[i] & 0xFF);
            if (c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F') continue;
            return false;
        }
        return true;
    }

    private int findSlashName(byte[] d, String name) {
        for (int i = 0; i < d.length; ++i) {
            if (d[i] != 47) continue;
            boolean found = true;
            for (int j = 0; j < name.length(); ++j) {
                if (d[i + j + 1] == name.charAt(j)) continue;
                found = false;
                break;
            }
            if (!found) continue;
            return i;
        }
        return -1;
    }

    private HashMap<String, byte[]> readChars(byte[] d) {
        HashMap<String, byte[]> hm = new HashMap<String, byte[]>();
        int i = this.findSlashName(d, "CharStrings");
        if (i < 0) {
            return hm;
        }
        PSParser psp = new PSParser(d, i);
        while (true) {
            String s;
            char c;
            if ((c = (s = psp.readThing()).charAt(0)) == '/') {
                int len = Integer.parseInt(psp.readThing());
                String go = psp.readThing();
                if (!go.equals("-|") && !go.equals("RD")) continue;
                psp.setLoc(psp.getLoc() + 1);
                byte[] line = psp.getNEncodedBytes(len, this.password, this.lenIV);
                hm.put(s.substring(1), line);
                continue;
            }
            if (s.equals("end")) break;
        }
        return hm;
    }

    private float pop() {
        float val = 0.0f;
        if (this.sloc > 0) {
            val = this.stack[--this.sloc];
        }
        return val;
    }

    private void parse(byte[] cs, GeneralPath gp, FlPoint pt, FlPoint wid) {
        int loc = 0;
        block27: while (loc < cs.length) {
            int v;
            if ((v = cs[loc++] & 0xFF) == 255) {
                this.stack[this.sloc++] = ((cs[loc] & 0xFF) << 24) + ((cs[loc + 1] & 0xFF) << 16) + ((cs[loc + 2] & 0xFF) << 8) + (cs[loc + 3] & 0xFF);
                loc += 4;
                continue;
            }
            if (v >= 251) {
                this.stack[this.sloc++] = -(v - 251 << 8) - (cs[loc] & 0xFF) - 108;
                ++loc;
                continue;
            }
            if (v >= 247) {
                this.stack[this.sloc++] = (v - 247 << 8) + (cs[loc] & 0xFF) + 108;
                ++loc;
                continue;
            }
            if (v >= 32) {
                this.stack[this.sloc++] = v - 139;
                continue;
            }
            block0 : switch (v) {
                case 0: {
                    throw new RuntimeException("Bad command (" + v + ")");
                }
                case 1: {
                    this.sloc = 0;
                    break;
                }
                case 2: {
                    throw new RuntimeException("Bad command (" + v + ")");
                }
                case 3: {
                    this.sloc = 0;
                    break;
                }
                case 4: {
                    pt.y += this.pop();
                    gp.moveTo(pt.x, pt.y);
                    this.sloc = 0;
                    break;
                }
                case 5: {
                    pt.y += this.pop();
                    pt.x += this.pop();
                    gp.lineTo(pt.x, pt.y);
                    this.sloc = 0;
                    break;
                }
                case 6: {
                    pt.x += this.pop();
                    gp.lineTo(pt.x, pt.y);
                    this.sloc = 0;
                    break;
                }
                case 7: {
                    pt.y += this.pop();
                    gp.lineTo(pt.x, pt.y);
                    this.sloc = 0;
                    break;
                }
                case 8: {
                    float y3 = this.pop();
                    float x3 = this.pop();
                    float y2 = this.pop();
                    float x2 = this.pop();
                    float y1 = this.pop();
                    float x1 = this.pop();
                    gp.curveTo(pt.x + x1, pt.y + y1, pt.x + x1 + x2, pt.y + y1 + y2, pt.x + x1 + x2 + x3, pt.y + y1 + y2 + y3);
                    pt.x += x1 + x2 + x3;
                    pt.y += y1 + y2 + y3;
                    this.sloc = 0;
                    break;
                }
                case 9: {
                    gp.closePath();
                    this.sloc = 0;
                    break;
                }
                case 10: {
                    int n = (int)this.pop();
                    if (this.subrs[n] == null) {
                        System.out.println("No subroutine #" + n);
                        break;
                    }
                    ++this.callcount;
                    if (this.callcount > 10) {
                        System.out.println("Call stack too large");
                    } else {
                        this.parse(this.subrs[n], gp, pt, wid);
                    }
                    --this.callcount;
                    break;
                }
                case 11: {
                    return;
                }
                case 12: {
                    v = cs[loc++] & 0xFF;
                    if (v == 6) {
                        char b = (char)this.pop();
                        char a = (char)this.pop();
                        float y = this.pop();
                        float x = this.pop();
                        this.buildAccentChar(x, y, a, b, gp);
                        this.sloc = 0;
                        break;
                    }
                    if (v == 7) {
                        wid.y = this.pop();
                        wid.x = this.pop();
                        pt.y = this.pop();
                        pt.x = this.pop();
                        this.sloc = 0;
                        break;
                    }
                    if (v == 12) {
                        float b = this.pop();
                        float a = this.pop();
                        this.stack[this.sloc++] = a / b;
                        break;
                    }
                    if (v == 33) {
                        pt.y = this.pop();
                        pt.x = this.pop();
                        gp.moveTo(pt.x, pt.y);
                        this.sloc = 0;
                        break;
                    }
                    if (v == 0) {
                        this.sloc = 0;
                        break;
                    }
                    if (v == 1) {
                        this.sloc = 0;
                        break;
                    }
                    if (v == 2) {
                        this.sloc = 0;
                        break;
                    }
                    if (v == 16) {
                        int cn = (int)this.pop();
                        int countargs = (int)this.pop();
                        switch (cn) {
                            case 0: {
                                this.psStack[this.psLoc++] = this.pop();
                                this.psStack[this.psLoc++] = this.pop();
                                this.pop();
                                break block0;
                            }
                            case 3: {
                                this.psStack[this.psLoc++] = 3.0f;
                                break block0;
                            }
                        }
                        for (int i = 0; i > countargs; --i) {
                            this.psStack[this.psLoc++] = this.pop();
                        }
                        continue block27;
                    }
                    if (v == 17) {
                        this.stack[this.sloc++] = this.psStack[this.psLoc - 1];
                        --this.psLoc;
                        break;
                    }
                    throw new RuntimeException("Bad command (" + v + ")");
                }
                case 13: {
                    wid.x = this.pop();
                    wid.y = 0.0f;
                    pt.x = this.pop();
                    pt.y = 0.0f;
                    this.sloc = 0;
                    break;
                }
                case 14: {
                    break;
                }
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: {
                    throw new RuntimeException("Bad command (" + v + ")");
                }
                case 21: {
                    pt.y += this.pop();
                    pt.x += this.pop();
                    gp.moveTo(pt.x, pt.y);
                    this.sloc = 0;
                    break;
                }
                case 22: {
                    pt.x += this.pop();
                    gp.moveTo(pt.x, pt.y);
                    this.sloc = 0;
                    break;
                }
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: {
                    throw new RuntimeException("Bad command (" + v + ")");
                }
                case 30: {
                    float x3 = this.pop();
                    float y2 = this.pop();
                    float x2 = this.pop();
                    float y1 = this.pop();
                    float y3 = 0.0f;
                    float x1 = 0.0f;
                    gp.curveTo(pt.x, pt.y + y1, pt.x + x2, pt.y + y1 + y2, pt.x + x2 + x3, pt.y + y1 + y2);
                    pt.x += x2 + x3;
                    pt.y += y1 + y2;
                    this.sloc = 0;
                    break;
                }
                case 31: {
                    float y3 = this.pop();
                    float y2 = this.pop();
                    float x2 = this.pop();
                    float x1 = this.pop();
                    float x3 = 0.0f;
                    float y1 = 0.0f;
                    gp.curveTo(pt.x + x1, pt.y, pt.x + x1 + x2, pt.y + y2, pt.x + x1 + x2, pt.y + y2 + y3);
                    pt.x += x1 + x2;
                    pt.y += y2 + y3;
                    this.sloc = 0;
                }
            }
        }
    }

    private void buildAccentChar(float x, float y, char a, char b, GeneralPath gp) {
        GeneralPath pathA = this.getOutline(a, this.getWidth(a, null));
        try {
            AffineTransform xformA = this.at.createInverse();
            xformA.translate(x, y);
            pathA.transform(xformA);
        }
        catch (NoninvertibleTransformException nte) {
            pathA.transform(AffineTransform.getTranslateInstance(x, y));
        }
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

    @Override
    public float getWidth(char code, String name) {
        if (this.getFirstChar() == -1 || this.getLastChar() == -1) {
            String key = this.chr2name[code & 0xFF];
            if (name != null) {
                key = name;
            }
            if (key != null && this.name2outline.containsKey(key)) {
                FlPoint width;
                if (!this.name2width.containsKey(key)) {
                    this.getOutline(key, 0.0f);
                }
                if ((width = this.name2width.get(key)) != null) {
                    return width.x / (float)this.getDefaultWidth();
                }
            }
            return 0.0f;
        }
        return super.getWidth(code, name);
    }

    private synchronized GeneralPath parseGlyph(byte[] cs, FlPoint advance, AffineTransform at) {
        GeneralPath gp = new GeneralPath();
        FlPoint curpoint = new FlPoint();
        this.sloc = 0;
        this.parse(cs, gp, curpoint, advance);
        gp.transform(at);
        return gp;
    }

    @Override
    protected GeneralPath getOutline(String name, float width) {
        Object obj;
        if (name == null || !this.name2outline.containsKey(name)) {
            name = ".notdef";
        }
        if ((obj = this.name2outline.get(name)) instanceof GeneralPath) {
            return (GeneralPath)obj;
        }
        byte[] cs = (byte[])obj;
        FlPoint advance = new FlPoint();
        GeneralPath gp = this.parseGlyph(cs, advance, this.at);
        if (width != 0.0f && advance.x != 0.0f) {
            Point2D.Float p = new Point2D.Float(advance.x, advance.y);
            this.at.transform(p, p);
            double scale = (double)width / ((Point2D)p).getX();
            AffineTransform xform = AffineTransform.getScaleInstance(scale, 1.0);
            gp.transform(xform);
        }
        this.name2outline.put(name, gp);
        this.name2width.put(name, advance);
        return gp;
    }

    @Override
    protected GeneralPath getOutline(char src, float width) {
        return this.getOutline(this.chr2name[src & 0xFF], width);
    }

    class PSParser {
        byte[] data;
        int loc;

        public PSParser(byte[] data, int start) {
            this.data = data;
            this.loc = start;
        }

        public String readThing() {
            while (PDFFile.isWhiteSpace(this.data[this.loc])) {
                ++this.loc;
            }
            int start = this.loc;
            while (!PDFFile.isWhiteSpace(this.data[this.loc])) {
                ++this.loc;
                if (PDFFile.isRegularCharacter(this.data[this.loc])) continue;
            }
            String s = new String(this.data, start, this.loc - start);
            return s;
        }

        public float[] readArray(int count) {
            float[] ary = new float[count];
            int idx = 0;
            while (idx < count) {
                String thing = this.readThing();
                if (thing.charAt(0) == '[') {
                    thing = thing.substring(1);
                }
                if (thing.endsWith("]")) {
                    thing = thing.substring(0, thing.length() - 1);
                }
                if (thing.length() <= 0) continue;
                ary[idx++] = Float.valueOf(thing).floatValue();
            }
            return ary;
        }

        public int getLoc() {
            return this.loc;
        }

        public void setLoc(int loc) {
            this.loc = loc;
        }

        public byte[] getNEncodedBytes(int n, int key, int skip) {
            byte[] result = Type1Font.this.decrypt(this.data, this.loc, this.loc + n, key, skip);
            this.loc += n;
            return result;
        }
    }
}

