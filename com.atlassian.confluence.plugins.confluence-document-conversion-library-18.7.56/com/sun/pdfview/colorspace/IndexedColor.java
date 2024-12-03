/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.colorspace.PDFColorSpace;
import java.awt.Color;
import java.io.IOException;

public class IndexedColor
extends PDFColorSpace {
    protected byte[] finalcolors;
    Color[] table;
    int count;
    int nchannels = 1;

    public IndexedColor(PDFColorSpace base, int count, PDFObject stream) throws IOException {
        super(null);
        this.count = ++count;
        byte[] data = stream.getStream();
        this.nchannels = base.getNumComponents();
        boolean offSized = data.length / this.nchannels < count;
        this.finalcolors = new byte[3 * count];
        this.table = new Color[count];
        float[] comps = new float[this.nchannels];
        int loc = 0;
        int finalloc = 0;
        for (int i = 0; i < count; ++i) {
            for (int j = 0; j < comps.length; ++j) {
                comps[j] = loc < data.length ? (float)(data[loc++] & 0xFF) / 255.0f : 1.0f;
            }
            this.table[i] = (Color)base.getPaint(comps).getPaint();
            this.finalcolors[finalloc++] = (byte)this.table[i].getRed();
            this.finalcolors[finalloc++] = (byte)this.table[i].getGreen();
            this.finalcolors[finalloc++] = (byte)this.table[i].getBlue();
        }
    }

    public IndexedColor(Color[] table) throws IOException {
        super(null);
        this.count = table.length;
        this.table = table;
        this.finalcolors = new byte[3 * this.count];
        this.nchannels = 3;
        int loc = 0;
        for (int i = 0; i < this.count; ++i) {
            this.finalcolors[loc++] = (byte)table[i].getRed();
            this.finalcolors[loc++] = (byte)table[i].getGreen();
            this.finalcolors[loc++] = (byte)table[i].getBlue();
        }
    }

    public int getCount() {
        return this.count;
    }

    public byte[] getColorComponents() {
        return this.finalcolors;
    }

    @Override
    public int getNumComponents() {
        return 1;
    }

    @Override
    public PDFPaint getPaint(float[] components) {
        return PDFPaint.getPaint(this.table[(int)components[0]]);
    }
}

