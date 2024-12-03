/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.CmapTable;
import com.sun.pdfview.font.ttf.GlyfTable;
import com.sun.pdfview.font.ttf.HeadTable;
import com.sun.pdfview.font.ttf.HheaTable;
import com.sun.pdfview.font.ttf.HmtxTable;
import com.sun.pdfview.font.ttf.LocaTable;
import com.sun.pdfview.font.ttf.MaxpTable;
import com.sun.pdfview.font.ttf.NameTable;
import com.sun.pdfview.font.ttf.PostTable;
import com.sun.pdfview.font.ttf.TrueTypeFont;
import java.nio.ByteBuffer;

public class TrueTypeTable {
    public static final int CMAP_TABLE = 1668112752;
    public static final int GLYF_TABLE = 1735162214;
    public static final int HEAD_TABLE = 1751474532;
    public static final int HHEA_TABLE = 1751672161;
    public static final int HMTX_TABLE = 1752003704;
    public static final int MAXP_TABLE = 1835104368;
    public static final int NAME_TABLE = 1851878757;
    public static final int POST_TABLE = 1886352244;
    public static final int LOCA_TABLE = 1819239265;
    private int tag;
    private ByteBuffer data;

    protected TrueTypeTable(int tag) {
        this.tag = tag;
    }

    public static TrueTypeTable createTable(TrueTypeFont ttf, String tagString) {
        return TrueTypeTable.createTable(ttf, tagString, null);
    }

    public static TrueTypeTable createTable(TrueTypeFont ttf, String tagString, ByteBuffer data) {
        TrueTypeTable outTable = null;
        int tag = TrueTypeTable.stringToTag(tagString);
        switch (tag) {
            case 1668112752: {
                outTable = new CmapTable();
                break;
            }
            case 1735162214: {
                outTable = new GlyfTable(ttf);
                break;
            }
            case 1751474532: {
                outTable = new HeadTable();
                break;
            }
            case 1751672161: {
                outTable = new HheaTable();
                break;
            }
            case 1752003704: {
                outTable = new HmtxTable(ttf);
                break;
            }
            case 1819239265: {
                outTable = new LocaTable(ttf);
                break;
            }
            case 1835104368: {
                outTable = new MaxpTable();
                break;
            }
            case 1851878757: {
                outTable = new NameTable();
                break;
            }
            case 1886352244: {
                outTable = new PostTable();
                break;
            }
            default: {
                outTable = new TrueTypeTable(tag);
            }
        }
        if (data != null) {
            outTable.setData(data);
        }
        return outTable;
    }

    public int getTag() {
        return this.tag;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public int getLength() {
        return this.getData().remaining();
    }

    public static String tagToString(int tag) {
        char[] c = new char[]{(char)(0xFF & tag >> 24), (char)(0xFF & tag >> 16), (char)(0xFF & tag >> 8), (char)(0xFF & tag)};
        return new String(c);
    }

    public static int stringToTag(String tag) {
        char[] c = tag.toCharArray();
        if (c.length != 4) {
            throw new IllegalArgumentException("Bad tag length: " + tag);
        }
        return c[0] << 24 | c[1] << 16 | c[2] << 8 | c[3];
    }

    public String toString() {
        String out = "    " + TrueTypeTable.tagToString(this.getTag()) + " Table.  Data is: ";
        out = this.getData() == null ? out + "not set" : out + "set";
        return out;
    }
}

