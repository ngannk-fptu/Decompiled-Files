/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.Glyf;
import com.sun.pdfview.font.ttf.LocaTable;
import com.sun.pdfview.font.ttf.MaxpTable;
import com.sun.pdfview.font.ttf.TrueTypeFont;
import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.nio.ByteBuffer;

public class GlyfTable
extends TrueTypeTable {
    private Object[] glyphs;
    private LocaTable loca;

    protected GlyfTable(TrueTypeFont ttf) {
        super(1735162214);
        this.loca = (LocaTable)ttf.getTable("loca");
        MaxpTable maxp = (MaxpTable)ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        this.glyphs = new Object[numGlyphs];
    }

    public Glyf getGlyph(int index) {
        Object o = this.glyphs[index];
        if (o == null) {
            return null;
        }
        if (o instanceof ByteBuffer) {
            Glyf g = Glyf.getGlyf((ByteBuffer)o);
            this.glyphs[index] = g;
            return g;
        }
        return (Glyf)o;
    }

    @Override
    public ByteBuffer getData() {
        int size = this.getLength();
        ByteBuffer buf = ByteBuffer.allocate(size);
        for (int i = 0; i < this.glyphs.length; ++i) {
            Object o = this.glyphs[i];
            if (o == null) continue;
            ByteBuffer glyfData = null;
            glyfData = o instanceof ByteBuffer ? (ByteBuffer)o : ((Glyf)o).getData();
            glyfData.rewind();
            buf.put(glyfData);
            glyfData.flip();
        }
        buf.flip();
        return buf;
    }

    @Override
    public void setData(ByteBuffer data) {
        for (int i = 0; i < this.glyphs.length; ++i) {
            int location = this.loca.getOffset(i);
            int length = this.loca.getSize(i);
            if (length == 0) continue;
            data.position(location);
            ByteBuffer glyfData = data.slice();
            glyfData.limit(length);
            this.glyphs[i] = glyfData;
        }
    }

    @Override
    public int getLength() {
        int length = 0;
        for (int i = 0; i < this.glyphs.length; ++i) {
            Object o = this.glyphs[i];
            if (o == null) continue;
            if (o instanceof ByteBuffer) {
                length += ((ByteBuffer)o).remaining();
                continue;
            }
            length += ((Glyf)o).getLength();
        }
        return length;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
        buf.append(indent + "Glyf Table: (" + this.glyphs.length + " glyphs)\n");
        buf.append(indent + "  Glyf 0: " + this.getGlyph(0));
        return buf.toString();
    }
}

